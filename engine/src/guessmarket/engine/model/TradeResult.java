package guessmarket.engine.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
public class TradeResult
{
    private final List<Trade> trades = new ArrayList<Trade>();
    private final Set<String> blockedUsers = new LinkedHashSet<String>();
    private int filledQuantity;
    private int restingQuantity;
    private boolean minted;

    public List<Trade> getTrades()
    {
        return trades;
    }

    public Set<String> getBlockedUsers()
    {
        return blockedUsers;
    }

    public int getFilledQuantity()
    {
        return filledQuantity;
    }

    public int getRestingQuantity()
    {
        return restingQuantity;
    }

    public boolean isMinted()
    {
        return minted;
    }

    public void addTrade(Trade trade)
    {
        trades.add(trade);
    }

    public void addBlockedUser(String name)
    {
        blockedUsers.add(name);
    }

    public void addFilled(int quantity)
    {
        filledQuantity += quantity;
    }

    public void setRestingQuantity(int quantity)
    {
        restingQuantity = quantity;
    }

    public void setMinted(boolean minted)
    {
        this.minted = minted;
    }
}
