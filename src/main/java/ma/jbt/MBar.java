package ma.jbt;

// import java.util.Date;

import com.ib.client.Bar;

public class MBar {
	private String date;
	private double open;
	private double high;
	private double low;
	private double close;
	private double average;
	private long volume;
	private int barCount;
	private String time;
	private String wap;
	
	@Override
	public String toString() {
		return "Date: " + date + " open: " +open + " High: " + high + " Low: " + low + " Close: " + close + " Average: " + average + " Volume: " + volume + " BarCount: " + barCount +" Wap: " + wap;
		
	}

	public MBar() {

	}

	public MBar(Bar b) {
		this.open = b.open();
		this.close = b.close();
		this.high = b.high();
		this.low = b.low();
		this.barCount = b.count();
		this.volume = b.volume().longValue();
		this.wap = b.wap().toString();
		this.date = b.time();
		
	}
	
	public double getOpen() {
		return open;
	}

	public void setWap(String wap) {
		this.wap = wap;
	}

	public String getWap() {
		return wap;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTime() {
		return time;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public double getAverage() {
		return average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public int getBarCount() {
		return barCount;
	}

	public void setBarCount(int barCount) {
		this.barCount = barCount;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	public String getDate() {
		return date;
	}


}
