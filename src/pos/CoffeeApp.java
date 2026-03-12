package pos;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pos.controller.MainController;
import pos.model.MenuItem;
import pos.model.ExistingOrder;
import util.MenuLoader;
import util.OrderLoader;
import java.util.List;
import java.util.Map;

public class CoffeeApp extends Application {
    private MainController controller;
    private static Map<String, MenuItem> menuItems;
    private static List<ExistingOrder> existingOrders;
    
    
    //init method runs before GUI and hence data is loaded before the GUI opens
    @Override
    public void init() {
        menuItems = MenuLoader.loadMenu(); //menu data loader
        System.out.println("Menu loaded: " + menuItems.size() + " items."); 

        existingOrders = OrderLoader.loadOrders(); //existing order data loader
        System.out.println("Orders loaded: " + existingOrders.size() + " orders.");
    }

    public static Map<String, MenuItem> getMenuItems() {
        return menuItems;
    }

    public static List<ExistingOrder> getExistingOrders() {
        return existingOrders;
    }

    
    //GUI loads from here 
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pos/view/main_view.fxml")); //GUI components file where we have our code for the ui. 
        Parent root = loader.load();
        controller = loader.getController(); //linking to our controller instance
        stage.setTitle("Moonbucks Coffee");
        stage.setScene(new Scene(root, 1000, 600));
        //loading summary before closing the GUI app
        stage.setOnCloseRequest(e -> {      
            e.consume();
            controller.showSummaryReport();
            stage.close();
        });
        stage.show();
    }

    // entry point to the code
    public static void main(String[] args) {
        launch(args);
    }
}