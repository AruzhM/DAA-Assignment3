package graph;
import algorithms.Kahn;
import algorithms.Kruskal;
import algorithms.Prim;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class GraphReaderTest {

    @Test
    void testConnectedGraph() throws Exception {
        Graph g = GraphReader.read(
                Path.of("data/connected.txt")
        );

        assertEquals(4, g.vertexCount());
        assertEquals(5, g.edges().size());
        assertFalse(g.isDirected());

        assertEquals(19, Kruskal.mst(g).totalWeight());
        assertEquals(19, Prim.mst(g).totalWeight());
    }

    @Test
    void testDisconnectedGraph() throws Exception {
        Graph g = GraphReader.read(
                Path.of("data/disconnected.txt")
        );

        assertEquals(5, g.vertexCount());
        assertEquals(3, Kruskal.mst(g).components());
        assertEquals(3, Prim.mst(g).components());
    }

    @Test
    void testCyclicGraph() throws Exception {
        Graph g = GraphReader.read(
                Path.of("data/cyclic.txt")
        );

        assertTrue(g.isDirected());
        assertEquals(4, g.edges().size());
        assertTrue(Kahn.topologicalSort(g).hasCycle());
    }
}
