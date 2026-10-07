package guessmarket.client.component.details;

import guessmarket.client.component.market.EventDetails;
import guessmarket.client.component.market.MarketMainController;
import guessmarket.client.util.Alerts;
import guessmarket.client.util.Constants;
import guessmarket.client.util.http.ActionCallback;
import guessmarket.client.util.http.HttpClientUtil;
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
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import static guessmarket.client.util.Constants.GSON_INSTANCE;

public class EventDetailsController {

    @FXML private VBox detailsBox;

    private MarketMainController marketMainController;

    private Integer shownEventId;
    private String shownStatus;
    private boolean shownAsMarketMaker;

    private Label titleLabel;
    private Label descriptionLabel;
    private Label summaryLabel;
    private TableView<OptionStateDto> optionsTable;
    private TableView<TradeDto> tradesTable;
    private final List<Label> bookHeaderLabels = new ArrayList<>();
    private final List<Label> bookStatsLabels = new ArrayList<>();
    private final List<TableView<OrderDto>> bidsTables = new ArrayList<>();
    private final List<TableView<OrderDto>> asksTables = new ArrayList<>();
    private TableView<ParticipantDto> participantsTable;
    private VBox participationBox;

    @FXML
    public void initialize() {
        showMessage("Select an event to see its details");
    }

    public void setMarketMainController(MarketMainController marketMainController) {
        this.marketMainController = marketMainController;
    }

    public void showLoading(Integer eventId) {
        if (eventId == null) {
            showMessage("Select an event to see its details");
        } else if (!eventId.equals(shownEventId)) {
            showMessage("Loading the event details...");
        }
    }

    public void update(EventDetails details) {
        if (details == null) {
            if (shownEventId != null) {
                showMessage("Select an event to see its details");
            }
            return;
        }

        EventDto event = details.getEvent();
        boolean isMarketMaker = event.getMarketMakerName().equalsIgnoreCase(marketMainController.getUserName());
        boolean sameView = shownEventId != null && shownEventId == event.getId()
                && event.getStatus().equals(shownStatus) && isMarketMaker == shownAsMarketMaker;

        if (!sameView) {
            build(details, isMarketMaker);
        }
        refreshValues(details);
    }

    private void showMessage(String message) {
        shownEventId = null;
        shownStatus = null;
        detailsBox.getChildren().setAll(new Label(message));
    }

    private void build(EventDetails details, boolean isMarketMaker) {
        EventDto event = details.getEvent();
        shownEventId = event.getId();
        shownStatus = event.getStatus();
        shownAsMarketMaker = isMarketMaker;
        detailsBox.getChildren().clear();

        titleLabel = new Label();
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        titleLabel.setWrapText(true);
        descriptionLabel = new Label(event.getDescription());
        descriptionLabel.setWrapText(true);
        summaryLabel = new Label();
        summaryLabel.setWrapText(true);
        detailsBox.getChildren().addAll(titleLabel, descriptionLabel, summaryLabel);

        if (details.isLmsr()) {
            buildLmsrPart();
        } else {
            buildOrderBookPart(details.getOrderBookState());
        }

        Label mineHeader = new Label("Your participation:");
        mineHeader.setStyle("-fx-font-weight: bold;");
        participationBox = new VBox(6);
        detailsBox.getChildren().addAll(mineHeader, participationBox, buildActionsBox(event, isMarketMaker));

        marketMainController.playFade(detailsBox, Duration.millis(300));
    }

    private void buildLmsrPart() {
        optionsTable = new TableView<>();
        optionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        optionsTable.setPrefHeight(90);
        optionsTable.getColumns().add(textColumn("Option", OptionStateDto::getName));
        optionsTable.getColumns().add(textColumn("Shares bought", option -> String.valueOf(option.getShares())));
        optionsTable.getColumns().add(textColumn("Current value", option -> formatNumber(option.getValue())));

        tradesTable = buildTradesTable();
        detailsBox.getChildren().addAll(optionsTable, new Label("Trade history (most recent first):"), tradesTable);
    }

    private void buildOrderBookPart(OrderBookStateDto state) {
        bookHeaderLabels.clear();
        bookStatsLabels.clear();
        bidsTables.clear();
        asksTables.clear();

        HBox optionsRow = new HBox(12);
        for (int i = 0; i < state.getBooks().size(); i++) {
            VBox block = buildOptionBookBlock();
            HBox.setHgrow(block, Priority.ALWAYS);
            optionsRow.getChildren().add(block);
        }

        participantsTable = new TableView<>();
        participantsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        participantsTable.setPrefHeight(130);
        participantsTable.setPlaceholder(new Label("No participants yet"));
        participantsTable.getColumns().add(textColumn("User", ParticipantDto::getUserName));
        participantsTable.getColumns().add(textColumn("Holdings", participant -> formatHoldings(participant, state.getBooks())));
        participantsTable.getColumns().add(textColumn("Commission paid", participant -> formatNumber(participant.getCommissionPaid())));

        detailsBox.getChildren().addAll(optionsRow, new Label("Participants:"), participantsTable);
    }

