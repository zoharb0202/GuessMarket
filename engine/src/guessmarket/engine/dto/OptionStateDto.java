package guessmarket.engine.dto;

public class OptionStateDto {
    private final String name;
    private final double value;
    private final int sharesBought;

    public OptionStateDto(String name, double value, int sharesBought) {
        this.name = name;
        this.value = value;
        this.sharesBought = sharesBought;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public int getSharesBought() {
        return sharesBought;
    }
}
