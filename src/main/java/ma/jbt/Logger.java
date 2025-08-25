package ma.jbt;

public class Logger {

    private String name;
    private boolean enableLogFlag = true;
    private boolean enableDebugFlag = true;

    public Logger(String name) {
        this.name = name;
    }

    public void log(String str) {
        if (enableLogFlag) {
            System.out.println("[" + name + "] " + str);
        }
    }

    public void debug(String str) {
        if (enableDebugFlag) {
            System.out.println("(D)[" + name + "] " + str);
        }
    }

    public void disableLog() {
        enableLogFlag = false;
    }
    public void disableDebug() {
        enableDebugFlag = false;
    }


    public void log(String tag, String msg) {
        System.out.println("[" + tag + "] " + msg);
    }

    
}
