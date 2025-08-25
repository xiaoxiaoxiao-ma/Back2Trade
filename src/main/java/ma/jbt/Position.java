package ma.jbt;

import java.math.BigDecimal;

public class Position {
    private String secName;
    private BigDecimal openPrice;
    private int quant;
    public Position(String secName, double openPrice, int quant) {
        this.secName = secName;
        this.openPrice = new BigDecimal(openPrice);
        this.quant = quant;
    }

    public String getSecName() {
        return secName;
    }

    public int getQuant() {
        return quant;
    }

    public void addQuant(int addQ) {
        quant += addQ;
    }

    public double getOpenPrice() {
        return openPrice.doubleValue();
    }
    
}
