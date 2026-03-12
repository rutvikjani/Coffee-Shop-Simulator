package pos.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import util.OrderLoader;
import pos.model.ExistingOrder;
import java.util.List;

public class OrderLoaderTest {

    // Test 1: Orders load successfully
    @Test
    public void testOrdersLoad() {
        List<ExistingOrder> orders = OrderLoader.loadOrders();
        assertNotNull(orders, "Orders list should not be null");
    }

    // Test 2: All orders have non-empty order number
    @Test
    public void testAllOrdersHaveOrderNo() {
        List<ExistingOrder> orders = OrderLoader.loadOrders();
        for (ExistingOrder order : orders) {
            assertFalse(order.getOrderNo().isBlank(), "Order number should not be blank");
        }
    }

    // Test 3: All orders have non-empty customer ID
    @Test
    public void testAllOrdersHaveCustomerId() {
        List<ExistingOrder> orders = OrderLoader.loadOrders();
        for (ExistingOrder order : orders) {
            assertFalse(order.getCustomerId().isBlank(), "Customer ID should not be blank");
        }
    }

    // Test 4: All orders have non-empty item IDs
    @Test
    public void testAllOrdersHaveItemIds() {
        List<ExistingOrder> orders = OrderLoader.loadOrders();
        for (ExistingOrder order : orders) {
            assertFalse(order.getItemIds().isBlank(), "Item IDs should not be blank");
        }
    }

    // Test 5: All orders have non-empty timestamp
    @Test
    public void testAllOrdersHaveTimestamp() {
        List<ExistingOrder> orders = OrderLoader.loadOrders();
        for (ExistingOrder order : orders) {
            assertFalse(order.getTimestamp().isBlank(), "Timestamp should not be blank");
        }
    }
}