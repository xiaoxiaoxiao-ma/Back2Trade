package ma.jbt.cli;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ma.jbt.DataSource;
import ma.jbt.MBar;
import ma.jbt.Main;

public class LoadCommandHandler implements CommandHandler {

    @Override
    public void exec(List<String> args) {
        Map<String,List<MBar>> secBarsMap = Main.getSecBarsMap();
        Scanner scanner = Main.getScanner();

        Main.printFilesInDir(Main.CSV_PATH);
		System.out.print("Please enter .csv file full name: ");
		String fileName = scanner.nextLine();
        // System.out.println("Read: " + fileName);
        try {
            secBarsMap.put(
                fileName.substring(0,fileName.indexOf(".")),
                DataSource.loadDataFromLocalFile(Main.CSV_PATH + fileName)
            );
        // retrieve secName from fileName (exclude the extend name)
        } catch (Exception e) {
            System.out.println("Cannot open file name " + fileName + " due to ");
            e.printStackTrace();
        }
    }

}
