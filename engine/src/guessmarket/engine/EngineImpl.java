package guessmarket.engine;

import guessmarket.dto.AccountLineDto;
import guessmarket.dto.EventDto;
import guessmarket.dto.EventStateDto;
import guessmarket.dto.OptionBookDto;
import guessmarket.dto.OptionStateDto;
import guessmarket.dto.OrderBookStateDto;
import guessmarket.dto.OrderDto;
import guessmarket.dto.ParticipantDto;
import guessmarket.dto.ParticipationDto;
import guessmarket.dto.TradeDto;
import guessmarket.dto.TradeResultDto;
import guessmarket.dto.UserDto;
import guessmarket.engine.exception.InvalidFileException;
import guessmarket.engine.exception.InvalidRequestException;
import guessmarket.engine.method.LmsrMethod;
import guessmarket.engine.method.OrderBookMethod;
import guessmarket.engine.model.AccountLine;
import guessmarket.engine.model.Event;
import guessmarket.engine.model.EventOption;
import guessmarket.engine.model.Market;
import guessmarket.engine.model.Order;
import guessmarket.engine.model.OrderBook;
import guessmarket.engine.model.Participation;
import guessmarket.engine.model.Side;
import guessmarket.engine.model.Trade;
import guessmarket.engine.model.TradeResult;
import guessmarket.engine.model.User;
import guessmarket.engine.xml.MarketLoader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EngineImpl implements GuessMarketEngine
{
    private final Market market = new Market();
    private int version;

    @Override
    public void addUser(String userName)
    {
        String name = userName == null ? "" : userName.trim();
        if (name.isEmpty())
        {
            throw new InvalidRequestException("Please enter a user name");
        }
        for (char c : name.toCharArray())
        {
            if (c < 32 || c > 126)
            {
                throw new InvalidRequestException("The user name must be written in English letters only");
            }
        }
        if (market.containsUser(name))
        {
            throw new InvalidRequestException("The user name '" + name + "' is already taken. Please choose a different name");
        }
        market.addUser(new User(name));
        version++;
    }

    @Override
    public boolean isUserExists(String userName)
    {
        return userName != null && market.containsUser(userName.trim());
    }

    @Override
    public int loadEventsFile(InputStream content, String fileName, String uploaderName) throws InvalidFileException
    {
        User uploader = requireUser(uploaderName);
        MarketLoader loader = new MarketLoader();
        List<Event> events = loader.load(content, fileName, market);
        for (Event event : events)
        {
            event.setMarketMakerName(uploader.getName());
            market.addEvent(event);
            uploader.addMarketMakerEvent(event.getId());
        }
        version++;
        return events.size();
    }

    @Override
    public int getVersion()
    {
        return version;
    }

    @Override
    public List<EventDto> getAllEvents()
    {
        List<EventDto> result = new ArrayList<EventDto>();
        for (Event event : market.getEvents())
        {
            result.add(toEventDto(event));
        }
        return result;
    }

    @Override
    public EventDto getEvent(int eventId)
    {
        return toEventDto(requireEvent(eventId));
    }

    @Override
    public EventStateDto getLmsrState(int eventId)
    {
        Event event = requireEvent(eventId);
        if (event.isOrderBook())
        {
            throw new InvalidRequestException("Event number " + eventId + " is not an LMSR event");
        }

        LmsrMethod lmsr = event.getLmsrMethod();
        List<OptionStateDto> options = new ArrayList<OptionStateDto>();
        for (int i = 0; i < event.getOptionCount(); i++)
        {
            EventOption option = event.getOptions().get(i);
            options.add(new OptionStateDto(option.getName(), option.getSharesBought(), lmsr.getOptionValue(i)));
        }

        return new EventStateDto(event.getId(), event.getName(), event.getStatus().getDescription(),
                event.getAccountBalance(), event.getCollectedCommission(), lmsr.getB(), options,
                toTradeDtos(event.getTrades()), winnerName(event));
    }

    @Override
    public OrderBookStateDto getOrderBookState(int eventId)
    {
        Event event = requireEvent(eventId);
        if (!event.isOrderBook())
        {
            throw new InvalidRequestException("Event number " + eventId + " is not an order book event");
        }

        OrderBookMethod method = event.getOrderBookMethod();
        List<OptionBookDto> books = new ArrayList<OptionBookDto>();
        for (int i = 0; i < event.getOptionCount(); i++)
        {
            OrderBook book = method.getBook(i);
            books.add(new OptionBookDto(i, event.getOptions().get(i).getName(),
                    event.getOptions().get(i).getSharesBought(),
                    toOrderDtos(book.getBids()), toOrderDtos(book.getAsks()),
                    book.getLastPrice(), book.getBestBidPrice(), book.getBestAskPrice(),
                    book.getMidPrice(), book.getSpread()));
        }

        List<ParticipantDto> participants = new ArrayList<ParticipantDto>();
        for (String name : event.getParticipants())
        {
            User user = market.find(name);
            Participation participation = user.getParticipation(eventId);
            if (participation == null)
            {
                continue;
            }
            participants.add(new ParticipantDto(name, sharesOf(participation), netPaidOf(participation), participation.getCommissionPaid()));
        }

        return new OrderBookStateDto(event.getId(), event.getName(), event.getStatus().getDescription(),
                method.getD(), method.isMintAllowed(), method.getInitialInvestment(),
                event.getAccountBalance(), event.getCollectedCommission(), books, participants, winnerName(event));
    }

    @Override
    public List<UserDto> getAllUsers()
    {
        List<UserDto> result = new ArrayList<UserDto>();
        for (User user : market.getUsers())
        {
            result.add(toUserDto(user));
        }
        return result;
    }

    @Override
    public UserDto getUser(String userName)
    {
        return toUserDto(requireUser(userName));
    }

    @Override
    public List<ParticipationDto> getParticipations(String userName)
    {
        User user = requireUser(userName);
        List<ParticipationDto> result = new ArrayList<ParticipationDto>();
        for (Participation participation : user.getParticipations())
        {
            result.add(toParticipationDto(participation));
        }
        return result;
    }

    @Override
    public ParticipationDto getParticipation(String userName, int eventId)
    {
        User user = requireUser(userName);
        Participation participation = user.getParticipation(eventId);
        if (participation == null)
        {
            return null;
        }
        return toParticipationDto(participation);
    }

    @Override
    public List<AccountLineDto> getAccountLines(String userName)
    {
        List<AccountLineDto> result = new ArrayList<AccountLineDto>();
        int number = 1;
        for (AccountLine line : requireUser(userName).getAccountLines())
        {
            result.add(new AccountLineDto(number, line.getDescription(), line.getAmount(), line.getBalanceAfter()));
            number++;
        }
        return result;
    }

    @Override
    public void loadFunds(String userName, double amount)
    {
        User user = requireUser(userName);
        if (amount <= 0)
        {
            throw new InvalidRequestException("The amount to load has to be a positive number");
        }
        user.receive(amount, "Loaded funds to the account");
        version++;
    }

    @Override
    public void openEvent(String userName, int eventId)
    {
        requireEvent(eventId).open(requireUser(userName));
        version++;
    }

    @Override
    public TradeResultDto buyShares(String userName, int eventId, int optionIndex, int quantity)
    {
        Event event = requireEvent(eventId);
        if (event.isOrderBook())
        {
            throw new InvalidRequestException("Event number " + eventId + " is an order book event - use an order instead");
        }
        TradeResultDto result = toResultDto(event.buyLmsr(requireUser(userName), optionIndex, quantity, market), userName);
        version++;
        return result;
    }

    @Override
    public TradeResultDto submitOrder(String userName, int eventId, int optionIndex, String side, int quantity, double price)
    {
        Event event = requireEvent(eventId);
        if (!event.isOrderBook())
        {
            throw new InvalidRequestException("Event number " + eventId + " is an LMSR event - orders are not used in it");
        }
        Side parsedSide = Side.fromDescription(side);
        if (parsedSide == null)
        {
            throw new InvalidRequestException("'" + side + "' is not a valid order side");
        }
        TradeResultDto result = toResultDto(event.submitOrder(requireUser(userName), parsedSide, optionIndex, quantity, price, market), userName);
        version++;
        return result;
    }

    @Override
    public void closeEvent(String userName, int eventId, int optionIndex)
    {
        requireEvent(eventId).close(requireUser(userName), optionIndex, market);
        version++;
    }


    private EventDto toEventDto(Event event)
    {
        List<String> optionNames = new ArrayList<String>();
        for (EventOption option : event.getOptions())
        {
            optionNames.add(option.getName());
        }
        return new EventDto(event.getId(), event.getName(), event.getDescription(), event.getCommissionPercent(),
                event.getCommissionType().getFileValue(), event.getCommissionType().getDescription(),
                event.getMethod().getTypeName(), event.getStatus().getDescription(), event.getAccountBalance(),
                event.getMarketMakerName(), optionNames);
    }

    private UserDto toUserDto(User user)
    {
        List<Integer> mmEvents = new ArrayList<Integer>(user.getMarketMakerEvents());
        List<Integer> participating = new ArrayList<Integer>();
        for (Participation participation : user.getParticipations())
        {
            participating.add(Integer.valueOf(participation.getEventId()));
        }
        return new UserDto(user.getName(), user.getBalance(), user.isBlocked(), mmEvents, participating);
    }

    private ParticipationDto toParticipationDto(Participation participation)
    {
        Event event = market.findById(participation.getEventId());
        List<String> optionNames = new ArrayList<String>();
        for (EventOption option : event.getOptions())
        {
            optionNames.add(option.getName());
        }
        return new ParticipationDto(event.getId(), event.getName(), event.getMethod().getTypeName(),
                event.getStatus().getDescription(), optionNames, sharesOf(participation), netPaidOf(participation),
                participation.getCommissionPaid(), toTradeDtos(participation.getTrades()), participation.isSettled(),
                participation.getPayout(), participation.getProfitOrLoss(), winnerName(event));
    }

    private TradeResultDto toResultDto(TradeResult result, String actingUserName)
    {
        double totalAmount = 0;
        double totalCommission = 0;
        for (Trade trade : result.getTrades())
        {
            if (trade.getUserName().equals(actingUserName))
            {
                totalAmount += trade.getAmount();
                totalCommission += trade.getCommission();
            }
        }
        return new TradeResultDto(result.getFilledQuantity(), result.getRestingQuantity(), result.isMinted(),
                totalAmount, totalCommission, toTradeDtos(result.getTrades()),
                new ArrayList<String>(result.getBlockedUsers()));
    }

    private List<TradeDto> toTradeDtos(List<Trade> trades)
    {
        List<TradeDto> result = new ArrayList<TradeDto>();
        for (int i = trades.size() - 1; i >= 0; i--)
        {
            Trade trade = trades.get(i);
            result.add(new TradeDto(trade.getUserName(), trade.getOptionName(), trade.getSide().getDescription(),
                    trade.getQuantity(), trade.getAmount(), trade.getPricePerShare(), trade.getCommission()));
        }
        return result;
    }

    private List<OrderDto> toOrderDtos(List<Order> orders)
    {
        List<OrderDto> result = new ArrayList<OrderDto>();
        for (Order order : orders)
        {
            result.add(new OrderDto(order.getId(), order.getUserName(), order.getSide().getDescription(),
                    order.getQuantity(), order.getPrice()));
        }
        return result;
    }

    private List<Integer> sharesOf(Participation participation)
    {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < participation.getOptionCount(); i++)
        {
            result.add(Integer.valueOf(participation.getShares(i)));
        }
        return result;
    }

    private List<Double> netPaidOf(Participation participation)
    {
        List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < participation.getOptionCount(); i++)
        {
            result.add(Double.valueOf(participation.getNetPaid(i)));
        }
        return result;
    }

    private String winnerName(Event event)
    {
        if (event.getWinningOption() == null)
        {
            return null;
        }
        return event.getWinningOption().getName();
    }

    private Event requireEvent(int eventId)
    {
        Event event = market.findById(eventId);
        if (event == null)
        {
            throw new InvalidRequestException("There is no event number " + eventId + " in the system");
        }
        return event;
    }

    private User requireUser(String userName)
    {
        User user = market.find(userName);
        if (user == null)
        {
            throw new InvalidRequestException("There is no user named '" + userName + "' in the system");
        }
        return user;
    }
}
