package aquanet.app;

import aquanet.approx.VertexCoverApprox;
import aquanet.core.CustomList;
import aquanet.core.CustomSort;
import aquanet.dp.EditDistanceDP;
import aquanet.flow.BottleneckAnalyzer;
import aquanet.flow.Edge;
import aquanet.flow.EdmondsKarp;
import aquanet.flow.FlowNetwork;
import aquanet.random.ReservoirSampler;
import aquanet.storage.PipelineCsvStore;
import aquanet.storage.PipelineRecord;
import aquanet.strings.KMPAlgorithm;

import java.util.Scanner;

public class AquaNetMain {
    private static PipelineCsvStore store;

    public static void main(String[] args) {
        store = new PipelineCsvStore("pipelines.csv");
        
        // Seed default data if CSV is empty
        if (store.getAll().isEmpty()) {
            store.add(new PipelineRecord("P101", "Reservoir_A", "Junction_1", 1200, 3));
            store.add(new PipelineRecord("P102", "Reservoir_A", "Junction_2", 800, 4));
            store.add(new PipelineRecord("P103", "Junction_1", "District_1", 900, 2));
            store.add(new PipelineRecord("P104", "Junction_2", "District_1", 600, 1));
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("==================================================");
        System.out.println("  AquaNet: Smart Water Resource Management OS");
        System.out.println("==================================================");

        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. View All Pipelines (Storage & QuickSort)");
            System.out.println("2. Add New Pipeline (Storage)");
            System.out.println("3. Edit Existing Pipeline (Storage)");
            System.out.println("4. Delete Pipeline (Storage)");
            System.out.println("5. Run Network Flow & Bottleneck Analysis (Flow / Min-Cut)");
            System.out.println("6. Find Station via Fuzzy Search (DP)");
            System.out.println("7. Compute Optimal Sensor Placement (Approximation)");
            System.out.println("8. Scan Telemetry Logs for Leaks (KMP)");
            System.out.println("9. Sample Live Telemetry Stream (Randomised)");
            System.out.println("10. Exit");
            System.out.print("Select a module to run (1-10): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": viewPipelines(scanner); break;
                case "2": addPipeline(scanner); break;
                case "3": editPipeline(scanner); break;
                case "4": deletePipeline(scanner); break;
                case "5": runFlowAnalysis(); break;
                case "6": runFuzzySearch(scanner); break;
                case "7": runSensorPlacement(); break;
                case "8": runLogScanner(); break;
                case "9": runTelemetrySampling(); break;
                case "10": 
                    running = false; 
                    System.out.println("Shutting down AquaNet Engine..."); 
                    break;
                default: System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    // --- ENHANCEMENT: CRASH PROOF NUMBER INPUT ---
    private static int readIntRobustly(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [Error] Invalid input. Please enter a whole number.");
            }
        }
    }

    // --- ENHANCEMENT: STRICT ID VALIDATION ---
    private static String readIdRobustly(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String id = scanner.nextLine().trim();
            
            if (id.length() >= 2 && (id.charAt(0) == 'P' || id.charAt(0) == 'p')) {
                boolean allDigits = true;
                for (int i = 1; i < id.length(); i++) {
                    if (!Character.isDigit(id.charAt(i))) {
                        allDigits = false;
                        break;
                    }
                }
                if (allDigits) {
                    return id.toUpperCase();
                }
            }
            System.out.println("  [Error] Invalid choice. ID must start with 'P' followed by numbers (e.g., P105).");
        }
    }

    // --- HELPER TO PRINT TABLE BEAUTIFULLY ---
    private static void printTable(CustomList<PipelineRecord> pipes, String sortTitle) {
        System.out.println("\n-- Current Network Grid (" + sortTitle + ") --");
        System.out.println("+------+----------------------+----------------------+-----------+------+");
        System.out.printf("| %-4s | %-20s | %-20s | %-9s | %-4s |\n", "ID", "Source Node", "Target Node", "Cap (L/s)", "Cost");
        System.out.println("+------+----------------------+----------------------+-----------+------+");
        
        for (int i = 0; i < pipes.size(); i++) {
            PipelineRecord p = pipes.get(i);
            System.out.printf("| %-4s | %-20s | %-20s | %-9d | %-4d |\n", p.id, p.source, p.target, p.capacity, p.cost);
        }
        System.out.println("+------+----------------------+----------------------+-----------+------+");
    }

