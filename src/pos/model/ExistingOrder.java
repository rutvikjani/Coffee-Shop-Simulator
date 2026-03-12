package pos.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ExistingOrder {

	//Automatically detects when the UI changes it's state
    private final StringProperty orderNo = new SimpleStringProperty();
    private final StringProperty customerId = new SimpleStringProperty();
    private final StringProperty timestamp = new SimpleStringProperty();
    private final StringProperty itemIds = new SimpleStringProperty();

    //Takes the order data and stores into parameter
    public ExistingOrder(String orderNo, String customerId, String timestamp, String itemIds) {
        this.orderNo.set(orderNo);
        this.customerId.set(customerId);
        this.timestamp.set(timestamp);
        this.itemIds.set(itemIds);
    }

	//Automatically detects when the UI changes it's state
    public StringProperty orderNoProperty() { return orderNo; }
    public StringProperty customerIdProperty() { return customerId; }
    public StringProperty timestampProperty() { return timestamp; }
    public StringProperty itemIdsProperty() { return itemIds; }

    //Order getters
    public String getOrderNo()    { return orderNo.get(); }
    public String getCustomerId() { return customerId.get(); }
    public String getTimestamp()  { return timestamp.get(); }
    public String getItemIds()    { return itemIds.get(); }
}
