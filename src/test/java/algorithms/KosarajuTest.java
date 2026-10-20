package algorithms;
import graph.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class KosarajuTest {

    @Test
    void testOneStronglyConnectedComponent() {
        Graph g = new Graph(3, true);

        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);

        Kosaraju.Result result = Kosaraju.findSCC(g);

        assertEquals(1, result.componentCount());
        assertEquals(6, result.edgeScans());
    }

    @Test
    void testMultipleComponents() {
        Graph g = new Graph(5, true);

        g.addEdge(0, 1, 1);
        g.addEdge(1, 0, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 2, 1);
        g.addEdge(3, 4, 1);

        Kosaraju.Result result = Kosaraju.findSCC(g);

        assertEquals(3, result.componentCount());
        assertEquals(12, result.edgeScans());

        int[] c = result.componentOf();

        assertEquals(c[0], c[1]);
        assertEquals(c[2], c[3]);
        assertNotEquals(c[1], c[2]);
        assertNotEquals(c[3], c[4]);
    }

    @Test
    void testDisconnectedGraph() {
        Graph g = new Graph(4, true);

        Kosaraju.Result result = Kosaraju.findSCC(g);

        assertEquals(4, result.componentCount());
        assertEquals(0, result.edgeScans());
    }

    @Test
    void testStackSafety100000() {
        int n = 100_000;
        Graph g = new Graph(n, true);

        for (int i = 0; i < n - 1; i++) {
            g.addEdge(i, i + 1, 1);
        }

        Kosaraju.Result result = Kosaraju.findSCC(g);

        assertEquals(n, result.componentCount());
        assertEquals(2L * (n - 1), result.edgeScans());
    }

    @Test
    void testUndirectedGraphRejected() {
        Graph g = new Graph(3, false);

        assertThrows(
                IllegalArgumentException.class,
                () -> Kosaraju.findSCC(g)
        );
    }
}
