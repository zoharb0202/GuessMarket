package guessmarket.client.component.market;

import guessmarket.dto.AccountLineDto;
import guessmarket.dto.EventDto;
import guessmarket.dto.UserDto;

import java.util.List;

public class MarketData {

    private final List<EventDto> events;
    private final List<UserDto> users;
    private final List<AccountLineDto> accountLines;
    private final EventDetails eventsTabDetails;
    private final EventDetails accountTabDetails;

    public MarketData(List<EventDto> events, List<UserDto> users, List<AccountLineDto> accountLines,
                      EventDetails eventsTabDetails, EventDetails accountTabDetails) {
        this.events = events;
        this.users = users;
        this.accountLines = accountLines;
        this.eventsTabDetails = eventsTabDetails;
        this.accountTabDetails = accountTabDetails;
    }

    public List<EventDto> getEvents() {
        return events;
    }

    public List<UserDto> getUsers() {
        return users;
    }

    public List<AccountLineDto> getAccountLines() {
        return accountLines;
    }

    public EventDetails getEventsTabDetails() {
        return eventsTabDetails;
    }

    public EventDetails getAccountTabDetails() {
        return accountTabDetails;
    }
}
