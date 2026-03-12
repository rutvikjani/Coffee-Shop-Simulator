package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import pos.model.ExistingOrder;

//Existing order csv reader
public class OrderLoader {
	
	//Called from CoffeeApp with real file at startup
    public static List<ExistingOrder> loadOrders() {
        return loadOrders("existing_orders.csv");
    }
    
   //Called from JUnit tests with test file path
    public static List<ExistingOrder> loadOrders(String filePath) {
        List<ExistingOrder> orders = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            boolean firstLine = true;
            
            //Reading the existing order items and skipping headers and validations of the order items
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");

                if (parts.length < 4) continue;

                String orderNo    = parts[0].trim();
                String customerId = parts[1].trim();
                String timestamp  = parts[2].trim();
                String itemIds    = parts[3].trim().replace("\"", "");

                orders.add(new ExistingOrder(orderNo, customerId, timestamp, itemIds));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }
}