import Server.HttpTaskServer;
import com.google.gson.Gson;
import enumeration.StatusOfTask;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {

    TaskManager taskManager = Managers.getDefault();

    HttpTaskServer httpTaskServer = new HttpTaskServer("localhost", 8080, taskManager);
    Gson gson = httpTaskServer.getJsonMapper();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop(1);
    }

    @Test
    public void testPostTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", StatusOfTask.NEW,
            LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(13,0)),
            Duration.ofMinutes(60));

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", StatusOfTask.NEW,
                LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(13,0)),
                Duration.ofMinutes(60));

        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String expectedBody = "[{\"id\":1,\"name\":\"Task 1\",\"description\":\"Description 1\",\"status\":\"NEW\",\"startTime\":\"13:00:00/09.03.2025\",\"duration\":\"60\"}]";

        assertEquals(expectedBody, response.body(), "Ответ не соответсвует ожидаемому");
    }

    @Test
    public void testGetAllSubtaskOfEpicId() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask 1", "Description 1", StatusOfTask.NEW,
                LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(14,30)),
                Duration.ofMinutes(60), 1);
        Subtask subtask2 = new Subtask("subtask 2", "Description 2", StatusOfTask.NEW,
                LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(11,30)),
                Duration.ofMinutes(60), 1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String expectedBody = "[{\"epicId\":1,\"id\":2,\"name\":\"subtask 1\",\"description\":\"Description 1\",\"status\":\"NEW\",\"startTime\":\"14:30:00/09.03.2025\",\"duration\":\"60\"},{\"epicId\":1,\"id\":3,\"name\":\"subtask 2\",\"description\":\"Description 2\",\"status\":\"NEW\",\"startTime\":\"11:30:00/09.03.2025\",\"duration\":\"60\"}]";

        assertEquals(expectedBody, response.body(), "Ответ не соответсвует ожидаемому");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("subtask 1", "Description 1", StatusOfTask.NEW,
                LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(14,30)),
                Duration.ofMinutes(60));
        String json = gson.toJson(task);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            client.send(request, HttpResponse.BodyHandlers.ofString());

            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/1"))
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode(), "Статус код должен быть 200");
            assertEquals(0, taskManager.getAllTasks().size(), "Менеджере задач не пуст");
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Epic 2", "Description 2");
        taskManager.createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String expectedBody = "[{\"subtasksId\":[],\"id\":1,\"name\":\"Epic 1\",\"description\":\"Description 1\",\"status\":\"NEW\"},{\"subtasksId\":[],\"id\":2,\"name\":\"Epic 2\",\"description\":\"Description 2\",\"status\":\"NEW\"}]";

        assertEquals(expectedBody, response.body(), "Ответ не соответсвует ожидаемому");
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", StatusOfTask.NEW,
                LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(13,0)),
                Duration.ofMinutes(60));
        taskManager.createTask(task);

        Epic epic = new Epic("Epic 1", "Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("subtask 1", "Description 1", StatusOfTask.NEW,
                LocalDateTime.of(LocalDate.of(2025, 3, 9), LocalTime.of(14,30)),
                Duration.ofMinutes(60), 2);
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String expectedBody = "[{\"id\":1,\"name\":\"Task 1\",\"description\":\"Description 1\",\"status\":\"NEW\",\"startTime\":\"13:00:00/09.03.2025\",\"duration\":\"60\"},{\"epicId\":2,\"id\":3,\"name\":\"subtask 1\",\"description\":\"Description 1\",\"status\":\"NEW\",\"startTime\":\"14:30:00/09.03.2025\",\"duration\":\"60\"}]";

        assertEquals(expectedBody, response.body(), "Ответ не соответсвует ожидаемому");
    }
}
