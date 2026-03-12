package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import pos.model.MenuItem;
import pos.exceptions.InvalidMenuItemException;

//Menu loader logic
public class MenuLoader {

    public static Map<String, MenuItem> loadMenu() {
        Map<String, MenuItem> menuItems = new TreeMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("cafe_menu_items.csv"));
            String line;
            boolean firstLine = true;
            
            //Reading the menu items and skipping headers and validations of the menu item
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] nextRecord = line.split(",");
                boolean hasEmptyField = false;

                for (int i = 0; i < nextRecord.length; i++) {
                    if (nextRecord[i] == null || nextRecord[i].trim().isEmpty()) {
                        hasEmptyField = true;
                        break;
                    }
                }
                
                //Menu Item validations
                if (nextRecord.length < 5) {
                    hasEmptyField = true;
                }

                if (!hasEmptyField && !nextRecord[0].trim().matches("^(FOOD|BEV|OTH)-[0-9]{3}$")) {
                    hasEmptyField = true;
                }

                if (!hasEmptyField && !Arrays.asList("FOOD", "BEV", "OTH").contains(nextRecord[4].trim())) {
                    hasEmptyField = true;
                }

                if (hasEmptyField) continue;

                String id          = nextRecord[0].trim();
                String name        = nextRecord[1].trim();
                String description = nextRecord[2].trim();
                double price       = Double.parseDouble(nextRecord[3].trim());
                String category    = nextRecord[4].trim();
                
              //Storing valid menu item in map with ID as key
                menuItems.put(id, new MenuItem(id, name, description, price, category));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return menuItems;
    }
}