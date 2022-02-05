package Q4;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


/*
 * This class handles instantiating and building a decision tree.
 * It also handles taking a csv as an input file, opening it and parsing
 * it to provide a dataset to the ID3 algorithm to use as its 
 * training dataset to build a decision tree.
 */


public class DecisionTree {
	
	
	public static void main(String[] args) throws NoMatchingClassException {
	
		//determine the class attribute
		String classAttr = "(A+) grade in programming?";   
		//String classAttr = "House sold in 10 days?";			
		
		//set file path for input file
		String localDir = System.getProperty("user.dir");	
		String filePath = localDir + "\\src\\test.csv";
		
		//parse file to a dataset in memory
		ArrayList<ArrayList<String>> training_dataset = parseCSV(filePath);
		
	   //try creating an instance of ID3; If there is no matching class from
		//the class, produce an exception (more for command-line inputs)
	  try {
		
		  //create an instance of the ID3 algorithm for the given datset, then built it and print it
		  ID3 ID3Search = new ID3(training_dataset, classAttr);
		 
		  //initialize the path for the root node to empty (this will track which attributes have been used for splits)
		  ArrayList<String> emptyPath = new ArrayList<String>();		   
		   
		  DTNode root = ID3Search.buildDecisionTree(emptyPath, training_dataset);
		  System.out.println("DECISION TREE: \n");
		  ID3Search.printHorizontal(root, "");	
		  
		  //test solution for this dataset
		  ID3Search.testSolution(training_dataset);
		   
	  }
	  catch(NoMatchingClassException e) {
		  System.err.print(e);
	  }	
	
	}
	
	

	/*
	 * This method imports the dataset from a csv file.
	 * It returns a dataset as an arraylist(rows) of arraylists(columns/fields) 
	 * 
	 * @param path  The filepath to the csv file
	 * @return   The dataset - a 2d arraylist of the csv data
	 */
	public static ArrayList<ArrayList<String>> parseCSV(String path) 
	{  
		//parsing the csv file 
		Scanner scan;
		//create array of rows (the dataset)
		ArrayList<ArrayList<String>> training_dataset = new ArrayList<ArrayList<String>>();
		
		try {
			scan = new Scanner(new File(path));
			scan.useDelimiter(",");
					
			while (scan.hasNext())  
			{  	
			  //temporarily hold line of csv data
			 String tempLine = scan.nextLine();
			 
			 //create a row from each new csv line
			 ArrayList<String> newRow= new ArrayList<String>(Arrays.asList(tempLine.split(",")));
			 
			 //add new row to dataset
			 training_dataset.add(newRow);			 
			 
			}
			
			scan.close();  //closes the scanner  
			
			//For testing csv parser, print the dataset
			System.out.println("DATASET: ");
			for (ArrayList<String> row : training_dataset) {
				 for (String field : row) {
					 System.out.print(String.format("%12s", field));
				 }
				//print next row
				 System.out.println(""); 
			 }
			System.out.println("\n\n");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  				
		return training_dataset;		
	}
	
}


