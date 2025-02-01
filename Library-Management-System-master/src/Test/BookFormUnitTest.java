import Controller.BookFormController;
import Model.BookTM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookFormUnitTest {

    @Mock
    private BookFormController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddAndRetrieveBooks() throws SQLException {
        BookTM book = new BookTM("B100", "Unit Testing", "Layan", "Available");
        when(controller.addBookToDatabase(book)).thenReturn(true);
        when(controller.getAllBooks()).thenReturn(List.of(book));

        assertTrue(controller.addBookToDatabase(book), "Book should be added successfully.");
        List<BookTM> books = controller.getAllBooks();
        assertEquals(1, books.size(), "There should be exactly one book retrieved.");
        assertEquals("B100", books.get(0).getId(), "The retrieved book ID should match.");
    }

    @Test
    public void testUpdateAndRetrieveBook() throws SQLException {
        BookTM updatedBook = new BookTM("B101", "New Title", "New Author", "Unavailable");
        when(controller.updateBookInDatabase(updatedBook)).thenReturn(true);
        when(controller.getAllBooks()).thenReturn(List.of(updatedBook));

        assertTrue(controller.updateBookInDatabase(updatedBook), "Book update should be successful.");
        List<BookTM> books = controller.getAllBooks();
        assertEquals(1, books.size(), "There should be exactly one book retrieved.");
        assertEquals("New Title", books.get(0).getTitle(), "The title should be updated.");
    }

    @Test
    public void testAddAndDeleteBook() throws SQLException {
        BookTM book = new BookTM("B102", "Book to Delete", "Author", "Available");
        when(controller.addBookToDatabase(book)).thenReturn(true);
        when(controller.deleteBook("B102")).thenReturn(true);
        when(controller.getAllBooks()).thenReturn(List.of());

        assertTrue(controller.addBookToDatabase(book), "Book should be added successfully.");
        assertTrue(controller.deleteBook("B102"), "Book deletion should be successful.");
        assertTrue(controller.getAllBooks().isEmpty(), "No books should be present after deletion.");
    }

    @Test
    public void testAddMultipleBooksAndRetrieveAll() throws SQLException {
        List<BookTM> books = Arrays.asList(
                new BookTM("B101", "Title1", "Author1", "Available"),
                new BookTM("B102", "Title2", "Author2", "Available"),
                new BookTM("B103", "Title3", "Author3", "Available")
        );
        when(controller.getAllBooks()).thenReturn(books);

        List<BookTM> retrievedBooks = controller.getAllBooks();
        assertEquals(3, retrievedBooks.size(), "There should be 3 books retrieved.");
    }
}