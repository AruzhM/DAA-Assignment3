package benchmark;
import algorithms.*;
import graph.Graph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReferenceValuesTest {

    @Test
    void testSparse1000() {
        int n = 1000;
        int m = 4000;

        Graph undirected = GraphGenerator.undirected(n, m);

        Kruskal.Result kruskal = Kruskal.mst(undirected);
        Prim.Result prim = Prim.mst(undirected);

        assertEquals(147970L, kruskal.totalWeight());
        assertEquals(147970L, prim.totalWeight());
        assertEquals(kruskal.totalWeight(), prim.totalWeight());

        Graph directed = GraphGenerator.directed(n, m);

        Kosaraju.Result scc = Kosaraju.findSCC(directed);

        assertEquals(27, scc.componentCount());

        Graph dag = GraphGenerator.dag(n, m);

        Kahn.Result topo = Kahn.topologicalSort(dag);

        assertFalse(topo.hasCycle());
        assertTrue(Kahn.isValidOrder(dag, topo.order()));

        DagShortestPath.Result paths =
                DagShortestPath.shortestPaths(dag);

        assertEquals(396274L, paths.sumDistances());
    }
}
