package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        manager.addHistory(new Task("Придти домой", "Сдать ФЗ"));
        manager.addHistory(new Task("Сходить в магазин", "Купить молоко"));

        manager.addHistory(new Epic("Прочитать ТЗ", "Приступить к выполнению", 1));
        manager.addHistory(new SubTask("Заказать продукты на дом", "Получить заказ", 1));

        manager.addHistory(new Epic("Приготовить ужин", "Поужинать", 2));
        manager.addHistory(new SubTask("Вернуться к обучению", "Приступить к написанию тестов", 2));

        assertEquals(manager.getHistory().size(), 6, "История не сохранена");

    }

    @DisplayName("Проверяет добавление в историю")
    @Test
    void shouldTaskBeAddedToHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Название", Status.NEW, "Описание", 1);
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
        Task task = new Task("Название", Status.NEW, "Описание", 1);
        historyManager.addHistory(task);
        historyManager.remove(1);
        final List<Task> tasks1 = historyManager.getHistory();

        assertTrue(tasks1.isEmpty());
    }

    @DisplayName("Проверяет заполнение истории")
    @Test
    void shouldHistoryBeFilled() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Название", Status.NEW, "Описание", 1);
        historyManager.addHistory(task);
        final List<Task> tasks1 = historyManager.getHistory();

        assertFalse(tasks1.isEmpty());
    }

}
