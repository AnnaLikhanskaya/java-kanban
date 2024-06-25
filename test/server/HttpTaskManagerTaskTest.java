package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import httpServer.HttpTaskServer;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTaskTest {

    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.gson;

    private HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void setUp() {
        taskManager.deleteAllSubTask();
        taskManager.deleteAllEpic();
        taskManager.deleteAllTask();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void shouldAddTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");

        Task task = new Task("Название", "Описание", 4, LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest
                .newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTaskAll();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(task.getName(), tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldGetTasks() throws IOException, InterruptedException {
        URI urlTask = URI.create("http://localhost:8080/tasks");

        taskManager.createTask(new Task("Название 1", "Описание 1", 4,
                LocalDateTime.now()));
        taskManager.createTask(new Task("Название 2", "Описание 2", 4,
                LocalDateTime.now().plusHours(1)));

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(urlTask)
                .GET()
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> taskFromHttp = gson.fromJson(response.body().toString(), new TypeToken<List<Task>>() {
        }.getType());
        List<Task> taskFromManager = taskManager.getTaskAll();

        assertEquals(200, response.statusCode());
        assertNotNull(taskFromHttp, "Задачи не возвращаются");
        boolean isListsEquals = taskFromHttp.containsAll(taskFromManager) && taskFromManager.containsAll(taskFromHttp);
        assertTrue(isListsEquals, "Возвращается не правильный список задач");
    }

    @Test
    public void shouldGetEpics() throws IOException, InterruptedException {
        URI urlEpic = URI.create("http://localhost:8080/epics");

        taskManager.createEpic(new Epic("Название эпика 1", "Описание эпика"));
        taskManager.createEpic(new Epic("Название эпика 2", "Описание эпика"));

        HttpRequest request = HttpRequest
                .newBuilder().uri(urlEpic)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicFromHttp = gson.fromJson(response.body().toString(),
                new TypeToken<List<Epic>>() {
                }.getType());
        List<Epic> epicFromManager = taskManager.getEpicAll();

        assertEquals(200, response.statusCode());
        assertNotNull(epicFromHttp, "Задачи не возвращаются");
        boolean isListsEquals = epicFromHttp.containsAll(epicFromManager) && epicFromManager.containsAll(epicFromHttp);
        assertTrue(isListsEquals, "Возвращается неправильный список эпиков");
    }

    @Test
    public void shouldGetSubtasks() throws IOException, InterruptedException {
        URI urlSubtasks = URI.create("http://localhost:8080/subtasks");

        Epic epic = new Epic("Название эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask("Название1", "Описание", 4, LocalDateTime.now().plusHours(2),
                Status.NEW, savedEpic.getId());
        SubTask subTask2 = new SubTask("Название2", "Описание", 4, LocalDateTime.now().plusHours(3),
                Status.NEW, savedEpic.getId());
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        HttpRequest request = HttpRequest
                .newBuilder().uri(urlSubtasks)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<SubTask> subtasksFromManager = taskManager.getSubTaskAll();
        List<SubTask> subtasksFromHttp = gson.fromJson(response.body().toString(),
                new TypeToken<List<SubTask>>() {
                }.getType());

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        boolean isListsEquals = subtasksFromManager.containsAll(subtasksFromHttp) && subtasksFromHttp.containsAll(subtasksFromManager);
        assertTrue(isListsEquals, "Возвращается не правильный список сабтасок");


    }

    @Test
    public void shouldDeleteTasks() throws IOException, InterruptedException {
        Task task = new Task("Название", "Описание", 4, LocalDateTime.now());
        int taskId = taskManager.createTask(task).getId();

        URI urlTask = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request =
                HttpRequest.newBuilder().uri(urlTask)
                        .DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getTaskAll().size(), 0, "Не удалилась");
    }


    @Test
    public void shouldDeleteEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Название эпика", "Описание эпика");
        int epicId = taskManager.createEpic(epic).getId();

        URI urlEpic = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest request =
                HttpRequest.newBuilder().uri(urlEpic)
                        .DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getEpicAll().size(), 0, "Не удалилась");
    }

    @Test
    public void shouldDeleteSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Название эпика", "Описание эпика");
        int epicId = taskManager.createEpic(epic).getId();

        SubTask subTask = new SubTask("Название", "Описание", 4, LocalDateTime.now().plusHours(2),
                Status.NEW, epicId);
        int subtaskId = taskManager.createSubTask(subTask).getId();

        URI urlSubtasks = URI.create("http://localhost:8080/subtasks/" + subtaskId);

        HttpRequest request =
                HttpRequest.newBuilder().uri(urlSubtasks)
                        .DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getSubTaskAll().size(), 0, "Не удалилась");
    }

    @Test
    public void shouldGetHistory() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/history");

        Task task = new Task("Название", "Описание", 4, LocalDateTime.now());
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());

        Epic epic = new Epic("Название эпика", "Описание эпика");
        taskManager.createEpic(epic);
        taskManager.getEpicById(epic.getId());
        int epicId = taskManager.createEpic(epic).getId();

        SubTask subTask = new SubTask("Название", "Описание", 4, LocalDateTime.now().plusHours(1),
                Status.NEW, epicId);
        taskManager.createSubTask(subTask);
        taskManager.getSubTaskById(subTask.getId());


        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> actual = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertNotNull(actual, "Пустой список");
        assertEquals(3, actual.size(), "Не корректное количество");
    }

    @Test
    public void shouldAddEpicAndSubtask() throws IOException, InterruptedException {
        URI urlEpic = URI.create("http://localhost:8080/epics");
        URI urlSubTask = URI.create("http://localhost:8080/subtasks");

        Epic epic = new Epic("Название эпика", "Описание эпика");
        String epicJson = gson.toJson(epic);
        HttpRequest epicRequest = HttpRequest.newBuilder()
                .uri(urlEpic)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, epicResponse.statusCode());
        String str = epicResponse.body().toString();
        Epic epicFromHttp = gson.fromJson(str, new TypeToken<Epic>() {
        }.getType());
        int epicId = epicFromHttp.getId();

        SubTask subTask1 = new SubTask("Название", "Описание", 4, LocalDateTime.now().plusHours(1),
                Status.NEW, epicId);
        String subtaskJson1 = gson.toJson(subTask1);

        SubTask subTask2 = new SubTask("Название", "Описание", 4, LocalDateTime.now().plusHours(2),
                Status.NEW, epicId);
        String subtaskJson2 = gson.toJson(subTask2);


        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(urlSubTask)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson1))
                .build();

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(urlSubTask)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson2))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response3.statusCode());

        List<Epic> epicFromManager = taskManager.getEpicAll();
        List<SubTask> subtaskFromManager = taskManager.getSubTaskAll();

        assertNotNull(epicFromManager, "Нет эпиков");
        assertNotNull(subtaskFromManager, "Нет сабтасок");

        assertEquals(1, epicFromManager.size(), "Не корректное количество эпиков");
        assertEquals(2, subtaskFromManager.size(), "Не корректное количество сабтасок");
    }

    @Test
    public void shouldGetPrioritized() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET().build();

        Task task = new Task("Название", "Описание", 4, LocalDateTime.now());
        int taskId = taskManager.createTask(task).getId();

        Epic epic = new Epic("Название эпика", "Описание эпика");
        taskManager.createEpic(epic);
        int epicId = taskManager.createEpic(epic).getId();

        SubTask subTask = new SubTask("Название", "Описание", 4, LocalDateTime.now().minusHours(6),
                Status.NEW, epicId);
        taskManager.createSubTask(subTask);
        int subtaskId = taskManager.getSubTaskById(subTask.getId()).getId();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> listFromResponse = gson.fromJson(response.body(), taskType);

        assertNotNull(listFromResponse, "Пусто");
        assertEquals(2, listFromResponse.size(), "Не корректное количество");
        boolean isSubtaskFirst = listFromResponse.get(0).getId() == subtaskId;
        boolean isTaskSecond = listFromResponse.get(1).getId() == taskId;
        assertTrue(isTaskSecond && isSubtaskFirst, "Неправильный порядок");
    }

}