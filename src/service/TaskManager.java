package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    //Вывод задач
    List<Task> getTaskAll();

    List<Epic> getEpicAll();

    List<SubTask> getSubTaskAll();

    //Удаление задач по ID
    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubTaskById(int id);

    // Просмотр задачи
    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    // Добавление(создание) задач
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask);

    // Обновление задач
    void updateTask(Task task);

    void updateEpic(Epic newEpic);

    void updateSubTasks(SubTask updatedSubTask);

    // Получение списка всех подзадач эпика
    List<SubTask> getSubTasksByEpic(int epicId);

    // Удаление все задач
    void deleteAllTask();

    void deleteAllEpic();

    void deleteAllSubTask();

    List<Task> getHistory();

    ArrayList<Task> getPrioritizedTasks();

}
