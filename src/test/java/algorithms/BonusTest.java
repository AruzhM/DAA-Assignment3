package algorithms;
import graph.Graph;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BonusTest {

    @Test
    void testCondensation() {
        Graph g = new Graph(5, true);

        g.addEdge(0, 1, 1);
        g.addEdge(1, 0, 1);
        g.addEdge(1, 2, 4);
        g.addEdge(2, 3, 2);
        g.addEdge(3, 2, 2);
        g.addEdge(3, 4, 5);

        Condensation.Result result =
                Condensation.build(g);

        assertEquals(3, result.componentCount());
        assertEquals(3, result.dag().vertexCount());

        Kahn.Result topo =
                Kahn.topologicalSort(result.dag());

        assertFalse(topo.hasCycle());
        assertTrue(Kahn.isValidOrder(
                result.dag(), topo.order()
        ));
    }

    @Test
    void testCriticalPath() {
        Graph g = new Graph(4, true);

        g.addEdge(0, 1, 3);
        g.addEdge(0, 2, 5);
        g.addEdge(1, 3, 4);
        g.addEdge(2, 3, 1);

        CriticalPath.Result result =
                CriticalPath.find(g);

        assertEquals(7, result.longestDistance());
        assertEquals(List.of(0, 1, 3), result.path());
        assertEquals(4, result.edgeScans());
    }

    @Test
    void testCondensationCriticalPath() {
        Graph g = new Graph(4, true);

        g.addEdge(0, 1, 1);
        g.addEdge(1, 0, 1);
        g.addEdge(1, 2, 5);
        g.addEdge(2, 3, 7);

        CriticalPath.Result result =
                CriticalPath.fromCondensation(g);

        assertEquals(12, result.longestDistance());
        assertEquals(3, result.path().size());
    }

    @Test
    void testCycleRejected() {
        Graph g = new Graph(2, true);

        g.addEdge(0, 1, 1);
        g.addEdge(1, 0, 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> CriticalPath.find(g)
        );
    }

    @Test
    void testEmptyGraph() {
        Graph g = new Graph(0, true);

        CriticalPath.Result result =
                CriticalPath.find(g);

        assertEquals(0, result.longestDistance());
        assertTrue(result.path().isEmpty());
    }
}
