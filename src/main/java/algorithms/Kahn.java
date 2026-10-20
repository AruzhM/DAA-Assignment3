package algorithms;
import graph.Graph;
import graph.Graph.Edge;
import java.util.*;

public class Kahn {

    public record Result(
            List<Integer> order,
            boolean hasCycle,
            long edgeScans
    ) {}

    public static Result topologicalSort(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Kahn requires a directed graph"
            );
        }

        int n = graph.vertexCount();
        int[] indegree = new int[n];

        // Calculate in-degrees.
        for (Edge edge : graph.edges()) {
            indegree[edge.to()]++;
        }

        Queue<Integer> queue = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
            }
        }

        List<Integer> order = new ArrayList<>();
        long edgeScans = 0;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);

            for (Edge edge : graph.neighbors(u)) {
                edgeScans++;

                int v = edge.to();
                indegree[v]--;

                if (indegree[v] == 0) {
                    queue.add(v);
                }
            }
        }

        boolean hasCycle = order.size() != n;

        return new Result(order, hasCycle, edgeScans);
    }

    public static boolean isValidOrder(
            Graph graph,
            List<Integer> order
    ) {
        int n = graph.vertexCount();

        if (order.size() != n) {
            return false;
        }

        int[] position = new int[n];
        Arrays.fill(position, -1);

        for (int i = 0; i < n; i++) {
            int vertex = order.get(i);

            if (vertex < 0 || vertex >= n) {
                return false;
            }

            if (position[vertex] != -1) {
                return false;
            }

            position[vertex] = i;
        }

        for (Edge edge : graph.edges()) {
            if (position[edge.from()] >= position[edge.to()]) {
                return false;
            }
        }

        return true;
    }
}
