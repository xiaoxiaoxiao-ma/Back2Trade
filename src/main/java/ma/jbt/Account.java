package ma.jbt;

import java.math.BigDecimal;
import java.util.HashMap;

public class Account{
    private String name = "default";
    private BigDecimal cash;
    private BigDecimal initCash;
    private HashMap<String, Position> positions;
    // String: secName
    // Position: opening positions

    public Account(String name, long cash) {
        this.name = name;
        this.cash = new BigDecimal(cash);
        this.initCash = this.cash;
        positions = new HashMap<String, Position>();
    }
    public double getCash() {
        return cash.doubleValue();
    }

    public double getInitialCash() {
        return initCash.doubleValue();
    }

    public HashMap<String, Position> getPositions() {
        return positions;
    }

    public String getName() {
        return name;
    }

    public void buyIn(String secName, double price, int quant) {
        BigDecimal priceBig = new BigDecimal(price);
        BigDecimal quantBig = new BigDecimal(quant);
        BigDecimal result = priceBig.multiply(quantBig);
        cash = cash.subtract(result);
        Position secPosition = positions.get(secName);
        if (secPosition != null) {
            secPosition.addQuant(quant);
        } else {
            Position newPosition = new Position(secName, price, quant);
            positions.put(secName, newPosition);
        }
    }


}