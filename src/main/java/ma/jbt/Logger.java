package ma.jbt;

public class Logger {

    private String name;
    private boolean enableLogFlag = true;
    private boolean enableDebugFlag = true;

    private boolean timeFlag = true;

    private static long startTime = System.currentTimeMillis();

    public Logger(String name) {
        this.name = name;
    }

    public void log(String str) {
        if (enableLogFlag) {
            if (timeFlag) {
                System.out.println("[" + name + " +" + (System.currentTimeMillis() - startTime) + "] " + str);
            } else {
                System.out.println("[" + name + "] " + str);
            }
        }
    }

    public void debug(String str) {
        if (enableDebugFlag) {
            if (timeFlag) {
                System.out.println("(D)[" + name + " +" + (System.currentTimeMillis() - startTime) + "] " + str);
            } else {
                System.out.println("(D)[" + name + "] " + str);
            }
        }
    }

    public void disableLog() {
        enableLogFlag = false;
    }
    public void disableDebug() {
        enableDebugFlag = false;
    }

    public void disableTimeDisplay() {
        timeFlag = false;
    }

    public void enableTimeDisplay() {
        timeFlag = true;
    }


    public void log(String tag, String msg) {
        System.out.println("[" + tag + "] " + msg);
    }

    
}
