package Controller;

import Model.MemberTM;
import db.DBConnection;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
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
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberFormController {
    public TextField mem_id;
    public TextField mem_nme;
    public TextField mem_addss;
    public TextField mem_num;
    public TableView<MemberTM> mem_tbl;
    public ImageView img_bk;
    public AnchorPane root;
    public Button btn_new;
    public Button btn_add;

    private Connection connection;

    public void initialize() {
        mem_id.setDisable(true);

        mem_tbl.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        mem_tbl.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        mem_tbl.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
        mem_tbl.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("contact"));

        loadAllMembers();

        mem_tbl.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                mem_id.setText(String.valueOf(newValue.getId()));
                mem_nme.setText(newValue.getName());
                mem_addss.setText(newValue.getAddress());
                mem_num.setText(newValue.getContact());
                btn_add.setText("Update");
                mem_id.setDisable(true);
            }
        });
    }

    public void loadAllMembers() {
        ObservableList<MemberTM> members = FXCollections.observableArrayList();
        try {
            connection = DBConnection.getInstance().getConnection();
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM memberdetail");
            ResultSet rst = stm.executeQuery();
            while (rst.next()) {
                members.add(new MemberTM(
                        rst.getInt(1),
                        rst.getString(2),
                        rst.getString(3),
                        rst.getString(4)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mem_tbl.setItems(members);
    }

    private int generateNewId() {
        try {
            connection = DBConnection.getInstance().getConnection();
            PreparedStatement stm = connection.prepareStatement("SELECT MAX(id) FROM memberdetail");
            ResultSet rst = stm.executeQuery();
            if (rst.next()) {
                return rst.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Default ID if the table is empty
    }

    public void btn_new(ActionEvent actionEvent) {
        mem_id.setDisable(false);
        mem_id.clear();
        mem_nme.clear();
        mem_addss.clear();
        mem_num.clear();
        btn_add.setText("Add");
        mem_id.setText(String.valueOf(generateNewId()));
    }

    // Modify the btn_add to handle the result of insertMember and updateMember
    public void btn_add(ActionEvent actionEvent) {
        if (mem_id.getText().isEmpty() || mem_nme.getText().isEmpty() || mem_addss.getText().isEmpty() || mem_num.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please fill all fields.", ButtonType.OK).show();
            return;
        }

        MemberTM member = new MemberTM(
                Integer.parseInt(mem_id.getText()),
                mem_nme.getText(),
                mem_addss.getText(),
                mem_num.getText()
        );

        boolean isSuccessful;
        if ("Add".equals(btn_add.getText())) {
            isSuccessful = insertMember(member);
        } else {
            isSuccessful = updateMember(member);
        }

        if (isSuccessful) {
            loadAllMembers();
            new Alert(Alert.AlertType.INFORMATION, "Operation Successful", ButtonType.OK).show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Operation Failed", ButtonType.OK).show();
        }
    }

    public boolean insertMember(MemberTM member) {
        if(!validateInput(member)){
            return false ;
        }
        try {
            connection = DBConnection.getInstance().getConnection();
            PreparedStatement stm = connection.prepareStatement("INSERT INTO memberdetail VALUES (?, ?, ?, ?)");
            stm.setInt(1, member.getId());
            stm.setString(2, member.getName());
            stm.setString(3, member.getAddress());
            stm.setString(4, member.getContact());
            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0; // Return true if the insert was successful
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false in case of an error
    }

    // Modify the updateMember method to return a boolean
    public boolean updateMember(MemberTM member) {
        if(!validateInput(member)){
            return false;
        }
        try {
            connection = DBConnection.getInstance().getConnection();
            PreparedStatement stm = connection.prepareStatement("UPDATE memberdetail SET name=?, address=?, contact=? WHERE id=?");
            stm.setString(1, member.getName());
            stm.setString(2, member.getAddress());
            stm.setString(3, member.getContact());
            stm.setInt(4, member.getId());
            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0; // Return true if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false in case of an error
    }

    // Modify the btn_dtl to handle the result of deleteMember
    public void btn_dtl(ActionEvent actionEvent) {
        MemberTM selectedMember = mem_tbl.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a member.", ButtonType.OK).show();
            return;
        }

        boolean isDeleted = deleteMember(selectedMember.getId());
        if (isDeleted) {
            loadAllMembers();
            new Alert(Alert.AlertType.INFORMATION, "Member Deleted Successfully", ButtonType.OK).show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to Delete Member", ButtonType.OK).show();
        }
    }

    // Modify the deleteMember method to return a boolean
    public boolean deleteMember(int id) {
        try {
            connection = DBConnection.getInstance().getConnection();
            PreparedStatement stm = connection.prepareStatement("DELETE FROM memberdetail WHERE id=?");
            stm.setInt(1, id);
            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0; // Return true if the deletion was successful
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false in case of an error
    }

    public void img_back(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/View/HomeFormView.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.root.getScene().getWindow();
        primaryStage.setScene(scene);
    }
    public void playMouseEnterAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();

            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1.2);
            scaleT.setToY(1.2);
            scaleT.play();

            javafx.scene.effect.DropShadow glow = new DropShadow();
            glow.setColor(Color.YELLOW);
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        }
    }

    private boolean validateInput(MemberTM member) {
        // Check if any required field (ID, Name, Address, Contact) is empty
        if (member.getName().isEmpty() || member.getAddress().isEmpty() || member.getContact().isEmpty()) {
            return false;
        }

        // Validate that Name only contains letters and spaces
        if (!member.getName().matches("^[A-Za-z\\s]+$")) {
            return false;
        }

        // Validate that Address only contains letters, numbers, spaces, and common symbols (e.g., commas, hyphens)
        if (!member.getAddress().matches("^[A-Za-z0-9\\s,.-]+$")) {
            return false;
        }

        // Validate that Contact contains only digits and has a length of 10 characters (assuming it's a phone number)
        if (!member.getContact().matches("^\\d{10}$")) {
            return false;
        }

        // If all conditions are met, return true
        return true;
    }


    public List<MemberTM> getAllMembers() throws SQLException {
        List<MemberTM> members = new ArrayList<>();
        String query = "SELECT * FROM memberdetail";

        // Open a statement and execute the query
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Iterate over the result set
            while (rs.next()) {
                // Assuming 'ID', 'Name', 'Address', and 'Contact' are columns in your database
                int id = rs.getInt("ID");
                String name = rs.getString("Name");
                String address = rs.getString("Address");
                String contact = rs.getString("Contact");

                // Create a new MemberTM object and add it to the list
                MemberTM member = new MemberTM(id, name, address, contact);
                members.add(member);
            }
        }

        return members;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
