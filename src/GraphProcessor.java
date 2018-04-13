import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
       Integer numVertices = 0;
        try {
            words = WordProcessor.getWordStream(filepath);
            String[] graphVertices = (String[]) words.toArray();
            for(String word : graphVertices) {
                numVertices++;
                graph.addVertex(word); //adds all the strings to the graph
            }
            
            for(int i = 0; i < graphVertices.length-1; i++) {
                for(int j = 1; j < graphVertices.length; j++) {
                   edgeNeeded = WordProcessor.isAdjacent(graphVertices[i], graphVertices[j]); //checking each word with each other word
                   if(edgeNeeded) {
                       graph.addEdge(graphVertices[i], graphVertices[j]);
                   }
                }
            }
            
        } catch (NullPointerException e) {
            return -1;
        } catch (IOException e) {
           return -1;
        }
        
        return numVertices;
    
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
       ArrayList<ArrayList<String>> paths = new ArrayList<ArrayList<String>>(); //creating an array list that has an arraylist in each index that holds the neighbors of the current vertex
       ArrayList<String> vertices = (ArrayList<String>) graph.getAllVertices(); //making an arraylist of all vertices to traverse through 
       for(int i = 0; i < vertices.size(); i++) {
           paths.add(i, (ArrayList<String>) graph.getNeighbors(vertices.get(i))); //puts the neighbors of each vertex in a position of the arraylist
       }
       
       
       for(String vertex : vertices) {
           ArrayList<String> v1 = (ArrayList<String>) graph.getNeighbors(vertex);
         
       }
        
        
        List<String> words = new ArrayList<>();
        words.add(word1);
        
        if(word1.equals(word2)) { //if word1 = word2, then there is an empty list
            return null;
        }
        
        Iterable<String> neighbors = graph.getNeighbors(word1);
        for(String s : neighbors) {
            if(s.equals(word2)) { //if word1 and word2 are connected 
                words.add(word2);
                return words;
            }
            else if(graph.isAdjacent(word2, s)) { //if s is connected to both word1 and word2
                words.add(s);
                words.add(word2);
                return words;
            }
            else {} //need to look to s's neighbors
        }
        
        return null;
    
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
        Integer count = -1;
        List<String> shortest = getShortestPath(word1, word2); //call th previous method then count edges
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
        
    }
}
