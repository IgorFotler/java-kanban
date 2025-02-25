package manager;
import enumeration.StatusOfTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();

    protected Integer numberOfId = 0;

    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    protected HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.returnHistory());
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new LinkedHashSet<>(prioritizedTasks);
    }

    private boolean hasInteractions(Task task) {
        return getPrioritizedTasks().stream()
                .anyMatch(t -> ((t.getStartTime().isBefore(task.getStartTime()) && (t.getEndTime().isAfter(task.getStartTime())))) ||
                        (t.getStartTime().isBefore(task.getEndTime()) && (t.getEndTime().isAfter(task.getEndTime()))) ||
                        (t.getStartTime().isBefore(task.getStartTime()) && (t.getEndTime().isAfter(task.getEndTime()))) ||
                        (t.getStartTime().isAfter(task.getStartTime()) && (t.getEndTime().isBefore(task.getEndTime()))) ||
                        (t.getStartTime().equals(task.getStartTime())));
    }

    //Task:_____________________________________________________________________________________________________________

    @Override
    public void createTask(Task newTask) {
        if (!hasInteractions(newTask)) {
            numberOfId++;
            newTask.setId(numberOfId);
            tasks.put(newTask.getId(), newTask);
            prioritizedTasks.add(newTask);
        }
    }

    @Override
    public boolean updateTask(Task task) {
        if (tasks.containsKey(task.getId()) && !hasInteractions(task)) {
            tasks.put(task.getId(), task);
            prioritizedTasks.remove(tasks.get(task.getId()));
            prioritizedTasks.add(task);
            return true;
        }
        return false;
    }

    @Override
    public Task findTasklById(Integer id) {
        Task task = tasks.get(id);
        Task taskForHistory = new Task(task.getName(), task.getDescription(), task.getStatus(), task.getStartTime(), task.getDuration());
        taskForHistory.setId(id);
        historyManager.addToHistory(taskForHistory);
        return tasks.get(id);
    }

    @Override
    public void deleteTask(Integer id) {
        if (tasks.containsKey(id)) {
            prioritizedTasks.remove(tasks.get(id));
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
        tasks.values().forEach(task -> {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        });
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

    public void upEpicTime(Epic epic) {
        LocalDateTime epicStartTime = getEpicStartTime(epic);
        Duration epicDuration = getEpicDuration(epic);
        epic.setStartTime(epicStartTime);
        epic.setDuration(epicDuration);
        epic.setEndTime(epicStartTime.plus(epicDuration));
    }

    public LocalDateTime getEpicStartTime (Epic epic) {
        List<Integer> epicSubtasksId = epic.getSubtasksId();
        List<LocalDateTime> startsTime = epicSubtasksId.stream()
                .map(id -> subtasks.get(id).getStartTime())
                .sorted()
                .collect(Collectors.toList());
        return startsTime.getFirst();
    }

    public Duration getEpicDuration(Epic epic) {
        return epic.getSubtasksId()
                .stream()
                .map(id -> subtasks.get(id).getDuration())
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public Epic findEpiclById(Integer id) {
        Epic epic = epics.get(id);
        Epic epicForHistory = new Epic(epic.getName(), epic.getDescription(), epic.getStartTime(), epic.getDuration());
        epicForHistory.setId(id);
        historyManager.addToHistory(epicForHistory);
        return epics.get(id);
    }

    @Override
    public void deleteEpic(Integer id) {

        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            epic.getSubtasksId().forEach(subtaskId -> {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            });
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
        epics.values().forEach(epic -> {
            historyManager.remove(epic.getId());
        });
        epics.clear();
        subtasks.values().forEach(subtask -> {
            historyManager.remove(subtask.getId());
        });
        subtasks.clear();
    }

    //Subtask:__________________________________________________________________________________________________________

    @Override
    public Subtask createSubtask(Subtask newSubtask) {
        if (epics.containsKey(newSubtask.getEpicId()) && !hasInteractions(newSubtask)) {
            numberOfId++;
            newSubtask.setId(numberOfId);
            Epic epic = epics.get(newSubtask.getEpicId());
            epic.addSubTaskId(newSubtask);
            subtasks.put(newSubtask.getId(), newSubtask);
            prioritizedTasks.add(newSubtask);
            upEpicStatus(epic);
            upEpicTime(epic);
            return newSubtask;
        }
        return null;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) && !hasInteractions(subtask)) {
            if (subtask.getEpicId() == subtasks.get(subtask.getId()).getEpicId()) {
                subtasks.put(subtask.getId(), subtask);
                prioritizedTasks.remove(subtasks.get(subtask.getId()));
                prioritizedTasks.add(subtask);
                upEpicStatus(epics.get(subtask.getEpicId()));
                upEpicTime(epics.get(subtask.getEpicId()));
                return true;
            }
        }
        return false;
    }

    @Override
    public Subtask findSubtasklById(Integer id) {
        Subtask subtask = subtasks.get(id);
        Subtask subtaskForHistory = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getStartTime(), subtask.getDuration(), subtask.getEpicId());
        subtaskForHistory.setId(id);
        historyManager.addToHistory(subtaskForHistory);
        return subtasks.get(id);
    }

    @Override
    public void deleteSubtask(Integer id) {
        if (subtasks.containsKey(id)) {
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            historyManager.remove(id);
            epics.get(subtasks.get(id).getEpicId()).deleteSubtaskId(id);
            upEpicStatus(epics.get(subtasks.get(id).getEpicId()));
            upEpicTime(epics.get(subtasks.get(id).getEpicId()));
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
            epics.get(epicId).getSubtasksId().forEach(subtaskId -> {
                epicSubtasks.add(subtasks.get(subtaskId));
            });
            //for (int i = 0; i < epics.get(epicId).getSubtasksId().size(); i++) {
            //    epicSubtasks.add(subtasks.get(epics.get(epicId).getSubtasksId().get(i)));
            //}
            return epicSubtasks;
        }
        return null;
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.values().forEach(subtask -> {
            epics.get(subtask.getEpicId()).deleteAllSubtasksId();
            upEpicStatus(epics.get(subtask.getEpicId()));
            upEpicTime(epics.get(subtask.getEpicId()));
        });
        subtasks.values().forEach(subtask -> {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        });
        subtasks.clear();
    }
}

