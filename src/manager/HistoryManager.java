package manager;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    void addToHistory(Task task);

    List<Task> returnHistory();

}
//Спасибо за ревью, с Наступающим)))
