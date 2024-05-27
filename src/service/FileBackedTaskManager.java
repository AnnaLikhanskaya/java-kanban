package service;

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
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;

    public static void main(String[] args) {
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(new File("resources/Tasks.csv"));

        Task task1 = taskManager.createTask(new Task("Новая задача", "Попробовать справиться с финальным заданием"));
        Task task2 = taskManager.createTask(new Task("Новая задача-2", "Прочитать ТЗ и уйти в дипрессию"));

        Epic epic1 = taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("Новая подзадача-1",
                "Начать писать код",
                epic1.getId()));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("Новая подзадача-2",
                "Уже реализованы Task и SubTask",
                epic1.getId()));
        Epic epic2 = taskManager.createEpic(new Epic("Новый эпик-2", "Показалось что справилась"));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("Новая подзадача-3", "Написала но снова показалось", epic2.getId()));

        taskManager.updateTask(new Task("Изменённая задача-1", Status.DONE, " Мозги ушли погулять", task1.getId()));
        taskManager.updateSubTasks(new SubTask("Изменённая подзадача-1", "Дело близиться к финалу", Status.IN_PROGRESS, subTask1.getEpicId()));
        taskManager.updateSubTasks(new SubTask("Изменённая подзадача-3", "Показалось что снова написала", Status.DONE, subTask3.getEpicId()));
        taskManager.updateEpic(new Epic("Изменённый эпик-1", "Поблагодарить ревьюера!", epic1.getId()));

        taskManager.deleteTaskById(task2.getId());
        taskManager.deleteSubTaskById(subTask2.getId());
        taskManager.deleteEpicById(epic1.getId());

        taskManager.deleteAllTask();
        taskManager.deleteAllEpic();
        taskManager.deleteAllSubTask();

    }

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            if (file.exists()) {
                String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
                String[] line = fileContents.split("\n");
                if (!line[0].isEmpty()) {
                    manager.readTasks(line, manager);
                }
            }
            return manager;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void readTasks(String[] line, FileBackedTaskManager manager) {
        int maxId = 0;
        for (int i = 1; i < line.length; i++) {
            Task task = manager.fromString(line[i]);
            identifier = task.getId();
            if (identifier > maxId) maxId = identifier;
            if (task instanceof Epic) {
                epicsStorage.put(identifier, (Epic) task);
            } else if (task instanceof SubTask) {
                subTasksStorage.put(identifier, (SubTask) task);
            } else {
                tasksStorage.put(identifier, task);
            }
        }
        identifier = maxId;
    }

    public String[] toString(Task task) {

        String[] dataTask = new String[6];
        dataTask[0] = String.valueOf(task.getId());
        dataTask[1] = Tasks.TASK.getTaskType();
        dataTask[2] = task.getName();
        dataTask[3] = task.getStatus().toString();
        dataTask[4] = task.getDescription();
        dataTask[5] = "\n";

        if (task instanceof Epic) {
            dataTask[1] = Tasks.EPIC.getTaskType();
        } else if (task instanceof SubTask) {
            dataTask[1] = Tasks.SUBTASK.getTaskType();
            dataTask[5] = ((SubTask) task).getEpicId() + "\n";
        }
        return dataTask;
    }

    public Task fromString(String value) {
        String[] taskData = value.split(",");
        if (taskData[1].equals(Tasks.TASK.getTaskType())) {
            return new Task(taskData[2], Status.valueOf(taskData[3]), taskData[4], Integer.parseInt(taskData[0]));
        } else if (taskData[1].equals(Tasks.EPIC.getTaskType())) {
            return new Epic(taskData[2], Status.valueOf(taskData[3]), taskData[4], Integer.parseInt(taskData[0]));
        } else {
            return new SubTask(taskData[2], Status.valueOf(taskData[3]), taskData[4], Integer.parseInt(taskData[0]),
                    Integer.parseInt(taskData[5]));
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
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(String.join(",", toString(task)));
            }
        } catch (IOException e) {
            throw new RuntimeException();

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
