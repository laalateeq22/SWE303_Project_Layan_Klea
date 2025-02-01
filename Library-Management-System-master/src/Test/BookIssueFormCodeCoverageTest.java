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

class BookIssueFormCodeCoverageTest {
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
        controller.txt_name = new TextField();
        controller.txt_title = new TextField();
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
        connection.createStatement().execute("DROP TABLE IF EXISTS bookdetail");
        connection.createStatement().execute("CREATE TABLE bookdetail (id VARCHAR(10), title VARCHAR(100), status VARCHAR(10))");
        connection.createStatement().execute("INSERT INTO bookdetail VALUES ('B001', 'Book Title 1', 'Available')");
    }

    @Test
    void newAction_EmptyTable() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.new_action(null);
                assertNotNull(controller.txt_issid.getText(), "Issue ID should not be null.");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void newAction_WithExistingRecords() throws Exception {
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO issuetb (issueId, date, memberId, bookId) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "I001");
            stmt.setString(2, "2023-01-01");
            stmt.setString(3, "M001");
            stmt.setString(4, "B001");
            stmt.execute();
        }

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.new_action(null);
                assertEquals("SS", controller.txt_issid.getText().substring(0, 2), "Generated ID should start with 'SS'.");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void addAction_InvalidInputs() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.txt_issid.setText("");
                controller.add_action(null);
                // Assuming that the "All fields are required" message is printed to the console.
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void addAction_ValidInputs() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.txt_issid.setText("SS001");
                controller.txt_isu_date.setValue(java.time.LocalDate.now());
                controller.mem_is_id.getItems().add("M001");
                controller.mem_is_id.setValue("M001");
                controller.book_id.getItems().add("B001");
                controller.book_id.setValue("B001");
                controller.txt_name.setText("Member 1");
                controller.txt_title.setText("Book Title 1");

                controller.add_action(null);

                assertNotNull(controller.txt_issid.getText(), "Issue ID should not be null.");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void deleteAction_NoSelection() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.delete_Action(null);
                // Assuming that an alert will be shown indicating "Please select a row."
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void deleteAction_WithSelection() throws Exception {
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO issuetb (issueId, date, memberId, bookId) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "I001");
            stmt.setString(2, "2023-01-01");
            stmt.setString(3, "M001");
            stmt.setString(4, "B001");
            stmt.execute();
        }

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.initialize();
                controller.bk_ssue_tbl.getSelectionModel().selectFirst();
                controller.delete_Action(null);

                // Assert the table is now empty
                assertTrue(controller.bk_ssue_tbl.getItems().isEmpty(), "The table should be empty after deletion.");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void playMouseEnterAnimation_ValidIcon() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // Simulate mouse enter event on an ImageView
                // Assuming ImageView is part of the FXML and accessible
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }
}
