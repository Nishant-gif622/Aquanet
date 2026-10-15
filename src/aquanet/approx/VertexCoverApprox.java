package aquanet.approx;

import aquanet.core.CustomList;
import aquanet.storage.PipelineRecord;

public class VertexCoverApprox {

    /**
     * Approximates the minimum number of sensors needed to monitor all pipelines.
     * Uses the greedy edge-picking 2-approximation algorithm.
     */
    public static CustomList<String> findSensorPlacement(CustomList<PipelineRecord> pipelines) {
        CustomList<String> selectedSensors = new CustomList<>();
        boolean[] edgeCovered = new boolean[pipelines.size()];

        for (int i = 0; i < pipelines.size(); i++) {
            // If this pipeline isn't monitored yet
            if (!edgeCovered[i]) {
                PipelineRecord pipe = pipelines.get(i);
                
                // Pick both endpoints of the edge
                String u = pipe.source;
                String v = pipe.target;
                
                selectedSensors.add(u);
                selectedSensors.add(v);

                // Mark all other pipelines connected to u or v as covered
                for (int j = 0; j < pipelines.size(); j++) {
                    if (!edgeCovered[j]) {
                        PipelineRecord otherPipe = pipelines.get(j);
                        if (otherPipe.source.equals(u) || otherPipe.source.equals(v) ||
                            otherPipe.target.equals(u) || otherPipe.target.equals(v)) {
                            edgeCovered[j] = true;
                        }
                    }
                }
            }
        }
        return selectedSensors;
    }
}