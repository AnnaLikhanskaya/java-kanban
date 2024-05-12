package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasksStorage;
    private final HashMap<Integer, Epic> epicsStorage;
    private final HashMap<Integer, SubTask> subTasksStorage;
    private final HistoryManager history = Managers.getDefaultHistory();

    private int identifier = 0;

    public InMemoryTaskManager() {
        this.epicsStorage = new HashMap<>();
        this.subTasksStorage = new HashMap<>();
        this.tasksStorage = new HashMap<>();
    }

    private int generateId() {
        return ++identifier;
    }

    @Override
    public List<Task> getTaskAll() {
        return new ArrayList<>(tasksStorage.values());
    }

    @Override
    public List<Epic> getEpicAll() {
        return new ArrayList<>(epicsStorage.values());
    }

    @Override
    public List<SubTask> getSubTaskAll() {
        return new ArrayList<>(subTasksStorage.values());
    }

    @Override
    public void deleteTaskById(int id) {
        tasksStorage.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epicsStorage.get(id);
        for (Integer subTaskId : epic.getSubTasksIds()) {
            subTasksStorage.remove(subTaskId);
            history.remove(id);
        }
        epicsStorage.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (!subTasksStorage.containsKey(id)) {
            return;
        }
        SubTask subTask = subTasksStorage.get(id);
        Epic epic = epicsStorage.get(subTask.getEpicId());
        epic.removeSubTaskId(id);
        epic.setStatus(calculateNewStatusByEpic(epic));
        subTasksStorage.remove(id);
        history.remove(id);
    }


    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasksStorage.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epicsStorage.put(epic.getId(), epic);
        return epic;
    }

    @Override
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

    @Override
    public void updateTask(Task task) {
        if (tasksStorage.containsKey(task.getId())) {
            tasksStorage.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic newEpic) {
        Epic savedEpic = epicsStorage.get((newEpic.getId()));
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(newEpic.getName());
        savedEpic.setDescription(newEpic.getDescription());

    }

    @Override
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

    @Override
    public List<SubTask> getSubTasksByEpic(int epicId) {
        List<SubTask> subTasks = new ArrayList<>();
        for (SubTask subTask : subTasksStorage.values()) {
            if (subTask.getEpicId() == epicId) {
                subTasks.add(subTask);
            }
        }
        return subTasks;
    }

    @Override
    public void deleteAllTask() {
        for (Task task : tasksStorage.values()) {
            history.remove(task.getId());
        }
        tasksStorage.clear();
    }

    @Override
    public void deleteAllEpic() {
        if (!epicsStorage.isEmpty()) {
            for (Epic epic : epicsStorage.values()) {
                history.remove(epic.getId());
            }
            epicsStorage.clear();
            for (SubTask subTask : subTasksStorage.values()) {
                history.remove(subTask.getId());
            }
            subTasksStorage.clear();
        }
    }

    @Override
    public void deleteAllSubTask() {
        for (SubTask subTask : subTasksStorage.values()) {
            history.remove(subTask.getId());
        }
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


    @Override
    public Task getTaskById(int id) {
        if (tasksStorage.containsKey(id)) {
            history.addHistory(tasksStorage.get(id));
        }
        return tasksStorage.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (epicsStorage.containsKey(id)) {
            history.addHistory(epicsStorage.get(id));
        }
        return epicsStorage.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        if (subTasksStorage.containsKey(id)) {
            history.addHistory(subTasksStorage.get(id));
        }
        return subTasksStorage.get(id);
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();

    }

}
