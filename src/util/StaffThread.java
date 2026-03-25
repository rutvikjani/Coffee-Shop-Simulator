package util;

import pos.model.Customer;
import pos.model.Staff;
import java.util.Random;
import util.LoggerService;

public class StaffThread implements Runnable {

    private final Staff staff;
    private final QueueService queueService;
    private final LoggerService logger;
    private final Runnable onStaffUpdate;
    private volatile boolean running = true;

    public StaffThread(Staff staff, QueueService queueService, LoggerService logger, Runnable onStaffUpdate) {
        this.staff = staff;
        this.queueService = queueService;
        this.logger = logger;
        this.onStaffUpdate = onStaffUpdate;
    }

    @Override
    public void run() {
        while (running) {
            try {
                if (queueService.isEmpty() ) {
                    Thread.sleep(500);
                    continue;
                }

                Customer customer = queueService.getNextCustomer();
                if (customer == null) continue;

                customer.setStatus("Being Served");
                staff.setStatus("Serving");
                staff.setCurrentOrder("Customer " + customer.getCustomerId());
                logger.log(staff.getName() + " is serving Customer " + customer.getCustomerId());
                onStaffUpdate.run();

                int processingTime = calculateProcessingTime(customer);
                Thread.sleep(processingTime);

                customer.setStatus("Complete");
                staff.setStatus("Idle");
                staff.setCurrentOrder("No order");
                logger.log(staff.getName() + " completed order for Customer " + customer.getCustomerId());
                onStaffUpdate.run();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private int calculateProcessingTime(Customer customer) {
        Random random = new Random();
        int totalTime = 0;
        for (String itemId : customer.getItemIds()) {
            if (itemId.startsWith("BEV")) {
                totalTime += (2 + random.nextInt(3)) * 1000;
            } else if (itemId.startsWith("FOOD")) {
                totalTime += (6 + random.nextInt(5)) * 1000;
            } else {
                totalTime += (1 + random.nextInt(2)) * 1000;
            }
        }
        return totalTime;
    }

    public void stop() {
        running = false;
    }
}