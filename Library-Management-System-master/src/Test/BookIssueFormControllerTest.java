import Controller.BookIssueFormController;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class BookIssueFormControllerTest {
    private BookIssueFormController controller;
    private Connection connection;

    @BeforeAll
    static void initToolkit() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new BookIssueFormController();
        MockitoAnnotations.openMocks(this);
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "");
        controller.connection = connection;
        setupDatabase();

        // Initialize UI components
        controller.txt_issid = new TextField();
        controller.txt_isu_date = new DatePicker();
        controller.mem_is_id = new ComboBox();
        controller.book_id = new ComboBox();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    private void setupDatabase() throws SQLException {
        // Reset the table for each test
        connection.createStatement().execute("DROP TABLE IF EXISTS issuetb");
        connection.createStatement().execute("CREATE TABLE issuetb (issueId VARCHAR(10) PRIMARY KEY, date DATE, memberId VARCHAR(10), bookId VARCHAR(10))");
    }

    @Test
    void newAction_EmptyTable() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.new_action(null);
                assertEquals("I001", controller.txt_issid.getText(), "The generated ID should be I001 for an empty table.");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void newAction_WithExistingSequentialRecords() throws Exception {
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO issuetb (issueId, date, memberId, bookId) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "I001");
            stmt.setString(2, "2003-11-12");
            stmt.setString(3, "M001");
            stmt.setString(4, "B001");
            stmt.execute();

            stmt.setString(1, "I002");
            stmt.setString(2, "2008-07-17");
            stmt.setString(3, "M002");
            stmt.setString(4, "B002");
            stmt.execute();
        }

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.new_action(null);
                assertEquals("I003", controller.txt_issid.getText(), "The generated ID should be I003.");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void newAction_WithNonSequentialRecords() throws Exception {
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO issuetb (issueId, date, memberId, bookId) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "I001");
            stmt.setString(2, "2003-12-11");
            stmt.setString(3, "M001");
            stmt.setString(4, "B001");
            stmt.execute();

            stmt.setString(1, "I003");
            stmt.setString(2, "2004-11-13");
            stmt.setString(3, "M003");
            stmt.setString(4, "B003");
            stmt.execute();
        }

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.new_action(null);
                assertEquals("I004", controller.txt_issid.getText(), "The generated ID should be I004, skipping the gap.");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void newAction_DatabaseConnectionError() throws Exception {
        connection.close();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.new_action(null);
                fail("Expected an exception due to database connection error.");
            } catch (Exception e) {
                assertNotNull(e.getMessage(), "An exception should be thrown for a database connection error.");
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void newAction_WithLargeNumberOfRecords() throws Exception {
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO issuetb (issueId, date, memberId, bookId) VALUES (?, ?, ?, ?)")) {
            for (int i = 1; i <= 1000; i++) {
                stmt.setString(1, String.format("I%03d", i));
                stmt.setString(2, "2004-11-11");
                stmt.setString(3, "M001");
                stmt.setString(4, "B001");
                stmt.execute();
            }
        }

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.new_action(null);
                assertEquals("I1001", controller.txt_issid.getText(), "The generated ID should be I1001 for large datasets.");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }
}
