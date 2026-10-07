package guessmarket.client.component.market;

import guessmarket.dto.EventDto;
import guessmarket.dto.EventStateDto;
import guessmarket.dto.OrderBookStateDto;
import guessmarket.dto.ParticipationDto;

public class EventDetails {

    private final EventDto event;
    private final EventStateDto lmsrState;
    private final OrderBookStateDto orderBookState;
    private final ParticipationDto participation;

    public EventDetails(EventDto event, EventStateDto lmsrState, OrderBookStateDto orderBookState, ParticipationDto participation) {
        this.event = event;
        this.lmsrState = lmsrState;
        this.orderBookState = orderBookState;
        this.participation = participation;
    }

    public EventDto getEvent() {
        return event;
    }

    public EventStateDto getLmsrState() {
        return lmsrState;
    }

    public OrderBookStateDto getOrderBookState() {
        return orderBookState;
    }

    public ParticipationDto getParticipation() {
        return participation;
    }

    public boolean isLmsr() {
        return lmsrState != null;
    }
}
