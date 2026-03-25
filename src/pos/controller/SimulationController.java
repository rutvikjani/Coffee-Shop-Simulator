package pos.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import pos.model.Customer;
import pos.model.Staff;
import util.LoggerService;
import util.QueueService;
import util.ReportService;
import util.StaffThread;

import java.util.ArrayList;
import java.util.List;

public class SimulationController {

    private final QueueService queueService = QueueService.getInstance();
    private final LoggerService logger = LoggerService.getInstance();
    private final ReportService reportService = new ReportService();

    private final List<Staff> staffList = new ArrayList<>();
    private final List<Thread> threads = new ArrayList<>();
    private final List<StaffThread> staffRunnables = new ArrayList<>();

    private final MainController mainController;
    private final ObservableList<Customer> queueItems;

    public SimulationController(MainController mainController) {
        this.mainController = mainController;
        this.queueItems = mainController.getQueueItems();

        staffList.add(new Staff(1, "Staff 1"));
        staffList.add(new Staff(2, "Staff 2"));
        staffList.add(new Staff(3, "Staff 3"));
    }

    public void startSimulation(List<pos.model.ExistingOrder> orders) {
        queueService.loadFromOrders(orders);

        Platform.runLater(() -> queueItems.setAll(queueService.getAllCustomers()));

        logger.log("Simulation started with " + orders.size() + " orders");
        mainController.appendLog("Simulation started with " + orders.size() + " orders");

        for (Staff staff : staffList) {
            StaffThread staffRunnable = new StaffThread(
                staff,
                queueService,
                logger,
                () -> {
                    mainController.updateStaffUI(
                        staff.getStaffId(),
                        staff.getStatus(),
                        staff.getCurrentOrder()
                    );
                    Platform.runLater(() -> queueItems.setAll(queueService.getAllCustomers()));
                    mainController.appendLog(staff.getName() + " → " + staff.getStatus() + " → " + staff.getCurrentOrder());
                }
            );

            staffRunnables.add(staffRunnable);
            Thread thread = new Thread(staffRunnable);
            thread.setDaemon(true);
            threads.add(thread);
            thread.start();
        }

        Thread monitorThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    boolean allIdle = staffList.stream()
                            .allMatch(s -> s.getStatus().equals("Idle"));

                    if (allIdle && queueService.isEmpty()) {
                        stopSimulation();
                        break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    public void stopSimulation() {
        for (StaffThread staffRunnable : staffRunnables) {
            staffRunnable.stop();
        }

        logger.writeToFile();

        String report = reportService.generateReport();
        logger.log("Simulation complete!");
        mainController.appendLog("Simulation complete!");
        mainController.appendLog(report);

        Platform.runLater(() ->
            mainController.showSimulationReport(report)
        );
    }
}