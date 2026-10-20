package algorithms;
import graph.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class KahnTest {

    @Test
    void testValidDAG() {
        Graph g = new Graph(5, true);

        g.addEdge(0, 1, 1);
        g.addEdge(0, 2, 1);
        g.addEdge(1, 3, 1);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 4, 1);

        Kahn.Result result = Kahn.topologicalSort(g);

        assertFalse(result.hasCycle());
        assertEquals(5, result.order().size());
        assertTrue(Kahn.isValidOrder(g, result.order()));
        assertEquals(5, result.edgeScans());
    }

    @Test
    void testCycleDetection() {
        Graph g = new Graph(3, true);

        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);

        Kahn.Result result = Kahn.topologicalSort(g);

        assertTrue(result.hasCycle());
        assertFalse(Kahn.isValidOrder(g, result.order()));
    }

    @Test
    void testDisconnectedDAG() {
        Graph g = new Graph(5, true);

        g.addEdge(0, 1, 1);
        g.addEdge(2, 3, 1);

        Kahn.Result result = Kahn.topologicalSort(g);

        assertFalse(result.hasCycle());
        assertEquals(5, result.order().size());
        assertTrue(Kahn.isValidOrder(g, result.order()));
    }

    @Test
    void testSelfLoopCycle() {
        Graph g = new Graph(2, true);

        g.addEdge(0, 0, 1);

        Kahn.Result result = Kahn.topologicalSort(g);

        assertTrue(result.hasCycle());
    }

    @Test
    void testEmptyGraph() {
        Graph g = new Graph(0, true);

        Kahn.Result result = Kahn.topologicalSort(g);

        assertFalse(result.hasCycle());
        assertTrue(result.order().isEmpty());
    }
}
