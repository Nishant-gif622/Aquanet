package aquanet.core;

import aquanet.storage.PipelineRecord;

/**
 * Custom implementation of QuickSort to avoid java.util.Collections.
 */
public class CustomSort {

    // --- 1. Sort by Capacity (Descending / Highest first) ---
    public static void sortPipelinesByCapacityDescending(CustomList<PipelineRecord> list) {
        if (list == null || list.size() <= 1) return;
        quickSortCapacity(list, 0, list.size() - 1);
    }

    private static void quickSortCapacity(CustomList<PipelineRecord> list, int low, int high) {
        if (low < high) {
            int pi = partitionCapacity(list, low, high);
            quickSortCapacity(list, low, pi - 1);
            quickSortCapacity(list, pi + 1, high);
        }
    }

    private static int partitionCapacity(CustomList<PipelineRecord> list, int low, int high) {
        int pivot = list.get(high).capacity;
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (list.get(j).capacity >= pivot) { // >= for descending order
                i++;
                swap(list, i, j);
            }
        }
        swap(list, i + 1, high);
        return i + 1;
    }

    // --- 2. Sort by Cost (Ascending / Lowest first) ---
    public static void sortPipelinesByCostAscending(CustomList<PipelineRecord> list) {
        if (list == null || list.size() <= 1) return;
        quickSortCost(list, 0, list.size() - 1);
    }

    private static void quickSortCost(CustomList<PipelineRecord> list, int low, int high) {
        if (low < high) {
            int pi = partitionCost(list, low, high);
            quickSortCost(list, low, pi - 1);
            quickSortCost(list, pi + 1, high);
        }
    }

    private static int partitionCost(CustomList<PipelineRecord> list, int low, int high) {
        int pivot = list.get(high).cost;
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (list.get(j).cost <= pivot) { // <= for ascending order
                i++;
                swap(list, i, j);
            }
        }
        swap(list, i + 1, high);
        return i + 1;
    }

    // --- 3. Sort by ID (Ascending / A to Z) ---
    public static void sortPipelinesByIdAscending(CustomList<PipelineRecord> list) {
        if (list == null || list.size() <= 1) return;
        quickSortId(list, 0, list.size() - 1);
    }

    private static void quickSortId(CustomList<PipelineRecord> list, int low, int high) {
        if (low < high) {
            int pi = partitionId(list, low, high);
            quickSortId(list, low, pi - 1);
            quickSortId(list, pi + 1, high);
        }
    }

    private static int partitionId(CustomList<PipelineRecord> list, int low, int high) {
        String pivot = list.get(high).id;
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            // String.compareTo() is part of java.lang, which is allowed in core Java
            if (list.get(j).id.compareTo(pivot) <= 0) { 
                i++;
                swap(list, i, j);
            }
        }
        swap(list, i + 1, high);
        return i + 1;
    }

    // --- Helper Method ---
    private static void swap(CustomList<PipelineRecord> list, int i, int j) {
        PipelineRecord temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}