package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    Task createTask(Task newTask);

    boolean updateTask(Task task);

    Task findTasklById(Integer id);

    void deleteTask(Integer id);

    List<Task> getAllTasks();

    void deleteAllTasks();

    Epic createEpic(Epic newEpic);

    boolean updateEpic(Epic epic);

    void upEpicStatus(Epic epic);

    Epic findEpiclById(Integer id);

    void deleteEpic(Integer id);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    Subtask createSubtask(Subtask newSubtask);

    boolean updateSubtask(Subtask subtask);

    Subtask findSubtasklById(Integer id);

    void deleteSubtask(Integer id);

    List<Subtask> getAllSubtasks();

    List<Subtask> getAllSubtasksOfEpic(int epicId);

    void deleteAllSubtasks();

    List<Task> getHistory();
}
