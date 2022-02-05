package Q4;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * This class handles the creation of a decision tree using the 
 * ID3 algorithm. It is based off of/instantiated from a given
 * training dataset.
 * 
 * This ID3 algorithm uses entropy, information gain and splitting methods
 * to build a decision tree. The decision tree can then be tested for a solution
 * (where all entries/rows in the dataset are classified correctly by the 
 * decision tree). A test dataset could also be used with the testSolution function,
 * if one is available.
 */
public class ID3 {
	
	//class attribute
		String classAttr;
		int classIndex;
		ArrayList<ArrayList<String>> training_ds; //the full trianing dataset 	
		DTNode root; //holds root of decision tree
		
	
		/*
		 * Constructor using the class Attribute and dataset for 
		 * building the decision tree
		 * 
		 * @param  classAttr   the String class attribute that the decision tree is classifying
		 * @param  dataset  the csv file dataset being used to build the decision tree
		 * @throws NoMatchingClassException
		 */
		public ID3(ArrayList<ArrayList<String>> dataset, String classAttribute) 
		throws NoMatchingClassException {
			this.classAttr = classAttribute;
			this.training_ds = dataset;
			
			//get the index for the class attribute by iterating through the entire header
			//and testing for a match; It is assumed that column names are unique
			ArrayList<String> header = this.training_ds.get(0);
			Boolean match = false; //flag to see if there is a match from command line
			int i = 0 ; //index counter
			while( !match && i < header.size()) {		
				
				if(header.get(i).contentEquals(this.classAttr)) {
					match = true;
					this.classIndex = i;	//set the matching index					
				}
				else i++; //increment header field			
			}	//throw an exception if there is no match as the program cannot continue without a valid class	
			if (!match) throw new NoMatchingClassException("no class in dataset matches class attribute entered on the command line");							
		}
		
		
		/*
		 * This function calculates the class values and their counts for a given dataset
		 * 
		 * @param ds the dataset where class values are being counted
		 * @return classValCount a hashmap with key-value pairs where the key is the class value
		 *			 and the value is the count of the class values in that dataset
		 */
		public HashMap<String, Integer>  getClassValCounts(ArrayList<ArrayList<String>> ds) {
			
			//find the counts of each class value and the total count of rows in dataset
			//hashmap that stores the class value (string) and count for that value in dataset
			HashMap<String, Integer> classValCount = new HashMap<String, Integer>();
			int total = ds.size() - 1; //-1 to not include header			
			
			//if class value not in classValCount, add it so it can be included and counted
			for (ArrayList<String> row : ds) {
				//get the class value from the row. skip first row.
				if(!(row == ds.get(0))) {
					String classKey = row.get(this.classIndex);
					if(!classValCount.containsKey(row.get(this.classIndex))) {
						classValCount.put(classKey, 0);
					}
					//increment the count for the class value
					//get current value first, then replace with incremented value
					int oldCount = classValCount.get(classKey);
					classValCount.replace(classKey, ++oldCount);
				}			
			}
			
			return classValCount;
		}
		
		
		/*
		 * This function calculates the entropy of a dataset
		 * The input is a dataset and the output is the entropy
		 * 
		 * @param  a 2d arraylist of strings - the dataset
		 * @return a double of the entropy value
		 */
		public double calculateEntropy(ArrayList<ArrayList<String>> ds){
			
			HashMap<String, Integer> classValCount = getClassValCounts(ds); //container for key-class value - value-count pairs
			int total = ds.size() - 1; //used to calculate proportions			
						
			//entropy -summation(P(i|t)log.2P(i|t)) 
			double entropy = 0;
			for (String classKey : classValCount.keySet()) {
				int count = classValCount.get(classKey);
				
				double part = ((double)count/(double)total);
				
				
				if (!(part == 0 || part == 1)) {//if partition value is 0, do nothing, value will be 0
					
					entropy = entropy - (part * logk(part, classValCount.size()));
				}			
			}
						
			return entropy;
		}			
		
		
		/*
		 * This function splits a given dataset based on a given attribute.
		 * It returns a collection of smaller datasets, subsets of the original argument dataset;
		 * The collection is a hashmap, with the keys being the attribute value for the split, and the values being the
		 * smaller datasets.
		 * 
		 * @param attribute - the attribute being split on
		 * @param dataset - the dataset being split into multiple sub-datasets
		 * @return a collection (hashmap) of datasets (key = attribute value, value = split dataset subset)
		 */
		public HashMap<String, ArrayList<ArrayList<String>>> splitDataset(String attribute, ArrayList<ArrayList<String>> dataset) {
		 
		
			
			//create the collection to be returned, hashmap of split values and the subset datasets (the splits)
			HashMap<String, ArrayList<ArrayList<String>>> splitDatasets = new HashMap<String, ArrayList<ArrayList<String>>>();
		
			
			//first get the attribute index to index into the ArrayLists when searching for matches in each row
			int attrIndex = getAttributeIndex(attribute);
			
			
			//get the header to add to each split; this is a bit redundant and the header could be a data member of the class
			ArrayList<String> header = dataset.get(0);
			
			//iterate row by row and add to collection of split datasets based on matches of attribute values for the split attribute
			for (ArrayList<String> row : dataset) {				
				//skip first row.
				if(!(row == dataset.get(0))) {
					
					String classKey = row.get(attrIndex); //if attr value not in collection, add it
				
					if(splitDatasets.containsKey(classKey)) { //dataset already created for that attribute value
						//add to existing dataset for that attribute value						
						ArrayList<ArrayList<String>> tempDataset = splitDatasets.get(classKey);
						tempDataset.add(row);
					}
					else { //dataset split not created yet for that attribute value
						//create new dataset
						ArrayList<ArrayList<String>> newDataset = new ArrayList<ArrayList<String>>();
						newDataset.add(header); //add header to each subset
						newDataset.add(row); //add first row to new dataset
						splitDatasets.put(classKey, newDataset); //add to collection
					}
				}
			}			 
			
	
			return splitDatasets;
		}				
		
		
		/*
		 * calculates the information gain for a given attribute
		 * 
		 * @param attribute is the attribute for calculating the information gain
		 * @param dataset is the dataset on which information gain is calculated
		 * @see calculateEntropy(dataset)
		 * @return infoGain the calculated information gain based on entropy
		 */
		public double calculateInformationGain(String attribute, ArrayList<ArrayList<String>> dataset) {
			
			
			
			//get total size for proportions in calculations of gain
			int parentCount = dataset.size()-1; //-1 to not count header 
			double infoGain = 0; //the accumulator for the information gain,
			int splitCount = 0 ; //will hold the count for each split
			double proportion = 0; //will hold the split to parent proportion for infoGain calculations
			
			//initialize infoGain as the entropy of parent ds as child entropy values will be subtracted
			infoGain = calculateEntropy(dataset);
			
			//create collection to hold the split datasets; split dataset based on attribute
			HashMap<String, ArrayList<ArrayList<String>>> splits = splitDataset(attribute, dataset);
			
			//calculate entropy for each split dataset 
		    //calculate the information gain using total # rows in each dataset/parent dataset * its entropy
			for(String splitVal : splits.keySet()) {
				//get dataset and calculate count and entropy, subtract product of proportion and entropy from infogain
				ArrayList<ArrayList<String>> splitDS = splits.get(splitVal);
				splitCount = splitDS.size() -1; //-1 don't include header
				proportion = (double)splitCount/parentCount;
				infoGain -= proportion * (calculateEntropy(splitDS));
			}
			
			return infoGain;
		}
			
		
		
