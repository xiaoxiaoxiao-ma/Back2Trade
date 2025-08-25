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
import com.ib.client.Types.SecType;

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
	
	private static Map<String,List<MBar>> barList;
	
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

        barList = new HashMap<String,List<MBar>>();
		
		Scanner scanner = new Scanner(System.in);
        while (true) {
        System.out.println("------ backTrader-Xiaoxiao ------");
		System.out.println("\tl\tload data from .csv file");
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
        
		String command1 = scanner.nextLine();
		System.out.println("read: " + command1);
        if(command1.equals("info")) {
            System.out.println(barList.size() + " securities have been loaded" );
            for (String key : barList.keySet()) {
                System.out.println(key + " has " + barList.get(key).size() +" bars");
            }
        }
		if (command1.equals("l")) {
            printFilesInDir(CSV_PATH);
			System.out.print("Please enter .csv file full name: ");
			String fileName = scanner.nextLine();
            // System.out.println("Read: " + fileName);
            try {
			barList.put(
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
            ba.MyAnalyze1(barList);
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
		barList.put(currentSecName, new ArrayList<>());
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
            /* 
             *BarTimeParser.convertToCsvFormat(bar.time()),
                bar.open(),
                bar.high(),
                bar.low(),
                bar.close(),
                bar.volume().toString(),
                bar.count(),
                bar.wap().toString()
             */
    }
	public static void addMBar(MBar b) {
		barList.get(currentSecName).add(b);
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
			BarUtils.barsToCSV(barList.get(currentSecName), pw, false);
			pw.close();
		} catch (IOException e) {
            System.err.print("Error(saveBarList): ");
			e.printStackTrace();
		}
	}


}

/*
 * ✅ 三、临时解决方案：使用延迟行情（Delayed Market Data）
即使你没有订阅实时数据，也可以请求延迟（如 15 分钟）的行情数据，用于测试和分析。

在 Java 中：

java
Copy
Edit
client.reqMarketDataType(3);  // 3 = Delayed (Frozen), 2 = Delayed
⚠️ 前提：你的账户必须启用了 delayed data 访问（大多数账户默认允许）

🔍 你可以这样测试当前账户能否访问哪些免费的行情：
示例请求外汇行情（EUR/USD）：
java
Copy
Edit
Contract eurusd = new Contract();
eurusd.symbol("EUR");
eurusd.secType("CASH");
eurusd.exchange("IDEALPRO");
eurusd.currency("USD");

client.reqMktData(1001, eurusd, "", false, false, null);
如果你能收到 tickPrice(...) 回调，就说明 你确实能免费获取 FX 实时数据。

 * 
 * 
 * Historical Bar Data
Requesting Historical Bar Data
Historical data is obtained from the the TWS via the IBApi.EClient.reqHistoricalData function. Every request needs:

tickerId, A unique identifier which will serve to identify the incoming data.
contract, The IBApi.Contract you are interested in.
endDateTime, The request's end date and time (the empty string indicates current present moment).
durationString, The amount of time (or Valid Duration String units) to go back from the request's given end date and time.
barSizeSetting, The data's granularity or Valid Bar Sizes
whatToShow, The type of data to retrieve. See Historical Data Types
useRTH, Whether (1) or not (0) to retrieve data generated only within Regular Trading Hours (RTH)
formatDate, The format in which the incoming bars' date should be presented. Note that for day bars, only yyyyMMdd format is available.
keepUpToDate, Whether a subscription is made to return updates of unfinished real time bars as they are available (True), or all data is returned on a one-time basis (False). Available starting with API v973.03+ and TWS v965+. If True, and endDateTime cannot be specified.
For example, making a request with an end date and time of "20160127-23:59:59", a duration string of "3 D" and a bar size of "1 hour" will return three days worth of 1 hour bars data in which the most recent bar will be the closest possible to 20160127-23:59:59.

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.add(Calendar.MONTH, -6);
        SimpleDateFormat form = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
        String formatted = form.format(cal.getTime());
        client.reqHistoricalData(4001, ContractSamples.EurGbpFx(), formatted, "1 M", "1 day", "MIDPOINT", 1, 1, false, null);
        client.reqHistoricalData(4002, ContractSamples.EuropeanStock(), formatted, "10 D", "1 min", "TRADES", 1, 1, false, null);
            client.reqHistoricalData(4003, ContractSamples.USStockAtSmart(), formatted, "1 M", "1 day", "SCHEDULE", 1, 1, false, null);
        Thread.sleep(2000);
        client.cancelHistoricalData(4001);
        client.cancelHistoricalData(4002);
        client.cancelHistoricalData(4003);
The daily bar size has several unique characteristics. This is true both in TWS and the API:

For futures, the close price of daily bars can be the settlement price if provided by the exchange. Generally the official settlement price is not available until several hours after a trading session closes. The Friday settlement price will sometimes not be available until Saturday.
A daily bar will refer to a trading session which may cross calendar days. In that case the date of the bar will correspond to the day on which the bar closes.
Receiving Historical Bar Data
The historical data will be delivered via the IBApi::EWrapper::historicalData method in the form of candlesticks. The time zone of returned bars is the time zone chosen in TWS on the login screen. If reqHistoricalData was invoked with keepUpToDate = false, once all candlesticks have been received the IBApi.EWrapper.historicalDataEnd marker will be sent. Otherwise updates of the most recent partial five-second bar will continue to be returned in real time to IBApi::EWrapper::historicalDataUpdate. The keepUpToDate functionality can only be used with bar sizes 5 seconds or greater and requires the endDate is set as the empty string. Also, with keepUpToDate = true, if bust event happens, then bust event error (code=10225) is reported to all API client versions connected to TWS version 981+.

Note: IB's historical data feed is filtered for some types of trades which generally occur away from the NBBO such as combos, block trades, and derivatives. For that reason the historical data volume will be lower than an unfiltered historical data feed.
IB does not maintain separate historical data for combos. Historical data returned for a combo contract will be the sum of data from the individual legs.
public class EWrapperImpl implements EWrapper {
...
    @Override
    public void historicalData(int reqId, Bar bar) {
        System.out.println("HistoricalData:  " + EWrapperMsgGenerator.historicalData(reqId, bar.time(), bar.open(), bar.high(), bar.low(), bar.close(), bar.volume(), bar.count(), bar.wap()));
    }
...
    @Override
    public void historicalDataEnd(int reqId, String startDateStr, String endDateStr) {
        System.out.println("HistoricalDataEnd. " + EWrapperMsgGenerator.historicalDataEnd(reqId, startDateStr, endDateStr));
    }


    @Override
    public void historicalDataUpdate(int reqId, Bar bar) {
        System.out.println("HistoricalDataUpdate. " + EWrapperMsgGenerator.historicalData(reqId, bar.time(), bar.open(), bar.high(), bar.low(), bar.close(), bar.volume(), bar.count(), bar.wap()));
    }
Historical Data with whatToShow "SCHEDULE"
A new whatToShow = SCHEDULE parameter has been introduced in the TWS API 10.12.
The data is requested via IBApi.EClient.reqHistoricalData method with whatToShow "SCHEDULE" and delivered via IBApi.EWrapper.historicalSchedule method.

    @Override
    public void historicalSchedule(int reqId, String startDateTime, String endDateTime, String timeZone, List<HistoricalSession> sessions) {
        System.out.println(EWrapperMsgGenerator.historicalSchedule(reqId, startDateTime, endDateTime, timeZone, sessions));
    }
Valid Duration String units
Unit	Description
S	Seconds
D	Day
W	Week
M	Month
Y	Year
Valid Bar Sizes
Size
1 secs	5 secs	10 secs	15 secs	30 secs
1 min	2 mins	3 mins	5 mins	10 mins	15 mins	20 mins	30 mins
1 hour	2 hours	3 hours	4 hours	8 hours
1 day
1 week
1 month
Historical Data Types
(whatToShow)

All different kinds of historical data are returned in the form of candlesticks and as such the values return represent the state of the market during the period covered by the candlestick.

Type	Open	High	Low	Close	Volume
TRADES	First traded price	Highest traded price	Lowest traded price	Last traded price	Total traded volume
MIDPOINT	Starting midpoint price	Highest midpoint price	Lowest midpoint price	Last midpoint price	N/A
BID	Starting bid price	Highest bid price	Lowest bid price	Last bid price	N/A
ASK	Starting ask price	Highest ask price	Lowest ask price	Last ask price	N/A
BID_ASK	Time average bid	Max Ask	Min Bid	Time average ask	N/A
ADJUSTED_LAST	Dividend-adjusted first traded price	Dividend-adjusted high trade	Dividend-adjusted low trade	Dividend-adjusted last trade	Total traded volume
HISTORICAL_VOLATILITY	Starting volatility	Highest volatility	Lowest volatility	Last volatility	N/A
OPTION_IMPLIED_VOLATILITY	Starting implied volatility	Highest implied volatility	Lowest implied volatility	Last implied volatility	N/A
FEE_RATE	Starting fee rate	Highest fee rate	Lowest fee rate	Last fee rate	N/A
YIELD_BID	Starting bid yield	Highest bid yield	Lowest bid yield	Last bid yield	N/A
YIELD_ASK	Starting ask yield	Highest ask yield	Lowest ask yield	Last ask yield	N/A
YIELD_BID_ASK	Time average bid yield	Highest ask yield	Lowest bid yield	Time average ask yield	N/A
YIELD_LAST	Starting last yield	Highest last yield	Lowest last yield	Last last yield	N/A
SCHEDULE	N/A	N/A	N/A	N/A	N/A
AGGTRADES	First traded price	Highest traded price	Lowest traded price	Last traded price	Total traded volume


TRADES data is adjusted for splits, but not dividends.
ADJUSTED_LAST data is adjusted for splits and dividends. Requires TWS 967+.
SCHEDULE returns historical trading schedule only with no information about OHLCV. Requires TWS API 10.12+.
AGGTRADES data should only be used with Crypto contracts.
Available Data per Product
Product Type
TRADES
MIDPOINT
BID
ASK
BID_ASK
HISTORICAL_VOLATILITY
OPTION_IMPLIED_VOLATILITY
YIELD_BID
YIELD_ASK
YIELD_BID_ASK
YIELD_LAST
SCHEDULE
AGGTRADES
Stocks
Y
Y
Y
Y
Y
Y
Y
N
N
N
N
Y
N
Commodities
N
Y
Y
Y
Y
N
N
N
N
N
N
Y
N
Options
Y
Y
Y
Y
Y
N
N
N
N
N
N
N
N
Futures
Y
Y
Y
Y
Y
N
N
N
N
N
N
Y
N
FOPs
Y
Y
Y
Y
Y
N
N
N
N
N
N
N
N
ETFs
Y
Y
Y
Y
Y
Y
Y
N
N
N
N
Y
N
Warrants
Y
Y
Y
Y
Y
N
N
N
N
N
N
Y
N
Structured Products
Y
Y
Y
Y
Y
N
N
N
N
N
N
Y
N
SSFs
Y
Y
Y
Y
Y
N
N
N
N
N
N
Y
N
Forex
N
Y
Y
Y
Y
N
N
N
N
N
N
Y
N
Metals
Y
Y
Y
Y
Y
N
N
N
N
N
N
Y
N
Indices
Y
N
N
N
N
Y
Y
N
N
N
N
Y
N
Bonds*
Y
Y
Y
Y
Y
N
N
Y
Y
Y
Y
Y
N
Funds
N
Y
Y
Y
Y
N
N
N
N
N
N
Y
N
CFDs*
N
Y
Y
Y
Y
N
N
N
N
N
N
Y
N
Cryptocurrency
Y
Y
Y
Y
Y
N
N
N
N
N
N
Y
Y
Yield historical data only available for corporate bonds.
SCHEDULE data returns only on 1 day bars.

 * 
 * 
 * 
 * 
 * 
 * 
 */
