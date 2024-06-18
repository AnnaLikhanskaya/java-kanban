package service;

import exceptions.DataNotFoundException;
import exceptions.EmptyStorageException;
import exceptions.IntersectionsOfTaskIntervalsException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasksStorage;
    protected final HashMap<Integer, Epic> epicsStorage;
    protected final HashMap<Integer, SubTask> subTasksStorage;
    protected final TreeSet<Task> sortedStorage;
    private final HistoryManager history = Managers.getDefaultHistory();

    protected Integer identifier = 0;

    public InMemoryTaskManager() {
        this.epicsStorage = new HashMap<>();
        this.subTasksStorage = new HashMap<>();
        this.tasksStorage = new HashMap<>();
        this.sortedStorage = new TreeSet<>(Comparator.comparing(Task::getStartTime));
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
        Task savedTask = tasksStorage.get(id);
        tasksStorage.remove(id);
        sortedStorage.remove(savedTask);
        history.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epicsStorage.get(id);
        epic.getSubTaskIds().stream()
                .map(subTasksStorage::get)
                .peek(subTask -> {
                    sortedStorage.remove(subTask);
                    subTasksStorage.remove(subTask.getId());
                    history.remove(subTask.getId());
                }).close();

        epicsStorage.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (!subTasksStorage.containsKey(id)) {
            throw new DataNotFoundException("Сабтаска не найдена");
        }
        SubTask subTask = subTasksStorage.get(id);
        Epic epic = epicsStorage.get(subTask.getEpicId());
        epic.removeSubTaskId(id);
        epic.setStatus(calculateNewStatusByEpic(epic));
        recalculateEpicTimes(epic);
        sortedStorage.remove(subTask);
        subTasksStorage.remove(id);
        history.remove(id);
    }


    @Override
    public Task createTask(Task task) {
        checkIntersections(task);
        task.setId(generateId());
        tasksStorage.put(task.getId(), task);
        if (task.getStartTime() != null) {
            sortedStorage.add(task);
        }

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
        if (epic == null) {
            throw new DataNotFoundException("Не найден эпик");
        }
        int newId = generateId();
        checkIntersections(subTask);
        subTask.setId(newId);
        subTasksStorage.put(newId, subTask);
        if (subTask.getStartTime() != null) {
            sortedStorage.add(subTask);
        }
        epic.addSubTaskId(newId);
        epic.setStatus(calculateNewStatusByEpic(epic));
        recalculateEpicTimes(epic);
        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        if (!tasksStorage.containsKey(task.getId())) {
            throw new DataNotFoundException("Таска с Id " + task.getId() + " не найдена");
        }
        final Task savedTask = tasksStorage.get(task.getId());
        sortedStorage.remove(savedTask);
        checkIntersections(task);
        tasksStorage.put(task.getId(), task);
        if (task.getStartTime() != null) {
            sortedStorage.add(task);
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
        final SubTask savedSubTask = subTasksStorage.get(subTaskId);
        sortedStorage.remove(savedSubTask);
        checkIntersections(updatedSubTask);
        if (updatedSubTask.getStartTime() != null) {
            sortedStorage.add(updatedSubTask);
        }
        subTasksStorage.put(subTaskId, updatedSubTask);


        epic.setStatus(calculateNewStatusByEpic(epic));
        recalculateEpicTimes(epic);
    }

    @Override
    public List<SubTask> getSubTasksByEpic(int epicId) {
        return subTasksStorage.values().stream()
                .filter(subTask -> subTask.getEpicId() == epicId)
                .toList();
    }

    @Override
    public void deleteAllTask() {
        tasksStorage.values().stream()
                .peek(task -> {
                    sortedStorage.remove(task);
                    history.remove(task.getId());
                }).close();
        tasksStorage.clear();
    }

    @Override
    public void deleteAllEpic() {
        if (epicsStorage.isEmpty()) {
            throw new EmptyStorageException("Хранилище эпиков пустое");
        }
        epicsStorage.values().stream()
                .peek(epic -> history.remove(epic.getId()))
                .close();
        epicsStorage.clear();

        subTasksStorage.values().stream()
                .peek(subTask -> {
                    sortedStorage.remove(subTask);
                    history.remove(subTask.getId());
                }).close();
        subTasksStorage.clear();
    }

    @Override
    public void deleteAllSubTask() {
        subTasksStorage.values().stream()
                .peek(subTask -> {
                    sortedStorage.remove(subTask);
                    history.remove(subTask.getId());
                }).close();
        epicsStorage.values().stream()
                .peek(epic -> {
                    epic.removeAllSubTask();

                    epic.setStatus(calculateNewStatusByEpic(epic));
                    recalculateEpicTimes(epic);
                }).close();
        subTasksStorage.clear();
    }

    protected Status calculateNewStatusByEpic(Epic epic) {
        List<Status> statuses =
                epic.getSubTaskIds().stream()
                        .map(id -> subTasksStorage.get(id).getStatus())
                        .toList();
        int counterNew = (int) statuses.stream().filter(status -> status.equals(Status.NEW)).count();
        int counterDone = (int) statuses.stream().filter(status -> status.equals(Status.DONE)).count();

        if (counterNew == statuses.size()) {
            return Status.NEW;
        } else if (counterDone == statuses.size()) {
            return Status.DONE;
        } else {
            return Status.IN_PROGRESS;
        }
    }

    protected void recalculateEpicTimes(Epic epic) {
        List<SubTask> subtasks =
                epic.getSubTasksIds().stream()
                        .map(subTasksStorage::get)
                        .toList();

        LocalDateTime minTime = subtasks.stream()
                .map(SubTask::getStartTime)
                .min(LocalDateTime::compareTo).orElse(null);
        epic.setStartTime(minTime);

        LocalDateTime maxTime = subtasks.stream()
                .map(SubTask::getEndTime)
                .max(LocalDateTime::compareTo).orElse(null);
        epic.setEndTime(maxTime);

        Duration sumDurations = subtasks.stream()
                .map(SubTask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
        epic.setDuration(sumDurations);

    }

    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedStorage);
    }

    protected void checkIntersections(Task checkingTask) {
        long intersectionsCount = sortedStorage.stream()
                .filter(task -> {

                    boolean isInterceptStart = isDateTimeBetween(checkingTask.getStartTime(), task)
                            && isDateTimeBetween(task.getStartTime(), checkingTask);
                    boolean isInterceptEnd = isDateTimeBetween(checkingTask.getEndTime(), task)
                            && isDateTimeBetween(task.getEndTime(), checkingTask);
                    return isInterceptEnd && isInterceptStart;
                }).count();
        if (intersectionsCount > 0) {
            throw new IntersectionsOfTaskIntervalsException("Интервалы задач не могут пересекаться");
        }

    }

    private boolean isDateTimeBetween(LocalDateTime checking, Task task) {
        return checking.isAfter(task.getStartTime()) && checking.isBefore(task.getEndTime());
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
