package guessmarket.engine;

import guessmarket.engine.dto.EventDto;
import guessmarket.engine.dto.EventStateDto;
import guessmarket.engine.dto.PurchaseResultDto;
import guessmarket.engine.exception.InvalidFileException;
import guessmarket.engine.exception.StateFileException;

import java.util.List;

public interface GuessMarketEngine 
{

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
