package ma.jbt.cli;

import java.io.File;
import java.util.List;
import java.util.Map;

import ma.jbt.DataSource;
import ma.jbt.MBar;
import ma.jbt.Main;

public class LoadAllCommandHandler implements CommandHandler {

    @Override
    public void exec(List<String> args) {
        Map<String,List<MBar>> secBarsMap = Main.getSecBarsMap();

        File folder = new File(Main.CSV_PATH);
        System.out.println(Main.CSV_PATH + "->");

        for (File subFile : folder.listFiles()) {
            String fileName = subFile.getName();
            System.out.println("\t\tloading: " + fileName);
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

}