		/*
		 * choose best split attribute (input dataset - output, split attribute)
		 * @param dataset - the dataset to find the best split for
		 * @param path - the path up until the current node, used to avoid repeating splits
		 * @return String maxAttribute - returns the attribute for which it is the best split (highest information gain)
		 */
		public String chooseBestSplit(ArrayList<ArrayList<String>>dataset, ArrayList<String> path) {
			
			double maxGain = 0; //tracks maximum info gain for comparison
			String maxAttribute = ""; //holds the attribute with the max info gain of all attributes in header
			
			//for all attributes in the header not in the path, find the information gain and track the max
			//make sure the class attribute is not tested or it will always be chosen as the best split
			for (String field : dataset.get(0)) {
				if (!(path.contains(field)) && !(field.contentEquals(this.classAttr))) {
					double tempGain = calculateInformationGain(field, dataset);
					if (tempGain > maxGain) {
						maxGain = tempGain;
						maxAttribute = field;
					}
				}
			}
			return maxAttribute;
		}
		
			

		/*
		 * A recursive tree building algorithm. It makes use of the static class variables
		 * for this instance of an ID3 algorithm (the class and its index in the dataset fields)
		 * 
		 * It creates a node for a split, and in doing so it creates the child nodes for that split.
		 * It then recursively calls itself for each child node and then returns its root node.
		 * 
		 * @param path - the path of split attributes up to the current recursive call - to avoid repeating 
		 * 					split attributes
		 * @param nodeDataset - the current dataset/subdataset for the current recursive call
		 */
		public DTNode buildDecisionTree(ArrayList<String> path, ArrayList<ArrayList<String>> nodeDataset) {
			
			//the node to be returned
			DTNode newNode = new DTNode();
			
			//base case (stopping condition)
			//if all class values are the same, make it a leaf node with that class value
			Boolean oneClass = true; //flag to determine if there are multiple classes
			Boolean sameAttributes = false; //flag to determine if all the attributes are the same
					
			
			//first check that there is at least one row
			if (nodeDataset.size() > 1) {	
				
				//see if there are more than one class value in this dataset
				HashMap<String, Integer> classCounts =  getClassValCounts(nodeDataset);
				if(classCounts.keySet().size() > 1) {
					oneClass = false;
				}
				
				//or if all attribute values are the same, make it the majority class
				//get the first row to compare the rest to (arbitrary, could be any row);
				//first check that the class test was false to avoid unnecessary work
				if (!oneClass) {					
					ArrayList<String> singleRow = nodeDataset.get(1);
					for (ArrayList<String> row : nodeDataset) {
						
						int index = 0; //index to iterate through each row
						
						while (sameAttributes && index < row.size()) {						
							if (row.get(index).equals(singleRow.get(index))) {
								sameAttributes = false; //some attribute is a mismatch	
							}
						}
					}
				}								
			}
			
			//test base case (i.e. it is at/creating a leaf node)
			if(oneClass || sameAttributes) {				
								
				//if oneClass, set it to that class
				if(oneClass) {
					String leafClass = nodeDataset.get(1).get(classIndex);
					DTNode leafNode = new DTNode("leaf", leafClass, path);
					newNode= leafNode;
					
				} else if (sameAttributes) {
					int maxCount = 0;
					String maxClassVal = "";
					String majorityClassVal = ""; //used to stored whatever the max value is
					
					//get the counts for each class, then iterate through and get the majority
					HashMap<String, Integer> classValMap = getClassValCounts(nodeDataset);
					for (String classVal : classValMap.keySet()) {
						if (classValMap.get(classVal) > maxCount) {
							maxClassVal = classVal;
						}
					}
					//if not one class, set it to the majority attribute
					DTNode leafNode = new DTNode("leaf", maxClassVal, path);
					newNode = leafNode;
				}	
				
			}
			else { //not a leaf node, create internal node and recursively call for children
			
				//find the best split attribute
				String bestAttribute = chooseBestSplit(nodeDataset, path);
										
				//create a node with the best split attribute
				DTNode thisNode = new DTNode("internal", bestAttribute, path);	
				//add this attribute to the child nodes' paths; //explicitly copy to not access object reference
				ArrayList<String> childPath = new ArrayList<String>();
				for (String pathElement : path) {
					childPath.add(pathElement);
				}
				childPath.add(bestAttribute); //add to the path
				
				
				//recursively call this algorithm and assign the returned nodes as the children of this node
				//get the split datasets, then iterate through the list of datasets based on their split value
				HashMap<String, ArrayList<ArrayList<String>>> splitSets =  splitDataset(bestAttribute, nodeDataset);
				for (String attrVal : splitSets.keySet()) {
					DTNode newChild = buildDecisionTree(childPath, splitSets.get(attrVal));
					//set parent of new child for traversing the tree later
					newChild.setParent(thisNode);
					//note that the path is updated already for both leaf nodes and internal nodes
					//add child to this nodes children
					thisNode.addChildNode(attrVal, newChild);
				}				
				newNode = thisNode;				
			}
			
			//set node to root (the last recursive call should be the root)
			this.root = newNode;
			return newNode;
		}
		
