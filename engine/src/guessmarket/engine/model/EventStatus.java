package guessmarket.engine.model;

public enum EventStatus {
    ACTIVE("Active"),
    CLOSED("Closed");

    private final String description;

    EventStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
