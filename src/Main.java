import model.Task;
import service.Managers;

public class Main {

    public static void main(String[] args) {
//
//        TaskManager taskManager = Managers.getDefault();
//
//        Task task1 = taskManager.createTask(new Task("Новая задача", "Попробовать справиться с финальным заданием"));
//        Task task2 = taskManager.createTask(new Task("Новая задача-2", "Прочитать ТЗ и уйти в дипрессию"));
//
//        Epic epic1 = taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));
//        SubTask subTask1 = taskManager.createSubTask(new SubTask("Новая подзадача-1", "Начать писать код", epic1.getId()));
//        SubTask subTask2 = taskManager.createSubTask(new SubTask("Новая подзадача-2", "Уже реализованы Task и SubTask", epic1.getId()));
//        Epic epic2 = taskManager.createEpic(new Epic("Новый эпик-2", "Показалось, что справилась"));
//        SubTask subTask3 = taskManager.createSubTask(new SubTask("Новая подзадача-3", "Написала, но снова показалось", epic2.getId()));
//        System.out.println(taskManager.getTaskAll());
//        System.out.println(taskManager.getEpicAll());
//        System.out.println(taskManager.getSubTaskAll());
//
//        taskManager.updateTask(new Task("Изменённая задача-1", Status.DONE, " Мозги ушли погулять", task1.getId()));
//        taskManager.updateSubTasks(new SubTask("Изменённая подзадача-1", "Дело близиться к финалу", Status.IN_PROGRESS, subTask1.getEpicId()));
//        taskManager.updateSubTasks(new SubTask("Изменённая подзадача-3", "Показалось, что снова написала", Status.DONE, subTask3.getEpicId()));
//        taskManager.updateEpic(new Epic("Изменённый эпик-1", "Поблагодарить ревьюера!", epic1.getId()));
//        System.out.println();
//        System.out.println(taskManager.getTaskAll());
//        System.out.println(taskManager.getEpicAll());
//        System.out.println(taskManager.getSubTaskAll());
//
//        System.out.println();
//        System.out.println(taskManager.getTaskById(task1.getId()));
//        System.out.println(taskManager.getEpicById(epic1.getId()));
//        System.out.println(taskManager.getSubTaskById(subTask1.getId()));
//
//        System.out.println(taskManager.getSubTasksByEpic(epic1.getId()));
//
//        taskManager.deleteTaskById(task2.getId());
//        taskManager.deleteSubTaskById(subTask2.getId());
//        taskManager.deleteEpicById(epic1.getId());
//
//
//        System.out.println();
//        System.out.println(taskManager.getTaskAll());
//        System.out.println(taskManager.getEpicAll());
//        System.out.println(taskManager.getSubTaskAll());
//
//
//
//        taskManager.deleteAllTask();
//        taskManager.deleteAllEpic();
//        taskManager.deleteAllSubTask();
//
//        System.out.println("После удаления");
//        System.out.println(taskManager.getTaskAll());
//        System.out.println(taskManager.getEpicAll());
//        System.out.println(taskManager.getSubTaskAll());
//
//        printAllTasks();
//
   }

    private static void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : Managers.getDefault().getTaskAll()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : Managers.getDefault().getEpicAll()) {
            System.out.println(epic);

            for (Task task : Managers.getDefault().getSubTasksByEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : Managers.getDefault().getSubTaskAll()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : Managers.getDefaultHistory().getHistory()) {
            System.out.println(task);
        }
    }
}


