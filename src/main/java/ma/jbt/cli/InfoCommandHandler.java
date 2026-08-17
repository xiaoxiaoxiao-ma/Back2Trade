package ma.jbt.cli;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import ma.jbt.MBar;
import ma.jbt.Main;

public class InfoCommandHandler implements CommandHandler {

    @Override
    public void exec(List<String> args) {
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime now = ZonedDateTime.now(zone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println(now.format(formatter));

        Map<String,List<MBar>> secBarsMap = Main.getSecBarsMap();

        System.out.println(secBarsMap.size() + " securities have been loaded" );
        for (String key : secBarsMap.keySet()) {
            System.out.println(key + " has " + secBarsMap.get(key).size() +" bars");
        }
/*
        for (String zoneId : ZoneId.getAvailableZoneIds()) {
            System.out.println(zoneId);
        }
            */
    }
}
