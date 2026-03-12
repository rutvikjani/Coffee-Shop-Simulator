package pos.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import util.MenuLoader;
import pos.model.MenuItem;
import java.util.Collection;
import java.util.Map;

public class MenuLoaderTest {

    @Test
    public void testMenuLoadsItems() {
        Map<String, MenuItem> items = MenuLoader.loadMenu();
        assertFalse(items.isEmpty(), "Menu should not be empty");
    }

    @Test
    public void testAllItemsHaveValidIdFormat() {
        Collection<MenuItem> items = MenuLoader.loadMenu().values();
        for (MenuItem item : items) {
            assertTrue(item.getId().matches("^(FOOD|BEV|OTH)-[0-9]{3}$"),
                "Invalid ID format: " + item.getId());
        }
    }

    @Test
    public void testAllItemsHaveValidCategory() {
        Collection<MenuItem> items = MenuLoader.loadMenu().values();
        for (MenuItem item : items) {
            assertTrue(
                item.getCategory().equals("FOOD") ||
                item.getCategory().equals("BEV") ||
                item.getCategory().equals("OTH"),
                "Invalid category: " + item.getCategory()
            );
        }
    }

    @Test
    public void testAllItemsHavePositivePrice() {
        Collection<MenuItem> items = MenuLoader.loadMenu().values();
        for (MenuItem item : items) {
            assertTrue(item.getPrice() >= 0, "Price should not be negative: " + item.getId());
        }
    }

    @Test
    public void testAllItemsHaveNonEmptyName() {
        Collection<MenuItem> items = MenuLoader.loadMenu().values();
        for (MenuItem item : items) {
            assertNotNull(item.getName(), "Name should not be null");
            assertFalse(item.getName().isBlank(), "Name should not be blank");
        }
    }
    
    @Test
    public void testInvalidRowsAreSkipped() {
        Map<String, MenuItem> items = MenuLoader.loadMenu();
        assertFalse(items.containsKey("Bev-0032"), "Invalid ID Bev-0032 should be skipped");
        assertFalse(items.containsKey("0231-BEV"), "Invalid ID 0231-BEV should be skipped");
        assertFalse(items.containsKey("BEV-012"), "Invalid category 'drinks' should be skipped");
    }
}