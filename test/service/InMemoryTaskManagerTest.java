package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;


    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();

    }

    @DisplayName("Задачи должны быть равны ")
    @Test
    void tasksShouldBeEqualsTest() {
        Task task = new Task("Название", "Описание");
        int taskId = taskManager.createTask(task).getId();
        Task saved1 = taskManager.getTaskById(taskId);
        Task saved2 = taskManager.getTaskById(taskId);
        assertEquals(saved1, saved2);
    }

    @DisplayName("Подзадача должна быть равной тесту")
    @Test
    void subTaskShouldBeEqualsTest() {
        SubTask subTask = new SubTask("Название", "Описание", 1);
        int subTaskId = taskManager.createTask(subTask).getId();
        SubTask saved1 = taskManager.getSubTaskById(subTaskId);
        SubTask saved2 = taskManager.getSubTaskById(subTaskId);
        assertEquals(saved1, saved2);
    }

    @DisplayName("Эпик должен быть равным тесту")
    @Test
    void epicShouldBeEqualsTest() {
        Epic epic = new Epic("Название", "Описание");
        int epicId = taskManager.createTask(epic).getId();
        Epic saved1 = taskManager.getEpicById(epicId);
        Epic saved2 = taskManager.getEpicById(epicId);
        assertEquals(saved1, saved2);
    }

    @DisplayName("ID эпика не должен быть равен собственному ID ")
    @Test
    void subtaskEpicIdShouldNotEqualsSelfId() {
        Epic epic = new Epic("Название", "Описание");
        int epicId = taskManager.createEpic(epic).getId();
        SubTask subTask = new SubTask("Название", "Описание", epicId);
        int subtaskId = taskManager.createSubTask(subTask).getId();
        SubTask changedTask = new SubTask("Название новое", "Описание", subtaskId);
        changedTask.setId(subtaskId);
        taskManager.updateSubTasks(changedTask);
        SubTask subtaskAfterUpdate = taskManager.getSubTaskById(subtaskId);
        assertNotEquals(changedTask, subtaskAfterUpdate);
    }


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
        SubTask subTask1 = new SubTask("Название", "Описание", epicId);
        SubTask subTask2 = new SubTask("Название", "Описание", epicId);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        Status epicStatus = taskManager.getEpicById(epicId).getStatus();
        assertEquals(epicStatus, Status.NEW, "Не соответсвует");
    }


    @DisplayName("Задача должна сохраниться в TaskManager")
    @Test
    void TaksShouldBeSaved() {
        Task task = new Task("Название", NEW, "Описание");
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
    void shouldUpdateSubTasks() {
        Epic epicWithSubtask = new Epic("Название", "Описание");
        taskManager.createEpic(epicWithSubtask);
        SubTask subtask = new SubTask("Название", "Описание", epicWithSubtask.getId());
        int newId = taskManager.createSubTask(subtask).getId();
        subtask.setName("Новое название");
        taskManager.updateSubTasks(subtask);
        SubTask savedSubtask = taskManager.getSubTaskById(newId);
        assertEquals(savedSubtask, subtask, "Не обновлена ");

    }

}