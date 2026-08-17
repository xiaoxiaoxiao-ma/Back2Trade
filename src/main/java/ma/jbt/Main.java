package ma.jbt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.ib.client.Bar;
import com.ib.client.EClientSocket;
import com.ib.client.EJavaSignal;
import com.ib.client.EReader;

import ma.jbt.cli.InfoCommandHandler;
import ma.jbt.cli.LiveDataCommandHandler;
import ma.jbt.cli.LoadAllCommandHandler;
import ma.jbt.cli.LoadCommandHandler;
import ma.jbt.cli.NewsCommandHandler;
import ma.jbt.cli.QuitCommandHandler;
import ma.jbt.cli.TickPriceCommandHandler;
import ma.jbt.cli.AnalyzeCommandHandler;
import ma.jbt.cli.CommandHandler;
import ma.jbt.cli.CommandRegistry;
import ma.jbt.cli.ConnectCommandHandler;
import ma.jbt.cli.HistoricalDataCommandHandler;

import org.fusesource.jansi.Ansi;

// Lanterna dashboard + JLine command shell

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

    private static CommandRegistry commandRegistry;
	
	private static Map<String,List<MBar>> secBarsMap;
    // contains all loaded securities' names and their barLists
	
	private static final String ADDRESS = "127.0.0.1";
	private static final int PORT = 7496;
	public static final String HOME_PATH = System.getProperty("user.home");
	public static final String CSV_PATH = HOME_PATH + "/Documents/jbt/";

    private static boolean isConnected = false;
    private static boolean exitFlag = false;

    private static Scanner scanner;
	
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
		scanner = new Scanner(System.in);
        commandRegistry = new CommandRegistry();
        commandRegistry.register("info", "get current status", new InfoCommandHandler());
        commandRegistry.register("la", "load all .csv files at " + CSV_PATH, new LoadAllCommandHandler());
        commandRegistry.register("q", "quit", new QuitCommandHandler());
        commandRegistry.register("l", "load data from .csv file", new LoadCommandHandler());
        commandRegistry.register("t","start running backtests", new AnalyzeCommandHandler());
        commandRegistry.register("c", "connect to IBKR server (enable all TWS funtcions)", new ConnectCommandHandler());
        commandRegistry.register("g", "get real-time data from IBKR", new LiveDataCommandHandler());
        commandRegistry.register("h", "get historical data from IBKR", new HistoricalDataCommandHandler());
        commandRegistry.register("n", "get news", new NewsCommandHandler());
        commandRegistry.register("tp", "get tick price", new TickPriceCommandHandler());

        while (!exitFlag) {
        printWelcomeMessage();
        
		String command1 = scanner.nextLine();
		System.out.println("read: " + command1);
        
        CommandHandler handler = commandRegistry.get(command1);
        if (handler != null) {
            handler.exec(null);
        } else {
            System.out.println("Cannot find command");
        }
    }

    // end of the main loop
    // if connected to server, close connection
    if (isConnected) {
        try {
            System.out.println("disconnecting...");
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

    public static void tryConnectToServer() {
        signal = new EJavaSignal();
        wrapper = new MyWrapper();
        client = new EClientSocket(wrapper, signal);
        // Init tws related services
        client.eConnect(ADDRESS, PORT, 0);
        // connect to local tws server
    }
	
	public static void connectedToServer() {
		//client.reqMarketDataType(4);//delayed and frozen
        isConnected = true;
        System.out.println("Successfully connected to server!");
        
        final EReader reader = new EReader(client, signal);
		reader.start();
        new Thread(() -> {
            while (client.isConnected()) {
                signal.waitForSignal();
                try {
                    reader.processMsgs();
                } catch (Exception e) {
                    System.out.println("Exception: "+ e.getMessage());
                }
            }
        }).start();

        client.reqNewsProviders();
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

    public static Map<String,List<MBar>> getSecBarsMap() {
        return secBarsMap;
    }

    public static void requestExit() {
        exitFlag = true;
    }

    public static Scanner getScanner() {
        return scanner;
    }

    public static EClientSocket getClient() {
        return client;
    }

    public static void setCurrentSecName(String newCurrentSecName) {
        currentSecName = newCurrentSecName;
    }

    public static void printWelcomeMessage() {
        // System.out.println(  Ansi.ansi().render("@|red Hello|@ @|green World|@") );
        System.out.println(Ansi.ansi().render("------ @|green backTrader-Xiaoxiao |@------"));
		/*System.out.println("\tl\tload data from .csv file");
        System.out.println("\tla\tload all .csv files at " + CSV_PATH);
        if (!isConnected) {
            System.out.println("\tc\tconnect to IBKR server (enable all TWS funtcions)");
        }
        System.out.println("\tinfo\tget current status");
        System.out.println("\tt\tstart running backtests");
        if (isConnected) {
            System.out.println("\tg\tget real-time data from IBKR");
            System.out.println("\th\tget historical data from IBKR");
            // System.out.println("\ts\tstart receiving real-time data from server");
        }
        System.out.println("\tq\tquit");
        */
        commandRegistry.printUsage();
        System.out.println("------------");
        System.out.println();
    }

    public static void requestHistoryNews(int id, String providerCode) {
        // reqId, conId
        client.reqHistoricalNews(id, 8314, providerCode, "", "", 10, null);
    }
}
