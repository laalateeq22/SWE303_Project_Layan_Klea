package Controller;

import Model.BookTM;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.*;

public class BookSearchFormController {
    public TextField bk_sch;
    public TableView<BookTM> tbl_bk;
    public AnchorPane sch_root;
    public Connection connection;

    // Initialize the JavaFX UI
    public void initialize() {
        setupTableColumns();
        loadAllBooks();
        setupSearchListener();
    }

    // Separate JavaFX Table Column Setup
    private void setupTableColumns() {
        tbl_bk.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tbl_bk.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("title"));
        tbl_bk.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("author"));
        tbl_bk.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    // Listener for the Search Field
    private void setupSearchListener() {
        bk_sch.textProperty().addListener((observable, oldValue, newValue) -> searchBooks(newValue));
    }

    // Separate Method to Load All Books
    public void loadAllBooks() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            ObservableList<BookTM> books = tbl_bk.getItems();
            books.clear();

            String sql = "SELECT * FROM bookdetail";
            try (PreparedStatement pstm = connection.prepareStatement(sql); ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    books.add(new BookTM(rst.getString(1), rst.getString(2), rst.getString(3), rst.getString(4)));
                }
            }
            tbl_bk.setItems(books);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Separate Method for Search Functionality
    public void searchBooks(String searchText) {
        try {
            tbl_bk.getItems().clear();
            String sql = "SELECT * FROM bookdetail WHERE id LIKE ? OR title LIKE ? OR author LIKE ?";
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
                 PreparedStatement pstm = connection.prepareStatement(sql)) {

                String like = "%" + searchText + "%";
                pstm.setString(1, like);
                pstm.setString(2, like);
                pstm.setString(3, like);

                try (ResultSet rst = pstm.executeQuery()) {
                    ObservableList<BookTM> books = tbl_bk.getItems();
                    while (rst.next()) {
                        books.add(new BookTM(rst.getString(1), rst.getString(2), rst.getString(3), rst.getString(4)));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Separate Method for Image Click Handler
    public void img_bk(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/View/HomeFormView.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.sch_root.getScene().getWindow();
        primaryStage.setScene(scene);

        TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
        tt.setFromX(-scene.getWidth());
        tt.setToX(0);
        tt.play();
    }

    // Separate Method for Mouse Animation
    public void playMouseEnterAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();

            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1.2);
            scaleT.setToY(1.2);
            scaleT.play();

            DropShadow glow = new DropShadow();
            glow.setColor(Color.GREEN);
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        }
    }
}
