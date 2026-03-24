package pos.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import pos.model.Staff;

public class StaffTest {

    private Staff staff;

    @BeforeEach
    public void setUp() {
        staff = new Staff(1, "Staff 1");
    }

    @Test
    public void testStaffIdIsSet() {
        assertEquals(1, staff.getStaffId());
    }

    @Test
    public void testStaffNameIsSet() {
        assertEquals("Staff 1", staff.getName());
    }

    @Test
    public void testDefaultStatusIsIdle() {
        assertEquals("Idle", staff.getStatus());
    }

    @Test
    public void testDefaultCurrentOrderIsNoOrder() {
        assertEquals("No order", staff.getCurrentOrder());
    }

    @Test
    public void testSetStatus() {
        staff.setStatus("Serving");
        assertEquals("Serving", staff.getStatus());
    }

    @Test
    public void testSetStatusBackToIdle() {
        staff.setStatus("Serving");
        staff.setStatus("Idle");
        assertEquals("Idle", staff.getStatus());
    }

    @Test
    public void testSetCurrentOrder() {
        staff.setCurrentOrder("Customer 1");
        assertEquals("Customer 1", staff.getCurrentOrder());
    }

    @Test
    public void testSetCurrentOrderBackToNoOrder() {
        staff.setCurrentOrder("Customer 1");
        staff.setCurrentOrder("No order");
        assertEquals("No order", staff.getCurrentOrder());
    }

    @Test
    public void testNameProperty() {
        assertNotNull(staff.nameProperty());
    }

    @Test
    public void testStatusProperty() {
        assertNotNull(staff.statusProperty());
    }

    @Test
    public void testCurrentOrderProperty() {
        assertNotNull(staff.currentOrderProperty());
    }

    @Test
    public void testToString() {
        assertNotNull(staff.toString());
    }

    @Test
    public void testDifferentStaffId() {
        Staff staff2 = new Staff(2, "Staff 2");
        assertEquals(2, staff2.getStaffId());
    }

    @Test
    public void testDifferentStaffName() {
        Staff staff3 = new Staff(3, "Staff 3");
        assertEquals("Staff 3", staff3.getName());
    }

    @Test
    public void testStatusChangesMultipleTimes() {
        staff.setStatus("Serving");
        assertEquals("Serving", staff.getStatus());
        staff.setStatus("Idle");
        assertEquals("Idle", staff.getStatus());
        staff.setStatus("Serving");
        assertEquals("Serving", staff.getStatus());
    }
}