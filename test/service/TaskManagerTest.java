package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected TaskManager taskManager;

    @BeforeEach
    abstract void beforeEach();


    @DisplayName("Статус NEW если нет подзадач")
    @Test
    void StatusShouldBeNewWhenEpicNoSubTask() {
        Epic epic = new Epic("Название", "Описание");
        int epicId = taskManager.createEpic(epic).getId();
        Status epicStatus = taskManager.getEpicById(epicId).getStatus();
        assertEquals(epicStatus, Status.NEW, "Не соответсвует");
    }

    @DisplayName("Статус NEW если все подзадачи тоже NEW")
    @Test
    void StatusShouldNewWhenSubTaskStatusNew() {
        Epic epic = new Epic("Название", "Описание");
        int epicId = taskManager.createEpic(epic).getId();
        SubTask subTask1 = new SubTask("Название", "Описание",
                1,
                LocalDateTime.of(2020, 1, 2, 3, 4),
                Status.NEW,
                epicId);
        SubTask subTask2 = new SubTask("Название", "Описание",
                1,
                LocalDateTime.of(2020, 1, 2, 3, 4),
                Status.NEW,
                epicId);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        Status epicStatus = taskManager.getEpicById(epicId).getStatus();
        assertEquals(epicStatus, Status.NEW, "Не соответсвует");
    }

    @DisplayName("Статус DONE если все подзадачи тоже DONE")
    @Test
    void StatusShouldNewWhenSubTaskStatusDone() {
        Epic epic = new Epic("Название", "Описание");
        int epicId = taskManager.createEpic(epic).getId();
        SubTask subTask1 = new SubTask("Название", "Описание",
                1,
                LocalDateTime.of(2020, 1, 2, 3, 4),
                Status.DONE,
                epicId);
        SubTask subTask2 = new SubTask("Название", "Описание",
                1,
                LocalDateTime.of(2020, 1, 2, 3, 4),
                Status.DONE,
                epicId);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        Status epicStatus = taskManager.getEpicById(epicId).getStatus();
        assertEquals(epicStatus, Status.DONE, "Не соответсвует");
    }

    @DisplayName("Статус InProgress если все подзадачи тоже InProgress")
    @Test
    void StatusShouldNewWhenSubTaskStatusInProgress() {
        Epic epic = new Epic("Название", "Описание");
        int epicId = taskManager.createEpic(epic).getId();
        SubTask subTask1 = new SubTask("Название", "Описание",
                1,
                LocalDateTime.of(2020, 1, 2, 3, 4),
                Status.IN_PROGRESS,
                epicId);
        SubTask subTask2 = new SubTask("Название", "Описание",
                1,
                LocalDateTime.of(2020, 1, 2, 3, 4),
                Status.IN_PROGRESS,
                epicId);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        Status epicStatus = taskManager.getEpicById(epicId).getStatus();
        assertEquals(epicStatus, Status.IN_PROGRESS, "Не соответсвует");
    }

    @DisplayName("Статусы New и Done")
    @Test
    void StatusShouldWhenSubTaskStatusNewAndDone() {
        Epic epic = new Epic("Название", "Описание");
        int epicId = taskManager.createEpic(epic).getId();
        SubTask subTask1 = new SubTask("Название", "Описание",
                1,
                LocalDateTime.of(2020, 1, 2, 3, 4),
                Status.NEW,
                epicId);
        SubTask subTask2 = new SubTask("Название", "Описание",
                1,
                LocalDateTime.of(2020, 1, 2, 3, 4),
                Status.DONE,
                epicId);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        Status epicStatus = taskManager.getEpicById(epicId).getStatus();

        assertEquals(epicStatus, Status.IN_PROGRESS, "Не соответсвует");
    }

    @DisplayName("Задача должна сохраниться в TaskManager")
    @Test
    void TaskShouldBeSaved() {
        Task task = new Task("Название", "Описание",
                10,
                LocalDateTime.of(2022, 10, 11, 18, 17));
        int taskId = taskManager.createTask(task).getId();
        Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        List<Task> tasks = taskManager.getTaskAll();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @DisplayName("Должен обновить SubTask")
    @Test
    void ShouldUpdateSubTasks() {
        Epic epicWithSubtask = new Epic("Название", "Описание");
        int epicId = taskManager.createEpic(epicWithSubtask).getId();
        SubTask subtask = new SubTask("Название", "Описание", 1,
                LocalDateTime.of(2020, 1, 2, 3, 4),
                Status.NEW,
                epicId);
        int newId = taskManager.createSubTask(subtask).getId();
        subtask.setName("Новое название");
        taskManager.updateSubTasks(subtask);
        SubTask savedSubtask = taskManager.getSubTaskById(newId);
        assertEquals(savedSubtask, subtask, "Не обновлена ");
    }

    @DisplayName("Следует проверить правильность расчета пересечения интервалов")
    @Test
    public void getPrioritizedTasks() {
        final List<Task> emptyTasks = taskManager.getPrioritizedTasks();
        Epic epicWithSubtask = new Epic("Название", "Описание");
        int epicId = taskManager.createEpic(epicWithSubtask).getId();
        SubTask subtask2 = new SubTask("a", "b", 2, LocalDateTime.of(2020, 1, 1, 1, 0), Status.NEW, epicId);
        SubTask subtask3 = new SubTask("a", "b", 3, LocalDateTime.of(2020, 1, 1, 3, 0), Status.IN_PROGRESS, epicId);
        SubTask subtask1 = new SubTask("a", "b", 4, LocalDateTime.of(2020, 1, 1, 2, 0), Status.NEW, epicId);
        Task task = new Task("Test addNewTask", "Test addNewTask description", 1, LocalDateTime.of(2021, 1, 1, 0, 0), Status.NEW);

        taskManager.createTask(task);
        taskManager.createEpic(epicWithSubtask);
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);

        final List<Task> unsortedTasks = new ArrayList<>();
        unsortedTasks.addAll(taskManager.getTaskAll());
        unsortedTasks.addAll(taskManager.getSubTaskAll());


        final List<Task> sortedTasks = taskManager.getPrioritizedTasks();

        assertAll(
                () -> assertEquals(0, emptyTasks.size()),
                () -> assertNotEquals(unsortedTasks, sortedTasks, "very strange things")
        );
    }

}

