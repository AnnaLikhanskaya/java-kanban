package service;

import Exception.ManagerSaveException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public static void main(String[] args) {
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(new File("resources/Tasks.csv"));
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 1, 0);

        Task task1 = taskManager.createTask(new Task("Новая задача",
                "Попробовать справиться с финальным заданием", 10, startTime));
        Task task2 = taskManager.createTask(new Task("Новая задача-2",
                "Прочитать ТЗ и уйти в дипрессию", 20, startTime));

        Epic epic1 = taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));

        SubTask subTask1 = taskManager.createSubTask(new SubTask("Новая подзадача-1", "Начать писать код", 10,
                startTime, Status.NEW, epic1.getId()));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("Новая подзадача-2",
                "Уже реализованы Task и SubTask", 30, startTime, Status.NEW, epic1.getId()));
        Epic epic2 = taskManager.createEpic(new Epic("Новый эпик-2",
                "Показалось что справилась"));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("Новая подзадача-3", "Написала но снова показалось",
                25, startTime, Status.NEW, epic1.getId()));

        Task updatingTask = new Task("Изенённая задача-1", " Мозги ушли погулять", 25, startTime);
        updatingTask.setId(task1.getId());
        taskManager.updateTask(updatingTask);

        SubTask updatingSubTask1 = new SubTask("Изменённая подзадача-1", "Дело близится к финалу",
                25, startTime, Status.IN_PROGRESS, subTask1.getEpicId());
        updatingSubTask1.setId(subTask1.getId());
        taskManager.updateSubTasks(updatingSubTask1);

        SubTask updatingSubTask3 = new SubTask("Изменённая подзадача-3", "Показалось что снова написала",
                25, startTime, Status.DONE, subTask3.getEpicId());
        updatingSubTask3.setId(subTask3.getId());
        taskManager.updateSubTasks(updatingSubTask3);

        Epic updatingEpic = (new Epic("Изменённый эпик-1",
                "Поблагодарить ревьюера!"));
        updatingEpic.setId(epic1.getId());
        taskManager.updateEpic(updatingEpic);

    }

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            if (file.exists()) {
                String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
                String[] lines = fileContents.split("\n");
                if (lines.length != 0 && !lines[0].isEmpty()) {
                    manager.readTasks(lines);
                }
            }
            return manager;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в файле" + file.getAbsolutePath());
        }
    }

    private void readTasks(String[] lines) {
        int maxId = 0;
        for (int i = 1; i < lines.length; i++) {
            Task task = this.fromString(lines[i]);
            int id = task.getId();

            if (id > maxId) maxId = id;
            if (task instanceof Epic) {
                epicsStorage.put(id, (Epic) task);
            } else if (task instanceof SubTask) {
                SubTask subTask = (SubTask) task;
                Epic epic = epicsStorage.get(subTask.getEpicId());
                epic.addSubTaskId(subTask.getId());
                subTasksStorage.put(id, subTask);
                if (subTask.getStartTime() != null) {
                    sortedStorage.add(subTask);
                }

                epic.setStatus(calculateNewStatusByEpic(epic));
                recalculateEpicTimes(epic);
            } else {
                tasksStorage.put(id, task);
                if (task.getStartTime() != null) {
                    sortedStorage.add(task);
                }
            }
        }

        identifier = maxId;
    }

    private String[] toString(Task task) {
        String[] dataTask = new String[8];
        dataTask[0] = String.valueOf(task.getId());
        dataTask[1] = Tasks.TASK.getTaskType();
        dataTask[2] = task.getName();
        dataTask[3] = task.getDescription();
        dataTask[4] = String.valueOf(task.getDuration().toMinutes());
        if (task.getStartTime() != null) {
            dataTask[5] = task.getStartTime().format(DATE_TIME_FORMATTER);
        } else {
            dataTask[5] = "NULL";
        }
        dataTask[6] = task.getStatus().toString();
        dataTask[7] = "\n";

        if (task instanceof Epic) {
            dataTask[1] = Tasks.EPIC.getTaskType();
        } else if (task instanceof SubTask) {
            dataTask[1] = Tasks.SUBTASK.getTaskType();
            dataTask[7] = ((SubTask) task).getEpicId() + "\n";
        }
        return dataTask;
    }

    private Task fromString(String value) {

        String[] taskData = value.split(",");

        if (taskData[1].equals(Tasks.TASK.getTaskType())) {
            LocalDateTime dateBuffer;
            if (!taskData[5].equals("NULL")) {
                dateBuffer = LocalDateTime.parse(taskData[5], DATE_TIME_FORMATTER);
            } else {
                dateBuffer = null;
            }
            Task task = new Task(
                    taskData[2],
                    taskData[3],
                    Integer.parseInt(taskData[4]),
                    dateBuffer,
                    Status.valueOf(taskData[6]));
            task.setId(Integer.parseInt(taskData[0]));
            return task;
        } else if (taskData[1].equals(Tasks.EPIC.getTaskType())) {
            Epic epic = new Epic(
                    taskData[2],
                    taskData[3]);
            epic.setId(Integer.parseInt(taskData[0]));
            return epic;
        } else {
            SubTask subTask = new SubTask(
                    taskData[2],
                    taskData[3],
                    Integer.parseInt(taskData[4]),
                    LocalDateTime.parse(taskData[5], DATE_TIME_FORMATTER),
                    Status.valueOf(taskData[6]),
                    Integer.parseInt(taskData[7]));
            subTask.setId(Integer.parseInt(taskData[0]));
            return subTask;
        }
    }

    private List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(getTaskAll());
        tasks.addAll(getEpicAll());
        tasks.addAll(getSubTaskAll());
        return tasks;
    }

    private void save() {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,description,duration,startTime,status,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(String.join(",", toString(task)));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в файле" + file.getAbsolutePath());

        }
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask newSubTsk = super.createSubTask(subTask);
        save();
        return newSubTsk;
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void updateSubTasks(SubTask updatedSubTask) {
        super.updateSubTasks(updatedSubTask);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }
}
