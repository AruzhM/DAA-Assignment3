package benchmark;
import graph.Graph;
import java.util.Random;

public class GraphGenerator {

    private static final long SEED = 42L;

    private static int[] shuffled(int n, Random rnd) {
        int[] values = new int[n];

        for (int i = 0; i < n; i++) {
            values[i] = i;
        }

        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);

            int temp = values[i];
            values[i] = values[j];
            values[j] = temp;
        }

        return values;
    }

    public static Graph undirected(int n, int m) {
        if (n < 1 || m < n - 1) {
            throw new IllegalArgumentException(
                    "Need n >= 1 and m >= n-1"
            );
        }

        Random rnd = new Random(SEED);
        Graph graph = new Graph(n, false);

        int[] perm = shuffled(n, rnd);

        // Random spanning tree: guarantees connectivity.
        for (int i = 1; i < n; i++) {
            int u = perm[rnd.nextInt(i)];
            int v = perm[i];
            int weight = 1 + rnd.nextInt(1000);

            graph.addEdge(u, v, weight);
        }


        for (int i = n - 1; i < m; i++) {
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);

            while (v == u) {
                v = rnd.nextInt(n);
            }

            int weight = 1 + rnd.nextInt(1000);
            graph.addEdge(u, v, weight);
        }

        return graph;
    }

    public static Graph directed(int n, int m) {
        if (n < 1 || m < 0) {
            throw new IllegalArgumentException();
        }

        Random rnd = new Random(SEED);
        Graph graph = new Graph(n, true);

        for (int i = 0; i < m; i++) {
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);
            int weight = 1 + rnd.nextInt(1000);

            graph.addEdge(u, v, weight);
        }

        return graph;
    }


    public static Graph dag(int n, int m) {
        if (n < 1 || m < 0) {
            throw new IllegalArgumentException();
        }

        Random rnd = new Random(SEED);
        Graph graph = new Graph(n, true);


        int[] rank = shuffled(n, rnd);

        for (int i = 0; i < m; i++) {
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);

            while (u == v) {
                v = rnd.nextInt(n);
            }

            if (rank[u] > rank[v]) {
                int temp = u;
                u = v;
                v = temp;
            }

            int weight = 1 + rnd.nextInt(1000);
            graph.addEdge(u, v, weight);
        }

        return graph;
    }
}
