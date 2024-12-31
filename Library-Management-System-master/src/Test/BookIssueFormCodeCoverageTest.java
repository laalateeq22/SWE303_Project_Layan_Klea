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

class BookIssueFormControllerBranchCoverageTest {
    private BookIssueFormController controller;
    private Connection connection;

    @BeforeAll
    static void initToolkit() {
        Platform.startup(() -> {}); // Initializes JavaFX runtime
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
        connection.createStatement().execute("DROP TABLE IF EXISTS issuetb");
        connection.createStatement().execute("CREATE TABLE issuetb (issueId VARCHAR(10) PRIMARY KEY, date DATE, memberId VARCHAR(10), bookId VARCHAR(10))");
    }

    @Test
    void NewAction_EmptyTable() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.new_action(null);
                assertEquals("I001", controller.txt_issid.getText(), "Generated ID should be I001 for an empty table.");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void NewAction_NonEmptyTable_SequentialIDs() throws Exception {

        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO issuetb (issueId, date, memberId, bookId) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "I001");
            stmt.setString(2, "2003-11-11");
            stmt.setString(3, "M001");
            stmt.setString(4, "B001");
            stmt.execute();
            stmt.setString(1, "I002");
            stmt.setString(2, "2007-17-07");
            stmt.setString(3, "M002");
            stmt.setString(4, "B002");
            stmt.execute();
        }

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.new_action(null);
                assertEquals("I003", controller.txt_issid.getText(), "Generated ID should be I003 for sequential IDs.");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void testNewAction_NonSequentialIDs() throws Exception {

        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO issuetb (issueId, date, memberId, bookId) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "I001");
            stmt.setString(2, "2003-11-11");
            stmt.setString(3, "M001");
            stmt.setString(4, "B001");
            stmt.execute();
            stmt.setString(1, "I003");
            stmt.setString(2, "2008-17-07");
            stmt.setString(3, "M003");
            stmt.setString(4, "B003");
            stmt.execute();
        }

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.new_action(null);
                assertEquals("I002", controller.txt_issid.getText(), "Generated ID should be I002 for non-sequential IDs.");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void NewAction_LargeDataset() throws Exception {

        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO issuetb (issueId, date, memberId, bookId) VALUES (?, ?, ?, ?)")) {
            for (int i = 1; i <= 5000; i++) {
                stmt.setString(1, String.format("I%04d", i));
                stmt.setString(2, "2003-11-11");
                stmt.setString(3, "M001");
                stmt.setString(4, "B001");
                stmt.execute();
            }
        }

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.new_action(null);
                assertEquals("I5001", controller.txt_issid.getText(), "Generated ID should be I5001 for a large dataset.");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void  NewAction_DatabaseError_NoTable() throws Exception {
        connection.createStatement().execute("DROP TABLE IF EXISTS issuetb");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.new_action(null);
                fail("Expected exception due to missing table.");
            } catch (Exception e) {
                assertTrue(e.getMessage().contains("issuetb"), "Exception message should indicate missing table.");
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void NewAction_NullDatabaseConnection() throws Exception {

        controller.connection = null;

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.new_action(null);
                fail("Expected exception due to null database connection.");
            } catch (Exception e) {
                assertTrue(e.getMessage().contains("connection"), "Exception message should indicate null connection.");
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void NewAction_NonNumericIDValues() throws Exception {
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO issuetb (issueId, date, memberId, bookId) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "IA001"); // Unexpected prefix
            stmt.setString(2, "2003-11-11");
            stmt.setString(3, "M001");
            stmt.setString(4, "B001");
            stmt.execute();
        }

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.new_action(null);
                assertEquals("I002", controller.txt_issid.getText(), "Generated ID should skip malformed entries.");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }
    @Test
    void NewAction_MissingColumn() throws Exception {
        connection.createStatement().execute("ALTER TABLE issuetb DROP COLUMN issueId");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.new_action(null);
                fail("Expected an exception due to missing column.");
            } catch (Exception e) {
                assertTrue(e.getMessage().contains("issueId"), "Exception should indicate missing column.");
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

}
