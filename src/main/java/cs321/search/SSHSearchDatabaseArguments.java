package cs321.search;

import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;

/**
 * Represents the arguments for the SSHSearchDatabase program.
 * @author MichaelR25
 */
public class SSHSearchDatabaseArguments {
    private static final String TYPE = "--type";
    private static final String DATABASE_PATH = "--database";
    private static final String FREQUENCY = "--top-frequency";

    private String type;
    private String databasePath;
    private int frequency;

    /**
     * Constructs a new SSHSearchDatabaseArguments object by parsing the command-line arguments.
     * @param args the command-line arguments
     * @throws ParseArgumentException if any argument is invalid or missing
     */
    SSHSearchDatabaseArguments(String[] args) throws ParseArgumentException{
        String[] parts;
        String frequencyArg = null;
        for (String arg : args) {
            parts = arg.split("=");
            if (parts.length != 2) {
                throw new ParseArgumentException("Invalid argument format: " + arg);
            }

            if (parts[0].equals(TYPE)) {
                parseType(parts[1]);
            } else if (parts[0].equals(DATABASE_PATH)) {
                parseDatabasePath(parts[1]);
            } else if (parts[0].equals(FREQUENCY)) {
                frequencyArg = parts[1];
            } else {
                throw new ParseArgumentException("Unknown argument: " + arg);
            }
        }

        if (type == null) {
            throw new ParseArgumentException("Missing required argument: " + TYPE);
        }
        if (databasePath == null) {
            throw new ParseArgumentException("Missing required argument: " + DATABASE_PATH);
        }
        if (!type.equals("test")) {
            if (frequencyArg != null) {
                parseFrequency(frequencyArg);
            } else {
                throw new ParseArgumentException("Missing required argument: " + FREQUENCY);
            }
        }
    }

    /**
     * Returns the type of the SSHSearchDatabase program.
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the database path for the SSHSearchDatabase program.
     * @return the database path
     */
    public String getDatabasePath() {
        return databasePath;
    }

    /**
     * Returns the frequency for the SSHSearchDatabase program.
     * @return the frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Prints the usage information for the SSHSearchDatabase program.
     */
    public static void printUsage() {
        System.out.println("Usage: java -jar build/libs/SSHSearchDatabase.jar " +
            "--type=<type> --database=<file-path> --top-frequency=<10/25/50>");
    }

    /**
     * Parses the type argument for the SSHSearchDatabase program.
     * @param type the type argument
     * @throws ParseArgumentException if the type is invalid
     */
    private void parseType(String type) throws ParseArgumentException {
        switch (type) {
            case "test":
                this.type = "test";
                break;
            case "accepted-time":
                this.type = "acceptedtime";
                break;
            case "accepted-ip":
                this.type = "acceptedip";
                break;
            case "invalid-ip":
                this.type = "invalidip";
                break;
            case "invalid-time":
                this.type = "invalidtime";
                break;
            case "failed-ip":
                this.type = "failedip";
                break;
            case "failed-time":
                this.type = "failedtime";
                break;
            case "reverseaddress-ip":
                this.type = "reverseaddressip";
                break;
            case "reverseaddress-time":
                this.type = "reverseaddresstime";
                break;
            case "user-ip":
                this.type = "userip";
                break;
            default:
                throw new ParseArgumentException("Invalid type: " + type);
        }
    }

    /**
     * Parses the database path argument for the SSHSearchDatabase program.
     * @param databasePath the database path argument
     * @throws ParseArgumentException if the database path is invalid
     */
    private void parseDatabasePath(String databasePath) throws ParseArgumentException {
        if (databasePath.isEmpty()) {
            throw new ParseArgumentException("Database path cannot be empty");
        }
        this.databasePath = databasePath;
    }

    /**
     * Parses the frequency argument for the SSHSearchDatabase program.
     * @param frequency the frequency argument
     * @throws ParseArgumentException if the frequency is invalid
     */
    private void parseFrequency(String frequency) throws ParseArgumentException {
        int parsedFrequency;
        try {
            parsedFrequency = Integer.parseInt(frequency);
        } catch (NumberFormatException e) {
            throw new ParseArgumentException("Invalid frequency: " + frequency);
        }

        switch (parsedFrequency) {
            case 10:
            case 25:
            case 50:
                this.frequency = parsedFrequency;
                break;
            default:
                throw new ParseArgumentException("Frequency out of range: " + frequency);
        }
    }

}
