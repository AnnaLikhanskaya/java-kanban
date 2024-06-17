package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @DisplayName("Должен удалить ID подзадачи")
    @Test
    void shouldRemoveSubTaskId() {
        Epic epic = new Epic("Название эпика ", "Описание эпика");
        epic.addSubTaskId(1);
        epic.addSubTaskId(2);
        epic.addSubTaskId(3);
        epic.removeSubTaskId(2);
        assertEquals(epic.getSubTasksIds().size(), 2, "Не соответсвует");

    }

    @DisplayName("Должен удалить все подзадачи")
    @Test
    void shouldRemoveAllSubTask() {
        Epic epic = new Epic("Название эпика ", "Описание эпика");
        epic.setId(1);
        epic.addSubTaskId(1);
        epic.addSubTaskId(2);
        epic.removeAllSubTask();
        assertEquals(epic.getSubTasksIds().size(), 0, "Не соответсвует");
    }

    @DisplayName("Должен быть новый статус")
    @Test
    void shouldByNewStatusEpic() {

        Epic epic = new Epic("Название эпика ", "Описание эпика");
        epic.setStatus(Status.NEW);
        assertEquals(Status.NEW, epic.getStatus());
    }


    @DisplayName("Эпик должкн сожержать 2  подзадачи")
    @Test
    void shouldEpicTwoSubTask() {
        Epic epic = new Epic("Название эпика ", "Описание эпика");
        epic.addSubTaskId(1);
        epic.addSubTaskId(2);
        assertEquals(2, epic.getSubTasksIds().size());
    }
}