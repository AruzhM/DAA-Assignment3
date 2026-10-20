package algorithms;
import graph.Graph;
import graph.Graph.Edge;
import java.util.PriorityQueue;

public class Prim {

    public record Result(
            long totalWeight,
            int edgeCount,
            int components,
            long dsuOps,
            long edgeScans,
            long comparisons
    ) {}

    private record Candidate(int vertex, int weight) {}

    public static Result mst(Graph graph) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Prim requires an undirected graph"
            );
        }

        int n = graph.vertexCount();
        boolean[] visited = new boolean[n];

        final long[] comparisons = {0};

        PriorityQueue<Candidate> pq = new PriorityQueue<>(
                (a, b) -> {
                    comparisons[0]++;
                    return Integer.compare(
                            a.weight(),
                            b.weight()
                    );
                }
        );

        long totalWeight = 0;
        int edgeCount = 0;
        int components = 0;
        long edgeScans = 0;

        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }

            components++;
            pq.add(new Candidate(start, 0));

            while (!pq.isEmpty()) {
                Candidate current = pq.poll();
                int u = current.vertex();

                if (visited[u]) {
                    continue;
                }

                visited[u] = true;
                totalWeight += current.weight();

                if (u != start) {
                    edgeCount++;
                }

                for (Edge edge : graph.neighbors(u)) {
                    edgeScans++;

                    int v = edge.to();

                    if (!visited[v]) {
                        pq.add(new Candidate(
                                v,
                                edge.weight()
                        ));
                    }
                }
            }
        }

        return new Result(
                totalWeight,
                edgeCount,
                components,
                0,
                edgeScans,
                comparisons[0]
        );
    }
}
