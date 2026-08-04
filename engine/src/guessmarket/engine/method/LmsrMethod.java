package guessmarket.engine.method;

import guessmarket.engine.model.EventOption;

import java.util.List;

public class LmsrMethod extends TradingMethod {
    private static final long serialVersionUID = 1L;

    private final int b;

    public LmsrMethod(List<EventOption> options, int b) {
        super(options);
        this.b = b;
    }

    public int getB() {
        return b;
    }

    @Override
    public double getOptionValue(int optionIndex) {
        int[] quantities = currentQuantities();
        int max = findMax(quantities);
        return weightOf(quantities[optionIndex], max) / sumOfWeights(quantities, max);
    }

    @Override
    public double getBuyCost(int optionIndex, int quantity) {
        int[] before = currentQuantities();
        int[] after = currentQuantities();
        after[optionIndex] += quantity;
        return costOf(after) - costOf(before);
    }

    @Override
    public double getInitialSubsidy() {
        return costOf(new int[getOptions().size()]);
    }

    private int[] currentQuantities() {
        List<EventOption> options = getOptions();
        int[] quantities = new int[options.size()];
        for (int i = 0; i < options.size(); i++) {
            quantities[i] = options.get(i).getSharesBought();
        }
        return quantities;
    }

    // C(q) = b * ln( sum of e^(qi/b) ).
    // the biggest quantity is pulled out of the exponent, otherwise a large purchase
    // makes Math.exp return infinity and every number after that becomes NaN
    private double costOf(int[] quantities) {
        int max = findMax(quantities);
        return max + b * Math.log(sumOfWeights(quantities, max));
    }

    private double sumOfWeights(int[] quantities, int max) {
        double sum = 0;
        for (int quantity : quantities) {
            sum += weightOf(quantity, max);
        }
        return sum;
    }

    private double weightOf(int quantity, int max) {
        return Math.exp((quantity - max) / (double) b);
    }

    private int findMax(int[] quantities) {
        int max = quantities[0];
        for (int quantity : quantities) {
            if (quantity > max) {
                max = quantity;
            }
        }
        return max;
    }
}