    // --- MODULE METHODS ---
    private static void viewPipelines(Scanner scanner) {
        CustomList<PipelineRecord> pipes = store.getAll();
        if (pipes.isEmpty()) {
            System.out.println("No pipelines found in CSV.");
            return;
        }

        // 1. First, print the default table exactly as it is in memory/CSV
        printTable(pipes, "Default Order");

        // 2. Ask user if they want to sort it
        System.out.print("\nWould you like to sort this table? (y/n): ");
        String wantSort = scanner.nextLine().trim().toLowerCase();

        if (wantSort.equals("y") || wantSort.equals("yes")) {
            System.out.println("\n-- Sort Options --");
            System.out.println("1. By Capacity (Highest to Lowest)");
            System.out.println("2. By Cost (Lowest to Highest)");
            System.out.println("3. By ID (A to Z)");
            System.out.print("Choose sorting method (1-3): ");
            
            String sortChoice = scanner.nextLine().trim();
            String sortTitle = "";

            switch (sortChoice) {
                case "2":
                    CustomSort.sortPipelinesByCostAscending(pipes);
                    sortTitle = "Sorted by Cost Ascending";
                    break;
                case "3":
                    CustomSort.sortPipelinesByIdAscending(pipes);
                    sortTitle = "Sorted by ID Ascending";
                    break;
                case "1":
                default:
                    CustomSort.sortPipelinesByCapacityDescending(pipes);
                    sortTitle = "Sorted by Capacity Descending";
                    break;
            }

            // 3. Print the table again in the new sorted order
            printTable(pipes, sortTitle);
        }
    }

    private static void addPipeline(Scanner scanner) {
        String id = readIdRobustly(scanner, "Enter Pipeline ID (e.g., P105): ");
        System.out.print("Enter Source Node: ");
        String source = scanner.nextLine().trim();
        System.out.print("Enter Target Node: ");
        String target = scanner.nextLine().trim();
        
        int capacity = readIntRobustly(scanner, "Enter Capacity (L/s): ");
        int cost = readIntRobustly(scanner, "Enter Cost per Liter: ");

        store.add(new PipelineRecord(id, source, target, capacity, cost));
        System.out.println("Pipeline saved to CSV successfully!");
    }

    private static void editPipeline(Scanner scanner) {
        String id = readIdRobustly(scanner, "Enter the ID of the Pipeline to edit (e.g., P101): ");
        
        int newCapacity = readIntRobustly(scanner, "Enter NEW Capacity (L/s): ");
        int newCost = readIntRobustly(scanner, "Enter NEW Cost per Liter: ");

        boolean success = store.update(id, newCapacity, newCost);
        if (success) {
            System.out.println("Pipeline " + id + " updated successfully in pipelines.csv!");
        } else {
            System.out.println("Error: Pipeline ID not found.");
        }
    }

    private static void deletePipeline(Scanner scanner) {
        String id = readIdRobustly(scanner, "Enter the ID of the Pipeline to delete: ");

        boolean success = store.delete(id);
        if (success) {
            System.out.println("Pipeline " + id + " deleted successfully from pipelines.csv!");
        } else {
            System.out.println("Error: Pipeline ID not found.");
        }
    }

