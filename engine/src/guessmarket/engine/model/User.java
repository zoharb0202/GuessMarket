package guessmarket.engine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class User implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String name;
    private final Account account;
    private final Set<Integer> marketMakerEvents;
    private final Map<Integer, Participation> participations;
    private boolean blocked;

    public User(String name)
    {
        this.name = name;
        account = new Account(0);
        marketMakerEvents = new LinkedHashSet<Integer>();
        participations = new LinkedHashMap<Integer, Participation>();
        blocked = false;
    }

    public String getName()
    {
        return name;
    }

    public double getBalance()
    {
        return account.getBalance();
    }

    public boolean isBlocked()
    {
        return blocked;
    }

    public Set<Integer> getMarketMakerEvents()
    {
        return marketMakerEvents;
    }

    public boolean isMarketMakerOf(int eventId)
    {
        return marketMakerEvents.contains(Integer.valueOf(eventId));
    }

    public void addMarketMakerEvent(int eventId)
    {
        marketMakerEvents.add(Integer.valueOf(eventId));
    }

    public boolean canAfford(double amount)
    {
        return account.canAfford(amount);
    }

    public boolean pay(double amount, String description)
    {
        account.withdraw(amount, description);
        if (account.getBalance() < 0 && !blocked)
        {
            blocked = true;
            return true;
        }
        return false;
    }

    public void receive(double amount, String description)
    {
        account.deposit(amount, description);
        if (blocked && account.getBalance() >= 0)
        {
            blocked = false;
        }
    }

    public List<AccountLine> getAccountLines()
    {
        return account.getLines();
    }

    public List<Participation> getParticipations()
    {
        return new ArrayList<Participation>(participations.values());
    }

    public Participation getParticipation(int eventId)
    {
        return participations.get(Integer.valueOf(eventId));
    }

    public boolean participatesIn(int eventId)
    {
        return participations.containsKey(Integer.valueOf(eventId));
    }

    public Participation openParticipation(int eventId, int optionCount)
    {
        Participation participation = participations.get(Integer.valueOf(eventId));
        if (participation == null)
        {
            participation = new Participation(eventId, optionCount);
            participations.put(Integer.valueOf(eventId), participation);
        }
        return participation;
    }
}
