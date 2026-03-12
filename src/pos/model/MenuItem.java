package pos.model;
import javafx.beans.property.*;
import pos.exceptions.InvalidMenuItemException;

public class MenuItem {

	//Changes Menu automatically if any change occurs
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();
    public MenuItem() {}
    //Menu Item validation when the menu loads 
    public MenuItem(String id, String name, String description, double price, String category)
            throws InvalidMenuItemException {
        if (id == null || id.isBlank()) {
            throw new InvalidMenuItemException("Invalid ID: cannot be empty");
        }
        if (!id.matches("^(FOOD|BEV|OTH)-[0-9]{3}$")) {
            throw new InvalidMenuItemException("Invalid ID: must follow format FOOD-001, BEV-001, or OTH-001");
        }
        if (name == null || name.length() < 2) {
            throw new InvalidMenuItemException("Invalid name: must be at least 2 characters");
        }
        if (price < 0) {
            throw new InvalidMenuItemException("Invalid price: cannot be negative");
        }
        if (!category.matches("FOOD|BEV|OTH")) {
            throw new InvalidMenuItemException("Invalid category: must be FOOD, BEV, or OTH");
        }
        
        //Setting the data into each parameter
        this.id.set(id);
        this.name.set(name);
        this.description.set(description);
        this.price.set(price);
        this.category.set(category);
    }
    
    //Menu Getters
    public String getId() { return id.get(); }
    public StringProperty idProperty() { return id; }
    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }
    public String getCategory() { return category.get(); }
    public StringProperty categoryProperty() { return category; }
    public double getPrice() { return price.get(); }
    public DoubleProperty priceProperty() { return price; }
}