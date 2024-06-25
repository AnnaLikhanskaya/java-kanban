package handler;

import model.Task;

import static httpserver.HttpTaskServer.taskManager;


public class HistoryHandler extends BaseHttpHandler<Task> {
    public HistoryHandler() {
        firstPartOfPath = "history";
        getAllHandler = taskManager::getHistory;
        type = Task.class;
    }
}
