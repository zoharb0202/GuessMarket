package guessmarket.engine.method;

import guessmarket.engine.model.EventOption;

import java.io.Serializable;
import java.util.List;

public abstract class TradingMethod implements Serializable {
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

    public abstract double getOptionValue(int optionIndex);

    public abstract double getBuyCost(int optionIndex, int quantity);

    public abstract double getInitialSubsidy();
}
