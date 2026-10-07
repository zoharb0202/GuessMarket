package guessmarket.engine.method;

import guessmarket.engine.model.EventOption;

import java.io.Serializable;
import java.util.List;

public abstract class TradingMethod implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final List<EventOption> options;

    protected TradingMethod(List<EventOption> options)
    {
        this.options = options;
    }

    protected List<EventOption> getOptions()
    {
        return options;
    }

    public int getOptionCount()
    {
        return options.size();
    }

    public abstract String getTypeName();

    public abstract double getInitialInvestment();

    public abstract double getWinningShareValue();
}
