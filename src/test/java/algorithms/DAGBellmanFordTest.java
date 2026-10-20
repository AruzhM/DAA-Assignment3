package algorithms;
import graph.Graph;
import graph.Graph.Edge;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DAGBellmanFordTest {

    private long[] bellmanFord(Graph graph) {
        int n = graph.vertexCount();

        long INF = Long.MAX_VALUE / 4;

        long[] dist = new long[n];
        int[] indegree = new int[n];

        Arrays.fill(dist, INF);

        for (Edge edge : graph.edges()) {
            indegree[edge.to()]++;
        }

        // Same virtual super-source as the assignment.
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                dist[i] = 0;
            }
        }

        // Bellman-Ford: up to V-1 passes.
        for (int pass = 0; pass < n - 1; pass++) {
            boolean changed = false;

            for (Edge edge : graph.edges()) {
                int u = edge.from();
                int v = edge.to();

                if (dist[u] == INF) {
                    continue;
                }

                long candidate = dist[u] + edge.weight();

                if (candidate < dist[v]) {
                    dist[v] = candidate;
                    changed = true;
                }
            }

            if (!changed) {
                break;
            }
        }

        return dist;
    }

    @Test
    void compareRandomDAGs() {
        Random random = new Random(42);

        for (int test = 0; test < 100; test++) {
            int n = 2 + random.nextInt(29);

            Graph graph = new Graph(n, true);

            for (int u = 0; u < n; u++) {
                for (int v = u + 1; v < n; v++) {
                    if (random.nextDouble() < 0.25) {
                        int weight =
                                random.nextInt(41) - 20;

                        graph.addEdge(u, v, weight);
                    }
                }
            }

            long[] expected = bellmanFord(graph);

            DagShortestPath.Result actual =
                    DagShortestPath.shortestPaths(graph);

            assertArrayEquals(
                    expected,
                    actual.distances(),
                    "Shortest paths mismatch, test=" + test
            );
        }
    }

    @Test
    void testNegativeWeights() {
        Graph graph = new Graph(4, true);

        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 3, 3);
        graph.addEdge(2, 3, -4);

        long[] expected = bellmanFord(graph);

        DagShortestPath.Result actual =
                DagShortestPath.shortestPaths(graph);

        assertArrayEquals(expected, actual.distances());

        assertEquals(-2, actual.distances()[3]);
    }

    @Test
    void testMultipleSources() {
        Graph graph = new Graph(5, true);

        graph.addEdge(0, 2, 10);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 3, -5);

        long[] expected = bellmanFord(graph);

        DagShortestPath.Result actual =
                DagShortestPath.shortestPaths(graph);

        assertArrayEquals(expected, actual.distances());

        assertEquals(-2, actual.distances()[3]);
        assertEquals(0, actual.distances()[4]);
    }
}
