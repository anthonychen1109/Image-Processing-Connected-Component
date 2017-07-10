import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Scanner;

public class connectedComponent {
	 int numRows;
	    int numCols;
	    int minVal;
	    int maxVal;
	    int newMin;
	    int newMax;
	    int newLabel; //initialize to 0
	    int[][] zeroFramedAry; //dynamically allocate to size of numRows+2 by numCols+2
	    int[] neighborAry = new int[3];
	    int[] EQAry; //size of (numRows * numCols)/4
	    int NLU;
	    int count;
	    
	    connectedComponent(int numRow, int numCol, int min, int max){
	        numRows = numRow;
	        numCols = numCol;
	        minVal = min;
	        maxVal = max;
	        newLabel = 0;
	        int size = (numRows * numCols)/4;
	        EQAry = new int[size];
	        
	        for(int i=0; i<size; i++){
	            EQAry[i] = i;
	        }//for
	        
	        zeroFramedAry = new int[numRows+2][numCols+2];
	        
	        for(int i=0; i<numRows+2; i++){
	        	for(int j=0; j<numCols+2; j++){
	        		zeroFramedAry[i][j] = 0;
	        	}//for j
	        }//for i
	        
	        for(int i=0; i<3; i++){
	        	neighborAry[i] = 0;
	        }//for i

	    };//constructor
	    
	    void readImage(String path) throws FileNotFoundException{
	        int data;
	        
	        FileReader file = new FileReader(path);
	        Scanner inFile = new Scanner(file);
			
			numRows = inFile.nextInt();
			System.out.println("numRows: " + numRows);
			numCols = inFile.nextInt();
			System.out.println("numCols: " + numCols);
			minVal = inFile.nextInt();
			System.out.println("minVal: " + minVal);
			maxVal = inFile.nextInt();
			System.out.println("maxVal: " + maxVal);
	    }//readImage
	    
	    void loadImage(String path) throws FileNotFoundException{
	        int data;
	        FileReader file = new FileReader(path);
	        Scanner inFile = new Scanner(file);
	        numRows = inFile.nextInt();
	        numCols = inFile.nextInt();
	        minVal = inFile.nextInt();
	        maxVal = inFile.nextInt();
	        for(int i=1; i<=numRows+1; i++){
	            for(int j=1; j<numCols+1; j++){
	                while(inFile.hasNextInt()){
	                	zeroFramedAry[i][j] = inFile.nextInt();
	                    break;
	                }//while
	            }//for j
	        }//for i
	    }//loadImage
	    
	    void connectCC_Pass1(){
	        count = 0;
	        for(int i=1; i<numRows+1; i++){
	            for(int j=1; j<numCols+1; j++){
	                if(zeroFramedAry[i][j] > 0){
	                    neighborAry[0] = zeroFramedAry[i-1][j];
	                    neighborAry[1] = zeroFramedAry[i][j-1];
	                    int max = findMax(neighborAry,1,0);
	                    int min = findMin(max,neighborAry,1,max);
	                    //case1
	                    if(neighborAry[0]==0 && neighborAry[1]==0){
	                        zeroFramedAry[i][j] = newLabel++;
	                        NLU = newLabel;
	                        count++;
	                    }//if
	                    //case2
	                    else if(min==max){
	                        zeroFramedAry[i][j] = max;
	                        count++;
	                    }//else if
	                    //case3
	                    else{
	                        zeroFramedAry[i][j] = min;
	                        updateEQAry(neighborAry, min);
	                        count++;
	                    }//else if
	                }//if
	            }//for j
	        }//for i
	        newMin = 1;
	        newMax = newLabel-1;
	    }//connectCC_Pass1
	    
	    void connectCC_Pass2(){
	        count = 0;
	        newMin = 999;
	        newMax = 1;
	        for(int i=numRows; i>0; i--){
	            for(int j=numCols; j>0; j--){
	                if(zeroFramedAry[i][j] > 0){
	                    neighborAry[0] = zeroFramedAry[i][j];
	                    neighborAry[1] = zeroFramedAry[i][j+1];
	                    neighborAry[2] = zeroFramedAry[i+1][j];
	                    int max = findMax(neighborAry,2,zeroFramedAry[i][j]);
	                    int min = findMin(max,neighborAry,2,zeroFramedAry[i][j]);
	                    if(min!=max){
	                        zeroFramedAry[i][j] = min;
	                        updateEQAry(neighborAry, min);
	                        count++;
	                    }//if
	                }//if
	            }//for j
	        }//for i
	        for(int i=numRows; i>0; i--){
	            for(int j=numCols; j>0; j--){
	            	if(zeroFramedAry[i][j] > 0){
	            		if(zeroFramedAry[i][j] > newMax){
	            			newMax = zeroFramedAry[i][j];
	            		}//if
	            		else if(zeroFramedAry[i][j] < newMin){
	            			newMin = zeroFramedAry[i][j];
	            		}//else if
	            	}//if
	            }//for j
	        }//for i
	    }//connectCC_Pass2
	    
