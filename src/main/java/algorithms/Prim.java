package algorithms;
import graph.Graph;
import graph.Graph.Edge;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Prim {

    public record Result(
            long totalWeight,
            int edgeCount,
            int components
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

        PriorityQueue<Candidate> pq = new PriorityQueue<>(
                Comparator.comparingInt(Candidate::weight)
        );

        long totalWeight = 0;
        int edgeCount = 0;
        int components = 0;

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
                    int v = edge.to();

                    if (!visited[v]) {
                        pq.add(new Candidate(v, edge.weight()));
                    }
                }
            }
        }

        return new Result(
                totalWeight,
                edgeCount,
                components
        );
    }
}