    private VBox buildOptionBookBlock() {
        Label header = new Label();
        header.setStyle("-fx-font-weight: bold;");
        Label stats = new Label();
        stats.setWrapText(true);
        stats.setMaxWidth(Double.MAX_VALUE);
        TableView<OrderDto> bidsTable = buildOrdersTable("No buy orders");
        TableView<OrderDto> asksTable = buildOrdersTable("No sell orders");

        bookHeaderLabels.add(header);
        bookStatsLabels.add(stats);
        bidsTables.add(bidsTable);
        asksTables.add(asksTable);

        return new VBox(4, header, stats, new Label("Bids (buy orders):"), bidsTable, new Label("Asks (sell orders):"), asksTable);
    }

    private void refreshValues(EventDetails details) {
        EventDto event = details.getEvent();
        String title = event.getName() + " - " + event.getMethodType() + " (" + event.getStatus() + ")";

        if (details.isLmsr()) {
            EventStateDto state = details.getLmsrState();
            if (state.getWinningOptionName() != null) {
                title += "  winner: " + state.getWinningOptionName();
            }
            summaryLabel.setText(String.format(Locale.US,
                    "Market maker: %s    Event account: %.2f    Commission: %d%% %s    Commission collected: %.2f    b: %d",
                    event.getMarketMakerName(), state.getAccountBalance(), event.getCommissionPercent(),
                    event.getCommissionTypeValue(), state.getCollectedCommission(), state.getB()));
            optionsTable.getItems().setAll(state.getOptions());
            tradesTable.getItems().setAll(state.getTrades());
        } else {
            OrderBookStateDto state = details.getOrderBookState();
            if (state.getWinningOptionName() != null) {
                title += "  winner: " + state.getWinningOptionName();
            }
            summaryLabel.setText(String.format(Locale.US,
                    "Market maker: %s    Event account: %.2f    Commission: %d%% %s    Commission collected: %.2f    Base value (d): %d    Mint allowed: %s",
                    event.getMarketMakerName(), state.getAccountBalance(), event.getCommissionPercent(),
                    event.getCommissionTypeValue(), state.getCollectedCommission(), state.getBaseValue(),
                    state.isMintAllowed() ? "Yes" : "No"));
            for (int i = 0; i < state.getBooks().size(); i++) {
                OptionBookDto book = state.getBooks().get(i);
                bookHeaderLabels.get(i).setText(book.getOptionName() + " (shares outstanding: " + book.getTotalShares() + ")");
                bookStatsLabels.get(i).setText("LAST: " + formatPrice(book.getLastPrice()) + "    BID: " + formatPrice(book.getBestBid())
                        + "    ASK: " + formatPrice(book.getBestAsk()) + "    MID: " + formatPrice(book.getMidPrice())
                        + "    SPREAD: " + formatPrice(book.getSpread()));
                bidsTables.get(i).getItems().setAll(book.getBids());
                asksTables.get(i).getItems().setAll(book.getAsks());
            }
            participantsTable.getItems().setAll(state.getParticipants());
        }
        titleLabel.setText(title);

        participationBox.getChildren().setAll(buildParticipationBlock(details));
    }

    private VBox buildParticipationBlock(EventDetails details) {
        VBox box = new VBox(6);
        ParticipationDto participation = details.getParticipation();
        if (participation == null) {
            box.getChildren().add(new Label("You did not take part in this event yet"));
            return box;
        }

        if (details.isLmsr()) {
            TableView<TradeDto> myTrades = buildTradesTable();
            myTrades.setPrefHeight(110);
            myTrades.getItems().setAll(participation.getTrades());
            box.getChildren().add(myTrades);
        } else {
            List<String> optionNames = participation.getOptionNames();
            for (int i = 0; i < optionNames.size(); i++) {
                box.getChildren().add(new Label(String.format(Locale.US, "%s: %d shares (paid %.2f)",
                        optionNames.get(i), participation.getShares().get(i), participation.getNetPaid().get(i))));
            }
        }
        box.getChildren().add(new Label(String.format(Locale.US, "Commission paid: %.2f", participation.getCommissionPaid())));

        if (participation.isSettled()) {
            box.getChildren().add(new Label(String.format(Locale.US,
                    "Event closed - winning option: %s    Your payout: %.2f    Profit/Loss: %.2f",
                    participation.getWinningOptionName(), participation.getPayout(), participation.getProfitOrLoss())));
        }
        return box;
    }

