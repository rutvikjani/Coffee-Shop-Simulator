package pos.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pos.model.Customer;
import pos.model.MenuItem;
import pos.model.OrderLine;
import pos.model.ExistingOrder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class MainController implements util.SimulationObserver {

    @FXML private Button btnAll;
    @FXML private Button btnBeverages;
    @FXML private Button btnFood;
    @FXML private Button btnOther;
    @FXML private Slider speedSlider;
    @FXML private Label speedLabel;

    @FXML private TableView<MenuItem> menuTable;
    @FXML private TableColumn<MenuItem, String> colMenuId;
    @FXML private TableColumn<MenuItem, String> colMenuName;
    @FXML private TableColumn<MenuItem, String> colMenuCategory;
    @FXML private TableColumn<MenuItem, Double> colMenuPrice;

    @FXML private TableView<OrderLine> orderTable;
    @FXML private TableColumn<OrderLine, String> colOrderItem;
    @FXML private TableColumn<OrderLine, Integer> colOrderQty;
    @FXML private TableColumn<OrderLine, Double> colOrderLineTotal;

    @FXML private TableView<ExistingOrder> existingOrdersTable;
    @FXML private TableColumn<ExistingOrder, String> colExistingOrderNo;
    @FXML private TableColumn<ExistingOrder, String> colExistingCustomerId;
    @FXML private TableColumn<ExistingOrder, String> colExistingTimestamp;
    @FXML private TableColumn<ExistingOrder, String> colExistingItemIds;

    @FXML private TableView<Customer> queueTable;
    @FXML private TableColumn<Customer, String> colQueueCustomerId;
    @FXML private TableColumn<Customer, String> colQueueItems;
    @FXML private TableColumn<Customer, String> colQueueStatus;

    @FXML private Label staff1Status;
    @FXML private Label staff1Order;
    @FXML private Label staff2Status;
    @FXML private Label staff2Order;
    @FXML private Label staff3Status;
    @FXML private Label staff3Order;

    @FXML private TextArea logArea;
    @FXML private Button btnStartSimulation;
    @FXML private Button btnRemoveItem;

    @FXML private TextField customerIdField;
    @FXML private Label subtotalLabel;
    @FXML private Label discountLabel;
    @FXML private Label totalLabel;
    @FXML private Label discountBadge;
    @FXML private Button btnClearOrder;
    @FXML private Button btnConfirmOrder;
    @FXML private Label statusLabel;

    private int nextCustomerId = 1;
    private int nextOrderNo = 1;

    private ObservableList<MenuItem> allMenuItems = FXCollections.observableArrayList();
    private ObservableList<OrderLine> currentOrderLines = FXCollections.observableArrayList();
    private ObservableList<ExistingOrder> existingOrders = FXCollections.observableArrayList();
    private ObservableList<Customer> queueItems = FXCollections.observableArrayList();

    private String discountBreakdown = "";
    private double lastDiscount = 0;

    private MenuItem freeBeverageSelected = null;
    private boolean freeBeverageClaimedThisOrder = false;

    private final Map<String, Integer> itemOrderCount = new TreeMap<>();
    private final Map<String, Double> itemRevenue = new TreeMap<>();
    private double totalRevenue = 0;
    private double totalDiscountGiven = 0;
    private int totalOrders = 0;
    
    public void enableStartButton() {
        btnStartSimulation.setDisable(false);
        statusLabel.setText("Simulation complete. Ready to run again.");
    }

    @FXML
    public void initialize() {
        allMenuItems.setAll(pos.CoffeeApp.getMenuItems().values());
        statusLabel.setText("Menu loaded from file.");

        menuTable.setItems(allMenuItems);
        orderTable.setItems(currentOrderLines);
        existingOrdersTable.setItems(existingOrders);
        queueTable.setItems(queueItems);
        existingOrders.setAll(pos.CoffeeApp.getExistingOrders());

        colMenuId.setCellValueFactory(data -> data.getValue().idProperty());
        colMenuName.setCellValueFactory(data -> data.getValue().nameProperty());
        colMenuCategory.setCellValueFactory(data -> data.getValue().categoryProperty());
        colMenuPrice.setCellValueFactory(data -> data.getValue().priceProperty().asObject());

        colOrderItem.setCellValueFactory(data -> data.getValue().itemNameProperty());
        colOrderQty.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        colOrderLineTotal.setCellValueFactory(data -> data.getValue().lineTotalProperty().asObject());

        colExistingOrderNo.setCellValueFactory(data -> data.getValue().orderNoProperty());
        colExistingCustomerId.setCellValueFactory(data -> data.getValue().customerIdProperty());
        colExistingTimestamp.setCellValueFactory(data -> data.getValue().timestampProperty());
        colExistingItemIds.setCellValueFactory(data -> data.getValue().itemIdsProperty());

        colQueueCustomerId.setCellValueFactory(data -> data.getValue().customerIdProperty());
        colQueueItems.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.join(", ", data.getValue().getItemIds())));
        colQueueStatus.setCellValueFactory(data -> data.getValue().statusProperty());

        btnAll.setOnAction(e -> menuTable.setItems(allMenuItems));
        btnBeverages.setOnAction(e -> menuTable.setItems(allMenuItems.filtered(i -> i.getCategory().equalsIgnoreCase("BEV"))));
        btnFood.setOnAction(e -> menuTable.setItems(allMenuItems.filtered(i -> i.getCategory().equalsIgnoreCase("FOOD"))));
        btnOther.setOnAction(e -> menuTable.setItems(allMenuItems.filtered(i -> i.getCategory().equalsIgnoreCase("OTH"))));

        btnRemoveItem.setOnAction(e -> removeSelectedLine());
        btnClearOrder.setOnAction(e -> clearOrder());
        btnConfirmOrder.setOnAction(e -> confirmOrder());
        btnStartSimulation.setOnAction(e -> startSimulation());
        
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int speed = newVal.intValue();
            String[] labels = {"", "Very Slow", "Slow", "Normal", "Fast", "Very Fast"};
            speedLabel.setText("Speed: " + labels[speed]);
        });

        menuTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showQuantitySelector(selected);
                }
            }
        });

        customerIdField.setText(String.valueOf(nextCustomerId));
        discountBadge.setVisible(false);
    }

    private void startSimulation() {
        logArea.appendText("Simulation started...\n");
        statusLabel.setText("Simulation running...");
        btnStartSimulation.setDisable(true);

        SimulationController simulationController = new SimulationController(this);
        simulationController.startSimulation(new ArrayList<>(existingOrders));
    }

    public void appendLog(String message) {
        Platform.runLater(() -> logArea.appendText(message + "\n"));
    }

    public void updateStaffUI(int staffId, String status, String order) {
        Platform.runLater(() -> {
            if (staffId == 1) {
                staff1Status.setText(status);
                staff1Order.setText(order);
                staff1Status.setStyle(status.equals("Idle") ?
                        "-fx-text-fill: green;" : "-fx-text-fill: red;");
            } else if (staffId == 2) {
                staff2Status.setText(status);
                staff2Order.setText(order);
                staff2Status.setStyle(status.equals("Idle") ?
                        "-fx-text-fill: green;" : "-fx-text-fill: red;");
            } else if (staffId == 3) {
                staff3Status.setText(status);
                staff3Order.setText(order);
                staff3Status.setStyle(status.equals("Idle") ?
                        "-fx-text-fill: green;" : "-fx-text-fill: red;");
            }
        });
    }

    public ObservableList<Customer> getQueueItems() {
        return queueItems;
    }

    private void showQuantitySelector(MenuItem item) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Select Quantity");

        Label itemLabel = new Label("Add: " + item.getName());
        itemLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label qtyLabel = new Label("1");
        qtyLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button btnMinus = new Button("-");
        btnMinus.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        btnMinus.setOnAction(e -> {
            int q = Integer.parseInt(qtyLabel.getText());
            if (q > 1) qtyLabel.setText(String.valueOf(q - 1));
        });

        Button btnPlus = new Button("+");
        btnPlus.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        btnPlus.setOnAction(e -> {
            int q = Integer.parseInt(qtyLabel.getText());
            qtyLabel.setText(String.valueOf(q + 1));
        });

        HBox qtyBox = new HBox(10, btnMinus, qtyLabel, btnPlus);
        qtyBox.setStyle("-fx-alignment: center;");

        Button btnAdd = new Button("Add to Order");
        btnAdd.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        btnAdd.setOnAction(e -> {
            int qty = Integer.parseInt(qtyLabel.getText());
            addItemToOrder(item, qty);
            popup.close();
        });

        VBox layout = new VBox(15, itemLabel, qtyBox, btnAdd);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        popup.setScene(new Scene(layout, 250, 200));
        popup.showAndWait();
    }

    private void addItemToOrder(MenuItem item, int qty) {
        OrderLine existing = currentOrderLines.stream()
                .filter(l -> l.getItem().equals(item))
                .findFirst()
                .orElse(null);

        if (existing == null) {
            currentOrderLines.add(new OrderLine(item, qty));
        } else {
            existing.setQuantity(existing.getQuantity() + qty);
        }
        updateBill();
    }

    private void removeSelectedLine() {
        OrderLine line = orderTable.getSelectionModel().getSelectedItem();
        if (line != null) currentOrderLines.remove(line);
        updateBill();
    }

    private void clearOrder() {
        currentOrderLines.clear();
        freeBeverageSelected = null;
        freeBeverageClaimedThisOrder = false;
        updateBill();
        statusLabel.setText("Order cleared.");
    }

    private void confirmOrder() {
        if (currentOrderLines.isEmpty()) {
            statusLabel.setText("Cannot confirm: order is empty.");
            return;
        }

        double subtotal = currentOrderLines.stream()
                .mapToDouble(OrderLine::getLineTotal)
                .sum();

        double total = subtotal - lastDiscount;

        String customerId = customerIdField.getText();
        String timestamp = java.time.LocalTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));

        for (OrderLine line : currentOrderLines) {
            for (int i = 0; i < line.getQuantity(); i++) {
                ExistingOrder eo = new ExistingOrder(
                        String.valueOf(nextOrderNo),
                        customerId,
                        timestamp,
                        line.getItem().getId()
                );
                existingOrders.add(eo);
                writeOrderToCSV(eo);
            }
        }

        totalRevenue += total;
        totalDiscountGiven += lastDiscount;
        totalOrders++;

        for (OrderLine line : currentOrderLines) {
            String id = line.getItem().getId();
            int qty = line.getQuantity();
            double lineTotal = line.getLineTotal();
            itemOrderCount.merge(id, qty, Integer::sum);
            itemRevenue.merge(id, lineTotal, Double::sum);
        }

        statusLabel.setText("Order confirmed for customer " + customerId);
        nextOrderNo++;
        nextCustomerId++;

        currentOrderLines.clear();
        freeBeverageSelected = null;
        freeBeverageClaimedThisOrder = false;
        updateBill();
        customerIdField.setText(String.valueOf(nextCustomerId));
    }

    private void writeOrderToCSV(ExistingOrder order) {
        String fileName = "existing_orders.csv";
        try (FileWriter writer = new FileWriter(fileName, true)) {
            java.io.File file = new java.io.File(fileName);
            if (file.length() == 0) {
                writer.write("orderNo,customerId,timestamp,itemId\n");
            }
            writer.write(
                order.getOrderNo() + "," +
                order.getCustomerId() + "," +
                order.getTimestamp() + "," +
                order.getItemIds() + "\n"
            );
        } catch (IOException e) {
            System.out.println("Error writing to CSV: " + e.getMessage());
        }
    }

    private void showDiscountPopup() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Discount Breakdown");

        Label title = new Label("Discounts Applied");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label details = new Label(discountBreakdown.isEmpty() ? "No discounts applied." : discountBreakdown);
        details.setStyle("-fx-font-size: 14px;");

        VBox layout = new VBox(15, title, details);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center-left;");

        popup.setScene(new Scene(layout, 380, 220));
        popup.showAndWait();
    }

    private void showFreeBeveragePopup() {
        ObservableList<MenuItem> beverages = allMenuItems.filtered(
                i -> i.getCategory().equalsIgnoreCase("BEV")
        );
        if (beverages.isEmpty()) return;

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Choose Your Free Beverage");

        Label title = new Label("You qualify for a free beverage!");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<MenuItem> listView = new ListView<>(beverages);
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(MenuItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (£" + String.format("%.2f", item.getPrice()) + ")");
                }
            }
        });

        Button btnSelect = new Button("Select Free Beverage");
        btnSelect.setOnAction(e -> {
            MenuItem selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                freeBeverageSelected = selected;
                freeBeverageClaimedThisOrder = true;
                addItemToOrder(selected, 1);
                popup.close();
            }
        });

        VBox layout = new VBox(10, title, listView, btnSelect);
        layout.setStyle("-fx-padding: 15; -fx-alignment: center;");

        popup.setScene(new Scene(layout, 320, 300));
        popup.showAndWait();
    }

    private void updateBill() {
        double subtotal = currentOrderLines.stream()
                .mapToDouble(OrderLine::getLineTotal)
                .sum();

        int foodCount = 0;
        for (OrderLine line : currentOrderLines) {
            if (line.getItem().getCategory().equalsIgnoreCase("FOOD")) {
                foodCount += line.getQuantity();
            }
        }

        if (foodCount >= 2 && !freeBeverageClaimedThisOrder) {
            showFreeBeveragePopup();
        }

        double discount = 0;
        StringBuilder breakdown = new StringBuilder();

        if (freeBeverageSelected != null) {
            boolean hasSelectedBev = currentOrderLines.stream()
                    .anyMatch(l -> l.getItem().equals(freeBeverageSelected));
            if (hasSelectedBev) {
                double bevPrice = freeBeverageSelected.getPrice();
                discount += bevPrice;
                breakdown.append("Free Beverage (")
                        .append(freeBeverageSelected.getName())
                        .append("): -£")
                        .append(String.format("%.2f", bevPrice))
                        .append("\n");
            }
        }

        if (subtotal > 5) {
            double d = subtotal * 0.05;
            discount += d;
            breakdown.append("5% off above £5: -£").append(String.format("%.2f", d)).append("\n");
        }

        if (subtotal > 20) {
            double d = subtotal * 0.10;
            discount += d;
            breakdown.append("10% off above £20: -£").append(String.format("%.2f", d)).append("\n");
        }

        double total = subtotal - discount;

        subtotalLabel.setText("Subtotal: £" + String.format("%.2f", subtotal));
        discountLabel.setText("Discount: £" + String.format("%.2f", discount));
        totalLabel.setText("Total: £" + String.format("%.2f", total));

        lastDiscount = discount;
        discountBreakdown = breakdown.toString();
        discountBadge.setVisible(discount > 0);

        if (discount > 0) {
            showDiscountPopup();
        }
    }

    public void showSummaryReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Moonbucks Session Summary\n");
        sb.append("-------------------------\n\n");
        sb.append("Total Orders: ").append(totalOrders).append("\n");
        sb.append("Total Revenue: £").append(String.format("%.2f", totalRevenue)).append("\n");
        sb.append("Total Discount Given: £").append(String.format("%.2f", totalDiscountGiven)).append("\n\n");
        sb.append("Per-Item Summary:\n");

        for (MenuItem item : allMenuItems) {
            String id = item.getId();
            int count = itemOrderCount.getOrDefault(id, 0);
            double rev = itemRevenue.getOrDefault(id, 0.0);
            sb.append("- ").append(item.getName())
              .append(" (").append(item.getCategory()).append(")")
              .append(" | Ordered: ").append(count)
              .append(" | Revenue: £").append(String.format("%.2f", rev))
              .append("\n");
        }

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Session Report Summary");

        Label title = new Label("Session Report Summary");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextArea area = new TextArea(sb.toString());
        area.setEditable(false);
        area.setWrapText(true);

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> popup.close());

        VBox layout = new VBox(10, title, area, closeBtn);
        layout.setStyle("-fx-padding: 15; -fx-alignment: center;");

        popup.setScene(new Scene(layout, 500, 400));
        popup.showAndWait();
    }
    
    public int getSimulationDelay() {
        int speed = (int) speedSlider.getValue();
        switch (speed) {
            case 1: return 4000;  
            case 2: return 3000;  
            case 3: return 2000;  
            case 4: return 1000;  
            case 5: return 500;   
            default: return 2000;
        }
    }

    public void showSimulationReport(String report) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Simulation Report");

        Label title = new Label("Simulation Report");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextArea area = new TextArea(report);
        area.setEditable(false);
        area.setWrapText(true);

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> popup.close());

        VBox layout = new VBox(10, title, area, closeBtn);
        layout.setStyle("-fx-padding: 15; -fx-alignment: center;");

        popup.setScene(new Scene(layout, 500, 400));
        popup.showAndWait();
    }

    @Override
    public void onStaffUpdated(int staffId, String status, String order) {
        updateStaffUI(staffId, status, order);
    }

    @Override
    public void onQueueUpdated() {
        // queue table updates automatically via ObservableList binding
    }
    
    @Override
    public void onSimulationReset() {
        Platform.runLater(() -> {
            btnStartSimulation.setDisable(false);
            statusLabel.setText("Simulation complete. Ready to run again.");
        });
    }
    
    @Override
    public void onLogUpdated(String message) {
        appendLog(message);
    }

    @Override
    public void onSimulationComplete(String report) {
        Platform.runLater(() -> showSimulationReport(report));
    }
}