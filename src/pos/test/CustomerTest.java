package pos.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import pos.model.Customer;
import java.util.List;
import java.util.ArrayList;

public class CustomerTest {

    private Customer customer;

    @BeforeEach
    public void setUp() {
        customer = new Customer("1", List.of("BEV-001", "FOOD-001"));
    }

    @Test
    public void testCustomerIdIsSet() {
        assertEquals("1", customer.getCustomerId());
    }

    @Test
    public void testDefaultStatusIsWaiting() {
        assertEquals("Waiting", customer.getStatus());
    }

    @Test
    public void testSetStatus() {
        customer.setStatus("Being Served");
        assertEquals("Being Served", customer.getStatus());
    }

    @Test
    public void testSetStatusComplete() {
        customer.setStatus("Complete");
        assertEquals("Complete", customer.getStatus());
    }

    @Test
    public void testItemIdsNotNull() {
        assertNotNull(customer.getItemIds());
    }

    @Test
    public void testItemIdsSize() {
        assertEquals(2, customer.getItemIds().size());
    }

    @Test
    public void testItemIdsContainsBev() {
        assertTrue(customer.getItemIds().contains("BEV-001"));
    }

    @Test
    public void testItemIdsContainsFood() {
        assertTrue(customer.getItemIds().contains("FOOD-001"));
    }

    @Test
    public void testCustomerIdProperty() {
        assertNotNull(customer.customerIdProperty());
    }

    @Test
    public void testStatusProperty() {
        assertNotNull(customer.statusProperty());
    }

    @Test
    public void testToString() {
        assertNotNull(customer.toString());
    }

    @Test
    public void testEmptyItemList() {
        Customer emptyCustomer = new Customer("2", new ArrayList<>());
        assertEquals(0, emptyCustomer.getItemIds().size());
    }

    @Test
    public void testDifferentCustomerId() {
        Customer customer2 = new Customer("99", List.of("BEV-001"));
        assertEquals("99", customer2.getCustomerId());
    }

    @Test
    public void testStatusChangesMultipleTimes() {
        customer.setStatus("Being Served");
        assertEquals("Being Served", customer.getStatus());
        customer.setStatus("Complete");
        assertEquals("Complete", customer.getStatus());
        customer.setStatus("Waiting");
        assertEquals("Waiting", customer.getStatus());
    }
}