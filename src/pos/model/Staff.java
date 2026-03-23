package pos.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Staff {

    private final int staffId;
    private final StringProperty name     = new SimpleStringProperty();
    private final StringProperty status   = new SimpleStringProperty();
    private final StringProperty currentOrder = new SimpleStringProperty();

    public Staff(int staffId, String name) {
        this.staffId = staffId;
        this.name.set(name);
        this.status.set("Idle");
        this.currentOrder.set("No order");
    }

    public int getStaffId()                       { return staffId; }

    public String getName()                       { return name.get(); }
    public StringProperty nameProperty()          { return name; }

    public String getStatus()                     { return status.get(); }
    public StringProperty statusProperty()        { return status; }
    public void setStatus(String status)          { this.status.set(status); }

    public String getCurrentOrder()               { return currentOrder.get(); }
    public StringProperty currentOrderProperty()  { return currentOrder; }
    public void setCurrentOrder(String order)     { this.currentOrder.set(order); }

    @Override
    public String toString() {
        return "Staff{id=" + staffId +
               ", name=" + name.get() +
               ", status=" + status.get() + "}";
    }
}