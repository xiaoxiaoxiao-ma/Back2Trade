package ma.jbt.cli;

import java.util.List;

import ma.jbt.Main;

public class ConnectCommandHandler implements CommandHandler {

    @Override
    public void exec(List<String> args) {
        Main.tryConnectToServer();;
    }

}
