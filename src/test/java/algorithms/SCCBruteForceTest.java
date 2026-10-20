package algorithms;
import graph.Graph;
import graph.Graph.Edge;
import org.junit.jupiter.api.Test;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class SCCBruteForceTest {

    private boolean[] reachable(Graph graph, int start) {
        int n = graph.vertexCount();
        boolean[] visited = new boolean[n];

        Queue<Integer> queue = new ArrayDeque<>();

        visited[start] = true;
        queue.add(start);

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (Edge edge : graph.neighbors(u)) {
                int v = edge.to();

                if (!visited[v]) {
                    visited[v] = true;
                    queue.add(v);
                }
            }
        }

        return visited;
    }

    @Test
    void compareKosarajuWithBruteForce() {
        Random random = new Random(42);

        for (int test = 0; test < 100; test++) {
            int n = 2 + random.nextInt(49);
            int m = random.nextInt(n * 4 + 1);

            Graph graph = new Graph(n, true);

            for (int i = 0; i < m; i++) {
                int u = random.nextInt(n);
                int v = random.nextInt(n);

                graph.addEdge(u, v, 1);
            }

            Kosaraju.Result result =
                    Kosaraju.findSCC(graph);

            Tarjan.Result tarjan =
                    Tarjan.findSCC(graph);

            boolean[][] reach = new boolean[n][];

            for (int i = 0; i < n; i++) {
                reach[i] = reachable(graph, i);
            }

            int[] components = result.componentOf();
            int[] tarjanComponents = tarjan.componentOf();

            for (int u = 0; u < n; u++) {
                for (int v = 0; v < n; v++) {

                    // Two vertices are strongly connected
                    // if each is reachable from the other.
                    boolean sameSCC =
                            reach[u][v] && reach[v][u];

                    assertEquals(
                            sameSCC,
                            components[u] == components[v],
                            "Kosaraju mismatch, test=" + test
                    );

                    assertEquals(
                            sameSCC,
                            tarjanComponents[u] ==
                                    tarjanComponents[v],
                            "Tarjan mismatch, test=" + test
                    );
                }
            }

            assertEquals(
                    result.componentCount(),
                    tarjan.componentCount()
            );
        }
    }

    @Test
    void testTwoSeparateCycles() {
        Graph graph = new Graph(6, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 5, 1);
        graph.addEdge(5, 3, 1);

        graph.addEdge(2, 3, 1);

        Kosaraju.Result result =
                Kosaraju.findSCC(graph);

        assertEquals(2, result.componentCount());

        int[] c = result.componentOf();

        assertEquals(c[0], c[1]);
        assertEquals(c[1], c[2]);
        assertEquals(c[3], c[4]);
        assertEquals(c[4], c[5]);
        assertNotEquals(c[0], c[3]);
    }
}
