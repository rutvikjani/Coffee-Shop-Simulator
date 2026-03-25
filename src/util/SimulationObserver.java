package util;

public interface SimulationObserver {
    void onStaffUpdated(int staffId, String status, String order);
    void onQueueUpdated();
    void onLogUpdated(String message);
    void onSimulationComplete(String report);
}