package Test;
import Controller.BookFormController;
import Model.BookTM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class BookFormControllerTest {
        private BookFormController controller = new BookFormController();


    @BeforeEach
    public void setUp() throws ClassNotFoundException, SQLException {

        controller.initDatabaseConnection();
    }


    //---------------------------------------- QUESTION 2 ----------------------------------------//
    //BOUNDRY VALUE TESTING FOR ADDING MULTIPLEBOOKS AT THE SAME TIME
    @Test
    public void testAddMultipleBooks() throws SQLException {
        // Test adding a single book
        BookTM singleBook = new BookTM("99", "Title One", "Author One", "Available");
        assertTrue(controller.addBookToDatabase(singleBook), "Single book should be added successfully.");

        // Test adding multiple books at once
        for (int i = 2; i <= 10; i++) {
            BookTM book = new BookTM(String.valueOf(i), "Title ", "Author ", "Available");
            assertTrue(controller.addBookToDatabase(book), "Book " + i + " should be added successfully.");
        }

        // Test adding more than 10 books
        for (int i = 11; i <= 15; i++) {
            BookTM book = new BookTM(String.valueOf(i), "Title ", "Author ", "Available");
            assertTrue(controller.addBookToDatabase(book), "Book " + i + " should be added successfully.");
        }
    }

    //DECISION TABLE TESTING
    /*	    title	    auth	    ID	status	    mesg
        1	no	        no	        no	invalid	    plese fill your details
        2	no	        yes	        yes	invalid	    plese fill your details
        3	yes	        no	        yes	invalid	    plese fill your details
        4	yes 	    yes	        no	invalid	    plese fill your details
        5	no	        no	        yes	invalid	    plese fill your details
        6	yes	        no	        no	invalid	    plese fill your details
        7	no	        yes	        no	invalid	    plese fill your details
        8	yes	        yes	        yes	vaild	    book added successfully
        9	not valid   yes	        yes	invalid	    plese fill your details
        10	yes	        not valid 	yes	invalid	    plese fill your details
        11	not valid 	not valid 	yes	invalid	    plese fill your details */
    @Test
    public void testAddBooksWithDecisionTable() throws SQLException {
        // Case 1: ID, Title, and Author are empty (invalid)
        BookTM case1 = new BookTM("", "", "", "Available");
        assertFalse(controller.addBookToDatabase(case1), "Case 1: Empty fields should result in failure.");

        // Case 2: Title empty (invalid)
        BookTM case2 = new BookTM("B001", "", "author", "Available");
        assertFalse(controller.addBookToDatabase(case2), "Case 2: Empty fields should result in failure.");

        // Case 3: Author empty (invalid)
        BookTM case3 = new BookTM("B001", "book", "", "Available");
        assertFalse(controller.addBookToDatabase(case3), "Case 3: Empty Author should result in failure.");

        // Case 4: ID empty (invalid)
        BookTM case4 = new BookTM("", "book", "author", "Available");
        assertFalse(controller.addBookToDatabase(case4), "Case 4: Empty ID should result in failure.");

        // Case 5: Title &  Author empty (invalid)
        BookTM case5 = new BookTM("B001", "", "", "Available");
        assertFalse(controller.addBookToDatabase(case5), "Case 5: Empty Author & Title should result in failure.");

        // Case 6: Author & ID empty (invalid)
        BookTM case6 = new BookTM("", "book", "", "Available");
        assertFalse(controller.addBookToDatabase(case6), "Case 6: Empty author & ID should result in failure.");

        // Case 7: Title & Id empty (invalid)
        BookTM case7 = new BookTM("", "", "author", "Available");
        assertFalse(controller.addBookToDatabase(case7), "Case 7: Empty Title & ID should result in failure.");

        // Case 8: all filled correctly (valid)
        BookTM case8 = new BookTM("B001", "book", "author", "Available");
        assertTrue(controller.addBookToDatabase(case8), "Case 8: All data entered correctly should reult in success. ");

        // Case 9: Title incorrect (invalid)
        BookTM case9 = new BookTM("B001", "123", "author", "Available");
        assertFalse(controller.addBookToDatabase(case9), "Case 9: invalid title fields should result in failure.");

        // Case 10: Author incorrect (invalid)
        BookTM case10 = new BookTM("B001", "book", "123", "Available");
        assertFalse(controller.addBookToDatabase(case10), "Case 10: invalid Author should result in failure.");

        // Case 11: Author & Title incorrect (invalid)
        BookTM case11 = new BookTM("B001", "123", "123", "Available");
        assertFalse(controller.addBookToDatabase(case11), "Case 11: invalid Author and Title should result in failure.");
    }

    @Test
    public void testAddBooksWithMC_DCTest() throws SQLException {
        // Case 1: Title is empty, Author and ID are valid
        BookTM case1 = new BookTM("MC1", "", "Author One", "Available");
        assertFalse(controller.addBookToDatabase(case1), "Case 1: Empty title should result in failure.");

        // Case 2: Author is empty, Title and ID are valid
        BookTM case2 = new BookTM("MC2", "Title One", "", "Available");
        assertFalse(controller.addBookToDatabase(case2), "Case 2: Empty author should result in failure.");

        // Case 3: ID is empty, Title and Author are valid
        BookTM case3 = new BookTM("", "Title One", "Author One", "Available");
        assertFalse(controller.addBookToDatabase(case3), "Case 3: Empty ID should result in failure.");

        // Case 4: All fields are filled correctly
        BookTM case4 = new BookTM("MC4", "Title One", "Author One", "Available");
        assertTrue(controller.addBookToDatabase(case4), "Case 4: All fields valid should result in success.");
    }

    //--------------------------------------QUESTION 3----------------------------------------//
    @Test
    public void testUpdateFunction() throws SQLException {
        //no change (Valid)
        BookTM case1 = new BookTM("10", "Title", "Author", "Available");
        assertTrue(controller.updateBookInDatabase(case1), "Case 1: All valid should be successful");

        //incorrect title (invalid)
        BookTM case2 = new BookTM("10", "Title1", "Author", "Available");
        assertFalse(controller.updateBookInDatabase(case2), "Case 2: incorrect title format should fail ");

        //incorrect Author (invalid )
        BookTM case3 = new BookTM("10", "Title", "Author2", "Available");
        assertFalse(controller.updateBookInDatabase(case3), "Case 3: incorrect author format should fail ");

        //change Author (Valid)
        BookTM case4 = new BookTM("10", "Title", "sun", "Available");
        assertTrue(controller.updateBookInDatabase(case4), "Case 4: All valid should be successful");

        //change title (Valid)
        BookTM case5 = new BookTM("10", "moon", "sun", "Available");
        assertTrue(controller.updateBookInDatabase(case5), "Case 5: All valid should be successful");

        //change Author & Title (Valid)
        BookTM case6 = new BookTM("10", "Title", "Author", "Available");
        assertTrue(controller.updateBookInDatabase(case6), "Case 6: All valid should be successful");

        //change status (valid)
        BookTM case7 = new BookTM("10", "Title", "Author", "unAvailable");
        assertTrue(controller.updateBookInDatabase(case7), "Case 7: changing staus shouold be be valid ");

        //change status Author and Title
        BookTM case8 = new BookTM("10", "Layan", "Alateeq", "Available");
        assertTrue(controller.updateBookInDatabase(case8), "Case 8: changing staus shouold be be valid ");

        //when book with wrrong ID (invalid)
        BookTM case9 = new BookTM("98", "Title", "Author", "Available");
        assertFalse(controller.updateBookInDatabase(case9), "Case 9: incorrect ID should fail");


    }

    @Test
    void testDeleteBook() throws SQLException {

        assertTrue(controller.deleteBook("B001"), "Case 1: Valid book ID should be successfully deleted");

        // Case 2: Invalid book ID (does not exist in the database)
        assertFalse(controller.deleteBook("B999"), "Case 2: Non-existent book ID should fail deletion");
        // Case 3: Null book ID
        assertFalse(controller.deleteBook(null), "Case 3: Null book ID should return false");

        // Case 4: Empty book ID
        assertFalse(controller.deleteBook(""), "Case 4: Empty book ID should return false");

        // Case 5: Book ID with special characters (invalid input)
        assertFalse(controller.deleteBook("@!#$"), "Case 5: Invalid book ID format should return false");
}

}
