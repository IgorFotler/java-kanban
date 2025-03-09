package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ErrorResponse;
import exception.TaskNotFoundException;
import exception.TasksHasInteraction;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpEpicHandler extends BaseHttpHandler {

    private TaskManager taskManager;
    private Gson jsonMapper;

    public HttpEpicHandler(TaskManager taskManager, Gson jsonMapper) {
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

        if (urlParts.length == 4) {
            List<Subtask> allSubtasksOfEpic = taskManager.getAllSubtasksOfEpic(Integer.parseInt(urlParts[2]));
            String json = jsonMapper.toJson(allSubtasksOfEpic);
            sendText(exchange, json, 200);
        }

        if (urlParts.length == 3) {
            Integer id = Integer.valueOf(urlParts[2]);
            Epic epicById = taskManager.findEpiclById(id);
            String json = jsonMapper.toJson(epicById);
            sendText(exchange, json, 200);
        }

        if (urlParts.length == 2) {
            List<Epic> allEpics = taskManager.getAllEpics();
            String json = jsonMapper.toJson(allEpics);
            sendText(exchange, json, 200);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();

        String[] urlParts = path.split("/");

        Integer id = Integer.valueOf(urlParts[2]);
        taskManager.deleteEpic(id);
        sendText(exchange, String.format("Эпик с id=%s удален", id), 200);
    }

    private void handlePost(HttpExchange exchange) throws IOException {

        byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
        String bodyString = new String(bodyBytes, StandardCharsets.UTF_8);
        Epic epic = jsonMapper.fromJson(bodyString, Epic.class);
        if (epic.getId() == null) {
            taskManager.createEpic(epic);
        } else {
            taskManager.updateEpic(epic);
        }
        String json = jsonMapper.toJson(epic);
        sendText(exchange, json, 201);
    }
}
