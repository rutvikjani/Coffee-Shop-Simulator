package util;

import pos.model.Customer;
import pos.model.ExistingOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueService {

    private static QueueService instance;
    private final BlockingQueue<Customer> queue = new LinkedBlockingQueue<>();

    private QueueService() {}

    public static synchronized QueueService getInstance() {
        if (instance == null) {
            instance = new QueueService();
        }
        return instance;
    }

    public void addCustomer(Customer customer) {
        queue.offer(customer);
    }

    public Customer getNextCustomer() throws InterruptedException {
        return queue.take();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int getSize() {
        return queue.size();
    }
    
    public void clear() {
        queue.clear();
    }
    
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(queue);
    }

    public void loadFromOrders(List<ExistingOrder> orders) {
        for (ExistingOrder order : orders) {
            List<String> itemIds = List.of(order.getItemIds().split(";"));
            Customer customer = new Customer(order.getCustomerId(), itemIds);
            addCustomer(customer);
        }
    }
}