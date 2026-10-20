package benchmark;
import algorithms.*;
import graph.Graph;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Supplier;

public class Benchmark {

    private static final int[] SIZES = {
            1000, 3162, 10000, 31623, 100000
    };

    private static final long[] EXPECTED_MST = {
            147970, 30363, 468911, 97589,
            1486139, 309550, 4715515, 968517,
            15012094, 3056681
    };

    private static final long[] EXPECTED_SCC = {
            27, 1, 137, 1, 373, 1,
            1188, 1, 3923, 1
    };

    private static final long[] EXPECTED_DAG = {
            396274, 252411, 1271911, 723157,
            3941828, 2381342, 12551891, 7376612,
            40096539, 23512087
    };

    private record Measurement(
            long result,
            long dsuOps,
            long edgeScans,
            long comparisons,
            long maxDepth
    ) {}

    private static Measurement measure(
            BufferedWriter writer,
            String algorithm,
            String graphName,
            int n,
            int m,
            Supplier<Measurement> task
    ) throws Exception {

        double[] times = new double[5];
        Measurement last = null;

        for (int run = 0; run < 5; run++) {
            long start = System.nanoTime();
            Measurement current = task.get();
            long end = System.nanoTime();

            times[run] = (end - start) / 1_000_000.0;

            if (last != null &&
                    !last.equals(current)) {
                throw new IllegalStateException(
                        "Inconsistent counters: " + algorithm
                );
            }

            last = current;
        }

        Arrays.sort(times);
        double median = times[2];

        writer.write(String.format(
                java.util.Locale.US,
                "%s,%s,%d,%d,%.3f,%d,%d,%d,%d,%d%n",
                algorithm,
                graphName,
                n,
                m,
                median,
                last.dsuOps(),
                last.edgeScans(),
                last.comparisons(),
                last.maxDepth(),
                last.result()
        ));

        System.out.println(
                algorithm + " | n=" + n +
                        " m=" + m +
                        " | result=" + last.result()
        );

        return last;
    }

    private static void require(
            boolean condition,
            String message
    ) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private static void benchmarkMST(
            BufferedWriter writer,
            int n,
            int m,
            int index
    ) throws Exception {

        Graph graph = GraphGenerator.undirected(n, m);
        String name = "undirected_sparse";

        if (m == 20 * n) {
            name = "undirected_dense";
        }

        Measurement kruskal = measure(
                writer, "Kruskal", name, n, m,
                () -> {
                    Kruskal.Result r = Kruskal.mst(graph);
                    return new Measurement(
                            r.totalWeight(),
                            r.dsuOps(),
                            r.edgeScans(),
                            r.comparisons(),
                            0
                    );
                }
        );

        Measurement prim = measure(
                writer, "Prim", name, n, m,
                () -> {
                    Prim.Result r = Prim.mst(graph);
                    return new Measurement(
                            r.totalWeight(),
                            r.dsuOps(),
                            r.edgeScans(),
                            r.comparisons(),
                            0
                    );
                }
        );

        require(
                kruskal.result() == prim.result(),
                "Kruskal and Prim disagree"
        );

        require(
                kruskal.result() == EXPECTED_MST[index],
                "MST reference mismatch at n=" + n
        );
    }

    private static void benchmarkSCC(
            BufferedWriter writer,
            int n,
            int m,
            int index
    ) throws Exception {

        Graph graph = GraphGenerator.directed(n, m);

        String name = m == 4 * n
                ? "directed_sparse"
                : "directed_dense";

        Measurement scc = measure(
                writer, "Kosaraju", name, n, m,
                () -> {
                    Kosaraju.Result r =
                            Kosaraju.findSCC(graph);

                    return new Measurement(
                            r.componentCount(),
                            0,
                            r.edgeScans(),
                            0,
                            r.maxDepth()
                    );
                }
        );

        require(
                scc.result() == EXPECTED_SCC[index],
                "SCC reference mismatch at n=" + n
        );

        require(
                scc.edgeScans() == 2L * m,
                "Kosaraju edge scans mismatch"
        );
    }

    private static void benchmarkDAG(
            BufferedWriter writer,
            int n,
            int m,
            int index
    ) throws Exception {

        Graph graph = GraphGenerator.dag(n, m);

        String name = m == 4 * n
                ? "dag_sparse"
                : "dag_dense";

        Measurement kahn = measure(
                writer, "Kahn", name, n, m,
                () -> {
                    Kahn.Result r =
                            Kahn.topologicalSort(graph);

                    require(!r.hasCycle(),
                            "Cycle detected in DAG");

                    require(
                            Kahn.isValidOrder(graph, r.order()),
                            "Invalid topological order"
                    );

                    return new Measurement(
                            r.order().size(),
                            0,
                            r.edgeScans(),
                            0,
                            0
                    );
                }
        );

        Measurement shortest = measure(
                writer, "DagShortestPath", name, n, m,
                () -> {
                    DagShortestPath.Result r =
                            DagShortestPath.shortestPaths(graph);

                    return new Measurement(
                            r.sumDistances(),
                            0,
                            r.edgeScans(),
                            0,
                            0
                    );
                }
        );

        require(kahn.result() == n,
                "Topological order incomplete");

        require(kahn.edgeScans() == m,
                "Kahn edge scans mismatch");

        require(shortest.edgeScans() == m,
                "DAG edge scans mismatch");

        require(
                shortest.result() == EXPECTED_DAG[index],
                "Shortest path reference mismatch"
        );
    }

    public static void main(String[] args)
            throws Exception {

        Files.createDirectories(Path.of("results"));

        Path output = Path.of(
                "results", "results.csv"
        );

        try (BufferedWriter writer =
                     Files.newBufferedWriter(output)) {

            writer.write(
                    "algorithm,graph,n,m,time_ms," +
                            "dsu_ops,edge_scans,comparisons," +
                            "max_depth,result\n"
            );

            int index = 0;

            for (int n : SIZES) {
                for (int density : new int[]{4, 20}) {
                    int m = n * density;

                    System.out.println(
                            "\nBENCHMARK n=" + n + " m=" + m
                    );

                    benchmarkMST(writer, n, m, index);
                    benchmarkSCC(writer, n, m, index);
                    benchmarkDAG(writer, n, m, index);

                    writer.flush();
                    index++;
                }
            }
        }

        System.out.println(
                "\nBENCHMARK COMPLETED: " +
                        output.toAbsolutePath()
        );
    }
}
