package handler;

import model.SubTask;

import static httpServer.HttpTaskServer.taskManager;


public class SubTaskHandler extends BaseHttpHandler<SubTask> {
    public SubTaskHandler() {
        firstPartOfPath = "subtasks";
        createHandler = taskManager::createSubTask;
        getByIdHandler = taskManager::getSubTaskById;
        updateHandler = taskManager::updateSubTasks;
        getAllHandler = taskManager::getSubTaskAll;
        deleteHandler = taskManager::deleteSubTaskById;
        type = SubTask.class;
    }
}