    private static void runFlowAnalysis() {
        CustomList<PipelineRecord> pipes = store.getAll();
        if (pipes.isEmpty()) {
            System.out.println("Cannot run flow analysis: No pipelines exist.");
            return;
        }
        FlowNetwork network = new FlowNetwork(100);
        for (int i = 0; i < pipes.size(); i++) {
            PipelineRecord p = pipes.get(i);
            network.addEdge(p.source, p.target, p.capacity);
        }

        String source = "Reservoir_A";
        String sink = "District_1";
        int maxFlow = EdmondsKarp.calculateMaxFlow(network, source, sink);
        
        System.out.println("\n-- Flow Analysis --");
        System.out.println("MAXIMUM FLOW from " + source + " to " + sink + ": " + maxFlow + " Liters/sec");

        CustomList<Edge> bottlenecks = BottleneckAnalyzer.findBottlenecks(network, source);
        if (!bottlenecks.isEmpty()) {
            for (int i = 0; i < bottlenecks.size(); i++) {
                Edge e = bottlenecks.get(i);
                System.out.println("[CRITICAL] Bottleneck at " + network.getNodeName(e.from) + " -> " + network.getNodeName(e.to));
            }
        } else {
            System.out.println("No critical bottlenecks restricting current flow.");
        }
    }

    private static void runFuzzySearch(Scanner scanner) {
        CustomList<String> validStations = new CustomList<>();
        CustomList<PipelineRecord> pipes = store.getAll();
        
        for(int i = 0; i < pipes.size(); i++) {
            String src = pipes.get(i).source;
            String tgt = pipes.get(i).target;
            
            boolean srcExists = false, tgtExists = false;
            for(int j = 0; j < validStations.size(); j++) {
                if(validStations.get(j).equals(src)) srcExists = true;
                if(validStations.get(j).equals(tgt)) tgtExists = true;
            }
            if(!srcExists) validStations.add(src);
            if(!tgtExists) validStations.add(tgt);
        }
        
        if (validStations.isEmpty()) {
            System.out.println("No stations available to search.");
            return;
        }
        
        System.out.print("\nEnter station name to search (e.g., Junctin_1): ");
        String query = scanner.nextLine().trim();
        String match = EditDistanceDP.findClosestMatch(query, validStations);
        System.out.println("Did you mean: " + match + " (Edit Cost: " + EditDistanceDP.calculate(query, match) + ")");
    }

    private static void runSensorPlacement() {
        CustomList<PipelineRecord> pipes = store.getAll();
        if (pipes.isEmpty()) {
            System.out.println("No pipelines to monitor.");
            return;
        }
        CustomList<String> sensors = VertexCoverApprox.findSensorPlacement(pipes);
        System.out.println("\n-- Optimal Sensor Placement --");
        System.out.println("Place sensors at the following " + sensors.size() + " locations to cover all pipes:");
        for (int i = 0; i < sensors.size(); i++) {
            System.out.println(" -> " + sensors.get(i));
        }
    }

    private static void runLogScanner() {
        String log = "INFO:SYS_OK|WARN:LOW_PRESS|CRIT:ERR_LEAK_DETECTED|INFO:VALVE_OPEN|CRIT:ERR_LEAK_DETECTED";
        String sig = "ERR_LEAK";
        System.out.println("\n-- Scanning Telemetry --");
        System.out.println("Log: " + log);
        CustomList<Integer> hits = KMPAlgorithm.search(log, sig);
        System.out.println("Found [" + sig + "] at indices:");
        for (int i = 0; i < hits.size(); i++) {
            System.out.println(" -> " + hits.get(i));
        }
    }

    private static void runTelemetrySampling() {
        System.out.println("\n-- Live Telemetry Sampling --");
        ReservoirSampler sampler = new ReservoirSampler(5, System.currentTimeMillis());
        System.out.println("Simulating 10,000 rapid incoming packets...");
        for (int i = 1; i <= 10000; i++) {
            sampler.processStreamElement("Packet_" + i + "_[pH:7." + (i % 9) + "]");
        }
        CustomList<String> sample = sampler.getSample();
        System.out.println("Retained Sample (Size 5):");
        for (int i = 0; i < sample.size(); i++) {
            System.out.println(" -> " + sample.get(i));
        }
    }
}