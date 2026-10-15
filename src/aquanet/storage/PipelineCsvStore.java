package aquanet.storage;

import aquanet.core.CustomList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PipelineCsvStore {
    private final String filePath;
    private final CustomList<PipelineRecord> records;

    public PipelineCsvStore(String filePath) {
        this.filePath = filePath;
        this.records = new CustomList<>();
        loadFromDisk();
    }

    public void loadFromDisk() {
        records.clear();
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] tokens = line.split(",");
                if (tokens.length >= 5) {
                    records.add(new PipelineRecord(
                        tokens[0].trim(),
                        tokens[1].trim(),
                        tokens[2].trim(),
                        Integer.parseInt(tokens[3].trim()),
                        Integer.parseInt(tokens[4].trim())
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }
    }

    public void saveToDisk() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("id,source_node,target_node,capacity_liters_sec,cost_per_liter");
            writer.newLine();
            for (int i = 0; i < records.size(); i++) {
                writer.write(records.get(i).toCsvRow());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }

    // CRUD - Create
    public void add(PipelineRecord rec) {
        records.add(rec);
        saveToDisk();
    }

    // CRUD - Read
    public CustomList<PipelineRecord> getAll() {
        return records;
    }

    // CRUD - Update
    public boolean update(String id, int newCapacity, int newCost) {
        for (int i = 0; i < records.size(); i++) {
            PipelineRecord r = records.get(i);
            if (r.id.equals(id)) {
                r.capacity = newCapacity;
                r.cost = newCost;
                saveToDisk();
                return true;
            }
        }
        return false;
    }

    // CRUD - Delete
    public boolean delete(String id) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).id.equals(id)) {
                records.removeAt(i);
                saveToDisk();
                return true;
            }
        }
        return false;
    }
}