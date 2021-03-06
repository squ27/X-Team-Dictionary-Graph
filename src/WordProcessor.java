//Assignment Name: P4 Dictionary Graph
//File Name: Graph.java
//Author: X-Team 67
//Due Date: Apr 16, 2018
//Other Source: -
//Known Bugs: None


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class contains some utility helper methods
 * 
 * @author sapan (sapan@cs.wisc.edu)
 */
public class WordProcessor {
	
	/**
	 * Gets a Stream of words from the filepath.
	 * 
	 * The Stream should only contain trimmed, non-empty and UPPERCASE words.
	 * 
	 * @see <a href="http://www.oracle.com/technetwork/articles/java/ma14-java-se-8-streams-2177646.html">java8 stream blog</a>
	 * 
	 * @param filepath file path to the dictionary file
	 * @return Stream<String> stream of words read from the filepath
	 * @throws IOException exception resulting from accessing the filepath
	 */
	public static Stream<String> getWordStream(String filepath) throws IOException {
		/**
		 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html">java.nio.file.Files</a>
		 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/nio/file/Paths.html">java.nio.file.Paths</a>
		 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html">java.nio.file.Path</a>
		 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html">java.util.stream.Stream</a>
		 * 
		 * class Files has a method lines() which accepts an interface Path object and 
		 * produces a Stream<String> object via which one can read all the lines from a file as a Stream.
		 * 
		 * class Paths has a method get() which accepts one or more strings (filepath),  
		 * joins them if required and produces a interface Path object
		 * 
		 * Combining these two methods:
		 *     Files.lines(Paths.get(<string filepath>))
		 *     produces
		 *         a Stream of lines read from the filepath
		 * 
		 * Once this Stream of lines is available, you can use the powerful operations available for Stream objects to combine 
		 * multiple pre-processing operations of each line in a single statement.
		 * 
		 * Few of these features:
		 * 		1. map( )      [changes a line to the result of the applied function. Mathematically, line = operation(line)]
		 * 			-  trim all the lines
		 * 			-  convert all the lines to UpperCase
		 * 			-  example takes each of the lines one by one and apply the function toString on them as line.toString() 
		 * 			   and returns the Stream:
		 * 			        streamOfLines = streamOfLines.map(String::toString) 
		 * 
		 * 		2. filter( )   [keeps only lines which satisfy the provided condition]  
		 *      	-  can be used to only keep non-empty lines and drop empty lines
		 *      	-  example below removes all the lines from the Stream which do not equal the string "apple" 
		 *                 and returns the Stream:
		 *      			streamOfLines = streamOfLines.filter(x -> x != "apple");
		 *      			 
		 * 		3. collect( )  [collects all the lines into a java.util.List object]
		 * 			-  can be used in the function which will invoke this method to convert Stream<String> of lines to List<String> of lines
		 * 			-  example below collects all the elements of the Stream into a List and returns the List:
		 * 				List<String> listOfLines = streamOfLines.collect(Collectors::toList); 
		 * 
		 * Note: since map and filter return the updated Stream objects, they can chained together as:
		 * 		streamOfLines.map(...).filter(a -> ...).map(...) and so on
		 */
		
		return Files.lines(Paths.get(filepath)).map(String::trim)
											   .filter((a)->(a != null && !a.equals("")))
											   .map(String::toUpperCase);
	}
	
	/**
	 * Adjacency between word1 and word2 is defined by:
	 * if the difference between word1 and word2 is of
	 * 	1 char replacement
	 *  1 char addition
	 *  1 char deletion
	 * then 
	 *  word1 and word2 are adjacent
	 * else
	 *  word1 and word2 are not adjacent
	 *  
	 * Note: if word1 is equal to word2, they are not adjacent
	 * 
	 * @param word1 first word
	 * @param word2 second word
	 * @return true if word1 and word2 are adjacent else false
	 */
	public static boolean isAdjacent(String word1, String word2) {
		if(word1 == null || word2 == null) throw new IllegalArgumentException();
		// If either word given is null then it throws IllegalArgumentException
		word1 = word1.toLowerCase(); 
		word2 = word2.toLowerCase();
		
		if(word1.equals(word2)) return false;
		// If the words are the same, they are not adjacent
		if(Math.abs(word1.length()-word2.length()) > 1) return false;
		// Checks if the words are more than 1 letter apart, because then they are not adjacent
		
		boolean diff = false;
		//tracks if the difference between the words makes them not adjacent
		if(word1.length() == word2.length()) { 
		    // If the two words are the same length
			for(int i = 0; i < word1.length(); i++) {
				if(word1.charAt(i) == word2.charAt(i))
					continue;
				    // Steps through if the letters are the same
				else {
					if(diff) return false; 
					// If there is more than one letter found that varies, then it is not adjacent
					else diff = true; // If only one letter differs than it is adjacent
				}
			}
		}else {
			if(word1.length() < word2.length()) {
			    // If the word1 is one letter shorter than word2, set word1 to the value of word2
			    //and set word2 to value of word1 before moving to the next instruction 
				String temp = word1;
				word1 = word2;
				word2 = temp;
			}
			//checks if there is more than one letter differing between the two words 
			//if there is will return false, the words are not adjacent 
			for(int i = 0; i < word2.length(); i++) {
				if(diff) {
					if(word1.charAt(i+1) == word2.charAt(i)) continue;
					else return false;
				}else {
					if(word1.charAt(i) == word2.charAt(i)) continue;
					else { diff = true; i--;}
				}
			}
		}
		
		return true;	
	}
	
}
