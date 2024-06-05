package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    File file;
    TaskManager taskManager;

    @BeforeEach
    void init() throws IOException {
        file = File.createTempFile("tests", ".csv");
        taskManager = FileBackedTaskManager.loadFromFile(file);
    }

    @Test
    public void shouldCreateTaskFromFileTest() throws IOException {
        taskManager.createTask(new Task("Новая задача", "Попробовать справиться с финальным заданием"));
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 2);

    }

    @Test
    public void shouldCreateEpicFromFileTest() throws IOException {
        taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 2);

    }

    @Test
    public void shouldCreateSubTaskFromFileTest() throws IOException {
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));
        taskManager.createSubTask(new SubTask("Новая подзадача-1", "Начать писать код", epic.getId()));
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 3);

    }

    @Test
    public void shouldUpdateTaskFromFile() throws IOException {
        Task task = taskManager.createTask(new Task("Новая задача", "Попробовать справиться с финальным заданием"));
        taskManager.updateTask(new Task("Изменённая задача-1", Status.DONE, " Мозги ушли погулять", task.getId()));
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 2);

        String result = "id,type,name,status,description,epic\n" + task.getId() + ",TASK,Изменённая задача-1,DONE, Мозги ушли погулять,\n";
        assertEquals(fileContents, result);
    }

    @Test
    public void shouldUpdateEpicFromFile() throws IOException {
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));
        taskManager.updateEpic(new Epic("Изменённый эпик-1", Status.NEW, "Поблагодарить ревьюера!", epic.getId()));
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 2);

        String result = "id,type,name,status,description,epic\n" + epic.getId() + ",EPIC,Изменённый эпик-1,NEW,Поблагодарить ревьюера!,\n";
        assertEquals(fileContents, result);
    }

    @Test
    public void shouldUpdateSubTaskFromFile() throws IOException {
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));
        SubTask subTask = taskManager.createSubTask(new SubTask("Новая подзадача-1", "Начать писать код", epic.getId()));
        taskManager.updateSubTasks(new SubTask("Изменённая подзадача-3", Status.DONE, "Показалось что снова написала", subTask.getId(), epic.getId()));
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 3);

        String result = "id,type,name,status,description,epic\n" +
                epic.getId() + ",EPIC,Новый эпик,DONE,Смириться и начать думать,\n" +
                subTask.getId() + ",SUBTASK,Изменённая подзадача-3,DONE,Показалось что снова написала," + epic.getId() + "\n";
        assertEquals(fileContents, result);
    }

    @Test
    public void shouldDeleteTaskByIdFromFileTest() throws IOException {
        Task task = taskManager.createTask(new Task("Новая задача", "Попробовать справиться с финальным заданием"));
        taskManager.deleteTaskById(task.getId());
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 1);

    }

    @Test
    public void shouldDeleteEpicByIdFromFileTest() throws IOException {
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));
        taskManager.deleteEpicById(epic.getId());
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 1);

    }

    @Test
    public void shouldDeleteSubTaskByIdFromFileTest() throws IOException {
        taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));
        taskManager.createSubTask(new SubTask("Новая подзадача-1", "Начать писать код", 1));
        taskManager.deleteSubTaskById(2);
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 2);
    }

    @Test
    public void shouldDeleteAllTaskFromFileTest() throws IOException {
        taskManager.createTask(new Task("Новая задача", "Попробовать справиться с финальным заданием"));
        taskManager.createTask(new Task("Новая задача - 2", "Попробовать справиться с финальным заданием - 2"));
        taskManager.deleteAllTask();
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 1);

    }

    @Test
    public void shouldDeleteAllEpicFromFileTest() throws IOException {
        taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));
        taskManager.createSubTask(new SubTask("Новая подзадача-1", "Начать писать код", 1));
        taskManager.createEpic(new Epic("Новый эпик - 2", "Смириться и начать думать - 2"));
        taskManager.deleteAllEpic();
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 1);

    }

    @Test
    public void shouldDeleteAllSubTaskFromFileTest() throws IOException {
        taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));
        taskManager.createSubTask(new SubTask("Новая подзадача - 1", "Начать писать код", 1));
        taskManager.createSubTask(new SubTask("Новая подзадача - 2", "Начать писать код - 2", 1));
        taskManager.deleteAllSubTask();
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 2);
    }

    @AfterEach
    void close() {
        taskManager.deleteAllTask();
        taskManager.deleteAllEpic();
    }
}