		/*
		 * This function tests if the decision tree provides a complete solution
		 * for the given dataset
		 * 
		 * @param ds is the dataset used to test if there is a solution
		 * @return boolean - true if there is a complete solution, false otherwise
		 */
		public Boolean testSolution(ArrayList<ArrayList<String>> ds) {
			
			//get root index
			Boolean solution = false;
			ArrayList<String> header = ds.get(0);
			int currentIndex = getAttributeIndex(this.root.getValue(), header);
			DTNode currentNode = root; //tracks node while traversing through tree
			int correctCount = 0;
			
			
			//test for each row			
			for (ArrayList<String> row : ds) {
				currentNode = root;
				currentIndex = getAttributeIndex(currentNode.getValue(), header);
				
				if(!(row == ds.get(0))) { //skip first row
					
					while (currentNode.getType().contentEquals("internal")) {
						
						//follow edges given the values for each node in the row					
						String edgeValue = row.get(currentIndex);					
						currentNode = currentNode.children.get(edgeValue);
												
						if(currentNode.getType().contentEquals("internal")) {
							currentIndex = getAttributeIndex(currentNode.getValue(), header);						
						}
						else { //at leaf
							currentIndex = getAttributeIndex(currentNode.getValue(), row);		
							
							if(currentNode.getValue().contentEquals(row.get(currentIndex))) {
								correctCount++;
							}
						}						
					}						
				}				
			} //for each row in ds
			
			System.out.println("\n\nCorrect classifications: " + correctCount);
			System.out.println("Number of rows: " + (ds.size()-1));			
			if ((double)correctCount / (ds.size()-1) == 1.0) {
				solution = true;
				System.out.println("There is a solution where all dataset entries match the decision tree classification");
				
			}			
			return solution;
		} 
		
		
		/*
		 * This function recursively prints a completed decision tree using
		 * a pre-order traversal. It prints the tree horizontally
		 * 
		 * @param node - the current node being printed in the recursive call 
		 * @param indent - the indent for the current node being printed
		 */
		public void printHorizontal (DTNode node, String indent) {
			
			//base case
			if (node.getType().contentEquals("leaf")) {
				System.out.println(node.getValue()); //new line				
			}
			else {//internal node, call recursively in pre-order traversal
				String nodeString = (node.getValue());
				//add to indent for child nodes
				indent = addToIndent(indent, nodeString.length());
				
				System.out.print(nodeString);
				
				
				Boolean first = true;
				for(String childAttr : node.children.keySet()) {
					int stringLength = 0;
					String attrStr = "";
					if(first) {
						attrStr =  " ---(" + childAttr + ")--- ";
						stringLength = attrStr.length();
						first = false;
					}
					else {
						String edge = " ---(" + childAttr + ")--- ";
						attrStr =  indent + edge;
						stringLength = edge.length();
					}
					
					System.out.print(attrStr);
					String newIndent = addToIndent(indent, stringLength);
					printHorizontal(node.children.get(childAttr), newIndent);					
				}				
			}						
		}
		
