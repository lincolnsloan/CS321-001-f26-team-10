package cs321.search;

import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;

public class SSHSearchDatabaseArguments {
    private static final String TYPE = "--type";
    private static final String DATABASE_PATH = "--database";
    private static final String FREQUENCY = "--top-frequency";

    private String type;
    private String databasePath;
    private int frequency;


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

    public String getType() {
        return type;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public int getFrequency() {
        return frequency;
    }

    public void printUsage() {
        System.out.println("Usage: java -jar build/libs/SSHSearchDatabase.jar " +
            "--type=<type> --database=<file-path> --top-frequency=<10/25/50>");
    }

    private void parseType(String type) throws ParseArgumentException {
        switch (type) {
            case "test":
            case "accepted-time":
            case "accepted-ip":
            case "invalid-ip":
            case "invalid-time":
            case "failed-ip":
            case "failed-time":
            case "reverseaddress-ip":
            case "reverseaddress-time":
            case "user-ip":
                this.type = type;
                break;
            default:
                throw new ParseArgumentException("Invalid type: " + type);
        }
    }

    private void parseDatabasePath(String databasePath) throws ParseArgumentException {
        if (databasePath.isEmpty()) {
            throw new ParseArgumentException("Database path cannot be empty");
        }
        this.databasePath = databasePath;
    }

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
