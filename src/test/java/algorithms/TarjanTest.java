package algorithms;
import graph.Graph;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class TarjanTest {

    @Test
    void testOneSCC() {
        Graph g = new Graph(3, true);

        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);

        Tarjan.Result result = Tarjan.findSCC(g);

        assertEquals(1, result.componentCount());
        assertEquals(3, result.edgeScans());
    }

    @Test
    void testMultipleSCCs() {
        Graph g = new Graph(5, true);

        g.addEdge(0, 1, 1);
        g.addEdge(1, 0, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 2, 1);
        g.addEdge(3, 4, 1);

        Tarjan.Result result = Tarjan.findSCC(g);

        assertEquals(3, result.componentCount());
        assertEquals(6, result.edgeScans());
    }

    @Test
    void testDisconnectedGraph() {
        Graph g = new Graph(4, true);

        Tarjan.Result result = Tarjan.findSCC(g);

        assertEquals(4, result.componentCount());
    }

    @Test
    void compareTarjanWithKosaraju() {
        Random random = new Random(42);

        for (int test = 0; test < 100; test++) {
            int n = 2 + random.nextInt(30);
            Graph g = new Graph(n, true);

            int m = random.nextInt(n * 4 + 1);

            for (int i = 0; i < m; i++) {
                int u = random.nextInt(n);
                int v = random.nextInt(n);

                g.addEdge(u, v, 1);
            }

            Tarjan.Result tarjan = Tarjan.findSCC(g);
            Kosaraju.Result kosaraju = Kosaraju.findSCC(g);

            assertEquals(
                    kosaraju.componentCount(),
                    tarjan.componentCount()
            );

            int[] a = tarjan.componentOf();
            int[] b = kosaraju.componentOf();


            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    assertEquals(
                            b[i] == b[j],
                            a[i] == a[j],
                            "SCC mismatch in test " + test
                    );
                }
            }

            assertEquals(m, tarjan.edgeScans());
            assertEquals(2L * m, kosaraju.edgeScans());
        }
    }

    @Test
    void testStackSafety100000() {
        int n = 100_000;
        Graph g = new Graph(n, true);

        for (int i = 0; i < n - 1; i++) {
            g.addEdge(i, i + 1, 1);
        }

        Tarjan.Result result = Tarjan.findSCC(g);

        assertEquals(n, result.componentCount());
        assertEquals(n - 1, result.edgeScans());
    }
}
