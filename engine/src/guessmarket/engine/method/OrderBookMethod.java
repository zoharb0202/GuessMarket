package guessmarket.engine.method;

import guessmarket.engine.model.EventOption;
import guessmarket.engine.model.OrderBook;

import java.util.ArrayList;
import java.util.List;

public class OrderBookMethod extends TradingMethod
{
    private static final long serialVersionUID = 1L;

    public static final String TYPE_NAME = "Order Book";
    private static final double PRICE_STEP = 0.01;

    private final boolean allowMint;
    private final int initial;
    private final int d;
    private final List<OrderBook> books;
    private long nextOrderId;

    public OrderBookMethod(List<EventOption> options, boolean allowMint, int initial, int d)
    {
        super(options);
        this.allowMint = allowMint;
        this.initial = initial;
        this.d = d;
        books = new ArrayList<OrderBook>();
        for (int i = 0; i < options.size(); i++)
        {
            books.add(new OrderBook());
        }
        nextOrderId = 1;
    }

    public boolean isMintAllowed()
    {
        return allowMint;
    }

    public int getInitial()
    {
        return initial;
    }

    public int getD()
    {
        return d;
    }

    public OrderBook getBook(int optionIndex)
    {
        return books.get(optionIndex);
    }

    public List<OrderBook> getBooks()
    {
        return books;
    }

    public int getInitialPairs()
    {
        return initial / d;
    }

    public double getMaxPrice()
    {
        return d - PRICE_STEP;
    }

    public long takeOrderId()
    {
        return nextOrderId++;
    }

    public void closeBooks()
    {
        for (OrderBook book : books)
        {
            book.clear();
        }
    }

    @Override
    public String getTypeName()
    {
        return TYPE_NAME;
    }

    @Override
    public double getInitialInvestment()
    {
        return initial;
    }

    @Override
    public double getWinningShareValue()
    {
        return d;
    }
}
