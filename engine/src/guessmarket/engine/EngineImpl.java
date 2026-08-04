package guessmarket.engine;

import guessmarket.engine.dto.EventDto;
import guessmarket.engine.dto.EventStateDto;
import guessmarket.engine.dto.OptionStateDto;
import guessmarket.engine.dto.PurchaseResultDto;
import guessmarket.engine.dto.TradeDto;
import guessmarket.engine.exception.InvalidFileException;
import guessmarket.engine.exception.StateFileException;
import guessmarket.engine.model.Event;
import guessmarket.engine.model.EventOption;
import guessmarket.engine.model.Market;
import guessmarket.engine.model.Trade;
import guessmarket.engine.state.StateManager;
import guessmarket.engine.xml.MarketLoader;

import java.util.ArrayList;
import java.util.List;

public class EngineImpl implements GuessMarketEngine {
    private final MarketLoader loader;
    private final StateManager stateManager;
    private Market market;

    public EngineImpl() {
        loader = new MarketLoader();
        stateManager = new StateManager();
    }

    @Override
    public void loadEventsFile(String path) throws InvalidFileException {
        // the loader throws when the file is not valid, so the market that is
        // already loaded stays untouched
        market = loader.load(path);
    }

    @Override
    public boolean isFileLoaded() {
        return market != null;
    }

    @Override
    public List<EventDto> getAllEvents() {
        return toEventDtos(market.getEvents());
    }

    @Override
    public List<EventDto> getActiveEvents() {
        return toEventDtos(market.getActiveEvents());
    }

    @Override
    public EventStateDto getEventState(int eventId) {
        Event event = market.findById(eventId);

        List<OptionStateDto> options = new ArrayList<OptionStateDto>();
        for (int i = 0; i < event.getOptions().size(); i++) {
            EventOption option = event.getOptions().get(i);
            options.add(new OptionStateDto(option.getName(), event.getOptionValue(i), option.getSharesBought()));
        }

        // the history is returned from the newest trade to the oldest one
        List<TradeDto> trades = new ArrayList<TradeDto>();
        for (int i = event.getTrades().size() - 1; i >= 0; i--) {
            Trade trade = event.getTrades().get(i);
            trades.add(new TradeDto(trade.getOptionName(), trade.getQuantity(), trade.getTotalPaid()));
        }

        String winningOptionName = null;
        if (event.getWinningOption() != null) {
            winningOptionName = event.getWinningOption().getName();
        }

        return new EventStateDto(event.getId(), event.getName(), options, event.getAccountBalance(),
                event.getCollectedCommission(), trades, !event.isActive(), winningOptionName);
    }

    @Override
    public PurchaseResultDto buyShares(int eventId, int optionIndex, int quantity) {
        Event event = market.findById(eventId);
        Trade trade = event.buyShares(optionIndex, quantity);
        return new PurchaseResultDto(trade.getSharesCost(), trade.getCommission());
    }

    @Override
    public void closeEvent(int eventId, int optionIndex) {
        market.findById(eventId).close(optionIndex);
    }

    @Override
    public void saveState(String path) throws StateFileException {
        stateManager.save(market, path);
    }

    @Override
    public void loadState(String path) throws StateFileException {
        market = stateManager.load(path);
    }

    private List<EventDto> toEventDtos(List<Event> events) {
        List<EventDto> eventDtos = new ArrayList<EventDto>();
        for (Event event : events) {
            List<String> optionNames = new ArrayList<String>();
            for (EventOption option : event.getOptions()) {
                optionNames.add(option.getName());
            }
            eventDtos.add(new EventDto(event.getId(), event.getName(), event.getDescription(),
                    event.getCommissionPercent(), event.getCommissionType().getDescription(),
                    optionNames, event.getStatus().getDescription()));
        }
        return eventDtos;
    }
}
