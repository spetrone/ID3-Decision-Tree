package Q4;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * The node object used to build the decision tree
 * 
 * It contains information about its type (internal or leaf)
 * It also contains a container where it points to its children with key value pairs
 * where the key is the decision/branch based on its splitting attribute and the
 * value is the child node.
 */

public class DTNode {
	
	String type; //L = leaf, I = internal
	String value; //split attribute or class depending on type
	ArrayList<String> pathMap = new ArrayList<String>(); //contains attributes used along path for decisions (prevent repeats)
	ArrayList<ArrayList<String>> dataset; //the dataset associated with the split for this node; for root of tree, this is the full training dataset
	HashMap<String, DTNode> children = new HashMap<String, DTNode>(); //container for children key = split attribute value, value = child node
	
	
	//constructor
	public DTNode(String tp, String val, ArrayList<String> path) {
		this.type = tp;
		this.value = val;
		this.pathMap = path;		
	}
	
	public DTNode() {
		this.type = "";
		this.value = "";
		
	}
	
	public ArrayList<String> getPath() {
		return this.pathMap;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public String getType() {
		return this.type;
	}
	
	

	/*
	 * This function adds a child node to the child node container
	 * 
	 *  @param splitAttr the value of the split attribute, i.e. the branch/decision to the child node
	 *  @param node is the instantiated node object that is the child node
	 */
	public void addChildNode(String splitAttr, DTNode node) {
		children.put(splitAttr, node);
	}
	
	
}
