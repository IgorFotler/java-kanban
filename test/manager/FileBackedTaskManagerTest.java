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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class FileBackedTaskManagerTest {

    TaskManager taskManager;
    File file;

    @BeforeEach
    void init() throws IOException {
        file = File.createTempFile("data",".csv");
        taskManager = Managers.getDefaultFileBackedTaskManager(file);
    }

    @Test
    void testLoadFromFile() {

        Task task1 = new Task(
                "Тренировка",
                "Сходить в тренажерный зал",
                StatusOfTask.NEW,
                LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(13,0)),
                Duration.ofMinutes(15));
        taskManager.createTask(task1);
        Task task2 = new Task(
                "Проект",
                "Выполнить рабочий проект",
                StatusOfTask.NEW,
                LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(15,0)), Duration.ofMinutes(45));
        taskManager.createTask(task2);

        Epic epic1 = new Epic(
                "Уроки",
                "Сделать уроки");
        taskManager.createEpic(epic1);
        Subtask subtask11 = new Subtask(
                "Математика",
                "Сделать математику",
                StatusOfTask.NEW,
                LocalDateTime.of(LocalDate.of(2025, 2, 17), LocalTime.of(17,0)),
                Duration.ofMinutes(90),
                epic1.getId());
        taskManager.createSubtask(subtask11);
        Subtask subtask12 = new Subtask(
                "Русский язык",
                "Сделать русский язык",
                StatusOfTask.NEW,
                LocalDateTime.of(LocalDate.of(2025, 2, 20), LocalTime.of(12,0)),
                Duration.ofMinutes(60),
                epic1.getId());
        taskManager.createSubtask(subtask12);

        TaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(file);
        String expected = taskManager.getAllTasks() + " " + taskManager.getAllEpics() + " " + taskManager.getAllSubtasks();
        String actually = loadedTaskManager.getAllTasks() + " " + loadedTaskManager.getAllEpics() + " " + taskManager.getAllSubtasks();
        Assertions.assertEquals(expected, actually);
    }
}
