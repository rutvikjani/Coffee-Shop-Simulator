package pos.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pos.model.Customer;
import util.LoggerService;
import util.QueueService;
import util.ReportService;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SimulationTest {

    // ── QueueService Tests ────────────────────────────────────────────────────

    @BeforeEach
    void resetQueue() {
        QueueService.getInstance().clear();
    }

    @Test
    void testQueueSingletonSameInstance() {
        QueueService q1 = QueueService.getInstance();
        QueueService q2 = QueueService.getInstance();
        assertSame(q1, q2, "QueueService should return the same Singleton instance");
    }

    @Test
    void testQueueIsEmptyInitially() {
        assertTrue(QueueService.getInstance().isEmpty(), "Queue should be empty initially");
    }

    @Test
    void testQueueAddAndSize() {
        Customer c = new Customer("1", Arrays.asList("BEV-001"));
        QueueService.getInstance().addCustomer(c);
        assertEquals(1, QueueService.getInstance().getSize(), "Queue size should be 1 after adding one customer");
    }

    @Test
    void testQueueFIFOOrder() {
        QueueService q = QueueService.getInstance();
        Customer c1 = new Customer("1", Arrays.asList("BEV-001"));
        Customer c2 = new Customer("2", Arrays.asList("FOOD-001"));
        q.addCustomer(c1);
        q.addCustomer(c2);
        assertEquals("1", q.getAllCustomers().get(0).getCustomerId(), "First customer should be served first");
    }

    @Test
    void testQueueClear() {
        QueueService q = QueueService.getInstance();
        q.addCustomer(new Customer("1", Arrays.asList("BEV-001")));
        q.addCustomer(new Customer("2", Arrays.asList("FOOD-001")));
        q.clear();
        assertTrue(q.isEmpty(), "Queue should be empty after clear");
    }

    @Test
    void testLoggerSingletonSameInstance() {
        LoggerService l1 = LoggerService.getInstance();
        LoggerService l2 = LoggerService.getInstance();
        assertSame(l1, l2, "LoggerService should return the same Singleton instance");
    }

    @Test
    void testLoggerRecordsEntry() {
        LoggerService logger = LoggerService.getInstance();
        int sizeBefore = logger.getLogs().size();
        logger.log("Test message");
        assertEquals(sizeBefore + 1, logger.getLogs().size(), "Log should have one more entry after logging");
    }

    @Test
    void testLoggerEntryContainsMessage() {
        LoggerService logger = LoggerService.getInstance();
        logger.log("Hello simulation");
        boolean found = logger.getLogs().stream()
                .anyMatch(entry -> entry.contains("Hello simulation"));
        assertTrue(found, "Log entry should contain the logged message");
    }

    @Test
    void testLoggerEntryHasTimestamp() {
        LoggerService logger = LoggerService.getInstance();
        logger.log("Timestamp test");
        String last = logger.getLogs().get(logger.getLogs().size() - 1);
        assertTrue(last.startsWith("["), "Log entry should start with a timestamp in brackets");
    }

    // ── ReportService Tests ───────────────────────────────────────────────────

    @Test
    void testReportInitiallyZeroCustomers() {
        ReportService report = new ReportService();
        assertEquals(0, report.getTotalCustomersServed(), "Should start with 0 customers served");
    }

    @Test
    void testReportRecordsCustomer() {
        ReportService report = new ReportService();
        Customer c = new Customer("1", Arrays.asList("BEV-001", "FOOD-001"));
        report.recordOrder(c);
        assertEquals(1, report.getTotalCustomersServed(), "Should record 1 customer served");
    }

    @Test
    void testReportGeneratesNonEmptyString() {
        ReportService report = new ReportService();
        Customer c = new Customer("1", Arrays.asList("BEV-001"));
        report.recordOrder(c);
        String result = report.generateReport();
        assertNotNull(result, "Report should not be null");
        assertFalse(result.isEmpty(), "Report should not be empty");
    }

    @Test
    void testReportContainsTotalCustomers() {
        ReportService report = new ReportService();
        report.recordOrder(new Customer("1", Arrays.asList("BEV-001")));
        report.recordOrder(new Customer("2", Arrays.asList("FOOD-001")));
        String result = report.generateReport();
        assertTrue(result.contains("2"), "Report should mention 2 customers served");
    }

    @Test
    void testCustomerInitialStatus() {
        Customer c = new Customer("1", Arrays.asList("BEV-001"));
        assertEquals("Waiting", c.getStatus(), "Customer status should be Waiting initially");
    }

    @Test
    void testCustomerStatusUpdate() {
        Customer c = new Customer("1", Arrays.asList("BEV-001"));
        c.setStatus("Being Served");
        assertEquals("Being Served", c.getStatus(), "Customer status should update correctly");
    }

    @Test
    void testCustomerItemIds() {
        List<String> items = Arrays.asList("BEV-001", "FOOD-002");
        Customer c = new Customer("1", items);
        assertEquals(2, c.getItemIds().size(), "Customer should have 2 items");
    }
}