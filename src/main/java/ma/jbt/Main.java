package ma.jbt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.ib.client.Bar;
import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.EJavaSignal;
import com.ib.client.EReader;

// 加用户持仓的证券代码 数量 然后存储/读取 并获取报价 按要求货币计算盈亏
// 联动python实现回测
// 请求离岸人民币
/*
 * Contract usdCnh = new Contract();
usdCnh.symbol("USD");             // 基础货币：美元
usdCnh.secType("CASH");           // 外汇合约
usdCnh.currency("CNH");           // 报价货币：离岸人民币
usdCnh.exchange("IDEALPRO");      // IB 的外汇电子交易市场
 */

public class Main {
	private static EClientSocket client;
	private static EJavaSignal signal;
	private static MyWrapper wrapper;
	
	private static Map<String,List<MBar>> secBarsMap;
    // contains all loaded securities' names and their barLists
	
	private static final String ADDRESS = "127.0.0.1";
	private static final int PORT = 7496;
	private static final String HOME_PATH = System.getProperty("user.home");
	private static final String CSV_PATH = HOME_PATH + "/Documents/jbt/";

    private static boolean isConnected = false;
	
	String[] m7symbols = {"AAPL", "MSFT", "AMZN", "GOOGL", "META", "NVDA", "TSLA"};
	
	private static String currentSecName;
	public static void main(String[] args) {

		Path csv_path = Paths.get(CSV_PATH);
		try {
			Files.createDirectories(csv_path);
		} catch (IOException e) {
            System.err.print("Error(createDir): ");
			e.printStackTrace();
		}
        // init csv directory

        secBarsMap = new HashMap<String,List<MBar>>();
		
		Scanner scanner = new Scanner(System.in);
        while (true) {
        System.out.println("------ backTrader-Xiaoxiao ------");
		System.out.println("\tl\tload data from .csv file");
        System.out.println("\tla\tload all .csv files at " + CSV_PATH);
        if (!isConnected) {
            System.out.println("\tc\tconnect to IBKR server (enable all TWS funtcions)");
        }
        System.out.println("\tinfo\tget current status");
        System.out.println("\tt\tstart running backtests");
        if (isConnected) {
            System.out.println("\tg\tfor load data from IBKR");
            // System.out.println("\ts\tstart receiving real-time data from server");
        }
        System.out.println("\tq\tquit");
        System.out.println("------------");
        System.out.println();
        
		String command1 = scanner.nextLine();
		System.out.println("read: " + command1);
        if(command1.equals("info")) {
            System.out.println(secBarsMap.size() + " securities have been loaded" );
            for (String key : secBarsMap.keySet()) {
                System.out.println(key + " has " + secBarsMap.get(key).size() +" bars");
            }
        }

        if (command1.equals("la")) {
            File folder = new File(CSV_PATH);
            System.out.println(CSV_PATH + "->");
            for (File subFile : folder.listFiles()) {
                String fileName = subFile.getName();
                System.out.println("\t\tloading: " + fileName);
                try {
			secBarsMap.put(
                fileName.substring(0,fileName.indexOf(".")),
                DataSource.loadDataFromLocalFile(CSV_PATH + fileName)
                );
            // retrieve secName from fileName (exclude the extend name)
            } catch (Exception e) {
                System.out.println("Cannot open file name " + fileName + " due to ");
                e.printStackTrace();
            }
            }
        }
		if (command1.equals("l")) {
            printFilesInDir(CSV_PATH);
			System.out.print("Please enter .csv file full name: ");
			String fileName = scanner.nextLine();
            // System.out.println("Read: " + fileName);
            try {
			secBarsMap.put(
                fileName.substring(0,fileName.indexOf(".")),
                DataSource.loadDataFromLocalFile(CSV_PATH + fileName)
                );
            // retrieve secName from fileName (exclude the extend name)
            } catch (Exception e) {
                System.out.println("Cannot open file name " + fileName + " due to ");
                e.printStackTrace();
            }
		}
        else if (command1.equals("t")) {
            BarAnalyze ba = new BarAnalyze("MyAnalyze1");
            ba.MyAnalyze1(secBarsMap);
        }
        else if (command1.equals("q")) {
            break;
        }
        else if (command1.equals("s")) {
            
        }
        else if (command1.equals("c")) {
		signal = new EJavaSignal();
		wrapper = new MyWrapper();
		client = new EClientSocket(wrapper, signal);
        // Init tws related services
		client.eConnect(ADDRESS, PORT, 0);
        
        // connect to local tws server
        }
        else if (command1.equals("g")) {
		Contract contract = new Contract();
		
		System.out.println("Please Enter Security Type:");
		System.out.println("STK : Stock");
		System.out.println("FUT : Future");
		System.out.println("CASH : Forex");
		System.out.println("IND : Index");
        
		String securityType = scanner.nextLine();
		System.out.println("Please Enter Code:");
		currentSecName = scanner.nextLine();
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
		final EReader reader = new EReader(client,signal);
		reader.start();
        //An additional thread is created in this program design to empty the messaging queue
        new Thread(() -> {
            while (client.isConnected()) {
                signal.waitForSignal();
                try {
                    reader.processMsgs();
                } catch (Exception e) {
                    System.out.println("Exception: "+e.getMessage());
                }
            }
        }).start();
		try {
			Thread.sleep(1000);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

        System.out.println("request sent");
        client.reqMktData(1001, contract, "233", false, false, null);
        /*client.reqHistoricalData(1001, contract, "", "1 Y", "1 day", "TRADES", 1, 1, false, null );*/
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                    e.printStackTrace();
                }
        
        }
        
    }

    // end of the main loop
    // if connected to server, close connection
    if (isConnected) {
        try {
            client.eDisconnect();
        } catch(Exception e) {
            System.err.print("Error(disconnect): ");
            e.printStackTrace();
        }
    }

    // close system input
    scanner.close();
		
	}

    public static void printFilesInDir(String pathName) {
        File folder = new File(pathName);
        System.out.println(CSV_PATH + "->");
        for (File subFile : folder.listFiles()) {
            System.out.println("\t\t->" + subFile.getName());
        }
    }
	
    public static void addBar(Bar b) {
            MBar mb = new MBar(b);
            addMBar(mb);
    }
	public static void addMBar(MBar b) {
		secBarsMap.get(currentSecName).add(b);
	}
	
	public static void connectedToServer() {
		//client.reqMarketDataType(4);//delayed and frozen
        isConnected = true;
        System.out.println("Successfully connected to server!");
	}
    // caller: tws server (getHistoryData)
	public static void endBarList() {
		saveBarList();
		// BarAnalyze.MyAnalyze(barList);
	}
	
	public static void saveBarList() {
		try {
            // save current sec's bar chart (.csv) to specific filePath
			PrintWriter pw = new PrintWriter(new FileWriter(CSV_PATH + currentSecName +".csv"));
			BarUtils.barsToCSV(secBarsMap.get(currentSecName), pw, false);
			pw.close();
		} catch (IOException e) {
            System.err.print("Error(saveBarList): ");
			e.printStackTrace();
		}
	}
}
