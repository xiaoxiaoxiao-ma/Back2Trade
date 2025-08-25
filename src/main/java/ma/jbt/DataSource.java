package ma.jbt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataSource {

	public static List<MBar> loadDataFromLocalFile(String filePath) {
		
		List<MBar> bars = new ArrayList<>();
		int i = 0;
		long startTime = System.currentTimeMillis();

		System.out.println("Loading file from " + filePath);
		File file = new File(filePath);
		System.out.println("Exists: " + file.exists() + ", Length: " + file.length());

		try (BufferedReader reader = new BufferedReader(
			new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
			String line;
			boolean isFirstLine = true;

			while ((line = reader.readLine()) != null) {
				if (isFirstLine) {
	                isFirstLine = false; // skip header
					continue;
				}

				String[] parts = line.split(",");

	            if (parts.length < 8) continue; // skip invalid lines

				MBar bar = new MBar();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

				try {
					// parse date from saved bar's csv data
					Date date = sdf.parse(parts[0].trim());
					bar.setDate(date);
				} catch (Exception e) {
					e.printStackTrace();
				}

				bar.setOpen(	Double.parseDouble(parts[1].trim()));
				bar.setHigh(	Double.parseDouble(parts[2].trim()));
				bar.setLow(		Double.parseDouble(parts[3].trim()));
				bar.setClose(	Double.parseDouble(parts[4].trim()));
				bar.setVolume(	Long.parseLong(parts[5].trim()));
				bar.setBarCount(Integer.parseInt(parts[6].trim()));
				bar.setAverage(	Double.parseDouble(parts[7].trim()));

				System.out.println("One bar has been loaded: " + bar.toString());
				bars.add(bar);
				i++;
			}

		} catch (IOException e) {
			System.out.println("Error when loading data from csv file: " + e.getMessage());
		}

		long endTime = System.currentTimeMillis();
		System.out.println(i +" bars have been loaded (" + (endTime - startTime) + "ms)");
		return bars;
	}
}
