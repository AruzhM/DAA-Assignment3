package graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    public record Edge(int from, int to, int weight) {}

    private final int n;
    private final boolean directed;
    private final List<List<Edge>> adj;
    private final List<Edge> edges;

    public Graph(int n, boolean directed) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative vertex count");
        }

        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>();
        this.edges = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, int weight) {
        checkVertex(u);
        checkVertex(v);

        Edge edge = new Edge(u, v, weight);
        edges.add(edge);
        adj.get(u).add(edge);

        if (!directed) {
            adj.get(v).add(new Edge(v, u, weight));
        }
    }

    private void checkVertex(int v) {
        if (v < 0 || v >= n) {
            throw new IllegalArgumentException("Invalid vertex: " + v);
        }
    }

    public int vertexCount() {
        return n;
    }

    public boolean isDirected() {
        return directed;
    }

    public List<Edge> neighbors(int v) {
        checkVertex(v);
        return adj.get(v);
    }

    public List<Edge> edges() {
        return edges;
    }
}
