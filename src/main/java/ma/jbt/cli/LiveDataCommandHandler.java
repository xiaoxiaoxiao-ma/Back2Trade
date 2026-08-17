package ma.jbt.cli;

import java.util.List;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;

import ma.jbt.Main;

public class LiveDataCommandHandler extends DataCommandHandler {

    @Override
    public void exec(List<String> args) {
        super.exec(args);
        Contract resultContract = getContract();
        EClientSocket client = Main.getClient();
        client.reqMktData(1001, resultContract, "233", false, false, null);
    }

}
