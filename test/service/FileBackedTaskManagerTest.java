package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File file;


    @BeforeEach
    void beforeEach() {
        try {
            file = File.createTempFile("tests", ".csv");
            taskManager = FileBackedTaskManager.loadFromFile(file);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл");
        }
    }


    @DisplayName("Должен создать Task из файла")
    @Test
    public void shouldCreateTaskFromFileTest() throws IOException {
        taskManager.createTask(new Task("Новая задача",
                "Попробовать справиться с финальным заданием",
                10,
                LocalDateTime.of(2022, 11, 10, 9, 8),
                Status.NEW));
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 2);

    }

    @DisplayName("Должен создать Epic из файла")
    @Test
    public void shouldCreateEpicFromFileTest() throws IOException {
        taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 2);

    }

    @DisplayName("Должен создать SubTask из файла")
    @Test
    public void shouldCreateSubTaskFromFileTest() throws IOException {
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));
        taskManager.createSubTask(new SubTask("Новая подзадача-1", "Начать писать код",
                12,
                LocalDateTime.of(2022, 1, 2, 3, 4),
                Status.NEW,
                1));

        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 3);

    }

    @DisplayName("Должен обновить Task из файла")
    @Test
    public void shouldUpdateTaskFromFile() throws IOException {
        Task task = taskManager.createTask(new Task("Новая задача",
                "Попробовать справиться с финальным заданием",
                10,
                LocalDateTime.of(2022, 11, 10, 9, 8),
                Status.NEW));
        Task updatedTask = new Task("Изменённая задача-1",
                " Мозги ушли погулять", 10,
                LocalDateTime.of(2022, 11, 10, 9, 8));
        updatedTask.setId(task.getId());
        taskManager.updateTask(updatedTask);
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 2);

        String result = "id,type,name,description,duration,startTime,status,epic\n"
                + updatedTask.getId() + ",TASK,Изменённая задача-1, Мозги ушли погулять,10,09:08 10.11.2022,NEW,\n";
        assertEquals(fileContents, result);
    }

    @DisplayName("Должен обновить Epic из файла")
    @Test
    public void shouldUpdateEpicFromFile() throws IOException {
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));
        taskManager.updateEpic(new Epic("Изменённый эпик-1", "Поблагодарить ревьюера!"));
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 2);

        String result = "id,type,name,description,duration,startTime,status,epic\n"
                + epic.getId() + ",EPIC,Новый эпик,Смириться и начать думать,0,01:00 01.01.2024,NEW,\n";
        assertEquals(fileContents, result);
    }

    @DisplayName("Должен обновить SubTask из файла")
    @Test
    public void shouldUpdateSubTaskFromFile() throws IOException {
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));
        SubTask subTask = taskManager.createSubTask(new SubTask("Новая подзадача-1", "Начать писать код", 10,
                LocalDateTime.of(2022, 12, 11, 10, 9),
                Status.IN_PROGRESS,
                epic.getId()));
        taskManager.updateSubTasks(new SubTask("Изменённая подзадача-3", "Показалось что снова написала",
                12,
                LocalDateTime.of(2022, 1, 2, 3, 4),
                Status.NEW,
                1));
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 3);

        String result = "id,type,name,description,duration,startTime,status,epic\n" +
                epic.getId() + ",EPIC,Новый эпик,Смириться и начать думать,10,10:09 11.12.2022,IN_PROGRESS,\n" +
                subTask.getId() + ",SUBTASK,Новая подзадача-1,Начать писать код,10,10:09 11.12.2022,IN_PROGRESS," + epic.getId() + "\n";
        assertEquals(fileContents, result);
    }

    @DisplayName("Должен удалить Task по Id из файла")
    @Test
    public void shouldDeleteTaskByIdFromFileTest() throws IOException {
        Task task = taskManager.createTask(new Task("Новая задача", "Попробовать справиться с финальным заданием",
                10,
                LocalDateTime.of(2022, 10, 11, 18, 17)));
        taskManager.deleteTaskById(task.getId());
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 1);

    }

    @DisplayName("Должен удалить Epic по Id из файла")
    @Test
    public void shouldDeleteEpicByIdFromFileTest() throws IOException {
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));
        taskManager.deleteEpicById(epic.getId());
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 1);

    }

    @DisplayName("Должен удалить SubTask по Id из файла")
    @Test
    public void shouldDeleteSubTaskByIdFromFileTest() throws IOException {
        int epicId = taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать")).getId();
        int subTaskId = taskManager.createSubTask(new SubTask("Новая подзадача-1", "Начать писать код",
                1,
                LocalDateTime.of(2020, 1, 2, 3, 4),
                Status.NEW,
                epicId)).getId();
        taskManager.deleteSubTaskById(subTaskId);
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 2);
    }

    @DisplayName("Должен удалить все Task из файла")
    @Test
    public void shouldDeleteAllTaskFromFileTest() throws IOException {
        taskManager.createTask(new Task("Новая задача", "Попробовать справиться с финальным заданием",
                10,
                LocalDateTime.of(2022, 11, 10, 9, 8),
                Status.NEW));
        taskManager.createTask(new Task("Новая задача - 2", "Попробовать справиться с финальным заданием - 2",
                10,
                LocalDateTime.of(2022, 11, 10, 9, 8),
                Status.NEW));
        taskManager.deleteAllTask();
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 1);

    }

    @DisplayName("Должен удалить все Epic из файла")
    @Test
    public void shouldDeleteAllEpicFromFileTest() throws IOException {
        int epicId = taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать")).getId();
        taskManager.createSubTask(new SubTask("Новая подзадача-1", "Начать писать код",
                1,
                LocalDateTime.of(2020, 1, 2, 3, 4),
                Status.NEW,
                epicId));
        taskManager.createEpic(new Epic("Новый эпик - 2", "Смириться и начать думать - 2"));
        taskManager.deleteAllEpic();
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 1);

    }

    @DisplayName("Должен удалить все SubTask из файла")
    @Test
    public void shouldDeleteAllSubTaskFromFileTest() throws IOException {
        int epicId = taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать")).getId();

        taskManager.createSubTask(new SubTask("Новая подзадача - 1", "Начать писать код", 1,
                LocalDateTime.of(2020, 1, 2, 3, 4),
                Status.NEW,
                epicId));
        taskManager.createSubTask(new SubTask("Новая подзадача - 2", "Начать писать код - 2", 1,
                LocalDateTime.of(2020, 1, 2, 3, 4),
                Status.NEW,
                epicId));
        taskManager.deleteAllSubTask();
        String fileContents = Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        String[] line = fileContents.split("\n");
        assertEquals(line.length, 2);
    }

    @AfterEach
    void close() {
        if (!file.delete()) {
            throw new RuntimeException("Временный файл не удален");
        }
    }
}
