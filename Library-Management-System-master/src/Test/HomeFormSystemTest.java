package Test;

import Model.BookTM;
import Model.MemberTM;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.hasTableCell;
import static org.testfx.util.WaitForAsyncUtils.waitFor;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.service.query.PointQuery;
import Test.Main;
public class HomeFormSystemTest  extends ApplicationTest{
    public void displayActiveThreads() {
        System.out.println("\n=== Active Threads ===");
        Thread.getAllStackTraces().keySet().forEach(thread ->
                System.out.println("Thread Name: " + thread.getName() + ", State: " + thread.getState())
        );
        System.out.println("======================\n");
    }
    @Override
    public void start(Stage stage) throws Exception {
        // Initialize the main application and show the stage
        new Main().start(stage);
        stage.show();
    }
    //i split it into 2 because there was a problem with my virtual and there were too many tasks
    @Test
    public void homeSystemTest1() throws InterruptedException {
        displayActiveThreads();
        Thread thread = new Thread(() -> {
            try {
    clickOn("#member");
        Thread.sleep(1000);

    clickOn("#img_back");
        Thread.sleep(1000);

    clickOn("#books");
        Thread.sleep(1000);

    clickOn("#img_back");
        Thread.sleep(1000);

    clickOn("#issue");
        Thread.sleep(1000);

    clickOn("#img_back");
        Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        thread.join(); // Ensures the main test thread waits for the spawned thread
    }
    @Test
    public void homeSystemTest2() throws InterruptedException {

                displayActiveThreads();
                Thread thread = new Thread(() -> {
                    try {
        clickOn("#bk_return");
        Thread.sleep(1000);

        clickOn("#img_back");
        Thread.sleep(1000);

        clickOn("#bk_search");
        Thread.sleep(1000);

        clickOn("#img_back");
        Thread.sleep(1000);

    } catch (Exception e) {
        e.printStackTrace();
    }
});

        thread.start();
        thread.join(); // Ensures the main test thread waits for the spawned thread
    }
}
