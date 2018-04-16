import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class adds additional functionality to the graph as a whole.
 * 
 * Contains an instance variable, {@link #graph}, which stores information for all the vertices and edges.
 * @see #populateGraph(String)
 *  - loads a dictionary of words as vertices in the graph.
 *  - finds possible edges between all pairs of vertices and adds these edges in the graph.
 *  - returns number of vertices added as Integer.
 *  - every call to this method will add to the existing graph.
 *  - this method needs to be invoked first for other methods on shortest path computation to work.
 * @see #shortestPathPrecomputation()
 *  - applies a shortest path algorithm to precompute data structures (that store shortest path data)
 *  - the shortest path data structures are used later to 
 *    to quickly find the shortest path and distance between two vertices.
 *  - this method is called after any call to populateGraph.
 *  - It is not called again unless new graph information is added via populateGraph().
 * @see #getShortestPath(String, String)
 *  - returns a list of vertices that constitute the shortest path between two given vertices, 
 *    computed using the precomputed data structures computed as part of {@link #shortestPathPrecomputation()}.
 *  - {@link #shortestPathPrecomputation()} must have been invoked once before invoking this method.
 * @see #getShortestDistance(String, String)
 *  - returns distance (number of edges) as an Integer for the shortest path between two given vertices
 *  - this is computed using the precomputed data structures computed as part of {@link #shortestPathPrecomputation()}.
 *  - {@link #shortestPathPrecomputation()} must have been invoked once before invoking this method.
 *  
 * @author sapan (sapan@cs.wisc.edu)
 * 
 */
public class GraphProcessor {

    /**
     * Graph which stores the dictionary words and their associated connections
     */
    private GraphADT<String> graph;
    
    /**
     * The input stream from the given file name
     */
    private Stream<String> words;
    
    /**
     * An arraylist of all words that present in the graph
     */
    private List<String> wordIndex;
    
    /**
     * The shortest path that connects any two points in the graph. For example, if the index of "cat" and index of
     * "hat" are 1 and 3 in the wordIndex list, then the shortest path from "cat" to "hat" would be shortestPath[1][3]. 
     */
    private List<String>[][] shortestPath;
    
    /**
     * Constructor for this class. Initializes instances variables to set the starting state of the object
     */
    public GraphProcessor() {
        this.graph = new Graph<>();
    }
        
    /**
     * Builds a graph from the words in a file. Populate an internal graph, by adding words from the dictionary as vertices
     * and finding and adding the corresponding connections (edges) between 
     * existing words.
     * 
     * Reads a word from the file and adds it as a vertex to a graph.
     * Repeat for all words.
     * 
     * For all possible pairs of vertices, finds if the pair of vertices is adjacent {@link WordProcessor#isAdjacent(String, String)}
     * If a pair is adjacent, adds an undirected and unweighted edge between the pair of vertices in the graph.
     *
     * Log any issues encountered (print the issue details)
     * 
     * @param filepath file path to the dictionary
     * @return Integer the number of vertices (words) added; return -1 if file not found or if encountering other exceptions
     */
    public Integer populateGraph(String filepath) {
       boolean edgeNeeded = false;
        try {
            words = WordProcessor.getWordStream(filepath);
            wordIndex =  words.collect(Collectors.toList());
            for(String word : wordIndex) {
                graph.addVertex(word); //adds all the strings to the graph
            }
            
            for(int i = 0; i < wordIndex.size()-1; i++) {
                for(int j = i+1; j < wordIndex.size(); j++) {
                   edgeNeeded = WordProcessor.isAdjacent((String)wordIndex.get(i), (String)wordIndex.get(j)); //checking each word with each other word
                   if(edgeNeeded) {
                       graph.addEdge((String)wordIndex.get(i), (String)wordIndex.get(j));
                   }
                }
            }
            
        } catch (NullPointerException e) {
            return -1;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return -1;
        } catch(Exception e) {
        	e.printStackTrace();
        	return -1;
        }
        shortestPathPrecomputation();
        return wordIndex.size();
    
    }

    
    /**
     * Gets the list of words that create the shortest path between word1 and word2
     * 
     * Example: Given a dictionary,
     *             cat
     *             rat
     *             hat
     *             neat
     *             wheat
     *             kit
     *             
     *  shortest path between cat and wheat is the following list of words:
     *     [cat, hat, heat, wheat]
     *
     * If word1 = word2, List will be empty. 
     * Both the arguments will always be present in the graph.
     * 
     * Precondition: both words are present in the graph.
     * 
     * @param word1 first word
     * @param word2 second word
     * @return List<String> list of the words
     */
    public List<String> getShortestPath(String word1, String word2) {
    	word1 = word1.toUpperCase();
    	word2 = word2.toUpperCase();
    	if(word1.equals(word2)) return new ArrayList<String>();	//if two words equal, return an empty list
    	
    	int index1 = wordIndex.indexOf(word1);
    	int index2 = wordIndex.indexOf(word2);

    	List<String> path = shortestPath[index1][index2];		//if they are not equal, return the shortest path
        return path;
    }
    

    /**
     * Gets the distance of the shortest path between word1 and word2
     * 
     * Example: Given a dictionary,
     *             cat
     *             rat
     *             hat
     *             neat
     *             wheat
     *             kit
     *  distance of the shortest path between cat and wheat, [cat, hat, heat, wheat]
     *   = 3 (the number of edges in the shortest path)
     *
     * Distance = -1 if no path found between words (true also for word1=word2)
     * Both the arguments will always be present in the graph.
     * 
     * @param word1 first word
     * @param word2 second word
     * @return Integer distance
     */
    public Integer getShortestDistance(String word1, String word2) {
        List<String> shortest = getShortestPath(word1, word2); 	//call the previous method then count edges
        if(word1.equals(word2) || shortest == null) return -1;	//if there is not a path or the words are equal, return
        														//-1
        return shortest.size()-1;
    }
    
    /**
     * Computes shortest paths and distances between all possible pairs of vertices.
     * This method is called after every set of updates in the graph to recompute the path information.
     * Any shortest path algorithm can be used (Djikstra's or Floyd-Warshall recommended).
     * 
     * This method uses Djikstra's to find the shortest path between every pair of vertices and stores the shortest 
     * path in to the 2D array of lists shortestPath. The shortest path for a word to itself would contain only the word
     * itself and the shortest path between two vertices that are not connected would be null. 
     */
    public void shortestPathPrecomputation() {
    	
    	shortestPath = new ArrayList[wordIndex.size()][wordIndex.size()];	//initialize the 2D arraylist. 
    	
    	for(int n = 0; n < wordIndex.size(); n++) {
	    	ArrayList<heapNode> visited = new ArrayList<heapNode>();		//A list of visited nodes. Words are added
	    																	//to this list if the node is removed from
	    																	//the priority queue. 
	    	minHeap mh = new minHeap(wordIndex.size());						//The priority queue
	    	String curWord = wordIndex.get(n);								//The starting word of the paths
	    	heapNode curNode = new heapNode(curWord, null);
	    	mh.insert(curNode);
	    	
	    	while(!mh.isEmpty()) {											//remove the element with the shortest path
	    																	//until the priority queue is empty
	    		heapNode min = mh.getMin();
	    		visited.add(min);											//mark the node with shortest path visited
	    		heapNode[] nodeList = mh.getList();
	    		for(heapNode i : nodeList) {								//for every item in the priority queue
	    			Iterable<String> neighbor = graph.getNeighbors(i.word);	//add all neighbors that are not marked 
	    			if(neighbor == null)									//visited and are not already in the queue
	    				continue;											//to the priority queue.
	    			Iterator<String> neighbors = neighbor.iterator();
	    			point: while(neighbors.hasNext()){
	    				String newWord = neighbors.next();
	    				for(heapNode j : visited) {
	    					if(j.word.equals(newWord))
								continue point;
	    				}
	    				heapNode oldWord = mh.findNode(newWord);
	    				heapNode newWordNode = new heapNode(newWord, i);
	    				if(oldWord == null)
	    					mh.insert(new heapNode(newWord,i));
	    				else
	    					if(oldWord.distance > newWordNode.distance)	//update the word in queue if a shorter path is
	    						oldWord.setDistance(i);					//found (not expected in this program since
	    																//the distance is continuously increasing)
	    			}
	    		}
	    		mh.removeMin();
	    	}
	    	
	    	for(int m = 0; m < visited.size(); m++) {				//construct shortest paths between the starting 
	    		List<String> path = new ArrayList<String>();		//vertex and every other vertex possible.
	    		heapNode pathNode = visited.get(m);
	    		path.add(pathNode.word);							//put the destination in the list
	    		int destIndex = wordIndex.indexOf(pathNode.word);	//find the index of destination.
	    		while(pathNode.ances != null) {						//this is true when it reaches the starting node.
	    			pathNode = pathNode.ances;
	    			path.add(0, pathNode.word);						//add the previous node to the beginning of the path
	    		}
	    		shortestPath[n][destIndex] = path;
	    	}
    	}
    }
    
    /**
     * This is a priority queue used in the Djikestra's algorithm. It compares the words distance and rank them in 
     * ascending order. 
     * 
     * @author Suyan Qu
     *
     */
    private class minHeap{
    	
    	/**
    	 * Number of elements in the priority queue. 
    	 */
    	private int size;
    	
    	/**
    	 * The list that stores the elements in the priority queue. 
    	 */
    	private heapNode[] list;
    	
    	/**
    	 * Construct a priority queue with the given size. 
    	 * @param a the size of the priority queue
    	 */
    	public minHeap(int a) {
    		size = 0;
    		list = new heapNode[a];
    	}
    	
    	/**
    	 * Checks if the priority queue is empty
    	 * @return if the priority queue is empty. 
    	 */
    	public boolean isEmpty() {
    		return size == 0;
    	}
    	
    	/**
    	 * This method finds the heapNode with the shortest distance (the heapNode with the highest priority).
    	 * @return return the heapNode with the highest priority. 
    	 */
    	public heapNode getMin() {
    		return list[0];
    	}
    	
    	/**
    	 * This method adds a new element to the priority queue. 
    	 * @param a	The heapNode object to be added to the priority queue. 
    	 */
    	public void insert(heapNode a) {
    		list[size] = a;
    		insertHelper(size++);
    	}
    	
    	/**
    	 * This method recursively restores the properties of a priority queue after an insertion is made.
    	 * @param index	The index of the node currently checking for restoring properties. 
    	 */
    	private void insertHelper(int index) {
    		if(index == 0) return;
    		
    		int parent = (index-1)/2;
    		int diff = list[index].distance - list[parent].distance;
    		
    		if(diff < 0) {
    			swap(index, parent);
    			insertHelper(parent);
    		}
    	}
    	
    	/**
    	 * This method removes the heapNode object with the highest priority from the priority queue. 
    	 * @return The heapNode with highest priority. 
    	 */
    	public heapNode removeMin() {
    		if(size == 1) return list[--size];
    		heapNode temp = list[0];
    		swap(0, --size);
    		removeHelper(0);
    		return temp;
    	}
    	
    	/**
    	 * This method recursively restores the property of a priority queue after an deletion is made. 
    	 * @param index The index of the node currently checking for restoring properties. 
    	 */
    	private void removeHelper(int index) {
    		int lc = 2*index+1;
    		int rc = 2*index+2;
    		
    		if(lc >= size) return;
    		if(rc >= size) {
    			if(list[index].distance - list[lc].distance <= 0) return;
    			else {
    				swap(index, lc);
    				removeHelper(lc);
    			}
    		}else {
    			if(list[lc].distance - list[rc].distance <= 0) {
    					if(list[index].distance - list[lc].distance <= 0) return;
    					else {
    						swap(index, lc);
    						removeHelper(lc);
    					}
    			}else {
    				if(list[index].distance - list[rc].distance <= 0) return;
    				else {
    					swap(index, rc);
    					removeHelper(rc);
    				}
    			}
    		}
    	}
    	
    	/**
    	 * This method returns the heapNode object that contains the given word. 
    	 * @param word the word stored in the heapNode we are looking for. 
    	 * @return the heapNode object in the priority queue that contains the given word. If the word is not in this 
    	 * 			priority queue, reutrn null. 
    	 */
    	public heapNode findNode(String word) {
    		for(int i = 0; i < size; i++) {
    			if(list[i].word == word) return list[i];
    		}
    		return null;
    	}
    	
    	/**
    	 * This method swaps two heapNodes in the priority queue. 
    	 * @param index1	the index of the first word to be swapped. 
    	 * @param index2	the index of the second word to be swapped. 
    	 */
    	private void swap(int index1, int index2) {
    		heapNode temp = list[index1];
			list[index1] = list[index2];
			list[index2] = temp;
    	}
    	
    	/**
    	 * This method returns a array of all elements stored in the priority queue. 
    	 * @return	An array of heapNodes stored in the priority queue. 
    	 */
    	public heapNode[] getList() {
    		return Arrays.copyOf(list, size);
    	}
    }
    
    /**
     * This is the class for nodes used in priority queue. It stores the distance, the word, and the previous node
     * @author Suyan Qu
     *
     */
    private class heapNode{
    	
    	/**
    	 * The distance from the starting vertex to this vertex. 
    	 */
    	public int distance;
    	
    	/**
    	 * The word being stored in this heapNode. 
    	 */
    	public String word;
    	
    	/**
    	 * The previous heapNode to the current node in the shortest path. 
    	 */
    	public heapNode ances;
    	
    	/**
    	 * This method constructs a new heapNode object. 
    	 * @param a	The word to be stored in this node
    	 * @param b	The distance of this node
    	 * @param an	The previous heapNode to this heapNode
    	 */
    	public heapNode(String a, heapNode an) {
    		word = a;
    		ances = an;
    		distance = an == null? 0:1+an.distance;	//since this is an unweighted graph, the edge weight is always 1
    	}
    	
    	/**
    	 * This method changes the value of this heapNode if a shorter path to this node is found. 
    	 * @param an	The new heapNode to this current node on the shortest path. 
    	 * @param val	The distance from the starting word to the current word. 
    	 */
    	public void setDistance(heapNode an) {
    		distance = 1+an.distance;
    		ances = an;
    	}
    	
    	@Override
    	public String toString() {
    		return word;
    	}
    }
}
