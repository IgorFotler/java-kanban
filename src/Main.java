import enumeration.StatusOfTask;
import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.TaskManager;
import manager.InMemoryTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        HistoryManager historyManager = new InMemoryHistoryManager();

        TaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Тренировка", "Сходить в тренажерный зал", StatusOfTask.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Проект", "Выполнить рабочий проект", StatusOfTask.NEW);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Уроки", "Сделать уроки");
        taskManager.createEpic(epic1);


        Subtask subtask11 = new Subtask("Математика", "Сделать математику", StatusOfTask.NEW, epic1.getId());
        taskManager.createSubtask(subtask11);
        Subtask subtask12 = new Subtask("Русский язык", "Сделать русский язык", StatusOfTask.NEW, epic1.getId());
        taskManager.createSubtask(subtask12);

        Epic epic2 = new Epic("Уборка", "Убраться в квартире");
        taskManager.createEpic(epic2);
        Subtask subtask21 = new Subtask("Посуда", "Помыть посуду", StatusOfTask.NEW, epic2.getId());
        taskManager.createSubtask(subtask21);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllSubtasksOfEpic(epic1.getId()));
        System.out.println("_________________________________________________________________________________________");

        task1.setStatus(StatusOfTask.IN_PROGRESS);
        taskManager.updateTask(task1);

        task2.setStatus(StatusOfTask.DONE);
        taskManager.updateTask(task2);

        subtask11.setStatus(StatusOfTask.IN_PROGRESS);
        taskManager.updateSubtask(subtask11);

        subtask12.setStatus(StatusOfTask.DONE);
        taskManager.updateSubtask(subtask12);

        subtask21.setStatus(StatusOfTask.DONE);
        taskManager.updateSubtask(subtask21);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("_________________________________________________________________________________________");

        taskManager.deleteTask(task2.getId());
        taskManager.deleteEpic(epic1.getId());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        taskManager.findTasklById(1);
        taskManager.findEpiclById(6);
        System.out.println(taskManager.getHistory());

    }
}
