import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Undirected and unweighed graph implementation
 * 
 * @param <E> type of a vertex
 * 
 * @author sapan (sapan@cs.wisc.edu)
 * 
 */
public class Graph<E> implements GraphADT<E> {
    
    HashSet<E> vertices;
    HashMap<E,ArrayList<E>> edge;
    /**
     * Instance variables and constructors
     */
    public Graph() {
        vertices = new HashSet<E>();
        edge = new HashMap<E,ArrayList<E>>();
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public boolean addEdge(E vertex1, E vertex2) {
        if(!(vertices.contains(vertex1) && vertices.contains(vertex2)) || vertex1.equals(vertex2)) return false;
        else {
            ArrayList<E> adjList1;
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
     * {@inheritDoc}
     */
    @Override
    public boolean removeEdge(E vertex1, E vertex2) {
        if(!(vertices.contains(vertex1) && vertices.contains(vertex2)) || vertex1.equals(vertex2)) return false;
        else {
            ArrayList<E> adjList1;
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
     * {@inheritDoc}
     */
    @Override
    public boolean isAdjacent(E vertex1, E vertex2) {
        if(!(vertices.contains(vertex1) && vertices.contains(vertex2)) || vertex1.equals(vertex2) || edge.get(vertex1) == null) return false;
        else if (edge.get(vertex1).contains(vertex2)) return true;
        else return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<E> getNeighbors(E vertex) {
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
     * {@inheritDoc}
     */
    @Override
    public Iterable<E> getAllVertices() {
        return vertices;
    }

}
