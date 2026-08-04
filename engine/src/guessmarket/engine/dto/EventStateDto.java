package guessmarket.engine.dto;

import java.util.List;

public class EventStateDto {
    private final int eventId;
    private final String eventName;
    private final List<OptionStateDto> options;
    private final double accountBalance;
    private final double collectedCommission;
    private final List<TradeDto> trades;
    private final boolean closed;
    private final String winningOptionName;

    public EventStateDto(int eventId, String eventName, List<OptionStateDto> options,
                         double accountBalance, double collectedCommission, List<TradeDto> trades,
                         boolean closed, String winningOptionName) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.options = options;
        this.accountBalance = accountBalance;
        this.collectedCommission = collectedCommission;
        this.trades = trades;
        this.closed = closed;
        this.winningOptionName = winningOptionName;
    }

    public int getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public List<OptionStateDto> getOptions() {
        return options;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public double getCollectedCommission() {
        return collectedCommission;
    }

    public List<TradeDto> getTrades() {
        return trades;
    }

    public boolean isClosed() {
        return closed;
    }

    public String getWinningOptionName() {
        return winningOptionName;
    }
}
