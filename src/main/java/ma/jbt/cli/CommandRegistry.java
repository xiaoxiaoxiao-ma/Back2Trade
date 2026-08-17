package ma.jbt.cli;

import java.util.HashMap;

public class CommandRegistry {

    private HashMap<String, CommandHandler> commands;
    private HashMap<String, String> descriptions;

    public CommandRegistry() {
        commands = new HashMap<String, CommandHandler>();
        descriptions = new HashMap<String, String>();
    }

    public void register(String command, String description, CommandHandler handler) {
        commands.put(command, handler);
        descriptions.put(command, description);
    }

    public CommandHandler get(String command) {
        return commands.get(command);
    }

    public void printUsage() {
        for (String command : commands.keySet()) {
            System.out.println("\t" + command + "\t" + descriptions.get(command));
        }
    }
}
