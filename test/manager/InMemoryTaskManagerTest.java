package manager;

import enumeration.StatusOfTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

class InMemoryTaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    public void init() {
        taskManager = Managers.getDefault();
    }

    @Test
    void createTask() {
        String name = "Уборка";
        String description = "Помыть пол";
        StatusOfTask status = StatusOfTask.NEW;
        Task task = new Task(name, description, status);

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
        Task task = new Task(name, description, status);

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
        Integer epicId = 1;
        Subtask subtask = new Subtask(name, description, status, epicId);

        taskManager.createSubtask(subtask);

        Subtask actualSubtask = taskManager.findSubtasklById(subtask.getId());
        Assertions.assertNotNull(actualSubtask.getId());
        Assertions.assertEquals(actualSubtask.getName(), name);
        Assertions.assertEquals(actualSubtask.getDescription(), description);
    }


}