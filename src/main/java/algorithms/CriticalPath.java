package algorithms;
import graph.Graph;
import graph.Graph.Edge;
import java.util.*;

public class CriticalPath {

    public record Result(
            long longestDistance,
            List<Integer> path,
            long edgeScans
    ) {}

    public static Result find(Graph graph) {
        Kahn.Result topo = Kahn.topologicalSort(graph);

        if (topo.hasCycle()) {
            throw new IllegalArgumentException(
                    "Critical path requires a DAG"
            );
        }

        int n = graph.vertexCount();

        if (n == 0) {
            return new Result(0, List.of(), 0);
        }

        long[] dist = new long[n];
        int[] parent = new int[n];

        Arrays.fill(parent, -1);

        long edgeScans = 0;

        for (int u : topo.order()) {
            for (Edge edge : graph.neighbors(u)) {
                edgeScans++;

                int v = edge.to();
                long candidate = dist[u] + edge.weight();

                if (candidate > dist[v]) {
                    dist[v] = candidate;
                    parent[v] = u;
                }
            }
        }

        int end = 0;

        for (int i = 1; i < n; i++) {
            if (dist[i] > dist[end]) {
                end = i;
            }
        }

        List<Integer> path = new ArrayList<>();

        int current = end;

        while (current != -1) {
            path.add(current);
            current = parent[current];
        }

        Collections.reverse(path);

        return new Result(
                dist[end],
                path,
                edgeScans
        );
    }

    public static Result fromCondensation(Graph graph) {
        Condensation.Result condensation =
                Condensation.build(graph);

        return find(condensation.dag());
    }
}

