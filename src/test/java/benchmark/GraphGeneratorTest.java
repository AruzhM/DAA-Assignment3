package benchmark;
import graph.Graph;
import algorithms.Kahn;
import algorithms.Kruskal;
import algorithms.Prim;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GraphGeneratorTest {

    @Test
    void testUndirectedGraph() {
        Graph g = GraphGenerator.undirected(100, 400);

        assertEquals(100, g.vertexCount());
        assertEquals(400, g.edges().size());
        assertFalse(g.isDirected());

        Kruskal.Result k = Kruskal.mst(g);
        Prim.Result p = Prim.mst(g);

        assertEquals(1, k.components());
        assertEquals(99, k.edgeCount());
        assertEquals(k.totalWeight(), p.totalWeight());
    }

    @Test
    void testDirectedGraph() {
        Graph g = GraphGenerator.directed(100, 400);

        assertEquals(100, g.vertexCount());
        assertEquals(400, g.edges().size());
        assertTrue(g.isDirected());
    }

    @Test
    void testDAG() {
        Graph g = GraphGenerator.dag(100, 400);

        assertEquals(100, g.vertexCount());
        assertEquals(400, g.edges().size());

        Kahn.Result result = Kahn.topologicalSort(g);

        assertFalse(result.hasCycle());
        assertTrue(Kahn.isValidOrder(g, result.order()));
    }

    @Test
    void testReproducibility() {
        Graph a = GraphGenerator.undirected(50, 200);
        Graph b = GraphGenerator.undirected(50, 200);

        assertEquals(a.edges(), b.edges());
    }
}
