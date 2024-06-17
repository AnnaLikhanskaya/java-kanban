package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @DisplayName("Должен возвращать рабочий менеджер истории")
    @Test
    void shouldReturnInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }

    @DisplayName("Должен сохранять истории")
    @Test
    void shouldSaveHistory() {

        InMemoryHistoryManager manager = new InMemoryHistoryManager();
        manager.addHistory(new Task("Придти домой", "Сдать ФЗ", 1,
                LocalDateTime.of(2022, 11, 10, 9, 8),
                Status.NEW));
        manager.addHistory(new Task("Сходить в магазин", "Купить молоко",
                2,
                LocalDateTime.of(2022, 11, 10, 9, 8),
                Status.NEW));
        manager.addHistory(new Epic("Прочитать ТЗ", "Приступить к выполнению"));
        SubTask subTask1 = new SubTask("Заказать продукты на дом", "Получить заказ",
                2,
                LocalDateTime.of(2022, 12, 11, 10, 9),
                Status.IN_PROGRESS, 6);
        subTask1.setId(4);
        manager.addHistory(subTask1);
        manager.addHistory(new Epic("Приготовить ужин", "Поужинать"));
        SubTask subTask2 = new SubTask("Заказать продукты на дом", "Получить заказ",
                2,
                LocalDateTime.of(2022, 12, 11, 10, 9),
                Status.IN_PROGRESS, 6);
        subTask1.setId(7);
        manager.addHistory(subTask2);

        assertEquals(manager.getHistory().size(), 2, "История не сохранена");
    }

    @DisplayName("Проверяет добавление в историю")
    @Test
    void shouldTaskBeAddedToHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Название", "Описание",
                4, LocalDateTime.of(2022, 3, 4, 5, 6));

        historyManager.addHistory(task);
        final List<Task> tasks1 = historyManager.getHistory();
        final List<Task> tasks2 = List.of(task);

        assertNotNull(tasks1, "История пустая");
        assertEquals(tasks1, tasks2, "Списки не равны");
    }

    @DisplayName("Проверяет удаление истории")
    @Test
    void shouldTaskBeRemovedFromHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Название", "Описание", 10,
                LocalDateTime.of(2022, 11, 10, 9, 8),
                Status.NEW);
        historyManager.addHistory(task);
        historyManager.remove(task.getId());
        final List<Task> tasks1 = historyManager.getHistory();

        assertTrue(tasks1.isEmpty());
    }

    @DisplayName("Проверяет заполнение истории")
    @Test
    void shouldHistoryBeFilled() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Название", "Описание",
                10,
                LocalDateTime.of(2022, 11, 10, 9, 8),
                Status.NEW);
        historyManager.addHistory(task);
        final List<Task> tasks1 = historyManager.getHistory();

        assertFalse(tasks1.isEmpty());
    }
}
