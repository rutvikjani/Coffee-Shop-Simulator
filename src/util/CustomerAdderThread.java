package util;

import pos.model.Customer;
import java.util.List;

public class CustomerAdderThread implements Runnable {

    private final List<Customer> customers;
    private final QueueService queueService;
    private final LoggerService logger;
    private final Runnable onCustomerAdded;
    private volatile int delayMs;
    private volatile boolean running = true;

    public CustomerAdderThread(List<Customer> customers, QueueService queueService,
                                LoggerService logger, int delayMs, Runnable onCustomerAdded) {
        this.customers = customers;
        this.queueService = queueService;
        this.logger = logger;
        this.delayMs = delayMs;
        this.onCustomerAdded = onCustomerAdded;
    }

    @Override
    public void run() {
        for (Customer customer : customers) {
            if (!running) break;
            try {
                Thread.sleep(delayMs);
                queueService.addCustomer(customer);
                logger.log("Customer " + customer.getCustomerId()
                        + " joined the queue (" + customer.getItemIds().size() + " items)");
                onCustomerAdded.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void setDelay(int delayMs) {
        this.delayMs = delayMs;
    }

    public void stop() {
        running = false;
    }
}