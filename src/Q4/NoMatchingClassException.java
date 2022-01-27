package Q4;

/*
 * This class describes the exception for when a command line
 * argument for the class name does not match any attributes
 * in the dataset
 */
public class NoMatchingClassException extends Exception{
	public NoMatchingClassException (String str)  
    {  
    	  super(str);  //calls parent function
    }  
}


	