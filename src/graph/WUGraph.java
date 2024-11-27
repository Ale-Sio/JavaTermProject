package graph;

import list.*;
import dict.*;
import java.util.*;

/**
 * The graph.WUGraph class represents a weighted, undirected graph.  Self-edges are
 * permitted.
 */

public class WUGraph {

    /**
     * graph.WUGraph() constructs a graph having no vertices or edges.
     * <p>
     * Running time:  O(1).
     *
     */
    private HashTableChained vertexDict;
    private HashTableChained edgeDict;
    private DList vertices;

    public WUGraph() {
        vertexDict = new HashTableChained();
        edgeDict = new HashTableChained();
        vertices = new DList();
    }

    /**
     * vertexCount() returns the number of vertices in the graph.
     * <p>
     * Running time:  O(1).
     */
    public int vertexCount() {
        return vertices.length();
    }

    /**
     * edgeCount() returns the total number of edges in the graph.
     * <p>
     * Running time:  O(1).
     */
    public int edgeCount() {
        return edgeDict.size();
    }

    /**
     * getVertices() returns an array containing all the objects that serve
     * as vertices of the graph.  The array's length is exactly equal to the
     * number of vertices.  If the graph has no vertices, the array has length
     * zero.
     * <p>
     * (NOTE:  Do not return any internal data structure you use to represent
     * vertices!  Return only the same objects that were provided by the
     * calling application in calls to addVertex().)
     * <p>
     * Running time:  O(|V|).
     */
    public Object[] getVertices() {
        int count = vertexCount();
        Object[] vertexList = new Object[count];
        int i = 0;

        DListNode current = vertices.front();

        while (current != null && i < count) {
            if (current.item != null) {
                Vertex tempVert = (Vertex) current.item;
                vertexList[i] = tempVert.key;
                i++;
            }
            current = current.getNext();
        }

        if (i != count) {
            System.out.println("Warning: Mismatch between vertex count and actual traversal.");
        }

        return vertexList;
    }


    /**
     * addVertex() adds a vertex (with no incident edges) to the graph.
     * The vertex's "name" is the object provided as the parameter "vertex".
     * If this object is already a vertex of the graph, the graph is unchanged.
     * <p>
     * Running time:  O(1).
     */
    public void addVertex(Object vertex) {
        if (vertex == null) {
            return;  // Prevent adding null vertex
        }

        // Check if the vertex already exists in the graph
        if (vertexDict.find(vertex) == null) {
            Vertex newVertex = new Vertex(vertex);

            vertices.insertBack(newVertex);

            vertexDict.insert(vertex, newVertex);
        }
    }

