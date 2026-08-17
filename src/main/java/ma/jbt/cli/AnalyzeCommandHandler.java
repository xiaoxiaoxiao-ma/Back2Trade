package ma.jbt.cli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ma.jbt.BarAnalyze;
import ma.jbt.MBar;
import ma.jbt.Main;

public class AnalyzeCommandHandler implements CommandHandler {

    @Override
    public void exec(List<String> args) {
        Map<String,List<MBar>> secBarsMap = Main.getSecBarsMap();
        BarAnalyze ba = new BarAnalyze("MyAnalyze1");
        ba.MyAnalyze1(secBarsMap);
    }

}
