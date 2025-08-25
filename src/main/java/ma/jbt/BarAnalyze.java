package ma.jbt;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarAnalyze {
	
	private int jumpRiseCount = 0;
	private double totalRise = 0;
	private String description = "default description";

	public BarAnalyze(String description) {
		this.description = description;
	}

	public void setDescription(String newDescription) {
		description = newDescription;
	}

	public String getDescription() {
		return description;
	}

	public void MyAnalyze1(Map<String, List<MBar>> allSecs) {

		Logger logger = new Logger("Analyze1");
		Account testAccount = new Account("account1", 1000);

		// secName, close prices
		HashMap<String, Double> finalPrices = new HashMap<String, Double>();
		for (String currSecName : allSecs.keySet()) {
			/*
			 * read all loaded securities
			 */
			List<MBar> barList = allSecs.get(currSecName);
			
			logger.log("------------");
			logger.log("secName:" + currSecName);
		
			MBar prev, curr = null;
			for (int i = 1; i < barList.size(); i ++) {
				prev = barList.get(i - 1);
				curr = barList.get(i);
			
				// open < last close, buy in
				if (prev.getClose() > curr.getOpen()) {
					jumpRiseCount++;
					int buyInAmount = 1;
					testAccount.buyIn(currSecName, curr.getAverage(), buyInAmount);
					logger.log("buy " + currSecName +": quant: " + buyInAmount + " price: " + curr.getAverage() + " at time: " + curr.getDate());
					totalRise = curr.getClose() - prev.getClose();
				} else {
				
				}
			}

			// the last bar's close price for each security is the latest price (during the backtest)
			finalPrices.put(currSecName, curr.getClose());
			logger.log("------------");
		}
		
		System.out.println("-----");
		System.out.println("open low avg change:" + totalRise/jumpRiseCount);
		System.out.println("remaining cash: " + testAccount.getCash());
		System.out.println("positions:");
		int i = 1;
		HashMap<String, Position> positions = testAccount.getPositions();

		BigDecimal totalHoldingValue = new BigDecimal(0);

		for (String positionName : positions.keySet()) {

			Position p = positions.get(positionName);
			System.out.println(i + " " + p.getSecName());

			BigDecimal finalSecPrice = new BigDecimal(finalPrices.get(positionName));
			BigDecimal finalTotalValue = finalSecPrice.multiply(new BigDecimal(p.getQuant()));

			// update total value of holdings
			totalHoldingValue = totalHoldingValue.add(finalTotalValue);
			// totalHoldingValue += finalTotalValue (single sec's price * amount)
			System.out.println(
				"amount: " + p.getQuant() +
				" value: " + finalSecPrice.doubleValue() +
				" total value: " + finalTotalValue.doubleValue()
				);
			i++;
		}

		DecimalFormat valueFormat = new DecimalFormat("#.##");
		// two digits

		System.out.println("-----");
		System.out.println("Summary");
		double cash = testAccount.getCash();
		System.out.println("cash\t\t" + cash);
		System.out.println("position value\t" + totalHoldingValue.doubleValue());
		BigDecimal totalValue = new BigDecimal(cash).add(totalHoldingValue);
		String totalValueOutput = valueFormat.format(totalValue);
		System.out.println("total\t\t" + totalValueOutput);
		BigDecimal ratio = totalValue.divide(new BigDecimal(testAccount.getInitialCash()));
		
		String ratioOutput = valueFormat.format(ratio);
		System.out.println("ratio\t\t" + ratioOutput + "%");
		System.out.println("-----");
		System.out.println();
		
	}

}
