package guessmarket.engine;

import guessmarket.engine.dto.EventDto;
import guessmarket.engine.dto.EventStateDto;
import guessmarket.engine.dto.PurchaseResultDto;
import guessmarket.engine.exception.InvalidFileException;
import guessmarket.engine.exception.StateFileException;

import java.util.List;

/**
 * The set of actions the system knows how to do.
 * The engine only answers requests, it does not know who is asking.
 * Option numbers that come in here are indexes that start from 0 - it is up to the
 * user interface to show them to the user starting from 1.
 */
public interface GuessMarketEngine {

    void loadEventsFile(String path) throws InvalidFileException;

    boolean isFileLoaded();

    List<EventDto> getAllEvents();

    List<EventDto> getActiveEvents();

    EventStateDto getEventState(int eventId);

    PurchaseResultDto buyShares(int eventId, int optionIndex, int quantity);

    void closeEvent(int eventId, int optionIndex);

    void saveState(String path) throws StateFileException;

    void loadState(String path) throws StateFileException;
}
