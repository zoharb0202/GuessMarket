package guessmarket.engine.model;

import java.io.Serializable;

public class Trade implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String optionName;
    private final int quantity;
    private final double sharesCost;
    private final double commission;

    public Trade(String optionName, int quantity, double sharesCost, double commission)
    {
        this.optionName = optionName;
        this.quantity = quantity;
        this.sharesCost = sharesCost;
        this.commission = commission;
    }

    public String getOptionName() 
    {
        return optionName;
    }

    public int getQuantity() 
    {
        return quantity;
    }

    public double getSharesCost()
    {
        return sharesCost;
    }

    public double getCommission()
    {
        return commission;
    }

    public double getTotalPaid()
    {
        return sharesCost + commission;
    }
}
