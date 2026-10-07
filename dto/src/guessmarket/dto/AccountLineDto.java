package guessmarket.dto;

public class AccountLineDto
{
    private final int number;
    private final String description;
    private final double amount;
    private final double balanceAfter;

    public AccountLineDto(int number, String description, double amount, double balanceAfter)
    {
        this.number = number;
        this.description = description;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    public int getNumber()
    {
        return number;
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
