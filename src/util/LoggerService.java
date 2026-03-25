package util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LoggerService {

    private static LoggerService instance;
    private final List<String> logs = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private LoggerService() {}

    public static synchronized LoggerService getInstance() {
        if (instance == null) {
            instance = new LoggerService();
        }
        return instance;
    }

    public void log(String message) {
        String entry = "[" + LocalTime.now().format(formatter) + "] " + message;
        logs.add(entry);
        System.out.println(entry);
    }

    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    public void writeToFile() {
        try (FileWriter writer = new FileWriter("simulation_log.txt")) {
            for (String log : logs) {
                writer.write(log + "\n");
            }
            System.out.println("Log written to simulation_log.txt");
        } catch (IOException e) {
            System.out.println("Error writing log: " + e.getMessage());
        }
    }
}