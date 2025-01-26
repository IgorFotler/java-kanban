package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> history = new ArrayList<>();

    @Override
    public void addToHistory(Task task) {
        if (history.size() > 10) {
            history.remove(0);
        }
        history.add(task);
    }


    @Override
    public List<Task> returnHistory() {
        return history;
    }
}
