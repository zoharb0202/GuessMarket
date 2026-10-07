package guessmarket.client.component.rows;

import guessmarket.dto.UserDto;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Locale;

public class UserRow {

    private final String name;
    private final StringProperty nameProperty;
    private final StringProperty balanceProperty;
    private final StringProperty marketMakerProperty;

    public UserRow(UserDto dto) {
        name = dto.getName();
        nameProperty = new SimpleStringProperty(name);
        balanceProperty = new SimpleStringProperty(String.format(Locale.US, "%.2f", dto.getBalance()));
        marketMakerProperty = new SimpleStringProperty(dto.getMarketMakerEventIds().isEmpty() ? "No" : "Yes");
    }

    public String getName() {
        return name;
    }

    public StringProperty nameProperty() {
        return nameProperty;
    }

    public StringProperty balanceProperty() {
        return balanceProperty;
    }

    public StringProperty marketMakerProperty() {
        return marketMakerProperty;
    }
}
