package service;

import model.SubTask;
import model.Task;
import model.Epic;
import model.Status;

import java.util.*;

public class TaskManager {
    private final HashMap<Integer, Task> tasksStorage;
    private final HashMap<Integer, Epic> epicsStorage;
    private final HashMap<Integer, SubTask> subTasksStorage;

    private int identifier = 0;

    public TaskManager() {
        this.epicsStorage = new HashMap<>();
        this.subTasksStorage = new HashMap<>();
        this.tasksStorage = new HashMap<>();
    }

    private int generateId() {
        return ++identifier;
    }

    public List<Task> getTaskAll() {  // сделала НЕ ТРОГАТЬ! Task
        return new ArrayList<>(tasksStorage.values());
    }

    public List<Epic> getEpicAll() {
        return new ArrayList<>(epicsStorage.values());
    }

    public List<SubTask> getSubTaskAll() {
        return new ArrayList<>(subTasksStorage.values());
    }


    public void deleteTaskById(int id) {         // удалить по ID сделала Task
        tasksStorage.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epicsStorage.get(id);
        for (Integer subTaskId : epic.getSubTasksIds()) {
            subTasksStorage.remove(subTaskId);
        }
        epicsStorage.remove(id);
    }

    public void deleteSubTaskById(int id) {
        if (!subTasksStorage.containsKey(id)) {
            return;
        }
        SubTask subTask = subTasksStorage.get(id);
        Epic epic = epicsStorage.get(subTask.getEpicId());
        epic.removeSubTaskId(id);
        epic.setStatus(calculateNewStatusByEpic(epic));
        subTasksStorage.remove(id);

    }

    public Task getTaskById(int id) {
        return tasksStorage.get(id);
    }

    public Epic getEpicById(int id) {
        return epicsStorage.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTasksStorage.get(id);
    }

    public Task createTask(Task task) {
        task.setId(generateId());
        tasksStorage.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epicsStorage.put(epic.getId(), epic);
        return epic;
    }

    public SubTask createSubTask(SubTask subTask) {
        Epic epic = epicsStorage.get(subTask.getEpicId());
        if (epic != null) {
            int newId = generateId();
            subTask.setId(newId);
            subTasksStorage.put(newId, subTask);
            epic.addSubTaskId(newId);
            epic.setStatus(calculateNewStatusByEpic(epic));
        }
        return subTask;
    }


    public void updateTask(Task task) {
        if (tasksStorage.containsKey(task.getId())) {
            tasksStorage.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic newEpic) {
        Epic savedEpic = epicsStorage.get((newEpic.getId()));
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(newEpic.getName());
        savedEpic.setDescription(newEpic.getDescription());

    }


    public void updateSubTasks(SubTask updatedSubTask) {
        int subTaskId = updatedSubTask.getId();
        if (!subTasksStorage.containsKey(subTaskId)) {
            return;
        }
        int epicId = subTasksStorage.get(subTaskId).getEpicId();
        if (epicId != updatedSubTask.getEpicId()) {
            return;
        }
        Epic epic = epicsStorage.get(epicId);
        subTasksStorage.put(subTaskId, updatedSubTask);
        epic.setStatus(calculateNewStatusByEpic(epic));
    }


    public List<SubTask> getSubTasksByEpic(int epicId) {
        List<SubTask> subTasks = new ArrayList<>();
        for (SubTask subTask : subTasksStorage.values()) {
            if (subTask.getEpicId() == epicId) {
                subTasks.add(subTask);
            }
        }
        return subTasks;
    }

    public void deleteAllTask() {
        tasksStorage.clear();
    }

    public void deleteAllEpic() {
        epicsStorage.clear();
        subTasksStorage.clear();

    }

    public void deleteAllSubTask() {
        for (Epic epic : epicsStorage.values()) {
            epic.removeAllSubTask();
            epic.setStatus(calculateNewStatusByEpic(epic));
        }
        subTasksStorage.clear();
    }

    private Status calculateNewStatusByEpic(Epic epic) {
        List<Status> statuses = new ArrayList<>();
        for (int subTaskId : epic.getSubTasksIds()) {
            statuses.add(subTasksStorage.get(subTaskId).getStatus());
        }

        int counterNew = 0;
        int counterDone = 0;
        for (Status status : statuses) {
            if (status.equals(Status.NEW)) {
                counterNew++;
            }
            if (status.equals(Status.DONE)) {
                counterDone++;
            }
        }

        if (counterNew == statuses.size()) {
            return Status.NEW;
        } else if (counterDone == statuses.size()) {
            return Status.DONE;
        } else {
            return Status.IN_PROGRESS;
        }
    }
}
