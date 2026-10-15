package aquanet.storage;

public class PipelineRecord {
    public String id;
    public String source;
    public String target;
    public int capacity;
    public int cost;

    public PipelineRecord(String id, String source, String target, int capacity, int cost) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.capacity = capacity;
        this.cost = cost;
    }

    public String toCsvRow() {
        return id + "," + source + "," + target + "," + capacity + "," + cost;
    }
}