package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBConnection {

    private static DBConnection dbConnection;
    private Connection connection;

    private DBConnection() {
        try {
            // Use the updated MySQL driver
            Class.forName("com.mysql.jdbc.Driver");

            // Establish connection to the database
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/library",
                    "root",
                    ""
            );

            // Check if tables exist, if not create them
            PreparedStatement pstm = connection.prepareStatement("SHOW TABLES");
            ResultSet resultSet = pstm.executeQuery();

            if (!resultSet.next()) { // If no tables exist
                String sql = """
                    CREATE TABLE IF NOT EXISTS bookdetail (
                        id VARCHAR(10) NOT NULL,
                        title VARCHAR(15) DEFAULT NULL,
                        author VARCHAR(20) DEFAULT NULL,
                        status VARCHAR(20) DEFAULT NULL,
                        PRIMARY KEY (id)
                    ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

                    CREATE TABLE IF NOT EXISTS memberdetail (
                        id INT(11) NOT NULL,
                        name VARCHAR(50) DEFAULT NULL,
                        address VARCHAR(50) DEFAULT NULL,
                        contact VARCHAR(12) DEFAULT NULL,
                        PRIMARY KEY (id)
                    ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

                    CREATE TABLE IF NOT EXISTS issuetb (
                        issueId VARCHAR(10) NOT NULL,
                        date DATE DEFAULT NULL,
                        memberId INT(11) DEFAULT NULL,
                        bookid VARCHAR(10) DEFAULT NULL,
                        PRIMARY KEY (issueId),
                        FOREIGN KEY (memberId) REFERENCES memberdetail (id),
                        FOREIGN KEY (bookid) REFERENCES bookdetail (id)
                    ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

                    CREATE TABLE IF NOT EXISTS returndetail (
                        id INT(11) NOT NULL,
                        issuedDate DATE NOT NULL,
                        returnedDate DATE DEFAULT NULL,
                        fine INT(10) DEFAULT NULL,
                        issueid VARCHAR(10) DEFAULT NULL,
                        PRIMARY KEY (id),
                        FOREIGN KEY (issueid) REFERENCES issuetb (issueId)
                    ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
                """;

                pstm = connection.prepareStatement(sql);
                pstm.execute();
            }

        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            throw new RuntimeException("Database connection failed: " + e.getMessage());
        }
    }

    public static DBConnection getInstance() {
        if (dbConnection == null) {
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }

    public Connection getConnection() {
        return connection;
    }
}
