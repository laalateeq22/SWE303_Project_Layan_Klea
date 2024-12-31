package Test;

import Controller.MemberFormController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;
import db.DBConnection;

import java.sql.*;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class MemberFormTest {

    private MemberFormController controller;
    private Connection connection;

    @BeforeAll
    static void initToolkit() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> latch.countDown());
        latch.await();
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new MemberFormController();
        MockitoAnnotations.openMocks(this);
        connection = DBConnection.getInstance().getConnection();

        controller.mem_id = new TextField();
        controller.mem_nme = new TextField();
        controller.mem_addss = new TextField();
        controller.mem_num = new TextField();
        controller.mem_tbl = new javafx.scene.control.TableView<>();
        controller.root = new AnchorPane();
        controller.btn_add = new javafx.scene.control.Button();
        controller.btn_new = new javafx.scene.control.Button();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    //test 1: when they are all missing
    @Test
    void testEmptyFields() {
        controller.mem_id.clear();
        controller.mem_nme.clear();
        controller.mem_addss.clear();
        controller.mem_num.clear();

        controller.btn_add.fire();

        Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill your details.", ButtonType.OK);
        assertTrue(alert.isShowing(), "Validation error alert should show.");
    }

    //test case 2: for the missing name
    @Test
    void testNameEmpty() {
        controller.mem_id.setText("M001");
        controller.mem_nme.clear();
        controller.mem_addss.setText("olya");
        controller.mem_num.setText("0114829930");

        controller.btn_add.fire();

        Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill your details.", ButtonType.OK);
        assertTrue(alert.isShowing(), "Validation error alert should show.");
    }

    //test 3: for the missing addresss
    @Test
    void testAddressEmpty() {
        controller.mem_id.setText("M001");
        controller.mem_nme.setText("Abdulaziz");
        controller.mem_addss.clear();
        controller.mem_num.setText("0582811111");

        controller.btn_add.fire();

        Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill your details.", ButtonType.OK);
        assertTrue(alert.isShowing(), "Validation error alert should show.");
    }

    //test 4: for missing phone number
    @Test
    void testContactEmpty() {
        controller.mem_id.setText("M001");
        controller.mem_nme.setText("Layan");
        controller.mem_addss.setText("epoka");
        controller.mem_num.clear();

        controller.btn_add.fire();

        Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill your details.", ButtonType.OK);
        assertTrue(alert.isShowing(), "Validation error alert should show.");
    }

    //test 5: for invalid phone number
    @Test
    void testInvalidContact() {
        controller.mem_id.setText("M001");
        controller.mem_nme.setText("ruqia");
        controller.mem_addss.setText("KSU");
        controller.mem_num.setText("12345"); //invalid number

        controller.btn_add.fire();


        Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill your details.", ButtonType.OK);
        assertTrue(alert.isShowing(), "Validation error alert should show.");
    }

    //test 6: for valid input in add
    @Test
    void testValidFieldsAdd() throws SQLException {
        controller.mem_id.setText("M001");
        controller.mem_nme.setText("leen");
        controller.mem_addss.setText("newyork");
        controller.mem_num.setText("0538889345");

        controller.btn_add.fire();

        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM memberdetail WHERE id = ?");
        stmt.setString(1, "M001");
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next(), "New member should be added to the database.");
        assertEquals("leen", rs.getString("name"), "Name should match.");
    }

    //test 7: for Valid Input in update
    @Test
    void testValidUpdate() throws SQLException {
        testValidFieldsAdd();

        controller.mem_id.setText("M001");
        controller.mem_nme.setText("rana");
        controller.mem_addss.setText("canberra");
        controller.mem_num.setText("0538889345");

        controller.btn_add.fire();

        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM memberdetail WHERE id = ?");
        stmt.setString(1, "M001");
        ResultSet rs = stmt.executeQuery();
        assertTrue(rs.next(), "Updated member should exist in the database.");
        assertEquals("rana", rs.getString("name"), "Name should be updated.");
        assertEquals("canberra", rs.getString("address"), "Address should be updated.");
    }
}
