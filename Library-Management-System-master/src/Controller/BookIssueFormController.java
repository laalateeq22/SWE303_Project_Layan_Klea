package Controller;

import Model.BookIssueTM;
import db.DBConnection;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class BookIssueFormController {
    public TextField txt_issid;
    public DatePicker txt_isu_date;
    public TextField txt_name;
    public TextField txt_title;
    public ComboBox mem_is_id;
    public ComboBox book_id;
    public TableView<BookIssueTM> bk_ssue_tbl;
    public AnchorPane bk_iss;
    public Connection connection;

    //JDBC
    private PreparedStatement selectALl;
    private PreparedStatement selectmemID;
    private PreparedStatement selectbkdtl;
    private PreparedStatement table;
    private PreparedStatement delete;

    public void initialize() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");

        bk_ssue_tbl.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("issueId"));
        bk_ssue_tbl.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("date"));
        bk_ssue_tbl.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("memberId"));
        bk_ssue_tbl.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("bookId"));


        try {
            connection = DBConnection.getInstance().getConnection();
            ObservableList<BookIssueTM> issue = bk_ssue_tbl.getItems();

            selectALl = connection.prepareStatement("SELECT * FROM issuetb");
            selectmemID = connection.prepareStatement("select name from memberdetail where id=?");
            selectbkdtl = connection.prepareStatement("select title,status from bookdetail where id=?");
            table = connection.prepareStatement("INSERT INTO issuetb values(?,?,?,?)");
            delete = connection.prepareStatement("DELETE FROM issuetb WHERE issueId=?");
            ResultSet rst = selectALl.executeQuery();

            while (rst.next()) {
                System.out.println("load");
                issue.add(new BookIssueTM(rst.getString(1),
                        rst.getString(2),
                        rst.getString(3),
                        rst.getString(4)));
            }

            bk_ssue_tbl.setItems(issue);
            mem_is_id.getItems().clear();
            ObservableList cmbmembers = mem_is_id.getItems();
            String sql2 = "select id from memberdetail";
            PreparedStatement pstm1 = connection.prepareStatement(sql2);
            ResultSet rst1 = pstm1.executeQuery();

            while (rst1.next()) {
                cmbmembers.add(rst1.getString(1));
            }

            book_id.getItems().clear();
            ObservableList cmbbooks = book_id.getItems();
            String sql3 = "select id from bookdetail";
            PreparedStatement pstm2 = connection.prepareStatement(sql3);
            ResultSet rst2 = pstm2.executeQuery();
            while (rst2.next()) {
                cmbbooks.add(rst2.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mem_is_id.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                if (mem_is_id.getSelectionModel().getSelectedItem() != null) {
                    Object selectedItem = mem_is_id.getSelectionModel().getSelectedItem();
                    if (selectedItem.equals(null) || mem_is_id.getSelectionModel().isEmpty()) {
                        return;
                    }
                    try {
                        selectmemID.setString(1, selectedItem.toString());
                        ResultSet rst = selectmemID.executeQuery();

                        if (rst.next()) {
                            txt_name.setText(rst.getString(1));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        book_id.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (book_id.getSelectionModel().getSelectedItem() != null) {
                    Object selectedItem = book_id.getSelectionModel().getSelectedItem();

                    try {
                        txt_title.clear();
                        selectbkdtl.setString(1, selectedItem.toString());
                        ResultSet rst = selectbkdtl.executeQuery();

                        if (rst.next()) {
                            if (rst.getString(2).equals("Available")) {
                                txt_title.setText(rst.getString(1));
                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR,
                                        "This book isn't available!",
                                        ButtonType.OK);
                                Optional<ButtonType> buttonType = alert.showAndWait();
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //button new action
    public void new_action(ActionEvent event) {
        // Generate a new Issue ID in a proper format
        String issueId = "SS" + System.currentTimeMillis(); // Example: SS1672538492345
        txt_issid.setText(issueId);

        // Clear other fields
        txt_isu_date.setValue(null);
        txt_name.clear();
        txt_title.clear();
        mem_is_id.setValue(null);
        book_id.setValue(null);
    }


    //button add action
    public void add_action(ActionEvent actionEvent) {
        String issueId = txt_issid.getText();
        String issueDate = (txt_isu_date.getValue() != null) ? txt_isu_date.getValue().toString() : null;
        String memberId = mem_is_id.getValue() != null ? mem_is_id.getValue().toString() : null;
        String bookId = book_id.getValue() != null ? book_id.getValue().toString() : null;
        String memberName = txt_name.getText();
        String bookTitle = txt_title.getText();

        // Validate inputs
        if (issueId.isEmpty() || issueDate == null || memberId == null || bookId == null || memberName.isEmpty() || bookTitle.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        // Add to table or data store
        System.out.println("Added new issue:");
        System.out.println("Issue ID: " + issueId);
        System.out.println("Issue Date: " + issueDate);
        System.out.println("Member ID: " + memberId);
        System.out.println("Member Name: " + memberName);
        System.out.println("Book ID: " + bookId);
        System.out.println("Book Title: " + bookTitle);
    }

    //button delete action
    public void delete_Action(ActionEvent actionEvent) throws SQLException {
        //BookIssueTM selectedItem = (BookIssueTM) FXCollections.observableList(DB.issued);
        BookIssueTM selectedItem = bk_ssue_tbl.getSelectionModel().getSelectedItem();
        if (bk_ssue_tbl.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Please select a raw.",
                    ButtonType.OK);
            Optional<ButtonType> buttonType = alert.showAndWait();
            return;
        } else {
            try {
                delete.setString(1, selectedItem.getIssueId());
                delete.executeUpdate();

                String sql2 = "Update bookdetail SET status=? where id=?";
                PreparedStatement pstm2 = connection.prepareStatement(sql2);
                String id = (String) book_id.getSelectionModel().getSelectedItem();
                pstm2.setString(1, "Available");
                pstm2.setString(2, id);
                pstm2.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Record deleted!",
                        ButtonType.OK);
                Optional<ButtonType> buttonType = alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        try {
            bk_ssue_tbl.getItems().clear();
            initialize();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void back_click(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/View/HomeFormView.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.bk_iss.getScene().getWindow();
        primaryStage.setScene(scene);

        TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
        tt.setFromX(-scene.getWidth());
        tt.setToX(0);
        tt.play();
    }

    public void playMouseEnterAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();

            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1.2);
            scaleT.setToY(1.2);
            scaleT.play();

            DropShadow glow = new DropShadow();
            glow.setColor(Color.YELLOW);
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        }
    }
}