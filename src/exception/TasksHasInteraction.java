package exception;

public class TasksHasInteraction extends RuntimeException {
    public TasksHasInteraction(String message) {
        super(message);
    }
}
