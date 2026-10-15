package aquanet.flow;

import aquanet.core.CustomList;

public class FlowNetwork {
    private final CustomList<String> nodeNames;
    private CustomList<Edge>[] adj;
    private int vertexCount;

    @SuppressWarnings("unchecked")
    public FlowNetwork(int maxVertices) {
        this.nodeNames = new CustomList<>();
        this.vertexCount = 0;
        this.adj = new CustomList[maxVertices];
        for (int i = 0; i < maxVertices; i++) {
            adj[i] = new CustomList<>();
        }
    }

    public int getOrAddNode(String name) {
        // Search for existing node
        for (int i = 0; i < nodeNames.size(); i++) {
            if (nodeNames.get(i).equals(name)) {
                return i;
            }
        }
        // Add new node
        nodeNames.add(name);
        return vertexCount++;
    }

    public String getNodeName(int id) {
        return nodeNames.get(id);
    }

    public void addEdge(String fromName, String toName, int capacity) {
        int u = getOrAddNode(fromName);
        int v = getOrAddNode(toName);

        Edge forward = new Edge(u, v, capacity);
        Edge backward = new Edge(v, u, 0); // Residual edge starts with 0 capacity

        forward.residual = backward;
        backward.residual = forward;

        adj[u].add(forward);
        adj[v].add(backward);
    }

    public CustomList<Edge> getEdges(int v) {
        return adj[v];
    }
    
    public int getVertexCount() {
        return vertexCount;
    }
}