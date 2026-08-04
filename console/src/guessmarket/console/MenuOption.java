package guessmarket.console;

public enum MenuOption {
    LOAD_FILE("Load an events file"),
    SHOW_EVENTS("Show all the events"),
    EVENT_STATE("Show the trading state of an event"),
    PARTICIPATE("Participate in an event"),
    CLOSE_EVENT("Close an event"),
    SAVE_STATE("Save the state of the system to a file"),
    LOAD_STATE("Load a state of the system from a file"),
    EXIT("Exit");

    private final String description;

    MenuOption(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
