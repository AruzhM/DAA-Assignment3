package algorithms;
import graph.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MSTMetricsTest {

    @Test
    void testKruskalMetrics() {
        Graph g = new Graph(4, false);

        g.addEdge(0, 1, 10);
        g.addEdge(0, 2, 6);
        g.addEdge(0, 3, 5);
        g.addEdge(1, 3, 15);
        g.addEdge(2, 3, 4);

        Kruskal.Result result = Kruskal.mst(g);

        assertEquals(19, result.totalWeight());
        assertEquals(3, result.edgeCount());
        assertEquals(1, result.components());

        assertEquals(5, result.edgeScans());
        assertTrue(result.dsuOps() > 0);
        assertTrue(result.comparisons() > 0);
    }

    @Test
    void testPrimMetrics() {
        Graph g = new Graph(4, false);

        g.addEdge(0, 1, 10);
        g.addEdge(0, 2, 6);
        g.addEdge(0, 3, 5);
        g.addEdge(1, 3, 15);
        g.addEdge(2, 3, 4);

        Prim.Result result = Prim.mst(g);

        assertEquals(19, result.totalWeight());
        assertEquals(3, result.edgeCount());

        // Undirected adjacency lists store each edge twice.
        assertEquals(10, result.edgeScans());

        assertEquals(0, result.dsuOps());
        assertTrue(result.comparisons() > 0);
    }

    @Test
    void testMetricsAgreement() {
        Graph g = new Graph(100, false);

        for (int i = 1; i < 100; i++) {
            g.addEdge(i - 1, i, i);
        }

        Kruskal.Result k = Kruskal.mst(g);
        Prim.Result p = Prim.mst(g);

        assertEquals(k.totalWeight(), p.totalWeight());
        assertEquals(99, k.edgeCount());
        assertEquals(99, p.edgeCount());

        assertEquals(99, k.edgeScans());
        assertEquals(198, p.edgeScans());
    }

    @Test
    void testDisconnectedForest() {
        Graph g = new Graph(5, false);

        g.addEdge(0, 1, 2);
        g.addEdge(2, 3, 4);

        Kruskal.Result k = Kruskal.mst(g);
        Prim.Result p = Prim.mst(g);

        assertEquals(6, k.totalWeight());
        assertEquals(k.totalWeight(), p.totalWeight());
        assertEquals(3, k.components());
        assertEquals(3, p.components());

        assertEquals(2, k.edgeScans());
        assertEquals(4, p.edgeScans());
    }
}
