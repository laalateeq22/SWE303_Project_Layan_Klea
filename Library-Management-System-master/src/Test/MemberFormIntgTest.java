package Test;

import Controller.MemberFormController;
import Model.MemberTM;
import db.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MemberFormIntgTest {

    private MemberFormController controller;

    @BeforeEach
    public void setUp() throws SQLException {
        // Initialize the controller
        controller = new MemberFormController();
        controller.initialize();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Clean up database after each test
        Connection connection = DBConnection.getInstance().getConnection();
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DELETE FROM memberdetail");  // Assumes all members are stored in 'memberdetail'
        controller.closeConnection();
    }

    @Test
    public void testAddAndRetrieveMember() throws SQLException {
        // Add a new member
        MemberTM member = new MemberTM(1, "John Doe", "123 Street", "1234567890");
        assertTrue(controller.insertMember(member), "Member should be added successfully.");

        // Retrieve the member
        List<MemberTM> members = controller.getAllMembers();
        assertEquals(1, members.size(), "There should be exactly one member in the database.");
        assertEquals(1, members.get(0).getId(), "The retrieved member ID should match.");
    }

    @Test
    public void testUpdateAndRetrieveMember() throws SQLException {
        // Add a member
        MemberTM member = new MemberTM(2, "Old Name", "Old Street", "9876543210");
        controller.insertMember(member);

        // Update the member
        MemberTM updatedMember = new MemberTM(2, "New Name", "New Street", "0123456789");
        assertTrue(controller.updateMember(updatedMember), "Member update should be successful.");

        // Retrieve the updated member
        List<MemberTM> members = controller.getAllMembers();
        assertEquals(1, members.size(), "There should be exactly one member in the database.");
        assertEquals("New Name", members.get(0).getName(), "The member's name should be updated.");
        assertEquals("New Street", members.get(0).getAddress(), "The member's address should be updated.");
        assertEquals("0123456789", members.get(0).getContact(), "The member's contact should be updated.");
    }

    @Test
    public void testAddAndDeleteMember() throws SQLException {
        // Add a member
        MemberTM member = new MemberTM(3, "Member to Delete", "123 Street", "1112223333");
        controller.insertMember(member);

        // Delete the member
        assertTrue(controller.deleteMember(3), "Member deletion should be successful.");

        // Verify that the member is deleted
        List<MemberTM> members = controller.getAllMembers();
        assertTrue(members.isEmpty(), "The database should be empty after deletion.");
    }

    @Test
    public void testAddMultipleMembersAndRetrieveAll() throws SQLException {
        // Add multiple members
        for (int i = 1; i <= 5; i++) {
            MemberTM member = new MemberTM(i, "Member " + i, "Street " + i, "1234567890");
            controller.insertMember(member);
        }

        // Retrieve all members
        List<MemberTM> members = controller.getAllMembers();
        assertEquals(5, members.size(), "There should be 5 members in the database.");

        // Verify each member
        for (int i = 1; i <= 5; i++) {
            MemberTM member = members.get(i - 1);
            assertEquals(i, member.getId(), "Member ID should match.");
            assertEquals("Member " + i, member.getName(), "Member name should match.");
            assertEquals("Street " + i, member.getAddress(), "Member address should match.");
            assertEquals("1234567890", member.getContact(), "Member contact should match.");
        }
    }
}
