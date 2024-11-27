package graph;
import list.*;

class Edge {

    protected DListNode nodeA;
    protected DListNode nodeB;
    protected int edgeWeight;

    Edge(DListNode a, DListNode b, int w) {
        nodeA = a;
        nodeB = b;
        edgeWeight = w;
    }

    void setWeight(int w) {
        edgeWeight = w;
    }

    int getWeight() {
        return edgeWeight;
    }

    void remove() {
        if (nodeA != null) {
            nodeA.remove();
            nodeA = null;
        }
        if (nodeB != null && nodeB != nodeA) {
            nodeB.remove();
            nodeB = null;
        }
    }
}

