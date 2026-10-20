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

        Tarjan.Result tarjan = Tarjan.findSCC(directed);

        System.out.println(
                "n=" + n + ", m=" + m
                        + " | Kosaraju=" + scc.componentCount()
                        + " | Tarjan=" + tarjan.componentCount()
        );

        assertEquals(
                scc.componentCount(),
                tarjan.componentCount(),
                "Kosaraju and Tarjan disagree"
        );

        assertEquals(27, scc.componentCount());

        Graph dag = GraphGenerator.dag(n, m);

        Kahn.Result topo = Kahn.topologicalSort(dag);

        assertFalse(topo.hasCycle());
        assertTrue(Kahn.isValidOrder(dag, topo.order()));

        DagShortestPath.Result paths =
                DagShortestPath.shortestPaths(dag);

        assertEquals(396274L, paths.sumDistances());
    }

    @Test
    void testAllReferenceValues() {
        int[] sizes = {
                1000, 1000,
                3162, 3162,
                10000, 10000,
                31623, 31623,
                100000, 100000
        };

        int[] edges = {
                4000, 20000,
                12648, 63240,
                40000, 200000,
                126492, 632460,
                400000, 2000000
        };

        long[] expectedMST = {
                147970, 30363,
                468911, 97589,
                1486139, 309550,
                4715515, 968517,
                15012094, 3056681
        };

        int[] expectedSCC = {
                27, 1,
                137, 1,
                373, 1,
                1188, 1,
                3923, 1
        };

        long[] expectedDAG = {
                396274, 252411,
                1271911, 723157,
                3941828, 2381342,
                12551891, 7376612,
                40096539, 23512087
        };

        for (int i = 0; i < sizes.length; i++) {
            int n = sizes[i];
            int m = edges[i];

            System.out.println(
                    "Checking graph: n=" + n + ", m=" + m
            );

            Graph undirected = GraphGenerator.undirected(n, m);

            Kruskal.Result kruskal = Kruskal.mst(undirected);
            Prim.Result prim = Prim.mst(undirected);

            assertEquals(
                    expectedMST[i],
                    kruskal.totalWeight(),
                    "Kruskal mismatch for n=" + n + ", m=" + m
            );

            assertEquals(
                    expectedMST[i],
                    prim.totalWeight(),
                    "Prim mismatch for n=" + n + ", m=" + m
            );

            assertEquals(
                    kruskal.totalWeight(),
                    prim.totalWeight()
            );

            assertEquals(1, kruskal.components());
            assertEquals(1, prim.components());

            Graph directed = GraphGenerator.directed(n, m);

            Kosaraju.Result scc = Kosaraju.findSCC(directed);

            assertEquals(
                    expectedSCC[i],
                    scc.componentCount(),
                    "SCC mismatch for n=" + n + ", m=" + m
            );

            Graph dag = GraphGenerator.dag(n, m);

            Kahn.Result topo = Kahn.topologicalSort(dag);

            assertFalse(topo.hasCycle());
            assertTrue(Kahn.isValidOrder(dag, topo.order()));

            DagShortestPath.Result paths =
                    DagShortestPath.shortestPaths(dag);

            assertEquals(
                    expectedDAG[i],
                    paths.sumDistances(),
                    "DAG mismatch for n=" + n + ", m=" + m
            );

            System.out.println("PASSED!");
        }
    }

    @Test
    void diagnoseSCC3162() {
        int n = 3162;
        int m = 12648;

        Graph directed = GraphGenerator.directed(n, m);

        Kosaraju.Result kosaraju =
                Kosaraju.findSCC(directed);

        Tarjan.Result tarjan =
                Tarjan.findSCC(directed);

        System.out.println("Kosaraju = "
                + kosaraju.componentCount());

        System.out.println("Tarjan = "
                + tarjan.componentCount());

        assertEquals(
                kosaraju.componentCount(),
                tarjan.componentCount(),
                "SCC algorithms disagree"
        );
    }

    @Test
    void diagnoseDirectedGenerator() {
        int n = 3162;
        int m = 12648;

        Graph original = GraphGenerator.directed(n, m);
        Graph noLoops = GraphGenerator.directedWithoutSelfLoops(n, m);

        int originalSCC =
                Kosaraju.findSCC(original).componentCount();

        int noLoopsSCC =
                Kosaraju.findSCC(noLoops).componentCount();

        System.out.println("Original SCC = " + originalSCC);
        System.out.println("Without loops SCC = " + noLoopsSCC);
        System.out.println("Expected SCC = 137");
    }


}
