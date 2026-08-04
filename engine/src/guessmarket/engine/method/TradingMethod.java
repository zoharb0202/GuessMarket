package guessmarket.engine.method;

import guessmarket.engine.model.EventOption;

import java.io.Serializable;
import java.util.List;

/**
 * A trading method decides how much an option is worth and how much a purchase costs.
 * For now the only method is LMSR, but the file format already allows other methods.
 */
public abstract class TradingMethod implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<EventOption> options;

    protected TradingMethod(List<EventOption> options) {
        this.options = options;
    }

    protected List<EventOption> getOptions() {
        return options;
    }

    public abstract double getOptionValue(int optionIndex);

    public abstract double getBuyCost(int optionIndex, int quantity);

    public abstract double getInitialSubsidy();
}
