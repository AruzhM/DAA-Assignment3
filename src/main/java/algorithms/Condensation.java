package algorithms;
import graph.Graph;
import graph.Graph.Edge;

public class Condensation {

    public record Result(
            Graph dag,
            int[] componentOf,
            int componentCount
    ) {}

    public static Result build(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Condensation requires a directed graph"
            );
        }

        Kosaraju.Result scc = Kosaraju.findSCC(graph);

        int count = scc.componentCount();
        int[] componentOf = scc.componentOf();

        Graph dag = new Graph(count, true);

        for (Edge edge : graph.edges()) {
            int from = componentOf[edge.from()];
            int to = componentOf[edge.to()];

            if (from != to) {
                dag.addEdge(from, to, edge.weight());
            }
        }

        return new Result(dag, componentOf, count);
    }
}

