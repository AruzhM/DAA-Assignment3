package algorithms;
import graph.Graph;
import graph.Graph.Edge;
import java.util.*;

public class DagShortestPath {

    public record Result(
            long[] distances,
            int[] parent,
            long edgeScans,
            long sumDistances
    ) {
        public List<Integer> restorePath(int target) {
            if (target < 0 || target >= distances.length) {
                throw new IllegalArgumentException("Invalid vertex");
            }

            List<Integer> path = new ArrayList<>();
            int current = target;

            while (current != -1) {
                path.add(current);
                current = parent[current];
            }

            Collections.reverse(path);
            return path;
        }
    }

    public static Result shortestPaths(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "DAG shortest paths requires a directed graph"
            );
        }

        Kahn.Result topo = Kahn.topologicalSort(graph);

        if (topo.hasCycle()) {
            throw new IllegalArgumentException(
                    "Graph contains a cycle"
            );
        }

        int n = graph.vertexCount();
        int[] indegree = new int[n];

        for (Edge edge : graph.edges()) {
            indegree[edge.to()]++;
        }

        long INF = Long.MAX_VALUE / 4;
        long[] dist = new long[n];
        int[] parent = new int[n];

        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);

        // Virtual super-source:
        // all vertices with in-degree 0 start at distance 0.
        for (int v = 0; v < n; v++) {
            if (indegree[v] == 0) {
                dist[v] = 0;
            }
        }

        long edgeScans = 0;

        for (int u : topo.order()) {
            for (Edge edge : graph.neighbors(u)) {
                edgeScans++;

                int v = edge.to();

                if (dist[u] != INF) {
                    long candidate = dist[u] + edge.weight();

                    if (candidate < dist[v]) {
                        dist[v] = candidate;
                        parent[v] = u;
                    }
                }
            }
        }

        long sumDistances = 0;

        for (long d : dist) {
            if (d != INF) {
                sumDistances += d;
            }
        }

        return new Result(
                dist,
                parent,
                edgeScans,
                sumDistances
        );
    }
}
