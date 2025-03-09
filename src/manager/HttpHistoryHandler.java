package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ErrorResponse;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HttpHistoryHandler extends BaseHttpHandler {

    private TaskManager taskManager;
    private Gson jsonMapper;

    public HttpHistoryHandler(TaskManager taskManager, Gson jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET":
                    List<Task> tasks = taskManager.getHistory();
                    String json = jsonMapper.toJson(tasks);
                    sendText(exchange, json, 200);
                default:
                    ErrorResponse errorRosponce = new ErrorResponse(String.format("Обработка метода %s не предусмотрена", method), 405, exchange.getRequestURI().getPath());
                    String jsonText = jsonMapper.toJson(errorRosponce);
                    sendText(exchange, jsonText, 405);
            }
        } catch (Exception e) {
            ErrorResponse errorResponce = new ErrorResponse(e.getMessage(), 500, exchange.getRequestURI().getPath());
            String jsonText = jsonMapper.toJson(errorResponce);
            sendText(exchange, jsonText, errorResponce.getErrorCode());
        } finally {
            exchange.close();
        }
    }
}
