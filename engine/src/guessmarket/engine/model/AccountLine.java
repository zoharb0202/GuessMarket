package guessmarket.engine.model;

import java.io.Serializable;

public class AccountLine implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String description;
    private final double amount;
    private final double balanceAfter;

    public AccountLine(String description, double amount, double balanceAfter)
    {
        this.description = description;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    public String getDescription()
    {
        return description;
    }

    public double getAmount()
    {
        return amount;
    }

    public double getBalanceAfter()
    {
        return balanceAfter;
    }
}
