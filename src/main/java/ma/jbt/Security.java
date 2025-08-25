package ma.jbt;

import java.math.BigDecimal;

public class Security {
    private String name;
    private BigDecimal currentPrice;
    
    public Security(String secName, float currentPrice) {
        name = secName;
        this.currentPrice = new BigDecimal(currentPrice);
    }

    public String getName() {
        return name;
    }
    public double getCurrentPrice() {
        return currentPrice.doubleValue();
    }

}
