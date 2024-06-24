package handler;

import com.sun.net.httpserver.HttpHandler;
import model.Task;

import static httpServer.HttpTaskServer.taskManager;

public class PriorityHandler extends BaseHttpHandler<Task> implements HttpHandler {

    public PriorityHandler() {
        firstPartOfPath = "prioritized";
        getAllHandler = taskManager::getPrioritizedTasks;
    }
}
