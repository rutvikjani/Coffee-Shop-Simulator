package util;

import pos.model.Customer;
import pos.model.ExistingOrder;
import java.util.*;

public class QueueService {

    private static QueueService instance;
    private final Queue<Customer> queue = new LinkedList<>();

    private QueueService() {}

    public static synchronized QueueService getInstance() {
        if (instance == null) {
            instance = new QueueService();
        }
        return instance;
    }

    public synchronized void addCustomer(Customer customer) {
        queue.offer(customer);
        notifyAll();
    }

    public synchronized Customer getNextCustomer() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.poll();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    public synchronized int getSize() {
        return queue.size();
    }

    public synchronized List<Customer> getAllCustomers() {
        return new ArrayList<>(queue);
    }

    public synchronized void clear() {
        queue.clear();
    }

    public void loadFromOrders(List<ExistingOrder> orders) {
        Map<String, List<String>> groupedOrders = new LinkedHashMap<>();

        for (ExistingOrder order : orders) {
            String orderNo = order.getOrderNo();
            String itemIds = order.getItemIds();

            if (!groupedOrders.containsKey(orderNo)) {
                groupedOrders.put(orderNo, new ArrayList<>());
            }

            for (String itemId : itemIds.split("[;,]")) {
                String trimmed = itemId.trim();
                if (!trimmed.isEmpty()) {
                    groupedOrders.get(orderNo).add(trimmed);
                }
            }
        }

        for (Map.Entry<String, List<String>> entry : groupedOrders.entrySet()) {
            Customer customer = new Customer(entry.getKey(), entry.getValue());
            addCustomer(customer);
        }
    }
}