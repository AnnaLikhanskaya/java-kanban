package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
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

}
