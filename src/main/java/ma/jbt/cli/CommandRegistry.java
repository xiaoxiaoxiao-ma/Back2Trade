package ma.jbt.cli;

import java.util.HashMap;

public class CommandRegistry {

    private HashMap<String, CommandHandler> commands;

    public CommandRegistry() {
        commands = new HashMap<String, CommandHandler>();
    }

    public void register(String command, CommandHandler handler) {
        commands.put(command, handler);
    }

    public CommandHandler get(String command) {
        return commands.get(command);
    }
}
