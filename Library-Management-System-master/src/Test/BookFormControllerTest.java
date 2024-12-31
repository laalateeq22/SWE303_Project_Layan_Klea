package Test;
import Controller.BookFormController;
import Model.BookTM;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import mocks.MockAlert;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.sql.*;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class BookFormControllerTest {
    private BookFormController bookFormController;
    private MockAlert mockAlertDisplay;

    @BeforeEach
    public void setUp() throws Exception {

        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> latch.countDown());
        latch.await();

        MockAlert mockAlerts = new MockAlert();

        bookFormController = new BookFormController(){};

        bookFormController.txt_bk_id = new TextField();
        bookFormController.txt_bk_title = new TextField();
        bookFormController.txt_bk_auth = new TextField();
        bookFormController.txt_bk_st = new TextField();
        bookFormController.btn_add = new Button();
        bookFormController.tbl_bk = new TableView<>();
    }

    @Test
    public void AllEmpty() throws Exception {
        bookFormController.txt_bk_id.setText("");
        bookFormController.txt_bk_title.setText("");
        bookFormController.txt_bk_auth.setText("");
        bookFormController.btn_add.setText("Add");

        bookFormController.btn_Add(new ActionEvent());

        assertEquals("Please fill your details.", mockAlertDisplay.getCapturedMessage());
    }

    @Test
    public void AllValid() throws SQLException {
        bookFormController.txt_bk_id.setText("1");
        bookFormController.txt_bk_title.setText("the snowman and me");
        bookFormController.txt_bk_auth.setText("harry");
        bookFormController.btn_add.setText("Add");

        bookFormController.btn_Add(new ActionEvent());

        assertEquals("Record added successfully.", mockAlertDisplay.getCapturedMessage());
    }

    @Test
    public void invalidTitle() throws SQLException {
        bookFormController.txt_bk_id.setText("1");
        bookFormController.txt_bk_title.setText("!!!"); //incorrect Input
        bookFormController.txt_bk_auth.setText("sam m");
        bookFormController.btn_add.setText("Add");

        bookFormController.btn_Add(new ActionEvent());

        assertEquals("Enter Valid Name", mockAlertDisplay.getCapturedMessage());
    }

    @Test
    public void validUpdate() throws SQLException {

        AllValid();

        bookFormController.txt_bk_id.setText("1");
        bookFormController.txt_bk_title.setText("gingerbread man");
        bookFormController.txt_bk_auth.setText("layan");
        bookFormController.btn_add.setText("Update");

        bookFormController.btn_Add(new ActionEvent());

        assertEquals("Record updated successfully.", mockAlertDisplay.getCapturedMessage());
    }

    @Test
    public void unchangedUpdate() throws SQLException {

        bookFormController.txt_bk_id.setText("1");
        bookFormController.txt_bk_title.setText("harry potter");
        bookFormController.txt_bk_auth.setText("jk rowling ");
        bookFormController.btn_add.setText("Update");

        bookFormController.btn_Add(new ActionEvent());

        assertNull(mockAlertDisplay.getCapturedMessage());
    }

    @Test
    public void BVTTitle() throws SQLException{
        //min:
        bookFormController.txt_bk_id.setText("001");
        bookFormController.txt_bk_title.setText("A");
        bookFormController.txt_bk_auth.setText("layan");
        bookFormController.btn_add.setText("Add");

        bookFormController.btn_Add(new ActionEvent());
        assertEquals("Record added successfully.", mockAlertDisplay.getCapturedMessage());

        //max:
        String maxTitle = "A".repeat(255);
        bookFormController.txt_bk_id.setText("001");
        bookFormController.txt_bk_title.setText(maxTitle);
        bookFormController.txt_bk_auth.setText("layan");
        bookFormController.btn_add.setText("Add");

        bookFormController.btn_Add(new ActionEvent());
        assertEquals("Record added successfully.", mockAlertDisplay.getCapturedMessage());

        //overflow
        String overflowTitle = "A".repeat(256);
        bookFormController.txt_bk_id.setText("12345");
        bookFormController.txt_bk_title.setText(overflowTitle);
        bookFormController.txt_bk_auth.setText("layan");
        bookFormController.btn_add.setText("Add");

        bookFormController.btn_Add(new ActionEvent());
        assertEquals("Entered detail Invalid", mockAlertDisplay.getCapturedMessage());
    }

    @Test
    public void BVTAuthur() throws SQLException{
        //min:
        bookFormController.txt_bk_id.setText("001");
        bookFormController.txt_bk_title.setText("snowman and me");
        bookFormController.txt_bk_auth.setText("A");
        bookFormController.btn_add.setText("Add");

        bookFormController.btn_Add(new ActionEvent());
        assertEquals("Record added successfully.", mockAlertDisplay.getCapturedMessage());

        //max:
        String maxAuthor = "A".repeat(255);
        bookFormController.txt_bk_id.setText("12345");
        bookFormController.txt_bk_title.setText("layan");
        bookFormController.txt_bk_auth.setText(maxAuthor);
        bookFormController.btn_add.setText("Add");

        bookFormController.btn_Add(new ActionEvent());
        assertEquals("Record added successfully.", mockAlertDisplay.getCapturedMessage());

        //overflow
        String overflowAuthor = "A".repeat(256);
        bookFormController.txt_bk_id.setText("011");
        bookFormController.txt_bk_title.setText("layan");
        bookFormController.txt_bk_auth.setText(overflowAuthor);
        bookFormController.btn_add.setText("Add");

        bookFormController.btn_Add(new ActionEvent());
        assertEquals("Entered detail Invalid", mockAlertDisplay.getCapturedMessage());
    }

    @Test
    public void BVTID() throws SQLException{
        //min:
        bookFormController.txt_bk_id.setText("1");
        bookFormController.txt_bk_title.setText("harry potter");
        bookFormController.txt_bk_auth.setText("jk rowling ");
        bookFormController.btn_add.setText("Add");

        bookFormController.btn_Add(new ActionEvent());
        assertEquals("Record added successfully.", mockAlertDisplay.getCapturedMessage());

        //max
        bookFormController.txt_bk_id.setText("12345");
        bookFormController.txt_bk_title.setText("harry potter");
        bookFormController.txt_bk_auth.setText("jk rowling");
        bookFormController.btn_add.setText("Add");

        bookFormController.btn_Add(new ActionEvent());
        assertEquals("Record added successfully.", mockAlertDisplay.getCapturedMessage());


        //overflow
        String overFlowTitle = "A".repeat(256);
        bookFormController.txt_bk_id.setText("011");
        bookFormController.txt_bk_title.setText(overFlowTitle);
        bookFormController.txt_bk_auth.setText("Layan");
        bookFormController.btn_add.setText("Add");

        bookFormController.btn_Add(new ActionEvent());
        assertEquals("Entered detail Invalid", mockAlertDisplay.getCapturedMessage());
    }

}