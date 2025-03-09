package server;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private final String hostname;
    private final int port;
    private final HttpServer httpServer;
    private final Gson jsonMapper;

    public HttpTaskServer(String hostname, int port, TaskManager taskManager) throws IOException {
        this.hostname = hostname;
        this.port = port;

        InetSocketAddress address = new InetSocketAddress(hostname, port);
        httpServer = HttpServer.create(address, 0);

        jsonMapper = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        httpServer.createContext("/tasks", new HttpTaskHandler(taskManager, jsonMapper));
        httpServer.createContext("/epics", new HttpEpicHandler(taskManager, jsonMapper));
        httpServer.createContext("/subtasks", new HttpSubtaskHandler(taskManager, jsonMapper));
        httpServer.createContext("/history", new HttpHistoryHandler(taskManager, jsonMapper));
        httpServer.createContext("/prioritized", new HttpPrioritizedHandler(taskManager, jsonMapper));
    }

    public void start() {
        httpServer.start();
    }

    public void stop(int delay) {
        httpServer.stop(delay);
    }

    public Gson getJsonMapper() {
        return jsonMapper;
    }
}