    private VBox buildActionsBox(EventDto event, boolean isMarketMaker) {
        VBox box = new VBox(8);

        if (isMarketMaker && Constants.NOT_STARTED.equals(event.getStatus())) {
            Button openButton = new Button("Open event");
            openButton.setOnAction(e -> openEvent(event));
            box.getChildren().addAll(new Label("You are the market maker of this event. Opening it takes the initial investment from your account."), openButton);
        } else if (isMarketMaker && Constants.ACTIVE.equals(event.getStatus())) {
            Button closeButton = new Button("Close event");
            closeButton.setOnAction(e -> closeEvent(event));
            box.getChildren().addAll(new Label("You are the market maker of this event."), closeButton);
        }

        if (Constants.ACTIVE.equals(event.getStatus())) {
            Label header = new Label("Trade:");
            header.setStyle("-fx-font-weight: bold;");
            FlowPane form = Constants.LMSR.equals(event.getMethodType())
                    ? buildLmsrTradeForm(event)
                    : buildOrderBookTradeForm(event);
            box.getChildren().addAll(header, form);
        } else if (!isMarketMaker || !Constants.NOT_STARTED.equals(event.getStatus())) {
            box.getChildren().add(new Label("Trading is possible only while the event is active"));
        }
        return box;
    }

    private FlowPane buildLmsrTradeForm(EventDto event) {
        ComboBox<String> optionCombo = new ComboBox<>(FXCollections.observableArrayList(event.getOptionNames()));
        optionCombo.getSelectionModel().selectFirst();
        TextField quantityField = new TextField();
        quantityField.setPromptText("Shares");
        quantityField.setPrefWidth(80);

        Button buyButton = new Button("Buy");
        buyButton.setOnAction(e -> {
            Integer quantity = readPositiveWholeNumber(quantityField.getText(), "amount of shares");
            if (quantity == null) {
                return;
            }
            String finalUrl = HttpUrl.parse(Constants.BUY_SHARES).newBuilder()
                    .addQueryParameter("eventId", String.valueOf(event.getId()))
                    .addQueryParameter("optionIndex", String.valueOf(optionCombo.getSelectionModel().getSelectedIndex()))
                    .addQueryParameter("quantity", String.valueOf(quantity))
                    .build()
                    .toString();
            HttpClientUtil.runPostAsync(finalUrl, new ActionCallback("Purchase failed", responseBody -> {
                TradeResultDto result = GSON_INSTANCE.fromJson(responseBody, TradeResultDto.class);
                quantityField.clear();
                marketMainController.refreshNow();
                String message = String.format(Locale.US, "You bought %d shares and paid %.2f in total (%.2f for the shares and %.2f commission).",
                        result.getFilledQuantity(), result.getTotalAmount() + result.getTotalCommission(),
                        result.getTotalAmount(), result.getTotalCommission());
                Alerts.showInfo("Purchase completed", message + blockedMessage(result));
            }));
        });

        FlowPane form = new FlowPane(8, 6, new Label("Option:"), optionCombo, new Label("Quantity:"), quantityField, buyButton);
        form.setAlignment(Pos.CENTER_LEFT);
        return form;
    }

    private FlowPane buildOrderBookTradeForm(EventDto event) {
        ComboBox<String> optionCombo = new ComboBox<>(FXCollections.observableArrayList(event.getOptionNames()));
        optionCombo.getSelectionModel().selectFirst();
        ComboBox<String> sideCombo = new ComboBox<>(FXCollections.observableArrayList("Buy", "Sell"));
        sideCombo.getSelectionModel().selectFirst();
        TextField quantityField = new TextField();
        quantityField.setPromptText("Shares");
        quantityField.setPrefWidth(70);
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        priceField.setPrefWidth(70);

        Button submitButton = new Button("Submit order");
        submitButton.setOnAction(e -> {
            Integer quantity = readPositiveWholeNumber(quantityField.getText(), "amount of shares");
            if (quantity == null) {
                return;
            }
            double price;
            try {
                price = Double.parseDouble(priceField.getText().trim());
            } catch (NumberFormatException ex) {
                Alerts.showError("Invalid price", "Please enter a number for the price of a single share (for example 0.55)");
                return;
            }
            String finalUrl = HttpUrl.parse(Constants.SUBMIT_ORDER).newBuilder()
                    .addQueryParameter("eventId", String.valueOf(event.getId()))
                    .addQueryParameter("optionIndex", String.valueOf(optionCombo.getSelectionModel().getSelectedIndex()))
                    .addQueryParameter("side", sideCombo.getValue())
                    .addQueryParameter("quantity", String.valueOf(quantity))
                    .addQueryParameter("price", String.valueOf(price))
                    .build()
                    .toString();
            HttpClientUtil.runPostAsync(finalUrl, new ActionCallback("Order failed", responseBody -> {
                TradeResultDto result = GSON_INSTANCE.fromJson(responseBody, TradeResultDto.class);
                quantityField.clear();
                priceField.clear();
                marketMainController.refreshNow();
                StringBuilder message = new StringBuilder("The order was submitted. Filled now: " + result.getFilledQuantity());
                if (result.getRestingQuantity() > 0) {
                    message.append(", waiting in the book: ").append(result.getRestingQuantity());
                }
                if (result.isMinted()) {
                    message.append(" (new shares were minted)");
                }
                Alerts.showInfo("Order submitted", message + blockedMessage(result));
            }));
        });

        FlowPane form = new FlowPane(8, 6, new Label("Option:"), optionCombo, new Label("Side:"), sideCombo,
                new Label("Quantity:"), quantityField, new Label("Price:"), priceField, submitButton);
        form.setAlignment(Pos.CENTER_LEFT);
        return form;
    }

