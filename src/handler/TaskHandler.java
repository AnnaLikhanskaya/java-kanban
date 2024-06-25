package handler;

import model.Task;

import static httpServer.HttpTaskServer.taskManager;


public class TaskHandler extends BaseHttpHandler<Task> {
    public TaskHandler() {
        firstPartOfPath = "tasks";
        createHandler = taskManager::createTask;
        getByIdHandler = taskManager::getTaskById;
        updateHandler = taskManager::updateTask;
        getAllHandler = taskManager::getTaskAll;
        deleteHandler = taskManager::deleteTaskById;
        type = Task.class;
    }
}
