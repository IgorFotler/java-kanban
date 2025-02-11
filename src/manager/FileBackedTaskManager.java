package manager;

import enumeration.StatusOfTask;
import exception.FileManagerFileInitializationException;
import exception.FileManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File data;

    public FileBackedTaskManager(File file) {
        this.data = file;
    }

    @Override
    public Task createTask(Task newTask) {
        Task createdTask = super.createTask(newTask);
        save();
        return createdTask;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean upTask = super.updateTask(task);
        save();
        return upTask;
    }

    @Override
    public void deleteTask(Integer id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Epic createEpic(Epic newEpic) {
        Epic createdEpic = super.createEpic(newEpic);
        save();
        return createdEpic;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        boolean upEpic = super.updateEpic(epic);
        save();
        return upEpic;
    }

    @Override
    public void deleteEpic(Integer id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Subtask createSubtask(Subtask newSubtask) {
        Subtask createdTask = super.createSubtask(newSubtask);
        save();
        return createdTask;
    }

    @Override
    public boolean updateSubtask(Subtask Subtask) {
        boolean upSubtask = super.updateSubtask(Subtask);
        save();
        return upSubtask;
    }

    @Override
    public void deleteSubtask(Integer id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    private void save() {
        List<Task> allTasks = getAllTasks();
        List<Epic> allEpics = getAllEpics();
        List<Subtask> allSubtasks = getAllSubtasks();
        final String title = "id,type,name,status,description,epic\n";
        writeStringToFile(title);
        for (Task task : allTasks) {
            String taskAsString = task.taskToString();
            writeStringToFile(taskAsString);
        }
        for (Epic epic : allEpics) {
            String taskAsString = epic.epicToString();
            writeStringToFile(taskAsString);
        }
        for (Subtask subtask : allSubtasks) {
            String taskAsString = subtask.subtaskToString();
            writeStringToFile(taskAsString);
        }
    }

    private void writeStringToFile(String taskAsString) {
        try (FileWriter fr = new FileWriter(data, true)) {
            fr.write(taskAsString);
        } catch (IOException exception) {
            String errorMessage = "Ошибка при сохранении в файл" + exception.getMessage();
            System.out.println(errorMessage);
            throw new FileManagerSaveException(errorMessage);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {

            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

            List<String> allLines = Files.readAllLines(file.toPath());
            allLines.remove(0);

            for (String line : allLines) {
                String[] lines = line.trim().split(",");
                switch (lines[1]) {
                    case "TASK":
                        Task task = new Task(lines[2], lines[4], getTaskStatusFromString(lines[3]));
                        task.setId(Integer.parseInt(lines[0]));
                        fileBackedTaskManager.tasks.put(task.getId(), task);
                        if (fileBackedTaskManager.numberOfId <= task.getId()) {
                            fileBackedTaskManager.numberOfId = task.getId() + 1;
                        }
                        break;
                    case "EPIC":
                        Epic epic = new Epic(lines[2], lines[4]);
                        epic.setId(Integer.parseInt(lines[0]));
                        epic.setStatus(getTaskStatusFromString(lines[3]));
                        fileBackedTaskManager.epics.put(epic.getId(), epic);
                        if (fileBackedTaskManager.numberOfId <= epic.getId()) {
                            fileBackedTaskManager.numberOfId = epic.getId() + 1;
                        }
                        break;
                    case "SUBTASK":
                        Subtask subtask = new Subtask(lines[2], lines[4], getTaskStatusFromString(lines[3]),
                                Integer.parseInt(lines[5]));
                        subtask.setId(Integer.parseInt(lines[0]));
                        fileBackedTaskManager.subtasks.put(subtask.getId(), subtask);
                        Epic epicForSubtask = fileBackedTaskManager.epics.get(subtask.getEpicId());
                        epicForSubtask.addSubTaskId(subtask);
                        if (fileBackedTaskManager.numberOfId <= subtask.getId()) {
                            fileBackedTaskManager.numberOfId = subtask.getId() + 1;
                        }
                        break;
                }
            }
            return fileBackedTaskManager;
        } catch (IOException exception) {
            String errorMessage = "Ошибка при загрузке файла" + exception.getMessage();
            System.out.println(errorMessage);
            throw new FileManagerFileInitializationException(errorMessage);
        }
    }

    private static StatusOfTask getTaskStatusFromString(String line) {
        switch (line) {
            case "NEW":
                return StatusOfTask.NEW;
            case "IN_PROGRESS":
                return StatusOfTask.IN_PROGRESS;
            case "DONE":
                return StatusOfTask.DONE;
            default:
                throw new IllegalStateException("Некорректное значение: " + line);
        }
    }
}
