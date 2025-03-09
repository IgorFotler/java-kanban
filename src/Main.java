import Server.HttpTaskServer;
import manager.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        TaskManager taskManager = Managers.getDefault();

        HttpTaskServer httpTaskServer = new HttpTaskServer("localhost", 8080, taskManager);
        httpTaskServer.start();
    }
}

