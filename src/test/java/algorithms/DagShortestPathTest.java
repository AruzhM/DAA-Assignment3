package algorithms;
import graph.Graph;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DagShortestPathTest {

    @Test
    void testSimpleDAG() {
        Graph g = new Graph(4, true);

        g.addEdge(0, 1, 5);
        g.addEdge(0, 2, 2);
        g.addEdge(1, 3, 3);
        g.addEdge(2, 3, 4);

        DagShortestPath.Result result =
                DagShortestPath.shortestPaths(g);

        assertArrayEquals(
                new long[]{0, 5, 2, 6},
                result.distances()
        );

        assertEquals(13, result.sumDistances());
        assertEquals(4, result.edgeScans());
        assertEquals(
                List.of(0, 2, 3),
                result.restorePath(3)
        );
    }

    @Test
    void testNegativeWeights() {
        Graph g = new Graph(4, true);

        g.addEdge(0, 1, 5);
        g.addEdge(0, 2, 2);
        g.addEdge(1, 3, 3);
        g.addEdge(2, 3, -4);

        DagShortestPath.Result result =
                DagShortestPath.shortestPaths(g);

        assertEquals(-2, result.distances()[3]);
        assertEquals(
                List.of(0, 2, 3),
                result.restorePath(3)
        );
    }

    @Test
    void testMultipleSources() {
        Graph g = new Graph(4, true);

        g.addEdge(0, 2, 10);
        g.addEdge(1, 2, 3);
        g.addEdge(2, 3, 5);

        DagShortestPath.Result result =
                DagShortestPath.shortestPaths(g);

        assertArrayEquals(
                new long[]{0, 0, 3, 8},
                result.distances()
        );
        assertEquals(11, result.sumDistances());
    }

    @Test
    void testCycleRejected() {
        Graph g = new Graph(3, true);

        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> DagShortestPath.shortestPaths(g)
        );
    }

    @Test
    void testIsolatedVertices() {
        Graph g = new Graph(3, true);

        DagShortestPath.Result result =
                DagShortestPath.shortestPaths(g);

        assertArrayEquals(
                new long[]{0, 0, 0},
                result.distances()
        );
        assertEquals(0, result.sumDistances());
    }
}
