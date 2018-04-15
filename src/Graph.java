//Assignment Name: P4 Dictionary Graph
//File Name: Graph.java
//Author: X-Team 67/Neal Pongmorrakot
//Email: pongmorrakot@wisc.edu
//Due Date: Apr 16, 2018
//Other Source: -
//Known Bugs: None


import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * Undirected and unweighed graph implementation
 * 
 * @param <E> type of a vertex
 * 
 * @author sapan (sapan@cs.wisc.edu)
 * 
 */
public class Graph<E> implements GraphADT<E> {
    
    //contains all vertices in unsorted order
    HashSet<E> vertices;
    //contains adjacency lists of all vertices
    HashMap<E,ArrayList<E>> edge;
    /**
     * Instance variables and constructors
     */
    public Graph() {
        vertices = new HashSet<E>();
        edge = new HashMap<E,ArrayList<E>>();
    }

    /**
     * Add new vertex to the graph
     * 
     * Valid argument conditions:
     * 1. vertex should be non-null
     * 2. vertex should not already exist in the graph 
     * 
     * @param vertex the vertex to be added
     * @return vertex if vertex added, else return null if vertex can not be added (also if valid conditions are violated)
     */
    @Override
    public E addVertex(E vertex) {
        if (vertex == null) return null;
        if(vertices.add(vertex)) {
            return vertex;
        }else {
            return null;
        }

    }

    /**
     * Remove the vertex and associated edge associations from the graph
     * 
     * Valid argument conditions:
     * 1. vertex should be non-null
     * 2. vertex should exist in the graph 
     *  
     * @param vertex the vertex to be removed
     * @return vertex if vertex removed, else return null if vertex and associated edges can not be removed (also if valid conditions are violated)
     */
    @Override
    public E removeVertex(E vertex) {
        if (vertex == null) return null;
        if(vertices.remove(vertex)) {
          //remove the vertex from adjacency lists of other vertices
            for (E i : edge.keySet()) {
                edge.get(i).remove(vertex);
            }
            return vertex;
        }else {
            return null;
        }
    }

    /**
     * Add an edge between two vertices (edge is undirected and unweighted)
     * 
     * Valid argument conditions:
     * 1. both the vertices should exist in the graph
     * 2. vertex1 should not equal vertex2
     *  
     * @param vertex1 the first vertex
     * @param vertex2 the second vertex
     * @return true if edge added, else return false if edge can not be added (also if valid conditions are violated)
     */
    @Override
    public boolean addEdge(E vertex1, E vertex2) {
        if(!(vertices.contains(vertex1) && vertices.contains(vertex2)) || vertex1.equals(vertex2)) return false;
        else {
            //adjacency list for vertex1
            ArrayList<E> adjList1;
            //adjacency list for vertex2
            ArrayList<E> adjList2;
            if(edge.containsKey(vertex1) && edge.containsKey(vertex2)) {
                adjList1 = edge.get(vertex1);
                adjList2 = edge.get(vertex2);
            }else if(edge.containsKey(vertex1)) {
                adjList1 = edge.get(vertex1);
                adjList2 = new ArrayList<E>();   
            }else if(edge.containsKey(vertex2)){
                adjList1 = new ArrayList<E>();
                adjList2 = edge.get(vertex2);
            }else {
                adjList1 = new ArrayList<E>();
                adjList2 = new ArrayList<E>();
            }
            adjList1.add(vertex2);
            adjList2.add(vertex1);
            edge.put(vertex1, adjList1);
            edge.put(vertex2, adjList2);
            return true;
        }
            
        
    }    

    /**
     * Remove the edge between two vertices (edge is undirected and unweighted)
     * 
     * Valid argument conditions:
     * 1. both the vertices should exist in the graph
     * 2. vertex1 should not equal vertex2
     *  
     * @param vertex1 the first vertex
     * @param vertex2 the second vertex
     * @return true if edge removed, else return false if edge can not be removed (also if valid conditions are violated)
     */
    @Override
    public boolean removeEdge(E vertex1, E vertex2) {
        if(!(vertices.contains(vertex1) && vertices.contains(vertex2)) || vertex1.equals(vertex2)) return false;
        else {
            //adjacency list for vertex1
            ArrayList<E> adjList1;
            //adjacency list for vertex2
            ArrayList<E> adjList2;
            adjList1 = edge.get(vertex1);
            adjList2 = edge.get(vertex2);
            adjList1.remove(vertex2);
            adjList2.remove(vertex1);
            edge.put(vertex1, adjList1);
            edge.put(vertex2, adjList2);
            return true;
        }
    }

    /**
     * Check whether the two vertices are adjacent
     * 
     * Valid argument conditions:
     * 1. both the vertices should exist in the graph
     * 2. vertex1 should not equal vertex2
     *  
     * @param vertex1 the first vertex
     * @param vertex2 the second vertex
     * @return true if both the vertices have an edge with each other, else return false if vertex1 and vertex2 are not connected (also if valid conditions are violated)
     */
    @Override
    public boolean isAdjacent(E vertex1, E vertex2) {
        if(!(vertices.contains(vertex1) && vertices.contains(vertex2)) || vertex1.equals(vertex2) || edge.get(vertex1) == null) return false;
        else if (edge.get(vertex1).contains(vertex2)) return true;
        else return false;
    }

    /**
     * Get all the neighbor vertices of a vertex
     * 
     * Valid argument conditions:
     * 1. vertex is not null
     * 2. vertex exists
     * 
     * @param vertex the vertex
     * @return an iterable for all the immediate connected neighbor vertices
     */
    @Override
    public Iterable<E> getNeighbors(E vertex) {
        //adjacency list for vertex
        ArrayList<E> adjList;
        if(vertex == null || !(vertices.contains(vertex)) || (edge.get(vertex) == null)) {
            adjList = new ArrayList<E>();
        }
        else {
            adjList = edge.get(vertex);

        }
        return adjList;
    }

    /**
     * Get all the vertices in the graph
     * 
     * @return an iterable for all the vertices
     */
    @Override
    public Iterable<E> getAllVertices() {
        return vertices;
    }

}
