package algorithms;
import graph.Graph;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class MSTComparisonTest {

    @Test
    void compareOn100RandomConnectedGraphs() {
        Random random = new Random(42);

        for (int test = 0; test < 100; test++) {
            int n = 2 + random.nextInt(49);
            Graph graph = new Graph(n, false);

            // Ensure the graph is connected.
            for (int v = 1; v < n; v++) {
                int u = random.nextInt(v);
                int weight = 1 + random.nextInt(1000);
                graph.addEdge(u, v, weight);
            }

            // Add extra random edges.
            int extraEdges = random.nextInt(n * 3 + 1);

            for (int i = 0; i < extraEdges; i++) {
                int u = random.nextInt(n);
                int v = random.nextInt(n);
                int weight = 1 + random.nextInt(1000);

                graph.addEdge(u, v, weight);
            }

            Kruskal.Result kruskal = Kruskal.mst(graph);
            Prim.Result prim = Prim.mst(graph);

            assertEquals(
                    kruskal.totalWeight(),
                    prim.totalWeight(),
                    "MST weights differ on test " + test
            );

            assertEquals(n - 1, kruskal.edgeCount());
            assertEquals(n - 1, prim.edgeCount());

            assertEquals(1, kruskal.components());
            assertEquals(1, prim.components());
        }
    }

    @Test
    void compareOnDisconnectedGraph() {
        Graph graph = new Graph(6, false);

        graph.addEdge(0, 1, 3);
        graph.addEdge(1, 2, 5);
        graph.addEdge(3, 4, 2);

        Kruskal.Result kruskal = Kruskal.mst(graph);
        Prim.Result prim = Prim.mst(graph);

        assertEquals(kruskal.totalWeight(), prim.totalWeight());
        assertEquals(kruskal.components(), prim.components());

        assertEquals(3, kruskal.components());
        assertEquals(3, prim.components());
    }
}
