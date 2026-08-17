package ma.jbt.cli;

import java.util.List;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;

import ma.jbt.Main;

public class HistoricalDataCommandHandler extends DataCommandHandler {
    @Override
    public void exec(List<String> args) {
        super.exec(args);
        EClientSocket client = Main.getClient();
        Contract resulContract = getContract();
        client.reqHistoricalData(1001, resulContract, "", "1 Y", "1 day", "TRADES", 1, 1, false, null);

    }
}
