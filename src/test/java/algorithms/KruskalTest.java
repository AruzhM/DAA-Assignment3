package algorithms;
import graph.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class KruskalTest {

    @Test
    void testConnectedGraph() {
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
    }

    @Test
    void testDisconnectedGraph() {
        Graph g = new Graph(5, false);

        g.addEdge(0, 1, 2);
        g.addEdge(2, 3, 4);

        Kruskal.Result result = Kruskal.mst(g);

        assertEquals(6, result.totalWeight());
        assertEquals(2, result.edgeCount());
        assertEquals(3, result.components());
    }

    @Test
    void testSelfLoop() {
        Graph g = new Graph(3, false);

        g.addEdge(0, 0, -10);
        g.addEdge(0, 1, 5);
        g.addEdge(1, 2, 7);

        Kruskal.Result result = Kruskal.mst(g);

        assertEquals(12, result.totalWeight());
        assertEquals(2, result.edgeCount());
    }

    @Test
    void testDirectedGraphRejected() {
        Graph g = new Graph(3, true);

        assertThrows(
                IllegalArgumentException.class,
                () -> Kruskal.mst(g)
        );
    }
}
