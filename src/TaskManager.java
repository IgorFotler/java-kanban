import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private Integer numberOfId = 0;

    //Task:_____________________________________________________________________________________________________________

    public Task createTask(Task newTask) {
        numberOfId++;
        newTask.setId(numberOfId);
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    public boolean updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            return true;
        }
        return false;
    }

    public Task findTasklById(Integer id) {
        return tasks.get(id);
    }

    public void deleteTask(Integer id) {
        if(tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    //Epic:_____________________________________________________________________________________________________________

    public Epic createEpic(Epic newEpic) {
        numberOfId++;
        newEpic.setId(numberOfId);
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    public boolean updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic upEpic = epics.get(epic.getId());
            upEpic.setName(epic.getName());
            upEpic.setDescription(epic.getDescription());
            return true;
        }
        return false;
    }

    private void upEpicStatus(Epic epic) {
        if(epic.getSubtasksId().isEmpty()) {
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

    public Epic findEpiclById(Integer id) {
        return epics.get(id);
    }

    public void deleteEpic(Integer id) {

        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (int i = 0; i < epic.getSubtasksId().size(); i++) {
                subtasks.remove(epic.getSubtasksId().get(i));
            }
            epics.remove(id);
        }
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    //Subtask:__________________________________________________________________________________________________________

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

    public boolean updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            if(subtask.getEpicId() == subtasks.get(subtask.getId()).getEpicId()) {
                subtasks.put(subtask.getId(), subtask);
                upEpicStatus(epics.get(subtask.getEpicId()));
                return true;
            }
        }
        return false;
    }

    public Subtask findSubtasklById(Integer id) {
        return subtasks.get(id);
    }

    public void deleteSubtask(Integer id) {
        if(subtasks.containsKey(id)) {
            subtasks.remove(id);
            epics.get(subtasks.get(id).getEpicId()).deleteSubtaskId(id);
            upEpicStatus(epics.get(subtasks.get(id).getEpicId()));
        }
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getAllSubtasksOfEpic(int epicId) {
        if(epics.containsKey(epicId)) {
            ArrayList<Subtask> epicSubtasks = new ArrayList<>();
            for (int i = 0; i < epics.get(epicId).getSubtasksId().size(); i++) {
                epicSubtasks.add(subtasks.get(epics.get(epicId).getSubtasksId().get(i)));
            }
            return epicSubtasks;
        }
        return null;
    }

    public void deleteAllSubtasks() {
        for (int i = 0; i < subtasks.size(); i++) {
            epics.get(subtasks.get(i).getEpicId()).deleteAllSubtasksId();
            upEpicStatus(epics.get(subtasks.get(i).getEpicId()));
        }
        subtasks.clear();
    }
}
//Еще раз спасибо)))

