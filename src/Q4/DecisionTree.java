package Q4;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;





public class DecisionTree {
	
	//
	public static void main(String[] args) throws NoMatchingClassException {
		
		if (args.length != 2) {
			System.out.println("arg1: " + args[0]);
			System.out.println("arg2: " + args[1]);
			System.out.println("there should be one argument, the file path, and the second, the class attribute");
		}
		
		//get the csv file and class attribute
		String classAttr = "(A+) grade in programming?";   //UPDATE TO args[1] after *************************************************************************
		String filePath = args[0].trim();
		
		//parse into dataset
		ArrayList<ArrayList<String>> training_dataset = parseCSV(filePath);
		
	   //try creating an instance of ID3; If there is no matching class from
		//the command line argument and the dataset, an exception is thrown
	  try {
		
		  ID3 ID3Search = new ID3(training_dataset, classAttr);
		  System.out.println("Class Attribute: " +ID3Search.getClassAttr());
		  System.out.println("Class index: " + ID3Search.getClassIndex());
		  
		  
			
		   //test entropy calculation
		   double ent = ID3Search.calculateEntropy(training_dataset);
		   System.out.println("entropy: " + ent);
		   
		   //test split
		   ID3Search.splitDataset("Statistics", training_dataset);
		   
		   //test infoGain
		   double gain = ID3Search.calculateInformationGain("Math", training_dataset);
		   System.out.println("\n\nInfo gain for Sciences: " + gain);
			
		   //test find best split
		   //make empty array list (empty path, therefore at root)
		   ArrayList<String> testPath = new ArrayList<String>();
		   String bestSplit =  ID3Search.chooseBestSplit(training_dataset, testPath);
		  
		   System.out.print("Its information gain is: " + ID3Search.calculateInformationGain( bestSplit, training_dataset));
		   System.out.print("The best split is: " + bestSplit);
		   
		   //test tree
		   //create empty path
		   ArrayList<String> emptyPath = new ArrayList<String>();
			DTNode root = ID3Search.buildDecisionTree(emptyPath, training_dataset);
			ID3Search.printTree(root);
			
		   
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
			 ArrayList<String> newRow= new ArrayList<>(Arrays.asList(tempLine.split(",")));
			 
			 //add new row to dataset
			 training_dataset.add(newRow);			 
			 
			}
			
			scan.close();  //closes the scanner  
			
			//For testing csv parser
			System.out.println("\n\nIn DecisionTree, parseCSV() ***\n");
			for (ArrayList<String> row : training_dataset) {
				 for (String field : row) {
					 System.out.print("" + field + " ");
				 }
				//print next row
				 System.out.println(""); 
			 }
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  
			
		
		return training_dataset;
		
	}
	
}


