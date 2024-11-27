package graph;
import list.*;

class Vertex {

    protected Object key;
    protected DList edges;

    Vertex(Object k) {
        key = k;
        edges = new DList();
    }

    int degree() {
        return edges.length();
    }

    Object key() {
        return key;
    }
}

