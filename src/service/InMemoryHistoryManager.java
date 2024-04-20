package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final List<Task> watchHistory = new ArrayList<>();

    @Override
    public void addHistory(Task task) {
        watchHistory.add(task);
        int maxElement = 10;
        if (watchHistory.size() > maxElement) {
            watchHistory.remove(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(watchHistory);
    }
}
