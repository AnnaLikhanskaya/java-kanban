package service;

import model.Task;

public class Managers {
    private static final TaskManager taskManager = new InMemoryTaskManager();
    private static final HistoryManager historyManager = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }


}
