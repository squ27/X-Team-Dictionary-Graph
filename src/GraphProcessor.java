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
    private Stream<String> words;
    private ArrayList<String> wordIndex;
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
       String[] graphVertices;
        try {
            words = WordProcessor.getWordStream(filepath);
            graphVertices =  words.toArray(String[]::new);
            for(String word : graphVertices) {
                graph.addVertex(word); //adds all the strings to the graph
            }
            
            for(int i = 0; i < graphVertices.length-1; i++) {
                for(int j = 1; j < graphVertices.length; j++) {
                   edgeNeeded = WordProcessor.isAdjacent((String)graphVertices[i], (String)graphVertices[j]); //checking each word with each other word
                   if(edgeNeeded) {
                       graph.addEdge((String)graphVertices[i], (String)graphVertices[j]);
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
        return graphVertices.length;
    
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
     * @param word1 first word
     * @param word2 second word
     * @return List<String> list of the words
     */
    public List<String> getShortestPath(String word1, String word2) {
    	word1 = word1.toUpperCase();
    	word2 = word2.toUpperCase();
    	if(word1.equals(word2)) return new ArrayList<String>();
    	
    	int index1 = wordIndex.indexOf(word1);
    	int index2 = wordIndex.indexOf(word2);
    	
    	if(index1 < 0 || index2 < 0)
    		throw new IllegalArgumentException("Word is not found in the graph. ");
    	
        return shortestPath[index1][index2];
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
        Integer count = 0;
        List<String> shortest = getShortestPath(word1, word2); //call the previous method then count edges
        if(word1.equals(word2) || shortest == null) return -1;
        for(String s : shortest) {
            count++;
        }
        return count;
    }
    
    /**
     * Computes shortest paths and distances between all possible pairs of vertices.
     * This method is called after every set of updates in the graph to recompute the path information.
     * Any shortest path algorithm can be used (Djikstra's or Floyd-Warshall recommended).
     */
    public void shortestPathPrecomputation() {
    	Iterable<String> vertices = graph.getAllVertices();
    	Iterator<String> vertex = vertices.iterator();
    	wordIndex = new ArrayList<String>();
        
    	while(vertex.hasNext())
    		wordIndex.add(vertex.next());
    	
    	shortestPath = new ArrayList[wordIndex.size()][wordIndex.size()];
    	
    	for(int n = 0; n < wordIndex.size(); n++) {
	    	ArrayList<heapNode> visited = new ArrayList<heapNode>();
	    	int distance = 0;
	    	minHeap mh = new minHeap(wordIndex.size());
	    	String curWord = wordIndex.get(n);
	    	heapNode curNode = new heapNode(curWord, 0, null);
	    	mh.insert(curNode);
	    	
	    	while(visited.size() < wordIndex.size()) {
	    		heapNode min = mh.getMin();
	    		visited.add(min);
	    		distance++;
	    		heapNode[] nodeList = mh.getList();
	    		for(heapNode i : nodeList) {
	    			Iterable<String> neighbor = graph.getNeighbors(i.word);
	    			if(neighbor == null)
	    				continue;
	    			Iterator<String> neighbors = neighbor.iterator();
	    			point: while(neighbors.hasNext()){
	    				String newWord = neighbors.next();
	    				for(heapNode j : visited) {
	    					if(j.word.equals(newWord))
								continue point;
	    				}
	    				heapNode oldWord = mh.findNode(newWord);
	    				if(oldWord == null)
	    					mh.insert(new heapNode(newWord,distance,i));
	    				else
	    					if(oldWord.distance > distance)
	    						oldWord.setDistance(i, distance);
	    			}
	    		}
	    		mh.removeMin();
	    	}
	    	
	    	for(int m = 0; m < visited.size(); m++) {
	    		List<String> path = new ArrayList<String>();
	    		heapNode pathNode = visited.get(m);
	    		path.add(pathNode.word);
	    		int destIndex = wordIndex.indexOf(pathNode.word);
	    		while(pathNode.ances != null) {
	    			pathNode = pathNode.ances;
	    			path.add(0, pathNode.word);
	    		}
	    		shortestPath[n][destIndex] = path;
	    	}
    	}
    }
    
    private class minHeap{
    	private int size;
    	private heapNode[] list;
    	
    	public minHeap(int a) {
    		size = 0;
    		list = new heapNode[a];
    	}
    	
    	public boolean isEmpty() {
    		return size == 0;
    	}
    	
    	public heapNode getMin() {
    		return list[0];
    	}
    	
    	public void insert(heapNode a) {
    		list[size] = a;
    		insertHelper(size++);
    	}
    	
    	private void insertHelper(int index) {
    		if(index == 0) return;
    		
    		int parent = (index-1)/2;
    		int diff = list[index].distance - list[parent].distance;
    		
    		if(diff < 0) {
    			swap(index, parent);
    			insertHelper(parent);
    		}
    	}
    	
    	public heapNode removeMin() {
    		if(size == 1) return list[--size];
    		heapNode temp = list[0];
    		swap(0, --size);
    		removeHelper(0);
    		return temp;
    	}
    	
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
    	
    	public heapNode findNode(String word) {
    		for(int i = 0; i < size; i++) {
    			if(list[i].word == word) return list[i];
    		}
    		return null;
    	}
    	
    	private void swap(int index1, int index2) {
    		heapNode temp = list[index1];
			list[index1] = list[index2];
			list[index2] = temp;
    	}
    	
    	public heapNode[] getList() {
    		return Arrays.copyOf(list, size);
    	}
    }
    
    private class heapNode{
    	public int distance;
    	public String word;
    	public heapNode ances;
    	
    	public heapNode(String a, int b, heapNode an) {
    		distance = b;
    		word = a;
    		ances = an;
    	}
    	
    	public void setDistance(heapNode an, int val) {
    		distance = val;
    		ances = an;
    	}
    	
    	public String toString() {
    		return word;
    	}
    }
}
