package guessmarket.engine.model;

import guessmarket.engine.method.TradingMethod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final double WINNING_SHARE_VALUE = 1.0;

    private final int id;
    private final String name;
    private final String description;
    private final int commissionPercent;
    private final CommissionType commissionType;
    private final List<EventOption> options;
    private final TradingMethod method;
    private final Account account;
    private final List<Trade> trades;
    private double collectedCommission;
    private EventStatus status;
    private EventOption winningOption;

    public Event(int id, String name, String description, int commissionPercent, CommissionType commissionType, List<EventOption> options, TradingMethod method)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.commissionPercent = commissionPercent;
        this.commissionType = commissionType;
        this.options = options;
        this.method = method;
        trades = new ArrayList<Trade>();
        status = EventStatus.ACTIVE;
        // the event starts with the subsidy its market maker has to put in
        account = new Account(method.getInitialSubsidy());
    }

    public int getId() 
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription() 
    {
        return description;
    }

    public int getCommissionPercent() 
    {
        return commissionPercent;
    }

    public CommissionType getCommissionType() 
    {
        return commissionType;
    }

    public List<EventOption> getOptions()
    {
        return options;
    }

    public EventStatus getStatus()
    {
        return status;
    }

    public boolean isActive()
    {
        return status == EventStatus.ACTIVE;
    }

    public double getAccountBalance()
    {
        return account.getBalance();
    }

    public double getCollectedCommission()
    {
        return collectedCommission;
    }

    public List<Trade> getTrades()
    {
        return trades;
    }

    public EventOption getWinningOption() 
    {
        return winningOption;
    }

    public double getOptionValue(int optionIndex)
    {
        return method.getOptionValue(optionIndex);
    }

    public Trade buyShares(int optionIndex, int quantity) 
    {
        double sharesCost = method.getBuyCost(optionIndex, quantity);
        double commission = 0;
        if (commissionType == CommissionType.ON_PURCHASE)
        {
            commission = sharesCost * commissionPercent / 100.0;
            collectedCommission += commission;
        }

        options.get(optionIndex).addShares(quantity);
        account.deposit(sharesCost + commission);

        Trade trade = new Trade(options.get(optionIndex).getName(), quantity, sharesCost, commission);
        trades.add(trade);
        return trade;
    }

    public void close(int optionIndex)
    {
        winningOption = options.get(optionIndex);
        double payout = winningOption.getSharesBought() * WINNING_SHARE_VALUE;

        if (commissionType == CommissionType.ON_CLOSE) 
        {
            double commission = payout * commissionPercent / 100.0;
            collectedCommission += commission;
            payout -= commission;
        }

        account.withdraw(payout);
        status = EventStatus.CLOSED;
    }
}
