package manager;

import enumeration.StatusOfTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    abstract T getTaskManager();

    @Test
    void createTask() {
        String name = "Уборка";
        String description = "Помыть пол";
        StatusOfTask status = StatusOfTask.NEW;
        LocalDateTime startTime = LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(13,0));
        Duration duration = Duration.ofMinutes(15);
        Task task = new Task(name, description, status, startTime, duration);
        taskManager.createTask(task);

        Task actualTask = taskManager.findTasklById(task.getId());
        Assertions.assertNotNull(actualTask.getId());
        Assertions.assertEquals(actualTask.getName(), name);
        Assertions.assertEquals(actualTask.getDescription(), description);
        Assertions.assertEquals(actualTask.getStatus(), status);
    }

    @Test
    void taskInHistoryListShouldNotBeUpdated () {
        String name = "Уборка";
        String description = "Помыть пол";
        StatusOfTask status = StatusOfTask.NEW;
        LocalDateTime startTime = LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(13,0));
        Duration duration = Duration.ofMinutes(15);
        Task task = new Task(name, description, status, startTime, duration);

        taskManager.createTask(task);
        taskManager.findTasklById(task.getId());
        Task taskInHistory = taskManager.getHistory().get(0);

        StatusOfTask statusInHistoryBeforeUpdate = taskInHistory.getStatus();

        task.setStatus(StatusOfTask.IN_PROGRESS);
        taskManager.updateTask(task);

        Task taskInHistoryAfterUpdate = taskManager.getHistory().get(0);
        Assertions.assertEquals(statusInHistoryBeforeUpdate, taskInHistoryAfterUpdate.getStatus());
    }

    @Test
    void createEpic() {
        String name = "Уборка";
        String description = "Помыть пол";
        Epic epic = new Epic(name, description);

        taskManager.createEpic(epic);

        Epic actualEpic = taskManager.findEpiclById(epic.getId());
        Assertions.assertNotNull(actualEpic.getId());
        Assertions.assertEquals(actualEpic.getName(), name);
        Assertions.assertEquals(actualEpic.getDescription(), description);
    }

    @Test
    void createSubtask() {
        Epic epic = new Epic("Уборка", "Помыть пол");
        taskManager.createEpic(epic);

        String name = "Подготовить воду";
        String description = "Развести моющее средство в ведре";
        StatusOfTask status = StatusOfTask.NEW;
        LocalDateTime startTime = LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(13,0));
        Duration duration = Duration.ofMinutes(15);
        Integer epicId = 1;
        Subtask subtask = new Subtask(name, description, status, startTime, duration, epicId);

        taskManager.createSubtask(subtask);

        Subtask actualSubtask = taskManager.findSubtasklById(subtask.getId());
        Assertions.assertNotNull(actualSubtask.getId());
        Assertions.assertEquals(actualSubtask.getName(), name);
        Assertions.assertEquals(actualSubtask.getDescription(), description);
    }

    @Test
    void testHitorySize() {
        Task task1 = new Task(
                "Тренировка",
                "Сходить в тренажерный зал",
                StatusOfTask.NEW,
                LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(13,0)),
                Duration.ofMinutes(15)
        );
        taskManager.createTask(task1);
        Task task2 = new Task(
                "Проект",
                "Выполнить рабочий проект",
                StatusOfTask.NEW,
                LocalDateTime.of(LocalDate.of(2025, 3, 10), LocalTime.of(14,0)),
                Duration.ofMinutes(50)
        );
        taskManager.createTask(task2);

        Epic epic1 = new Epic(
            "Уроки",
            "Сделать уроки");
        taskManager.createEpic(epic1);

        Subtask subtask11 = new Subtask(
                "Математика",
                "Сделать математику",
                StatusOfTask.NEW,
                LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(15,0)),
                Duration.ofMinutes(30),
                epic1.getId());
        taskManager.createSubtask(subtask11);
        Subtask subtask12 = new Subtask(
                "Русский язык",
                "Сделать русский язык",
                StatusOfTask.NEW,
                LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(16,0)),
                Duration.ofMinutes(20),
                epic1.getId());
        taskManager.createSubtask(subtask12);

        taskManager.findTasklById(1);
        taskManager.findTasklById(2);
        taskManager.findEpiclById(3);
        taskManager.findSubtasklById(4);
        taskManager.findSubtasklById(5);
        taskManager.findSubtasklById(4);
        taskManager.findTasklById(2);
        int historySize1 = 5;
        Assertions.assertEquals(taskManager.getHistory().size(), historySize1);

        taskManager.deleteTask(1);
        taskManager.deleteEpic(3);
        int historySize2 = 1;
        Assertions.assertEquals(taskManager.getHistory().size(), historySize2);
    }

    @Test
    void testGetHitory(){
        Task task1 = new Task(
                "Тренировка",
                "Сходить в тренажерный зал",
                StatusOfTask.NEW,
                LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(13,0)),
                Duration.ofMinutes(15)
        );
        taskManager.createTask(task1);
        Task task2 = new Task(
                "Проект",
                "Выполнить рабочий проект",
                StatusOfTask.NEW,
                LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(14,0)),
                Duration.ofMinutes(15)
        );
        taskManager.createTask(task2);

        Epic epic1 = new Epic(
                "Уроки",
                "Сделать уроки",
                LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(14,0)),
                Duration.ofMinutes(15)
        );
        taskManager.createEpic(epic1);

        taskManager.findTasklById(1);
        taskManager.findTasklById(2);
        taskManager.findEpiclById(3);

        String expected = "[Task{id=1, name='Тренировка', description='Сходить в тренажерный зал', status=NEW, startTime=13:00:00/09.03.2025, endTime=13:15:00/09.03.2025, duration=15}, Task{id=2, name='Проект', description='Выполнить рабочий проект', status=NEW, startTime=14:00:00/09.03.2025, endTime=14:15:00/09.03.2025, duration=15}, epics.Epic{id=3, name='Уроки', description='Сделать уроки', status=NEW, startTime=14:00:00/09.03.2025, endTime=14:15:00/09.03.2025, duration=15}]";
        String actually = taskManager.getHistory().toString();
        Assertions.assertEquals(expected, actually);
    }
}
