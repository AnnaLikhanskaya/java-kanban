package service;

import model.SubTask;
import model.Task;
import model.Epic;
import model.Status;

import java.util.*;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;

    private int identifier = 0;

    public TaskManager() {
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.tasks = new HashMap<>();

    }

    private int generateId() {
        return ++identifier;
    }

    public List<Task> getTaskAll() {  // сделала НЕ ТРОГАТЬ! Task
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getEpicAll() {
        return new ArrayList<>(epics.values());
    }

    public List<SubTask> getSubTaskAll() {
        return new ArrayList<>(subTasks.values());
    }


    public void deleteTaskById(int id) {         // удалить по ID сделала Task
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        for (SubTask subTask : epic.getSubTasks()) {
            subTasks.remove(subTask.getId());
        }
        epics.remove(id);
    }

    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask == null) {
            return;
        }
        Epic epic = subTask.getEpic();
        epic.removeSubTask(subTask);

        subTasks.remove(id);
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public Task createTask(Task task) {
        task.setStatus(Status.NEW);
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setStatus(Status.NEW);
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public SubTask createSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpic().getId());
        epic.addSubTask(subTask);
        subTask.setStatus(Status.NEW);
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        return subTask;
    }


    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpics(Epic epic) {
        Epic saved = epics.get((epic.getId()));
        if (saved == null) {
            return;
        }
        epic.setSubTasks(saved.getSubTasks());
        epic.updateStatus();
        epics.put(epic.getId(), epic);

    }

    public void updateSubTasks(SubTask subTask) {
        SubTask savedSubTask = subTasks.get(subTask.getId());
        Epic epic = savedSubTask.getEpic();
        epic.removeSubTask(subTask);

        if (savedSubTask == null) {
            return;
        }
        subTask.setEpic(savedSubTask.getEpic());
        epic.addSubTask(subTask);
        subTasks.put(subTask.getId(), subTask);
        epic.updateStatus();

    }

    public List<SubTask> getSubTaskByEpic(int epicId) {
        return epics.get(epicId).getSubTasks();
    }

    public void deleteAllTask() {
        tasks.clear();
    }

    public void deleteAllEpic() {
        epics.clear();
        subTasks.clear();

    }

    public void deleteAllSubTask() {
        for (SubTask subTask : subTasks.values()) {
            Epic epic = subTask.getEpic();
            epic.removeAllSubTask();
        }
        subTasks.clear();
    }

}