	    void connectCC_Pass3(){
	        count = 0;
	        newMin = 0;
	        newMax = 0;
	        for(int i=1; i<numRows+1; i++){
	            for(int j=1; j<numRows+1; j++){
	                if(zeroFramedAry[i][j] > 0){
	                    zeroFramedAry[i][j] = EQAry[zeroFramedAry[i][j]];
	                    count++;
	                }//if
	            }//for j
	        }//for i
	        for(int i=1; i<numRows+1; i++){
	            for(int j=1; j<numRows+1; j++){
	                if(zeroFramedAry[i][j] > 0){
	                    if(zeroFramedAry[i][j] > newMax){
	                    	newMax = zeroFramedAry[i][j];
	                    }//if
	                }//if
	            }//for j
	        }//for i
	    }//conectCC_Pass3
	    
	    int findMin(int max, int neighborAry[], int passNum, int currentValue){
	        int min = max;
	        for(int i=0; i<3; i++){
	            if(neighborAry[i] < min && neighborAry[i] != 0){
	                min = neighborAry[i];
	            }//if
	        }//for i
	        
	        if(passNum==1){
	            return min;
	        }//if
	        else if(currentValue < min){
	            return currentValue;
	        }//else if
	        else{
	            return min;
	        }//else
	    }//findMin
	    
	    int findMax(int neighborAry[], int passNum, int currentValue){
	        int max=0;
	        for(int i=0 ; i<3; i++){
	            if(neighborAry[i] > max){
	                max = neighborAry[i];
	            }//if
	        }//for i
	        
	        if(passNum==2){
	            if(currentValue > max){
	                return currentValue;
	            }//if
	            else{
	                return max;
	            }//else
	        }//if
	        else{
	            return max;
	        }//else
	    }//findMax
	    
	    void updateEQAry(int neighborAry[], int min){
	        for(int i=0; i<3; i++){
	            if(neighborAry[i] != 0){
	                EQAry[neighborAry[i]] = min;
	            }//if
	        }//for i
	    }//updateEQAry
	    
	    void manageEQAry(){
	        NLU = 1;
	        for(int i=0; i<newLabel; i++){
	            if(EQAry[i]==i){
	                EQAry[i] = NLU++;
	            }//if
	            else{
	                EQAry[i] = EQAry[EQAry[i]];
	            }//else
	        }//for i
	    }//updateEQAry
	    
	    void prettyPrint(PrintStream outFile1){
	    	outFile1.print("header: " + numRows + " " + numCols + " " + newMin + " " + newMax);
	        for(int i=0; i<numRows+2; i++){
	            for(int j=0; j<numRows+2; j++){
	                if(zeroFramedAry[i][j] == 0){
	                    outFile1.print(" ");
	                }//if
	                else{
	                    outFile1.print(zeroFramedAry[i][j]);
	                }//else
	            }//for j
	            outFile1.println();
	        }//for i
	    }//prettyPrint
	    
	    void printProperty(PrintStream outFile1){
	        outFile1.println();
	        outFile1.print("header: " + numRows + " " + numCols + " " + newMin + " " + newMax);
	        outFile1.println();
	        outFile1.print("#of pixel in the C.C: " + count);
	    }//prettyPrint
	    
	    void printout2(PrintStream outFile1){
	        outFile1.println();
	        outFile1.print("header: " + numRows + " " + numCols + " " + newMin + " " + newMax);
	        outFile1.println();
	        outFile1.print("#of pixel in the C.C: " + count);
	        outFile1.println();
	    }//prettyPrint
	    
	    void printEQAry(PrintStream outFile1){
	        outFile1.println();
	        outFile1.print("EQAry: ");
	        for(int i=1; i<newLabel; i++){
	            outFile1.print(EQAry[i] + " ");
	        }//for i
	        outFile1.println();
	    }//printEQAry

}//class
