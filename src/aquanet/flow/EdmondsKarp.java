package aquanet.flow;

import aquanet.core.CustomQueue;
import aquanet.core.CustomList;

public class EdmondsKarp {
    
    public static int calculateMaxFlow(FlowNetwork network, String sourceName, String sinkName) {
        int s = network.getOrAddNode(sourceName);
        int t = network.getOrAddNode(sinkName);
        int maxFlow = 0;
        int n = network.getVertexCount();

        while (true) {
            Edge[] edgeTo = new Edge[n]; // Tracks the path
            CustomQueue<Integer> queue = new CustomQueue<>();
            queue.enqueue(s);

            // BFS to find an augmenting path
            while (!queue.isEmpty() && edgeTo[t] == null) {
                int v = queue.dequeue();
                CustomList<Edge> edges = network.getEdges(v);
                
                for (int i = 0; i < edges.size(); i++) {
                    Edge e = edges.get(i);
                    int w = e.to;
                    
                    // If not visited, and capacity remains, and we aren't looping back to source
                    if (edgeTo[w] == null && w != s && e.remainingCapacity() > 0) {
                        edgeTo[w] = e;
                        queue.enqueue(w);
                    }
                }
            }

            // If we couldn't reach the sink, we are done
            if (edgeTo[t] == null) {
                break; 
            }

            // Find the bottleneck capacity along the path
            int bottleneck = Integer.MAX_VALUE;
            for (int v = t; v != s; v = edgeTo[v].from) {
                bottleneck = Math.min(bottleneck, edgeTo[v].remainingCapacity());
            }

            // Augment flow along the path
            for (int v = t; v != s; v = edgeTo[v].from) {
                edgeTo[v].augment(bottleneck);
            }

            maxFlow += bottleneck;
        }

        return maxFlow;
    }
}