package manager;

import enumeration.StatusOfTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;

public class FileBackedTaskManagerTest {

    TaskManager taskManager;
    File file;

    @BeforeEach
    public void init() throws IOException {
        file = File.createTempFile("data",".csv");
        taskManager = Managers.getDefaultFileBackedTaskManager(file);
    }

    @Test
    void testLoadFromFile() {

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

        TaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(file);
        String expected = taskManager.getAllTasks() + " " + taskManager.getAllEpics() + " " + taskManager.getAllSubtasks();
        String actually = loadedTaskManager.getAllTasks() + " " + loadedTaskManager.getAllEpics() + " " + taskManager.getAllSubtasks();
        Assertions.assertEquals(expected, actually);
    }
}
