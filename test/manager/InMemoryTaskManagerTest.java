package manager;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest {

   @BeforeEach
   void init() {
       taskManager = getTaskManager();
   }

    @Override
    TaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }
}