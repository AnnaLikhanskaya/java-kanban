import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = taskManager.createTask(new Task("Новая задача", "Попробовать справиться с финальным заданием"));
        Task task2 = taskManager.createTask(new Task("Новая задача-2", "Прочитать ТЗ и уйти в дипрессию"));

        Epic epic1 = taskManager.createEpic(new Epic("Новый эпик", "Смириться и начать думать"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("Новая подзадача-1", "Начать писать код", epic1.getId()));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("Новая подзадача-2", "Уже реализованы Task и SubTask", epic1.getId()));
        Epic epic2 = taskManager.createEpic(new Epic("Новый эпик-2", "Показалось, что справилась"));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("Новая подзадача-3", "Написала, но снова показалось", epic2.getId()));
        System.out.println(taskManager.getTaskAll());
        System.out.println(taskManager.getEpicAll());
        System.out.println(taskManager.getSubTaskAll());

        taskManager.updateTask(new Task("Изменённая задача-1", Status.DONE, " Мозги ушли погулять", task1.getId()));
        taskManager.updateSubTasks(new SubTask("Изменённая подзадача-1", "Дело близиться к финалу", Status.IN_PROGRESS, subTask1.getEpicId()));
        taskManager.updateSubTasks(new SubTask("Изменённая подзадача-3", "Показалось, что снова написала", Status.DONE, subTask3.getEpicId()));
        taskManager.updateEpics(new Epic("Изменённый эпик-1", "Поблагодарить ревьюера!", epic1.getId()));
        System.out.println();
        System.out.println(taskManager.getTaskAll());
        System.out.println(taskManager.getEpicAll());
        System.out.println(taskManager.getSubTaskAll());

        System.out.println();
        System.out.println(taskManager.getTaskById(task1.getId()));
        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println(taskManager.getSubTaskById(subTask1.getId()));

        System.out.println(taskManager.getSubTasksByEpic(epic1.getId()));

        taskManager.deleteTaskById(task2.getId());
        taskManager.deleteSubTaskById(subTask2.getId());
        taskManager.deleteEpicById(epic1.getId());


        System.out.println();
        System.out.println(taskManager.getTaskAll());
        System.out.println(taskManager.getEpicAll());
        System.out.println(taskManager.getSubTaskAll());



        taskManager.deleteAllTask();
        taskManager.deleteAllEpic();
        taskManager.deleteAllSubTask();

        System.out.println("После удаления");
        System.out.println(taskManager.getTaskAll());
        System.out.println(taskManager.getEpicAll());
        System.out.println(taskManager.getSubTaskAll());



    }
}


