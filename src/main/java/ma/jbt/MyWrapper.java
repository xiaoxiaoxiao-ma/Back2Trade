package ma.jbt;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fusesource.jansi.Ansi;

import com.ib.client.Bar;
import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDescription;
import com.ib.client.ContractDetails;
import com.ib.client.Decimal;
import com.ib.client.DeltaNeutralContract;
import com.ib.client.DepthMktDataDescription;
import com.ib.client.EWrapper;
import com.ib.client.EWrapperMsgGenerator;
import com.ib.client.Execution;
import com.ib.client.FamilyCode;
import com.ib.client.HistogramEntry;
import com.ib.client.HistoricalSession;
import com.ib.client.HistoricalTick;
import com.ib.client.HistoricalTickBidAsk;
import com.ib.client.HistoricalTickLast;
import com.ib.client.NewsProvider;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.PriceIncrement;
import com.ib.client.SoftDollarTier;
import com.ib.client.TickAttrib;
import com.ib.client.TickAttribBidAsk;
import com.ib.client.TickAttribLast;
import com.ib.client.TickType;

import ma.HistoricalNewsParser;

public class MyWrapper implements EWrapper{

private static Logger logger = new Logger("Server");

	@Override
	public void accountDownloadEnd(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accountSummary(int arg0, String arg1, String arg2, String arg3, String arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accountSummaryEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accountUpdateMulti(int arg0, String arg1, String arg2, String arg3, String arg4, String arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accountUpdateMultiEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bondContractDetails(int arg0, ContractDetails arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void commissionReport(CommissionReport arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void completedOrder(Contract arg0, Order arg1, OrderState arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void completedOrdersEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectAck() {
		System.out.println("Connected to server");
		Main.connectedToServer();
		
	}

	@Override
	public void connectionClosed() {
		System.out.println("connectionClosed");
		
	}

	@Override
	public void contractDetails(int arg0, ContractDetails arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contractDetailsEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void currentTime(long arg0) {
	System.out.println("Current time: " + arg0);
		
	}

	@Override
	public void deltaNeutralValidation(int arg0, DeltaNeutralContract arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayGroupList(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayGroupUpdated(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Exception arg0) {
		System.out.println("Err1");
		arg0.printStackTrace();
		
	}

	@Override
	public void error(String arg0) {
		System.out.println("Err2:" + arg0);
		
	}


	private static final Set<Integer> CONN_INFO = Set.of(2102,2103,2105,2106,2156,2157);

	// connection related
	@Override
	public void error(int id, int errorCode, String errorMsg, String advancedOrderRejectJson) {
		if (CONN_INFO.contains(errorCode)) {
			logger.log("connection: " + errorMsg);
		} else {
			logger.log("error: id:" + id  + " code: " + errorCode + " "+ errorMsg + " " + advancedOrderRejectJson);
		}
	}

	@Override
	public void execDetails(int arg0, Contract arg1, Execution arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execDetailsEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void familyCodes(FamilyCode[] arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fundamentalData(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void headTimestamp(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void histogramData(int arg0, List<HistogramEntry> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void historicalData(int arg0, Bar bar) {
		
		/*System.out.println(bar.time() + 
				":(" +
				BarTimeParser.autoParseBarTime(bar.time())+
				") "+
				bar.close());*/
		double open = bar.open();
		double high = bar.high();
		double low = bar.low();
		double close = bar.close();
		String volume = bar.volume().toString();
		String avg = bar.wap().toString();
		System.out.println("------");
		System.out.println("Time: " +bar.time());
		System.out.println("Open: " +open + " High: " + high + " Low: " + low +" Close: " + close + " Average: " +avg);
		System.out.println("Chg: " + ((close - open) / open) * 100 + "% Vol:" + volume);
		System.out.println("------");
		Main.addBar(bar);
	}

	@Override
	public void historicalDataEnd(int arg0, String arg1, String arg2) {
		Main.endBarList();
		System.out.println("Data receive finished");

		
	}

	@Override
	public void historicalDataUpdate(int arg0, Bar arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void historicalNews(
		int reqId, 
		String time, 
		String providerCode, 
		String articleId, 
		String rawHeadline) {
		// Logger newsLogger = new Logger("HNews");
		// newsLogger.log("historicalNews id: " + reqId + " providerCode: " + providerCode + " articleId: " + articleId);
		var p = HistoricalNewsParser.parse(rawHeadline);
		//newsLogger.log("\tk-value:" + p.sentimentK() + " c-value: " + p.confidenceC());
		//newsLogger.log("\tNews headline: " + p.text());

		if (
			p.confidenceC() != null &&
			p.sentimentK() != null &&
			p.confidenceC() > 0.9 &&
			Math.abs(p.sentimentK()) > 0.5
		) {
			// System.out.println(Ansi.ansi().render("------ @|green backTrader-Xiaoxiao |@------"));

			String color = "";
			if (p.sentimentK() > 0) {
				color = "@|green ";
			} else {
				color = "@|red ";
			}

			System.out.println(Ansi.ansi().render("[C: @|cyan " + p.confidenceC() + "|@ K: " + color + p.sentimentK() + "|@ " + time + "]" + p.text()));

		}
	}

	@Override
	public void historicalNewsEnd(int arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void historicalSchedule(int arg0, String arg1, String arg2, String arg3, List<HistoricalSession> arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void historicalTicks(int arg0, List<HistoricalTick> arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void historicalTicksBidAsk(int arg0, List<HistoricalTickBidAsk> arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void historicalTicksLast(int arg0, List<HistoricalTickLast> arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void managedAccounts(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void marketDataType(int arg0, int arg1) {
		System.out.println("MarketDataType: " + EWrapperMsgGenerator.marketDataType(arg0, arg1));
		
	}

	@Override
	public void marketRule(int arg0, PriceIncrement[] arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mktDepthExchanges(DepthMktDataDescription[] arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newsArticle(int arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newsProviders(NewsProvider[] providers) {
		logger.log("news providers:");
		int i = 2001;
		for (NewsProvider provider: providers) {
			logger.log("\tcode:" + provider.providerCode() + " name: " + provider.providerName());
			Main.requestHistoryNews(i++, provider.providerCode());
		}
	}

	@Override
	public void nextValidId(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openOrder(int arg0, Contract arg1, Order arg2, OrderState arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openOrderEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void orderBound(long arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void orderStatus(int arg0, String arg1, Decimal arg2, Decimal arg3, double arg4, int arg5, int arg6,
			double arg7, int arg8, String arg9, double arg10) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pnl(int arg0, double arg1, double arg2, double arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pnlSingle(int arg0, Decimal arg1, double arg2, double arg3, double arg4, double arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void position(String arg0, Contract arg1, Decimal arg2, double arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void positionEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void positionMulti(int arg0, String arg1, String arg2, Contract arg3, Decimal arg4, double arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void positionMultiEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}


	/*
	 * reqMktData → tick 级别报价逐笔、累积量、RTVolume
	*reqHistoricalData → 历史 bar 秒到月都行
	*reqRealTimeBars → 真正的实时 bar 固定 5 秒。
	 */
	@Override
	public void realtimeBar(int reqid, long time, double open, double high, double low, double close, Decimal volume,
			Decimal wap, int count) {
		System.out.println(reqid + " " +time + " " +open + " " + high + " " +low + " " + close + " " + volume.toString() + " " + wap.toString() + " " + count + " ");
		// wap: Volume Weighted Average Price
		// count: 成交笔数
		
	}
	@Override
	public void receiveFA(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void replaceFAEnd(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rerouteMktDataReq(int arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rerouteMktDepthReq(int arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scannerData(int arg0, int arg1, ContractDetails arg2, String arg3, String arg4, String arg5,
			String arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scannerDataEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scannerParameters(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void securityDefinitionOptionalParameter(int arg0, String arg1, int arg2, String arg3, String arg4,
			Set<String> arg5, Set<Double> arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void securityDefinitionOptionalParameterEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void smartComponents(int arg0, Map<Integer, Entry<String, Character>> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void softDollarTiers(int arg0, SoftDollarTier[] arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void symbolSamples(int arg0, ContractDescription[] arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickByTickAllLast(int arg0, int arg1, long arg2, double arg3, Decimal arg4, TickAttribLast arg5,
			String arg6, String arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickByTickBidAsk(int reqId,
        long time,
        double bidPrice,
        double askPrice,
        Decimal bidSize,
        Decimal askSize,
        TickAttribBidAsk attrib) {
		logger.log("tickByTickBidAsk", "id: " + reqId + " time: " + time);
		logger.log("bid: \t" + bidPrice + "\task: \t" + askPrice);
		logger.log("bSize: \t" + bidSize + "\taSize: \t" +askSize);
		
		
	}

	@Override
	public void tickByTickMidPoint(int arg0, long arg1, double arg2) {
		logger.log("tickByTickMidPoint" + arg0 + ", " + arg1 + ", " + arg2);
	}

	@Override
	public void tickEFP(int arg0, int arg1, double arg2, String arg3, double arg4, int arg5, String arg6, double arg7,
			double arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickGeneric(int arg0, int arg1, double arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickNews(int tickerId,
						long timeStamp,
						String providerCode,
						String articleId,
						String headline,
						String extraData){
		logger.log("NEWS", "id: "+tickerId + " timeStamp: " + timeStamp + " providerCode: " + providerCode + " articleId:" + articleId);
		logger.log("NEWS", "Headline: " + headline);
		logger.log("NEWS", "extraData: " + extraData);
		
	}

	@Override
	public void tickOptionComputation(int arg0, int arg1, int arg2, double arg3, double arg4, double arg5, double arg6,
			double arg7, double arg8, double arg9, double arg10) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickPrice(int tickerId, int field, double price, TickAttrib attribs) {
		logger.log("Tick Price. Ticker Id: " + tickerId + " " + TickType.getField(field) + " " + price);
	}

	@Override
	public void tickReqParams(int tickerId, double minTick, String bboExchange, int snapshotPermissions) {
		logger.debug("tickReqParams: tickerId:" + tickerId + "minTick: " + minTick + " bboExchange: " + bboExchange + " snapshotPremissions: " + snapshotPermissions);
		
	}

	@Override
	public void tickSize(int tickerId, int field, Decimal size) {
		logger.debug("tickSize: id:" + tickerId + " field:" + TickType.getField(field) + " size: " + size.longValue());
		
	}

	@Override
	public void tickSnapshotEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickString(int tickerId, int field, String value) {
		logger.debug("tickString:" + tickerId + " " + TickType.getField(field) + " value: " + value);
		if (TickType.getField(field).equals("lastTimestamp")) {
			long ping = (System.currentTimeMillis() / 1000) - Long.parseLong(value);
			//logger.debug("currTime:" + (System.currentTimeMillis() / 1000) + "received: " + value);
			logger.log("delay: " + ping + "s");
		}
	}

	@Override
	public void updateAccountTime(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAccountValue(String arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateMktDepth(int arg0, int arg1, int arg2, int arg3, double arg4, Decimal arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateMktDepthL2(int arg0, int arg1, String arg2, int arg3, int arg4, double arg5, Decimal arg6,
			boolean arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateNewsBulletin(int arg0, int arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updatePortfolio(Contract arg0, Decimal arg1, double arg2, double arg3, double arg4, double arg5,
			double arg6, String arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userInfo(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void verifyAndAuthCompleted(boolean arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void verifyAndAuthMessageAPI(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void verifyCompleted(boolean arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void verifyMessageAPI(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wshEventData(int reqId, String dataJson) {
		logger.log("wshEventData: " + dataJson);
		
	}

	@Override
	public void wshMetaData(int reqId, String arg1) {
		logger.log("wshMetaData: " + arg1);
		
	}
}
