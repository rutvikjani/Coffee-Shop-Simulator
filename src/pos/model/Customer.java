package pos.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.List;

public class Customer {

    private final StringProperty customerId = new SimpleStringProperty();
    private final StringProperty status     = new SimpleStringProperty();
    private final List<String> itemIds;

    public Customer(String customerId, List<String> itemIds) {
        this.customerId.set(customerId);
        this.itemIds = itemIds;
        this.status.set("Waiting");
    }

    public String getCustomerId()              { return customerId.get(); }
    public StringProperty customerIdProperty() { return customerId; }

    public List<String> getItemIds()           { return itemIds; }

    public String getStatus()                  { return status.get(); }
    public StringProperty statusProperty()     { return status; }
    public void setStatus(String status)       { this.status.set(status); }

    @Override
    public String toString() {
        return "Customer{id=" + customerId.get() +
               ", items=" + itemIds +
               ", status=" + status.get() + "}";
    }
}