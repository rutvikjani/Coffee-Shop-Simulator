package pos.model;

import javafx.beans.property.*;

public class OrderLine {
    private final MenuItem item;
    private final IntegerProperty quantity = new SimpleIntegerProperty();
    private final DoubleProperty lineTotal = new SimpleDoubleProperty();

    //Auto calculation of orders according to it's quantity
    public OrderLine(MenuItem item, int qty) {
        this.item = item;
        this.quantity.set(qty);
        recalc();
        quantity.addListener((obs, oldV, newV) -> recalc());
    }

    //Auto calculation logic
    private void recalc() {
        lineTotal.set(item.getPrice() * quantity.get());
    }

    //Getter methods
    public MenuItem getItem() { return item; }
    public StringProperty itemNameProperty() { return item.nameProperty(); }
    public IntegerProperty quantityProperty() { return quantity; }
    public DoubleProperty lineTotalProperty() { return lineTotal; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int q) { quantity.set(q); }

    public double getLineTotal() { return lineTotal.get(); }
}
