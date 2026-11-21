package cs321.search;

import cs321.btree.BTree;
import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;


/**
 * The driver class for searching a Database of a B-Tree.
 */
public class SSHSearchDatabase
{
	private static SSHSearchDatabaseArguments arguments;
    public static void main(String[] args) throws ParseArgumentException
    {
        System.out.println("Hello world from cs321.search.SSHSearchDatabase.main");
        try {
            arguments = new SSHSearchDatabaseArguments(args);
            System.out.println("Type: " + arguments.getType());
            System.out.println("Database Path: " + arguments.getDatabasePath());
            System.out.println("Frequency: " + arguments.getFrequency());
        }
        catch (ParseArgumentException e) {
            System.out.println("Error parsing arguments: " + e.getMessage());
            arguments.printUsage();
        }
    }

}
