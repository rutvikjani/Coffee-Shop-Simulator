package util;

import pos.model.Customer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {

    private int totalCustomersServed = 0;
    private int totalItemsProcessed = 0;
    private final Map<String, Integer> itemCount = new HashMap<>();

    public void recordOrder(Customer customer) {
        totalCustomersServed++;
        for (String itemId : customer.getItemIds()) {
            totalItemsProcessed++;
            itemCount.merge(itemId, 1, Integer::sum);
        }
    }

    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("============================\n");
        sb.append("   Simulation Report\n");
        sb.append("============================\n\n");
        sb.append("Total Customers Served: ").append(totalCustomersServed).append("\n");
        sb.append("Total Items Processed: ").append(totalItemsProcessed).append("\n\n");
        sb.append("Items Breakdown:\n");

        for (Map.Entry<String, Integer> entry : itemCount.entrySet()) {
            sb.append("  ").append(entry.getKey())
              .append(" → ").append(entry.getValue())
              .append(" times\n");
        }

        sb.append("\n============================\n");
        return sb.toString();
    }

    public int getTotalCustomersServed() {
        return totalCustomersServed;
    }
}