    /**
     * removeVertex() removes a vertex from the graph.  All edges incident on the
     * deleted vertex are removed as well.  If the parameter "vertex" does not
     * represent a vertex of the graph, the graph is unchanged.
     * <p>
     * Running time:  O(d), where d is the degree of "vertex".
     */
    public void removeVertex(Object vertex) {
        try {
            if (isVertex(vertex)) {
                Vertex vertexObj = (Vertex) vertexDict.find(vertex).value();
                DListNode currentNode = (DListNode) vertexObj.edges.front();

                // Use a separate list to track edges to remove
                List<Edge> edgesToRemove = new ArrayList<>();

                // Traverse all the edges for this vertex and mark them for removal
                while (currentNode != null) {
                    Object item = currentNode.item;
                    if (item instanceof Edge) {
                        Edge edge = (Edge) item;
                        if (edge.nodeA.item == vertexObj || edge.nodeB.item == vertexObj) {
                            edgesToRemove.add(edge);  // Mark edge for removal
                        }
                    }
                    currentNode = currentNode.getNext();
                }

                // Now remove the marked edges
                for (Edge edge : edgesToRemove) {
                    removeEdge(edge.nodeA.item, edge.nodeB.item);
                }

                // Remove the vertex from vertexDict and vertices list
                vertexDict.remove(vertex);

                DListNode vertexNode = vertices.front();
                while (vertexNode != null) {
                    if (vertexNode.item == vertexObj) {
                        vertices.remove(vertexNode);
                        break;
                    }
                    vertexNode = vertexNode.getNext();
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while removing vertex: " + vertex);
            e.printStackTrace();
        }
    }








    /**
     * isVertex() returns true if the parameter "vertex" represents a vertex of
     * the graph.
     * <p>
     * Running time:  O(1).
     */
    public boolean isVertex(Object vertex) {
        if(vertexDict.find(vertex) != null){
            return true;
        } else{
            return false;
        }
    }

    /**
     * degree() returns the degree of a vertex.  Self-edges add only one to the
     * degree of a vertex.  If the parameter "vertex" doesn't represent a vertex
     * of the graph, zero is returned.
     * <p>
     * Running time:  O(1).
     */
    public int degree(Object vertex) {
        Entry e = vertexDict.find(vertex);
        if(e != null){
            return ((Vertex) e.value()).degree();
        }
        return 0;
    }


    /**
     * getNeighbors() returns a new Neighbors object referencing two arrays.  The
     * Neighbors.neighborList array contains each object that is connected to the
     * input object by an edge.  The Neighbors.weightList array contains the
     * weights of the corresponding edges.  The length of both arrays is equal to
     * the number of edges incident on the input vertex.  If the vertex has
     * degree zero, or if the parameter "vertex" does not represent a vertex of
     * the graph, null is returned (instead of a Neighbors object).
     * <p>
     * The returned Neighbors object, and the two arrays, are both newly created.
     * No previously existing Neighbors object or array is changed.
     * <p>
     * (NOTE:  In the neighborList array, do not return any internal data
     * structure you use to represent vertices!  Return only the same objects
     * that were provided by the calling application in calls to addVertex().)
     * <p>
     * Running time:  O(d), where d is the degree of "vertex".
     */
    public Neighbors getNeighbors(Object vertex) {
        if (!isVertex(vertex)) {
            return null;
        }

        Vertex vertexObj = (Vertex) vertexDict.find(vertex).value();
        int degree = vertexObj.degree();

        if (degree == 0) {
            return null;
        }

        Object[] neighbors = new Object[degree];
        int[] weights = new int[degree];

        DListNode node = vertexObj.edges.front();
        int index = 0;

        while (node != null && index < degree) {
            Edge edge = (Edge) node.item;

            if (edge != null) {
                DListNode neighborNode = null;

                if (edge.nodeA == node) {
                    neighborNode = edge.nodeB;
                } else if (edge.nodeB == node) {
                    neighborNode = edge.nodeA;
                }

                if (neighborNode != null && neighborNode.item != null) {
                    boolean alreadyAdded = false;
                    for (int i = 0; i < index; i++) {
                        if (neighbors[i] == neighborNode.item) {
                            alreadyAdded = true;
                            break;
                        }
                    }

                    if (!alreadyAdded) {
                        neighbors[index] = neighborNode.item;
                        weights[index] = edge.getWeight();
                        index++;
                    }
                }
            }

            node = node.getNext();
        }

        Neighbors result = new Neighbors();
        result.neighborList = java.util.Arrays.copyOf(neighbors, index);
        result.weightList = java.util.Arrays.copyOf(weights, index);

        return result;
    }



    /**
     * addEdge() adds an edge (u, v) to the graph.  If either of the parameters
     * u and v does not represent a vertex of the graph, the graph is unchanged.
     * The edge is assigned a weight of "weight".  If the graph already contains
     * edge (u, v), the weight is updated to reflect the new value.  Self-edges
     * (where u.equals(v)) are allowed.
     * <p>
     * Running time:  O(1).
     */
    public void addEdge(Object u, Object v, int weight) {
        if (isVertex(u) && isVertex(v)) {
            Vertex vertexU = (Vertex) vertexDict.find(u).value();
            Vertex vertexV = (Vertex) vertexDict.find(v).value();
            VertexPair edgeKey = new VertexPair(u, v);

            if (isEdge(u, v)) {
                Edge edge = (Edge) edgeDict.find(edgeKey).value();
                edge.setWeight(weight);
            } else {
                DListNode nodeU = new DListNode(vertexU, null, null);
                DListNode nodeV = new DListNode(vertexV, null, null);
                Edge edge = new Edge(nodeU, nodeV, weight);
                edgeDict.insert(edgeKey, edge);

                vertexU.edges.insertBack(edge);
                edge.nodeA = vertexU.edges.back();

                if (vertexU != vertexV) {
                    vertexV.edges.insertBack(edge);
                    edge.nodeB = vertexV.edges.back();
                }
            }
        }
    }





    /**
     * removeEdge() removes an edge (u, v) from the graph.  If either of the
     * parameters u and v does not represent a vertex of the graph, the graph
     * is unchanged.  If (u, v) is not an edge of the graph, the graph is
     * unchanged.
     * <p>
     * Running time:  O(1).
     */
    public void removeEdge(Object u, Object v) {
        if (isEdge(u, v)) {
            VertexPair key = new VertexPair(u, v);
            Edge edge = (Edge) edgeDict.find(key).value();
            edgeDict.remove(key);
            edge.nodeA.remove();
            if (edge.nodeB != null) {
                edge.nodeB.remove();
            }
        }
    }


    /**
     * isEdge() returns true if (u, v) is an edge of the graph.  Returns false
     * if (u, v) is not an edge (including the case where either of the
     * parameters u and v does not represent a vertex of the graph).
     * <p>
     * Running time:  O(1).
     */
    public boolean isEdge(Object u, Object v) {
        return edgeDict.find(new VertexPair(u, v)) != null;
    }

    public int weight(Object u, Object v) {
        VertexPair key = new VertexPair(u, v);
        Entry entry = edgeDict.find(key);
        if(entry != null){
            return ((Edge) entry.value()).getWeight();
        } else{
            return 0;
        }
    }

}



/**
 * weight() returns the weight of (u, v).  Returns zero if (u, v) is not
 * an edge (including the case where either of the parameters u and v does
 * not represent a vertex of the graph).
 *
 * (NOTE:  A well-behaved application should try to avoid calling this
 * method for an edge that is not in the graph, and should certainly not
 * treat the result as if it actually represents an edge with weight zero.
 * However, some sort of default response is necessary for missing edges,
 * so we return zero.  An exception would be more appropriate, but also more
 * annoying.)
 */