    private void openEvent(EventDto event) {
        String finalUrl = HttpUrl.parse(Constants.OPEN_EVENT).newBuilder()
                .addQueryParameter("eventId", String.valueOf(event.getId()))
                .build()
                .toString();
        HttpClientUtil.runPostAsync(finalUrl, new ActionCallback("Could not open the event", responseBody -> {
            marketMainController.refreshNow();
            Alerts.showInfo("Event opened", responseBody);
        }));
    }

    private void closeEvent(EventDto event) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(event.getOptionNames().get(0), event.getOptionNames());
        dialog.setTitle("Close event");
        dialog.setHeaderText("Choose the winning option of '" + event.getName() + "'");
        dialog.setContentText("Winning option:");

        Optional<String> winner = dialog.showAndWait();
        if (!winner.isPresent()) {
            return;
        }
        String finalUrl = HttpUrl.parse(Constants.CLOSE_EVENT).newBuilder()
                .addQueryParameter("eventId", String.valueOf(event.getId()))
                .addQueryParameter("optionIndex", String.valueOf(event.getOptionNames().indexOf(winner.get())))
                .build()
                .toString();
        HttpClientUtil.runPostAsync(finalUrl, new ActionCallback("Could not close the event", responseBody -> {
            marketMainController.refreshNow();
            Alerts.showInfo("Event closed", responseBody + ". Winning option: " + winner.get());
        }));
    }

    private Integer readPositiveWholeNumber(String text, String fieldName) {
        try {
            int value = Integer.parseInt(text.trim());
            if (value > 0) {
                return value;
            }
        } catch (NumberFormatException ignored) {
        }
        Alerts.showError("Invalid " + fieldName, "Please enter a positive whole number for the " + fieldName);
        return null;
    }

    private String blockedMessage(TradeResultDto result) {
        StringBuilder message = new StringBuilder();
        for (String userName : result.getBlockedUsers()) {
            if (userName.equalsIgnoreCase(marketMainController.getUserName())) {
                message.append("\nYour balance went below zero, so you are blocked until you load money to your account.");
            } else {
                message.append("\n").append(userName).append(" went below zero and is now blocked.");
            }
        }
        return message.toString();
    }

    private TableView<TradeDto> buildTradesTable() {
        TableView<TradeDto> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(140);
        table.setPlaceholder(new Label("No trades yet"));
        table.getColumns().add(textColumn("User", TradeDto::getUserName));
        table.getColumns().add(textColumn("Option", TradeDto::getOptionName));
        table.getColumns().add(textColumn("Side", TradeDto::getSide));
        table.getColumns().add(textColumn("Quantity", trade -> String.valueOf(trade.getQuantity())));
        table.getColumns().add(textColumn("Amount paid", trade -> formatNumber(trade.getAmount())));
        table.getColumns().add(textColumn("Commission", trade -> formatNumber(trade.getCommission())));
        return table;
    }

    private TableView<OrderDto> buildOrdersTable(String placeholder) {
        TableView<OrderDto> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(110);
        table.setPlaceholder(new Label(placeholder));
        table.getColumns().add(textColumn("User", OrderDto::getUserName));
        table.getColumns().add(textColumn("Quantity", order -> String.valueOf(order.getQuantity())));
        table.getColumns().add(textColumn("Price", order -> formatNumber(order.getPrice())));
        return table;
    }

    private <T> TableColumn<T, String> textColumn(String title, Function<T, String> valueGetter) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(c -> new ReadOnlyStringWrapper(valueGetter.apply(c.getValue())));
        return column;
    }

    private String formatHoldings(ParticipantDto participant, List<OptionBookDto> books) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < books.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(String.format(Locale.US, "%s: %d (paid %.2f)", books.get(i).getOptionName(),
                    participant.getShares().get(i), participant.getNetPaid().get(i)));
        }
        return sb.toString();
    }

    private String formatNumber(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private String formatPrice(double value) {
        return value < 0 ? "-" : formatNumber(value);
    }
}
