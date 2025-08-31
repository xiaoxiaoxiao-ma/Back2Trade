package ma;
import java.util.regex.*;

public class HistoricalNewsParser {

    // {A:800015:L:en:K:0.95:C:0.9488}Text...
    private static final Pattern P = Pattern.compile(
        "^\\{\\s*A\\s*:\\s*(?<A>\\d+)\\s*"
        + ":\\s*L\\s*:\\s*(?<L>[^:}]+)\\s*"
        + "(?::\\s*K\\s*:\\s*(?<K>[^:}]+)\\s*)?"
        + "(?::\\s*C\\s*:\\s*(?<C>[^:}]+)\\s*)?"
        + "\\}\\s*(?<TXT>.*)$"
    );

    public static Parsed parse(String headline) {
        if (headline == null) return null;
        Matcher m = P.matcher(headline);
        if (!m.matches()) {
            return new Parsed(-1, null, null, null, headline);
        }
        long conId = Long.parseLong(m.group("A"));
        String lang = trimOrNull(m.group("L"));
        Double k = toDoubleOrNull(m.group("K")); // include "n/a"
        Double c = toDoubleOrNull(m.group("C"));
        String text = trimOrEmpty(m.group("TXT"));
        return new Parsed(conId, lang, k, c, text);
    }

    private static Double toDoubleOrNull(String s) {
        if (s == null) return null;
        s = s.trim().toLowerCase();
        if (s.isEmpty() || "n/a".equals(s)) {
            return null;
        }
        try { 
            return Double.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }
    private static String trimOrNull(String s){
        return s == null ? null : s.trim();
    }
    private static String trimOrEmpty(String s){
        return s == null ? "" : s.trim();
    }

    public record Parsed(long conId, String lang, Double sentimentK, Double confidenceC, String text) {}
}
