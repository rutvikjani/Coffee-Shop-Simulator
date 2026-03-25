package pos.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import pos.model.Customer;
import pos.model.ExistingOrder;
import util.QueueService;
import java.util.List;

public class QueueServiceTest {

    private QueueService queueService;

    @BeforeEach
    public void setUp() {
        queueService = QueueService.getInstance();
        queueService.clear();
    }

    @Test
    public void testSingletonInstance() {
        QueueService instance1 = QueueService.getInstance();
        QueueService instance2 = QueueService.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    public void testQueueIsEmptyOnStart() {
        assertTrue(queueService.isEmpty());
    }

    @Test
    public void testAddCustomer() {
        Customer customer = new Customer("1", List.of("BEV-001"));
        queueService.addCustomer(customer);
        assertFalse(queueService.isEmpty());
    }

    @Test
    public void testQueueSizeAfterAddingOne() {
        Customer customer = new Customer("1", List.of("BEV-001"));
        queueService.addCustomer(customer);
        assertEquals(1, queueService.getSize());
    }

    @Test
    public void testQueueSizeAfterAddingMultiple() {
        queueService.addCustomer(new Customer("1", List.of("BEV-001")));
        queueService.addCustomer(new Customer("2", List.of("FOOD-001")));
        queueService.addCustomer(new Customer("3", List.of("OTH-001")));
        assertEquals(3, queueService.getSize());
    }

    @Test
    public void testGetNextCustomer() throws InterruptedException {
        Customer customer = new Customer("1", List.of("BEV-001"));
        queueService.addCustomer(customer);
        Customer next = queueService.getNextCustomer();
        assertEquals("1", next.getCustomerId());
    }

    @Test
    public void testQueueIsEmptyAfterTaking() throws InterruptedException {
        Customer customer = new Customer("1", List.of("BEV-001"));
        queueService.addCustomer(customer);
        queueService.getNextCustomer();
        assertTrue(queueService.isEmpty());
    }

    @Test
    public void testFIFOOrder() throws InterruptedException {
        queueService.addCustomer(new Customer("1", List.of("BEV-001")));
        queueService.addCustomer(new Customer("2", List.of("FOOD-001")));
        Customer first = queueService.getNextCustomer();
        assertEquals("1", first.getCustomerId());
    }

    @Test
    public void testGetAllCustomers() {
        queueService.addCustomer(new Customer("1", List.of("BEV-001")));
        queueService.addCustomer(new Customer("2", List.of("FOOD-001")));
        List<Customer> all = queueService.getAllCustomers();
        assertEquals(2, all.size());
    }

    @Test
    public void testGetAllCustomersNotNull() {
        List<Customer> all = queueService.getAllCustomers();
        assertNotNull(all);
    }

    @Test
    public void testLoadFromOrders() {
        List<ExistingOrder> orders = List.of(
            new ExistingOrder("1", "1", "10:00:00", "BEV-001;FOOD-001"),
            new ExistingOrder("2", "2", "10:01:00", "BEV-002")
        );
        queueService.loadFromOrders(orders);
        assertEquals(2, queueService.getSize());
    }

    @Test
    public void testLoadFromOrdersNotEmpty() {
        List<ExistingOrder> orders = List.of(
            new ExistingOrder("1", "1", "10:00:00", "BEV-001")
        );
        queueService.loadFromOrders(orders);
        assertFalse(queueService.isEmpty());
    }

    @Test
    public void testClearQueue() {
        queueService.addCustomer(new Customer("1", List.of("BEV-001")));
        queueService.addCustomer(new Customer("2", List.of("FOOD-001")));
        queueService.clear();
        assertTrue(queueService.isEmpty());
    }

    @Test
    public void testQueueSizeAfterClear() {
        queueService.addCustomer(new Customer("1", List.of("BEV-001")));
        queueService.clear();
        assertEquals(0, queueService.getSize());
    }
}