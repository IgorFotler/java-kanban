package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ErrorResponse;
import exception.TaskNotFoundException;
import exception.TasksHasInteraction;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpTaskHandler extends BaseHttpHandler {

    private TaskManager taskManager;
    private Gson jsonMapper;

    public HttpTaskHandler(TaskManager taskManager, Gson jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange);
                case "POST":
                    handlePost(exchange);
                case "DELETE":
                    handleDelete(exchange);
                default:
                    ErrorResponse errorRosponce = new ErrorResponse(String.format("Обработка метода %s не предусмотрена", method), 405, exchange.getRequestURI().getPath());
                    String jsonText = jsonMapper.toJson(errorRosponce);
                    sendText(exchange, jsonText, 405);
            }
        } catch (TaskNotFoundException e) {
            ErrorResponse errorResponce = new ErrorResponse(e.getMessage(), 404, exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(errorResponce);
            sendText(exchange, jsonText, errorResponce.getErrorCode());
        } catch (TasksHasInteraction e) {
            ErrorResponse errorResponce = new ErrorResponse(e.getMessage(), 406, exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(errorResponce);
            sendText(exchange, jsonText, errorResponce.getErrorCode());
        } catch (NumberFormatException e) {
            ErrorResponse errorResponce = new ErrorResponse("id задачи должно быть числом", 406, exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(errorResponce);
            sendText(exchange, jsonText, errorResponce.getErrorCode());
        } catch (Exception e) {
            ErrorResponse errorResponce = new ErrorResponse(e.getMessage(), 500, exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(errorResponce);
            sendText(exchange, jsonText, errorResponce.getErrorCode());
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();

        String[] urlParts = path.split("/");

        if (urlParts.length == 3) {
                Integer id = Integer.valueOf(urlParts[2]);
                Task taskById = taskManager.findTasklById(id);
                String json = jsonMapper.toJson(taskById);
                sendText(exchange, json, 200);
        }

        if (urlParts.length == 2) {
            List<Task> allTasks = taskManager.getAllTasks();
            String json = jsonMapper.toJson(allTasks);
            sendText(exchange, json, 200);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();

        String[] urlParts = path.split("/");

            Integer id = Integer.valueOf(urlParts[2]);
            taskManager.deleteTask(id);
            sendText(exchange, String.format("Задача с id=%s удалена", id), 200);
    }

    private void handlePost(HttpExchange exchange) throws IOException {

        byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
        String bodyString = new String(bodyBytes, StandardCharsets.UTF_8);
        Task task = jsonMapper.fromJson(bodyString, Task.class);
        if (task.getId() == null) {
            taskManager.createTask(task);
        } else {
            taskManager.updateTask(task);
        }
        String json = jsonMapper.toJson(task);
        sendText(exchange, json, 201);
    }
}
