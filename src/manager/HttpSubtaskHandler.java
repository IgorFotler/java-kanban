package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ErrorResponse;
import exception.TaskNotFoundException;
import exception.TasksHasInteraction;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpSubtaskHandler extends BaseHttpHandler {

    private TaskManager taskManager;
    private Gson jsonMapper;

    public HttpSubtaskHandler(TaskManager taskManager, Gson jsonMapper) {
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
            Subtask subtaskById = taskManager.findSubtasklById(id);
            String json = jsonMapper.toJson(subtaskById);
            sendText(exchange, json, 200);
        }

        if (urlParts.length == 2) {
            List<Subtask> allSubtasks = taskManager.getAllSubtasks();
            String json = jsonMapper.toJson(allSubtasks);
            sendText(exchange, json, 200);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();

        String[] urlParts = path.split("/");

        Integer id = Integer.valueOf(urlParts[2]);
        taskManager.deleteSubtask(id);
        sendText(exchange, String.format("Подзадача с id=%s удалена", id), 200);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
        String bodyString = new String(bodyBytes, StandardCharsets.UTF_8);
        Subtask subtask = jsonMapper.fromJson(bodyString, Subtask.class);
        if (subtask.getId() == null) {
            taskManager.createSubtask(subtask);
        } else {
            taskManager.updateSubtask(subtask);
        }
        String json = jsonMapper.toJson(subtask);
        sendText(exchange, json, 201);
    }
}
