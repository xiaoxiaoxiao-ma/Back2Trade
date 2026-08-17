package ma.jbt.cli;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;

import ma.jbt.Logger;
import ma.jbt.MBar;
import ma.jbt.Main;

public class NewsCommandHandler implements CommandHandler {

    @Override
    public void exec(List<String> args) {
        Contract contract = new Contract();
    
        contract.symbol("BZ:BZ_ALL");
        contract.secType("NEWS");
        contract.exchange("BZ");

		try {
			Thread.sleep(1000);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
        EClientSocket client = Main.getClient();
        client.reqMktData(
            3001,
            contract,
            "mdoff,292",
            false,
            false,
            null
        );
        System.out.println("request sent");
    }
}
