package guessmarket.engine;

import guessmarket.dto.AccountLineDto;
import guessmarket.dto.EventDto;
import guessmarket.dto.EventStateDto;
import guessmarket.dto.OrderBookStateDto;
import guessmarket.dto.ParticipationDto;
import guessmarket.dto.TradeResultDto;
import guessmarket.dto.UserDto;
import guessmarket.engine.exception.InvalidFileException;

import java.io.InputStream;
import java.util.List;

public interface GuessMarketEngine
{
    void addUser(String userName);

    boolean isUserExists(String userName);

    int loadEventsFile(InputStream content, String fileName, String uploaderName) throws InvalidFileException;

    int getVersion();

    List<EventDto> getAllEvents();

    EventDto getEvent(int eventId);

    EventStateDto getLmsrState(int eventId);

    OrderBookStateDto getOrderBookState(int eventId);

    List<UserDto> getAllUsers();

    UserDto getUser(String userName);

    List<AccountLineDto> getAccountLines(String userName);

    List<ParticipationDto> getParticipations(String userName);

    ParticipationDto getParticipation(String userName, int eventId);

    void loadFunds(String userName, double amount);

    void openEvent(String userName, int eventId);

    TradeResultDto buyShares(String userName, int eventId, int optionIndex, int quantity);

    TradeResultDto submitOrder(String userName, int eventId, int optionIndex, String side, int quantity, double price);

    void closeEvent(String userName, int eventId, int optionIndex);
}