		/*
		 * Helper function to adjust indenting in printHorizontal
		 */
		String addToIndent(String ind, int add) {
			String newIndent = ind;
			for(int i = 0; i < add; i++) {
				newIndent += " ";
			}
			return newIndent;
		}			
		
		/*
		 * Helper function to get the index of an attribute from the dataset header
		 * 
		 * @param attribute  - the string attribute for which the index is being found in original dataset header
		 */
		public int getAttributeIndex(String attribute) {
			int index = 0 ; //index counter
			Boolean match = false;
			ArrayList<String> header = this.training_ds.get(0);
			while( !match && index < header.size()) {		
				if(header.get(index).equals(attribute)) {
					match = true;					
				}
				else index++; //increment header field			
			}
			return index;
		}		
		
		
		public int getAttributeIndex(String attribute, ArrayList<String> row) {
			int index = 0 ; //index counter
			Boolean match = false;
			
			while( !match && index < row.size()) {		
				if(row.get(index).equals(attribute)) {
					match = true;					
				}
				else index++; //increment header field			
			}
			return index;
		}		
		
		/*
		 * helper function to calculate log to base k 
		 * 
		 * @param N is the argument
		 * @param k is the base
		 * @return result is the solution
		 */
		public static double logk(double N, int k)
	    {
			// calculate using math.Log method
	        double result = (double)(Math.log(N) / Math.log(k));  
	        return result;
	    }		
		
} //ID3.java
	

