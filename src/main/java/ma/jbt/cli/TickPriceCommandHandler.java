package ma.jbt.cli;

import java.util.List;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;

import ma.jbt.Main;

public class TickPriceCommandHandler extends DataCommandHandler {

    @Override
    public void exec(List<String> args) {
        super.exec(args);
        Contract contract = getContract();
        EClientSocket client = Main.getClient();
        client.reqTickByTickData(
            2001,       // tickerId / reqId
            contract,
            "BidAsk",         // genericTickList
            0,
            false
        );
    }

}
