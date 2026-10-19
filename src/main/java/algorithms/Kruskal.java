package algorithms;
import graph.Graph;
import graph.Graph.Edge;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Kruskal {

    public record Result(
            long totalWeight,
            int edgeCount,
            int components
    ) {}

    public static Result mst(Graph graph) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Kruskal requires an undirected graph"
            );
        }

        int n = graph.vertexCount();
        DSU dsu = DSU.makeSet(n);

        List<Edge> edges = new ArrayList<>(graph.edges());
        edges.sort(Comparator.comparingInt(Edge::weight));

        long totalWeight = 0;
        int edgeCount = 0;

        for (Edge edge : edges) {
            int u = edge.from();
            int v = edge.to();

            if (dsu.find(u) != dsu.find(v)) {
                dsu.union(u, v);

                totalWeight += edge.weight();
                edgeCount++;
            }
        }

        return new Result(
                totalWeight,
                edgeCount,
                dsu.components()
        );
    }
}
