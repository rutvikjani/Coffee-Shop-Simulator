package pos.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import pos.model.Customer;
import pos.model.Staff;
import util.LoggerService;
import util.QueueService;
import util.ReportService;
import util.SimulationObserver;
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
    private final List<SimulationObserver> observers = new ArrayList<>();
    private final ObservableList<Customer> queueItems;

    public SimulationController(MainController mainController) {
        this.queueItems = mainController.getQueueItems();
        staffList.add(new Staff(1, "Staff 1"));
        staffList.add(new Staff(2, "Staff 2"));
        staffList.add(new Staff(3, "Staff 3"));

        registerObserver(mainController);
    }

    public void registerObserver(SimulationObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(SimulationObserver observer) {
        observers.remove(observer);
    }

    private void notifyStaffUpdated(int staffId, String status, String order) {
        for (SimulationObserver o : observers) {
            o.onStaffUpdated(staffId, status, order);
        }
    }

    private void notifyQueueUpdated() {
        for (SimulationObserver o : observers) {
            o.onQueueUpdated();
        }
    }

    private void notifyLogUpdated(String message) {
        for (SimulationObserver o : observers) {
            o.onLogUpdated(message);
        }
    }

    private void notifySimulationComplete(String report) {
        for (SimulationObserver o : observers) {
            o.onSimulationComplete(report);
        }
    }

    public void startSimulation(List<pos.model.ExistingOrder> orders) {
        queueService.loadFromOrders(orders);
        Platform.runLater(() -> queueItems.setAll(queueService.getAllCustomers()));

        String startMsg = "Simulation started with " + orders.size() + " orders";
        logger.log(startMsg);
        notifyLogUpdated(startMsg);

        for (Staff staff : staffList) {
            StaffThread staffRunnable = new StaffThread(
                staff,
                queueService,
                logger,
                reportService,
                () -> {
                    notifyStaffUpdated(staff.getStaffId(), staff.getStatus(), staff.getCurrentOrder());
                    Platform.runLater(() -> queueItems.setAll(queueService.getAllCustomers()));
                    notifyLogUpdated(staff.getName() + " → " + staff.getStatus() + " → " + staff.getCurrentOrder());
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
        notifyLogUpdated("Simulation complete!");
        notifySimulationComplete(report);
    }
}