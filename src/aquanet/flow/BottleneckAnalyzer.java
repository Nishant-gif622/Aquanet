package aquanet.flow;

import aquanet.core.CustomQueue;
import aquanet.core.CustomList;

public class BottleneckAnalyzer {

    /**
     * Finds bottleneck pipelines using the Min-Cut theorem.
     * Must be called AFTER EdmondsKarp.calculateMaxFlow has processed the network.
     */
    public static CustomList<Edge> findBottlenecks(FlowNetwork network, String sourceName) {
        int s = network.getOrAddNode(sourceName);
        int n = network.getVertexCount();
        boolean[] reachable = new boolean[n];

        // 1. BFS to find all nodes still reachable from the source in the residual graph
        CustomQueue<Integer> queue = new CustomQueue<>();
        queue.enqueue(s);
        reachable[s] = true;

        while (!queue.isEmpty()) {
            int v = queue.dequeue();
            CustomList<Edge> edges = network.getEdges(v);
            
            for (int i = 0; i < edges.size(); i++) {
                Edge e = edges.get(i);
                // We can only traverse if there is remaining capacity
                if (!reachable[e.to] && e.remainingCapacity() > 0) {
                    reachable[e.to] = true;
                    queue.enqueue(e.to);
                }
            }
        }

        // 2. Identify the Min-Cut edges
        // An edge is a bottleneck if it goes from a REACHABLE node to an UNREACHABLE node.
        CustomList<Edge> bottlenecks = new CustomList<>();
        
        for (int v = 0; v < n; v++) {
            if (reachable[v]) {
                CustomList<Edge> edges = network.getEdges(v);
                for (int i = 0; i < edges.size(); i++) {
                    Edge e = edges.get(i);
                    // Ignore residual back-edges (capacity == 0 initially)
                    if (e.capacity > 0 && !reachable[e.to]) {
                        bottlenecks.add(e);
                    }
                }
            }
        }

        return bottlenecks;
    }
}