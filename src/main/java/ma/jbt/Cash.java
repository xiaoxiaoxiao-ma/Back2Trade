package ma.jbt;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Cash {
    private String name;
    private BigDecimal cashAmount = new BigDecimal(0);

    private boolean canBeNegative = false;

    private DecimalFormat valueFormat = new DecimalFormat("#.##");
    
    public Cash(String cashName) {
        this.name = cashName;
    }

    public Cash(String cashName, int initAmount) {
        cashAmount = new BigDecimal(initAmount);
    }

    public void setNegativeFlag(boolean newFlag) {
        canBeNegative = newFlag;
    }

    public boolean getNegativeFlag() {
        return canBeNegative;
    }

    public void sell(double amount) throws NegativeCashException {
        BigDecimal sellAmount = new BigDecimal(amount);
        
        BigDecimal newCashAmount = cashAmount.subtract(sellAmount);
        if (!canBeNegative &&
            newCashAmount.compareTo(BigDecimal.ZERO) == -1) {
                throw new NegativeCashException("cash cannot be negative due to settings");
        }
        cashAmount = newCashAmount;
    }

    public void buy(double amount) {
        BigDecimal buyAmount = new BigDecimal(amount);
        BigDecimal newCashAmount = cashAmount.add(buyAmount);
        cashAmount = newCashAmount;
    }

    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    public String getCashName() {
        return name;
    }

    @Override
    public String toString() {
        return valueFormat.format(cashAmount.doubleValue()) + " " + name;
    }
}