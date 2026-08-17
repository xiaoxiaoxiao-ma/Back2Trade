package ma.jbt.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.ib.client.Contract;

import ma.jbt.Logger;
import ma.jbt.MBar;
import ma.jbt.Main;

public abstract class DataCommandHandler implements CommandHandler {

    private Contract contract;

    @Override
    public void exec(List<String> args) {
        Scanner scanner = Main.getScanner();
        contract = new Contract();
        String currentSecName;
        Map<String,List<MBar>> secBarsMap = Main.getSecBarsMap();

		System.out.println("Please Enter Security Type:");
		System.out.println("STK : Stock");
		System.out.println("FUT : Future");
		System.out.println("CASH : Forex");
		System.out.println("IND : Index");
        
		String securityType = scanner.nextLine();
		System.out.println("Please Enter Code:");
		currentSecName = scanner.nextLine();
        Main.setCurrentSecName(currentSecName);

		secBarsMap.put(currentSecName, new ArrayList<>());
        contract.symbol(currentSecName);
        contract.secType(securityType);
        if (securityType.equals("CASH")) {
            // if secType equals to forex, change currency type
            // eg. input: GBPUSD, symbol GBP, currency USD
            contract.symbol(currentSecName.substring(0,3));
            contract.currency(currentSecName.substring(3));
            contract.exchange("IDEALPRO");
            Logger log = new Logger("FOREX");
            log.debug(securityType);

        } else {
            // if secType is not Forex
            contract.exchange("SMART");
            contract.currency("USD");
        }
		try {
			Thread.sleep(1000);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

        System.out.println("request sent");
    }

    protected Contract getContract() {
        return contract;
    }
}
