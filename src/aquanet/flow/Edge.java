package aquanet.flow;

public class Edge {
    public final int from;
    public final int to;
    public final int capacity;
    public int flow;
    public Edge residual; // Pointer to the reverse edge in the residual graph

    public Edge(int from, int to, int capacity) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = 0;
    }

    public int remainingCapacity() {
        return capacity - flow;
    }

    public void augment(int bottleneck) {
        flow += bottleneck;
        residual.flow -= bottleneck;
    }
}