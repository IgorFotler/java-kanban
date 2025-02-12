package manager;
import enumeration.StatusOfTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();

    protected Integer numberOfId = 0;

    protected HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.returnHistory());
    }

    //Task:_____________________________________________________________________________________________________________

    @Override
    public Task createTask(Task newTask) {
        numberOfId++;
        newTask.setId(numberOfId);
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    @Override
    public boolean updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            return true;
        }
        return false;
    }

    @Override
    public Task findTasklById(Integer id) {
        Task task = tasks.get(id);
        Task taskForHistory = new Task(task.getName(), task.getDescription(), task.getStatus());
        taskForHistory.setId(id);
        historyManager.addToHistory(taskForHistory);
        return tasks.get(id);
    }

    @Override
    public void deleteTask(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (int i = 0; i < tasks.size(); i++) {
            historyManager.remove(tasks.get(i).getId());
        }
        tasks.clear();
    }

    //Epic:_____________________________________________________________________________________________________________

    @Override
    public Epic createEpic(Epic newEpic) {
        numberOfId++;
        newEpic.setId(numberOfId);
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic upEpic = epics.get(epic.getId());
            upEpic.setName(epic.getName());
            upEpic.setDescription(epic.getDescription());
            return true;
        }
        return false;
    }

    @Override
    public void upEpicStatus(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(StatusOfTask.NEW);
            return;
        }

        boolean allTasksIsNew = true;
        boolean allTasksIsDone = true;

        ArrayList<Integer> epicSubTasks = epic.getSubtasksId();

        for (Integer el : epicSubTasks) {
            if (subtasks.get(el).getStatus() != StatusOfTask.NEW) {
                allTasksIsNew = false;
            }

            if (subtasks.get(el).getStatus() != StatusOfTask.DONE) {
                allTasksIsDone = false;
            }
        }

        if (allTasksIsDone) {
            epic.setStatus(StatusOfTask.DONE);
        } else if (allTasksIsNew) {
            epic.setStatus(StatusOfTask.NEW);
        } else {
            epic.setStatus(StatusOfTask.IN_PROGRESS);
        }

    }

    @Override
    public Epic findEpiclById(Integer id) {
        Epic epic = epics.get(id);
        Epic epicForHistory = new Epic(epic.getName(), epic.getDescription());
        epicForHistory.setId(id);
        historyManager.addToHistory(epicForHistory);
        return epics.get(id);
    }

    @Override
    public void deleteEpic(Integer id) {

        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (int i = 0; i < epic.getSubtasksId().size(); i++) {
                subtasks.remove(epic.getSubtasksId().get(i));
                historyManager.remove(epic.getSubtasksId().get(i));
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        for (int i = 0; i < epics.size(); i++) {
            historyManager.remove(epics.get(i).getId());
        }
        epics.clear();
        for (int i = 0; i < subtasks.size(); i++) {
            historyManager.remove(subtasks.get(i).getId());
        }
        subtasks.clear();
    }

    //Subtask:__________________________________________________________________________________________________________

    @Override
    public Subtask createSubtask(Subtask newSubtask) {
        if (epics.containsKey(newSubtask.getEpicId())) {
            numberOfId++;
            newSubtask.setId(numberOfId);
            Epic epic = epics.get(newSubtask.getEpicId());
            epic.addSubTaskId(newSubtask);
            subtasks.put(newSubtask.getId(), newSubtask);
            upEpicStatus(epic);
            return newSubtask;
        }
        return null;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            if (subtask.getEpicId() == subtasks.get(subtask.getId()).getEpicId()) {
                subtasks.put(subtask.getId(), subtask);
                upEpicStatus(epics.get(subtask.getEpicId()));
                return true;
            }
        }
        return false;
    }

    @Override
    public Subtask findSubtasklById(Integer id) {
        Subtask subtask = subtasks.get(id);
        Subtask subtaskForHistory = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId());
        subtaskForHistory.setId(id);
        historyManager.addToHistory(subtaskForHistory);
        return subtasks.get(id);
    }

    @Override
    public void deleteSubtask(Integer id) {
        if (subtasks.containsKey(id)) {
            subtasks.remove(id);
            historyManager.remove(id);
            epics.get(subtasks.get(id).getEpicId()).deleteSubtaskId(id);
            upEpicStatus(epics.get(subtasks.get(id).getEpicId()));
        }
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasksOfEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            ArrayList<Subtask> epicSubtasks = new ArrayList<>();
            for (int i = 0; i < epics.get(epicId).getSubtasksId().size(); i++) {
                epicSubtasks.add(subtasks.get(epics.get(epicId).getSubtasksId().get(i)));
            }
            return epicSubtasks;
        }
        return null;
    }

    @Override
    public void deleteAllSubtasks() {
        for (int i = 0; i < subtasks.size(); i++) {
            epics.get(subtasks.get(i).getEpicId()).deleteAllSubtasksId();
            upEpicStatus(epics.get(subtasks.get(i).getEpicId()));
        }
        for (int i = 0; i < subtasks.size(); i++) {
            historyManager.remove(subtasks.get(i).getId());
        }
        subtasks.clear();
    }
}

