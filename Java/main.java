import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class main {
	public static void main(String args[]) throws IOException{
	    String input = args[0];
	    
	    FileReader file = new FileReader(args[0]);
	    PrintStream outFile1 = new PrintStream(args[1]);
	    PrintStream outFile2 = new PrintStream(args[2]);
	    PrintStream outFile3 = new PrintStream(args[3]);
	    
	    Scanner inFile = new Scanner(file);
	    
	    int data, numRows,numCols,maxVal,minVal;
	    numRows = inFile.nextInt();
	    System.out.println("numRows: " + numRows);
	    numCols = inFile.nextInt();
	    System.out.println("numCols: " + numCols);
	    minVal = inFile.nextInt();
	    System.out.println("minVal: " + minVal);
	    maxVal = inFile.nextInt();
	    System.out.println("maxVal: " + maxVal);
	    
	    connectedComponent cc = new connectedComponent(numRows,numCols,minVal,maxVal);
	    
	    cc.loadImage(input);
	    outFile1.println("result after pass 1");
	    cc.connectCC_Pass1();
	    cc.prettyPrint(outFile1);
	    cc.manageEQAry();
	    outFile1.println("EQAry 1st pass");
	    cc.printEQAry(outFile1);
	    cc.printout2(outFile2);
	    
	    outFile1.println("result after pass 2");
	    cc.connectCC_Pass2();
	    cc.prettyPrint(outFile1);
	    cc.manageEQAry();
	    outFile1.println("EQAry 2nd pass");
	    cc.printEQAry(outFile1);
	    cc.printout2(outFile2);
	    
	    outFile1.println("result after pass 3");
	    cc.connectCC_Pass3();
	    cc.prettyPrint(outFile1);
	    outFile1.println("EQAry 3rd pass");
	    cc.printEQAry(outFile1);
	    cc.printout2(outFile2);
	    cc.printProperty(outFile3);
	}//main
}//class
