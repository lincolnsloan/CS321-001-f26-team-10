package cs321.search;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cs321.btree.BTree;
import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;


/**
 * The driver class for searching a Database of a B-Tree.
 * @author MichaelR25
 */
public class SSHSearchDatabase
{
    private static final Object[][] TEST_DATA = {
        {"Accepted-111.222.107.90", 25},
        {"Accepted-112.96.173.55", 3},
        {"Accepted-112.96.33.40", 3},
        {"Accepted-113.116.236.34", 6},
        {"Accepted-113.118.187.34", 2},
        {"Accepted-113.99.127.215", 2},
        {"Accepted-119.137.60.156", 1},
        {"Accepted-119.137.62.123", 9},
        {"Accepted-119.137.62.142", 1},
        {"Accepted-119.137.63.195", 14},
        {"Accepted-123.255.103.142", 5},
        {"Accepted-123.255.103.215", 5},
        {"Accepted-137.189.204.138", 1},
        {"Accepted-137.189.204.155", 1},
        {"Accepted-137.189.204.220", 1},
        {"Accepted-137.189.204.236", 1},
        {"Accepted-137.189.204.246", 1},
        {"Accepted-137.189.204.253", 3},
        {"Accepted-137.189.205.44", 2},
        {"Accepted-137.189.206.152", 1},
        {"Accepted-137.189.206.243", 1},
        {"Accepted-137.189.207.18", 1},
        {"Accepted-137.189.207.28", 1},
        {"Accepted-137.189.240.159", 1},
        {"Accepted-137.189.241.19", 2}
    };

    public static void main(String[] args) throws ParseArgumentException
    {
        SSHSearchDatabaseArguments arguments;
        try {
            arguments = new SSHSearchDatabaseArguments(args);
        }
        catch (ParseArgumentException e) {
            System.err.println("Error parsing arguments: " + e.getMessage());
            SSHSearchDatabaseArguments.printUsage();
            System.exit(-1);
            return;
        }

        if (arguments.getType().equals("test")) {
            createTestDatabase(arguments.getDatabasePath());
        } else {
            searchDatabase(arguments.getDatabasePath(), arguments.getType(), arguments.getFrequency());
        }
        System.exit(0);
    }

    /**
     * Searches the specified table in the SQLite database for the top N entries by frequency.
     * @param databasePath the path to the SQLite database file
     * @param tableName the name of the table to search
     * @param frequency the number of top entries to retrieve
     * 
     * @author MichaelR25
     */
    private static void searchDatabase(String databasePath, String tableName, int frequency) {
        Connection connection = null;
        String query = "SELECT key, frequency FROM " + tableName + " ORDER BY frequency DESC, key ASC LIMIT ?";
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setQueryTimeout(15);
            preparedStatement.setInt(1, frequency);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getString("key") + " " + resultSet.getInt("frequency"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Creates a test SQLite database with a table named "acceptedip" and inserts test data.
     * @param databasePath the path to the SQLite database file
     * @author MichaelR25
     */
    private static void createTestDatabase(String databasePath) {
        String insertQuery = "INSERT INTO acceptedip (key, frequency) VALUES (?, ?)";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            Statement statement = connection.createStatement();

            statement.setQueryTimeout(15);
            statement.executeUpdate("DROP TABLE IF EXISTS acceptedip");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS acceptedip (key TEXT, frequency INTEGER)");

            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            for (Object[] row : TEST_DATA) {
                preparedStatement.setString(1, (String) row[0]);
                preparedStatement.setInt(2, (Integer) row[1]);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
