package pos.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import util.OrderLoader;
import pos.model.ExistingOrder;
import java.io.FileWriter;
import java.util.List;

public class OrderWriterTest {

    @Test
    public void testOrderIsWrittenToCSV() throws Exception {
        FileWriter writer = new FileWriter("existing_orders.csv", true);
        writer.write("99,99,12:00:00,\"BEV-001,FOOD-001\",9.00,0.45,8.55\n");
        writer.close();

        List<ExistingOrder> orders = OrderLoader.loadOrders();
        boolean found = orders.stream().anyMatch(o -> o.getOrderNo().equals("99"));
        assertTrue(found, "Written order should be in CSV");
    }

    @Test
    public void testWrittenOrderCustomerId() throws Exception {
        FileWriter writer = new FileWriter("existing_orders.csv", true);
        writer.write("98,98,11:00:00,\"BEV-002,FOOD-002\",5.90,0.30,5.60\n");
        writer.close();

        List<ExistingOrder> orders = OrderLoader.loadOrders();
        ExistingOrder found = orders.stream()
                .filter(o -> o.getOrderNo().equals("98"))
                .findFirst().orElse(null);
        assertNotNull(found);
        assertEquals("98", found.getCustomerId());
    }

    @Test
    public void testWrittenOrderTimestamp() throws Exception {
        FileWriter writer = new FileWriter("existing_orders.csv", true);
        writer.write("97,97,10:00:00,OTH-001,3.00,0.00,3.00\n");
        writer.close();

        List<ExistingOrder> orders = OrderLoader.loadOrders();
        ExistingOrder found = orders.stream()
                .filter(o -> o.getOrderNo().equals("97"))
                .findFirst().orElse(null);
        assertNotNull(found);
        assertEquals("10:00:00", found.getTimestamp());
    }

    @Test
    public void testWrittenOrderItemIds() throws Exception {
        FileWriter writer = new FileWriter("existing_orders.csv", true);
        writer.write("96,96,09:00:00,BEV-003\n");
        writer.close();
        List<ExistingOrder> orders = OrderLoader.loadOrders();
        ExistingOrder found = orders.stream()
                .filter(o -> o.getOrderNo().equals("96"))
                .findFirst().orElse(null);
        assertNotNull(found);
        assertEquals("BEV-003", found.getItemIds());
    }

    @Test
    public void testWrittenOrderNumber() throws Exception {
        FileWriter writer = new FileWriter("existing_orders.csv", true);
        writer.write("95,95,08:00:00,FOOD-004,4.00,0.00,4.00\n");
        writer.close();

        List<ExistingOrder> orders = OrderLoader.loadOrders();
        boolean found = orders.stream().anyMatch(o -> o.getOrderNo().equals("95"));
        assertTrue(found, "Order number 95 should exist in CSV");
    }
}