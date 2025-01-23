import Controller.MemberFormController;
import Model.MemberTM;
import db.DBConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class MemberFormControllerTest {

    private MemberFormController controller;

    @BeforeEach
    void setUp() throws SQLException {
        // Initialize the controller
        controller = new MemberFormController();

        // Set up the database (in-memory for simplicity)
    }


    // Boundary value testing for adding multiple members
    @Test
    void testAddMultipleMembers() throws SQLException {
        // Test adding a single member
        MemberTM singleMember = new MemberTM(1, "John Doe", "123 Street", "1234567890");
        assertTrue(controller.insertMember(singleMember), "Single member should be added successfully.");

        // Test adding multiple members at once
        for (int i = 2; i <= 10; i++) {
            MemberTM member = new MemberTM(i, "Member", "Street " + i, "1234567890");
            assertTrue(controller.insertMember(member), "Member " + i + " should be added successfully.");
        }

        // Test adding more than 10 members (Boundary test)
        for (int i = 11; i <= 15; i++) {
            MemberTM member = new MemberTM(i, "Member", "Street " + i, "1234567890");
            assertTrue(controller.insertMember(member), "Member " + i + " should be added successfully.");
        }
    }

    // Decision table testing for adding members
    @Test
    void testAddMembersWithDecisionTable() throws SQLException {
        // Case 1: Empty fields (invalid)
        MemberTM case1 = new MemberTM(0, "", "", "");
        assertFalse(controller.insertMember(case1), "Case 1: Empty fields should result in failure.");

        // Case 2: Valid ID, invalid name
        MemberTM case2 = new MemberTM(1, "", "123 Street", "123456789");
        assertFalse(controller.insertMember(case2), "Case 2: Empty name should result in failure.");

        // Case 3: Valid ID, invalid address
        MemberTM case3 = new MemberTM(2, "John Doe", "", "123456789");
        assertFalse(controller.insertMember(case3), "Case 3: Empty address should result in failure.");

        // Case 4: Valid ID, invalid contact
        MemberTM case4 = new MemberTM(3, "John Doe", "123 Street", "");
        assertFalse(controller.insertMember(case4), "Case 4: Empty contact should result in failure.");

        // Case 5: All fields valid (valid)
        MemberTM case5 = new MemberTM(4, "John Doe", "123 Street", "1234567890");
        assertTrue(controller.insertMember(case5), "Case 5: All fields filled should be valid.");

        // Case 6: Invalid ID (should fail)
        MemberTM case6 = new MemberTM(-1, "John Doe", "123 Street", "123456789");
        assertFalse(controller.insertMember(case6), "Case 6: Invalid ID should result in failure.");
    }


    // Testing the update functionality
    @Test
    void testUpdateMember() throws SQLException {
        // No change (Valid)
        MemberTM member = new MemberTM(5, "John Doe", "123 Street", "1234567890");
        controller.insertMember(member);
        assertTrue(controller.updateMember(member), "No changes: Update should succeed.");

        // Incorrect name (Invalid)
        member.setName("Jane_Doe");
        member.setAddress("456 Avenue");
        member.setContact("9876504321");
        assertFalse(controller.updateMember(member), "Name and address change should be valid.");

        // mpty name (Invalid)
        member.setName("");
        member.setAddress("456 Avenue");
        member.setContact("9876504321");
        assertFalse(controller.updateMember(member), "Name and address change should be valid.");


        // Incorrect address (Invalid)
        member.setName("Jane Doe");
        member.setAddress("45/6 -Avenue");
        member.setContact("9876504321");
        assertFalse(controller.updateMember(member), "Name and address change should be valid.");

        // Invalid contact (Invalid)
        member.setAddress("456 Avenue");
        member.setContact("abcd");
        assertFalse(controller.updateMember(member), "Invalid contact format should fail.");

        // Change contact to a valid format
        member.setContact("9876543201");
        assertTrue(controller.updateMember(member), "Valid contact should pass.");

        //empty contact
        member.setContact("");
        assertFalse(controller.updateMember(member), "empty contact format should fail.");

        //empty address
        member.setContact("9876543201");
        member.setAddress("");
        assertFalse(controller.updateMember(member), "empty address format should fail.");
    }

    // Testing the delete functionality
    @Test
    void testDeleteMember() throws SQLException {
        // Insert a member first
        MemberTM member = new MemberTM(6, "John Doe", "123 Street", "1234567890");
        controller.insertMember(member);

        // Test the delete function
        assertTrue(controller.deleteMember(member.getId()), "Valid member ID should be successfully deleted.");

        // Check if the member was deleted from the database
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM memberdetail WHERE id = ?");
        stmt.setInt(1, member.getId());
        ResultSet rs = stmt.executeQuery();

        assertFalse(rs.next(), "Member should be deleted from the database.");
    }
}
