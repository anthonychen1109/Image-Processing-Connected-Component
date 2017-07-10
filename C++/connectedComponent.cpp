#include <iostream>
#include <fstream>
#include <string>
using namespace std;

class connectedComponent{
private:
public:
    int numRows;
    int numCols;
    int minVal;
    int maxVal;
    int newMin;
    int newMax;
    int newLabel = 0; //initialize to 0
    int** zeroFramedAry; //dynamically allocate to size of numRows+2 by numCols+2
    int neighborAry[3];
    int* EQAry; //size of (numRows * numCols)/4
    int NLU;
    int count = 0;
    
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
        
        zeroFramedAry = new int*[numRows + 2];
        
        for(int j =0; j < numRows + 2; j++){
            zeroFramedAry[j] = new int[numCols + 2]();
        }//for
    };//constructor
    
    void loadImage(ifstream& inFile){
        int data;
        for(int i=1; i<=numRows+1; i++){
            for(int j=1; j<numCols+1; j++){
                while(!inFile.eof()){
                    inFile >> data;
                    zeroFramedAry[i][j] = data;
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
                    minVal = 1;
                    maxVal = newLabel - 1;
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
    }//connectCC_Pass1
    
    void connectCC_Pass2(){
        count = 0;
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
    }//connectCC_Pass2
    
    void connectCC_Pass3(){
        count = 0;
        minVal = 0;
        maxVal = newLabel-1;
        for(int i=1; i<numRows+1; i++){
            for(int j=1; j<numRows+1; j++){
                if(zeroFramedAry[i][j] > 0){
                    zeroFramedAry[i][j] = EQAry[zeroFramedAry[i][j]];
                    count++;
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
        for(int i=0; i<(numRows*numCols)/4; i++){
            if(EQAry[i]==i){
                EQAry[i] = NLU++;
            }//if
            else{
                EQAry[i] = EQAry[EQAry[i]];
            }//else
        }//for i
    }//updateEQAry
    
    void prettyPrint(string file, string caption){
        ofstream out;
        out.open(file, fstream::app);
        out<<caption;
        for(int i=0; i<numRows+2; i++){
            for(int j=0; j<numRows+2; j++){
                if(zeroFramedAry[i][j] == 0){
                    out << " ";
                }//if
                else{
                    out << zeroFramedAry[i][j] << " ";
                }//else
            }//for j
            out << "\n";
        }//for i
    }//prettyPrint
    
    void printProperty(string file, string caption){
        ofstream out;
        out.open(file, fstream::app);
        out<<caption;
        out << "\n";
        out << "header: " << numRows << " " <<numCols << " " << minVal << " " << maxVal;
        out << "\n";
        out << "#of pixel in the C.C: " << count;
    }//prettyPrint
    
    void printout2(string file, string caption){
        ofstream out;
        out.open(file, fstream::app);
        out<<caption;
        out << "\n";
        out << "header: " << numRows << " " <<numCols << " " << minVal << " " << maxVal;
        out << "\n";
        out << "#of pixel in the C.C: " << count;
        out << "\n";
    }//prettyPrint
    
    void printEQAry(string file, string caption){
        ofstream out;
        out.open(file, fstream::app);
        out<<caption;
        out << "\n";
        out << "EQAry: ";
        for(int i=1; i<newLabel; i++){
            out << EQAry[i] << " ";
        }//for i
        out << "\n";
        out << "\n";
    }//printEQAry
    
//    void toBinary(ofstream& outFile1){
//        outFile1 << "\n";
//        outFile1 << numRows << " " << numCols << " " << minVal << " " << maxVal << "\n";
//        for(int i=0; i<numRows+2; i++){
//            for(int j=0; j<numCols+2; j++){
//                if(zeroFramedAry[i][j] == 0){
//                    outFile1 << " ";
//                }//if
//                else{
//                    outFile1 << "1";
//                }//else
//            }//for j
//            outFile1 << "\n";
//        }//for i
//    }//toBinary
    
};//connectedComponent class

int main(int argc, const char * argv[]) {
    ifstream inFile;
    inFile.open(argv[1]);
    
    string outFile1 = argv[2];
    string outFile2 = argv[3];
    string outFile3 = argv[4];
    
    int data, numRows,numCols,maxVal,minVal;
    inFile >> data;
    numRows = data;
    cout << "numRows: " << numRows << "\n";
    inFile >> data;
    numCols = data;
    cout << "numCols: " << numCols << "\n";
    inFile >> data;
    minVal = data;
    cout << "minVal: " << minVal << "\n";
    inFile >> data;
    maxVal = data;
    cout << "maxVal: " << maxVal << "\n";
    
    connectedComponent cc(numRows,numCols,minVal,maxVal);
    cc.loadImage(inFile);
    cc.connectCC_Pass1();
    cc.prettyPrint(outFile1,"Result of 1st pass \n");
    cc.manageEQAry();
    cc.printEQAry(outFile1,"EQAry 1st pass \n");
    cc.printout2(outFile2, "Result of 1st cc");
    
    cc.connectCC_Pass2();
    cc.prettyPrint(outFile1,"Result of 2nd pass \n");
    cc.manageEQAry();
    cc.printEQAry(outFile1,"EQAry 2nd pass \n");
    cc.printout2(outFile2, "Result of 2nd cc");
    
    cc.connectCC_Pass3();
    cc.prettyPrint(outFile1,"Result of 3rd pass \n");
    cc.printEQAry(outFile1,"EQAry 3rd pass \n");
    cc.printout2(outFile2, "Result of 3rd cc");
//    cc.toBinary(outFile3);
    cc.printProperty(outFile3,"C.C properties");
    
    return 0;
}//class
