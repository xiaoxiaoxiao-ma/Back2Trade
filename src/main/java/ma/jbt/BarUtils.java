package ma.jbt;

import java.io.PrintWriter;
import java.util.List;

public class BarUtils {
	
public static void barsToCSV(List<MBar> barList, PrintWriter pw, boolean convertTime) {
	try {
        pw.println("time,open,high,low,close,volume,barCount,average");
        for (MBar bar : barList) {

        	//System.out.println("Bar Wap ToString is" + bar.wap().toString());
            String line;
            if (convertTime) {
            line = String.format(
                "%s,%.5f,%.5f,%.5f,%.5f,%s",
                BarTimeParser.convertToCsvFormat(bar.getTime()),
                bar.getOpen(),
                bar.getHigh(),
                bar.getLow(),
                bar.getClose(),
                bar.getVolume()
                //bar.count(),
                //bar.wap().toString()
            );
        } else {
            line = String.format(
                        "%s,%.5f,%.5f,%.5f,%.5f,%s",
                        bar.getTime(),
                        bar.getOpen(),
                        bar.getHigh(),
                        bar.getLow(),
                        bar.getClose(),
                        bar.getVolume()
                );
        }
            pw.println(line);
        }

	} catch (Exception e) {
		e.printStackTrace();
	}
}
}
