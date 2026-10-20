package algorithms;
import graph.Graph;
import graph.Graph.Edge;
import java.util.ArrayList;
import java.util.List;

public class Kruskal {

    public record Result(
            long totalWeight,
            int edgeCount,
            int components,
            long dsuOps,
            long edgeScans,
            long comparisons
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

        // Count actual weight comparisons during sorting.
        final long[] comparisons = {0};

        edges.sort((a, b) -> {
            comparisons[0]++;
            return Integer.compare(a.weight(), b.weight());
        });

        long totalWeight = 0;
        int edgeCount = 0;
        long edgeScans = 0;

        for (Edge edge : edges) {
            edgeScans++;

            int u = edge.from();
            int v = edge.to();

            if (dsu.find(u) != dsu.find(v)) {
                dsu.union(u, v);

                totalWeight += edge.weight();
                edgeCount++;
            }
        }

        long dsuOps = dsu.getDsuOps();

        return new Result(
                totalWeight,
                edgeCount,
                dsu.components(),
                dsuOps,
                edgeScans,
                comparisons[0]
        );
    }
}
