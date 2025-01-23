package Test;



import Controller.BookFormController;
import Model.BookTM;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookFormControllerIntgTest {

    private BookFormController controller;

    @BeforeEach
    public void setUp() throws ClassNotFoundException, SQLException {
        controller = new BookFormController();
        controller.initDatabaseConnection();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Clean up database after each test
        List<BookTM> allBooks = controller.getAllBooks();
        for (BookTM book : allBooks) {
            controller.deleteBook(book.getId());
        }
        controller.closeConnection();
    }

    @Test
    public void testAddAndRetrieveBooks() throws SQLException {
        // Add a new book
        BookTM book = new BookTM("B100", "Integration", "John Doe", "Available");
        assertTrue(controller.addBookToDatabase(book), "Book should be added successfully.");

        // Retrieve the book
        List<BookTM> books = controller.getAllBooks();
        assertEquals(1, books.size(), "There should be exactly one book in the database.");
        assertEquals("B100", books.get(0).getId(), "The retrieved book ID should match.");
    }

    @Test
    public void testUpdateAndRetrieveBook() throws SQLException {
        // Add a book
        BookTM book = new BookTM("B101", "Old Title", "Old Author", "Available");
        controller.addBookToDatabase(book);

        // Update the book
        BookTM updatedBook = new BookTM("B101", "New Title", "New Author", "Unavailable");
        assertTrue(controller.updateBookInDatabase(updatedBook), "Book update should be successful.");

        // Retrieve the updated book
        List<BookTM> books = controller.getAllBooks();
        assertEquals(1, books.size(), "There should be exactly one book in the database.");
        assertEquals("New Title", books.get(0).getTitle(), "The title should be updated.");
        assertEquals("New Author", books.get(0).getAuthor(), "The author should be updated.");
        assertEquals("Unavailable", books.get(0).getStatus(), "The status should be updated.");
    }

    @Test
    public void testAddAndDeleteBook() throws SQLException {
        // Add a book
        BookTM book = new BookTM("B102", "Book to Delete", "Author", "Available");
        controller.addBookToDatabase(book);

        // Delete the book
        assertTrue(controller.deleteBook("B102"), "Book deletion should be successful.");

        // Verify that the book is deleted
        List<BookTM> books = controller.getAllBooks();
        assertTrue(books.isEmpty(), "The database should be empty after deletion.");
    }

    @Test
    public void testAddMultipleBooksAndRetrieveAll() throws SQLException {
        // Add multiple books
        for (int i = 1; i <= 5; i++) {
            BookTM book = new BookTM("B10" + i, "Title", "Author", "Available");
            controller.addBookToDatabase(book);
        }

        // Retrieve all books
        List<BookTM> books = controller.getAllBooks();
        assertEquals(5, books.size(), "There should be 5 books in the database.");

        // Verify each book
        for (int i = 1; i <= 5; i++) {
            BookTM book = books.get(i - 1);
            assertEquals("B10" + i, book.getId(), "Book ID should match.");
            assertEquals("Title", book.getTitle(), "Book title should match.");
            assertEquals("Author", book.getAuthor(), "Book author should match.");
        }
    }
}

