package guessmarket.engine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Account implements Serializable
{
    private static final long serialVersionUID = 1L;

    private double balance;
    private final List<AccountLine> lines;

    public Account(double initialBalance)
    {
        balance = initialBalance;
        lines = new ArrayList<AccountLine>();
    }

    public double getBalance()
    {
        return balance;
    }

    public List<AccountLine> getLines()
    {
        return lines;
    }

    public boolean canAfford(double amount)
    {
        return balance >= amount;
    }

    public void deposit(double amount)
    {
        balance += amount;
    }

    public void withdraw(double amount)
    {
        balance -= amount;
    }

    public void deposit(double amount, String description)
    {
        deposit(amount);
        lines.add(new AccountLine(description, amount, balance));
    }

    public void withdraw(double amount, String description)
    {
        withdraw(amount);
        lines.add(new AccountLine(description, -amount, balance));
    }
}
