package guessmarket.client.component.account;

import guessmarket.client.component.details.EventDetailsController;
import guessmarket.client.component.market.MarketData;
import guessmarket.client.component.market.MarketMainController;
import guessmarket.client.component.rows.EventInvolvementRow;
import guessmarket.client.component.rows.UserRow;
import guessmarket.client.util.Alerts;
import guessmarket.client.util.Constants;
import guessmarket.client.util.http.ActionCallback;
import guessmarket.client.util.http.HttpClientUtil;
import guessmarket.dto.AccountLineDto;
import guessmarket.dto.EventDto;
import guessmarket.dto.UserDto;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AccountController {

    @FXML private Button loadFileButton;
    @FXML private Label filePathLabel;
    @FXML private TableView<UserRow> usersTable;
    @FXML private Label accountBalanceLabel;
    @FXML private TextField fundsAmountTextField;
    @FXML private TableView<AccountLineDto> accountLinesTable;
    @FXML private TableView<EventInvolvementRow> involvementTable;
    @FXML private Parent eventDetailsComponent;
    @FXML private EventDetailsController eventDetailsComponentController;

    private MarketMainController marketMainController;

    private volatile Integer selectedEventId;
    private boolean updatingTable;

    @FXML
    public void initialize() {
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        usersTable.setPlaceholder(new Label("No other users are logged in"));
        TableColumn<UserRow, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> c.getValue().nameProperty());
        TableColumn<UserRow, String> balanceCol = new TableColumn<>("Balance");
        balanceCol.setCellValueFactory(c -> c.getValue().balanceProperty());
        TableColumn<UserRow, String> marketMakerCol = new TableColumn<>("Market maker");
        marketMakerCol.setCellValueFactory(c -> c.getValue().marketMakerProperty());
        usersTable.getColumns().addAll(nameCol, balanceCol, marketMakerCol);

        accountLinesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        accountLinesTable.setPlaceholder(new Label("No actions in your account yet"));
        TableColumn<AccountLineDto, String> numberCol = new TableColumn<>("#");
        numberCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().getNumber())));
        numberCol.setMaxWidth(45);
        TableColumn<AccountLineDto, String> descriptionCol = new TableColumn<>("Action");
        descriptionCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getDescription()));
        TableColumn<AccountLineDto, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.format(Locale.US, "%+.2f", c.getValue().getAmount())));
        amountCol.setMaxWidth(90);
        TableColumn<AccountLineDto, String> balanceAfterCol = new TableColumn<>("Balance");
        balanceAfterCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.format(Locale.US, "%.2f", c.getValue().getBalanceAfter())));
        balanceAfterCol.setMaxWidth(90);
        accountLinesTable.getColumns().addAll(numberCol, descriptionCol, amountCol, balanceAfterCol);

        involvementTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        involvementTable.setPlaceholder(new Label("You are not involved in any event yet"));
        TableColumn<EventInvolvementRow, String> eventCol = new TableColumn<>("Event");
        eventCol.setCellValueFactory(c -> c.getValue().nameProperty());
        TableColumn<EventInvolvementRow, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(c -> c.getValue().roleProperty());
        TableColumn<EventInvolvementRow, String> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(c -> c.getValue().methodTypeProperty());
        TableColumn<EventInvolvementRow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> c.getValue().statusProperty());
        involvementTable.getColumns().addAll(eventCol, roleCol, methodCol, statusCol);

        involvementTable.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> {
            if (updatingTable) {
                return;
            }
            selectedEventId = newRow == null ? null : newRow.getEventId();
            eventDetailsComponentController.showLoading(selectedEventId);
            marketMainController.refreshNow();
        });
    }

    public void setMarketMainController(MarketMainController marketMainController) {
        this.marketMainController = marketMainController;
        eventDetailsComponentController.setMarketMainController(marketMainController);
    }

    public Integer getSelectedEventId() {
        return selectedEventId;
    }

    public void update(MarketData data, UserDto currentUser) {
        List<UserRow> otherUsers = new ArrayList<>();
        for (UserDto user : data.getUsers()) {
            if (currentUser == null || !user.getName().equals(currentUser.getName())) {
                otherUsers.add(new UserRow(user));
            }
        }
        usersTable.getItems().setAll(otherUsers);
        accountLinesTable.getItems().setAll(data.getAccountLines());

        if (currentUser != null) {
            String balance = String.format(Locale.US, "Current balance: %.2f", currentUser.getBalance());
            if (currentUser.isBlocked()) {
                balance += "  (blocked - load funds to continue)";
            }
            accountBalanceLabel.setText(balance);
            updateInvolvementTable(data.getEvents(), currentUser);
        }

        eventDetailsComponentController.update(data.getAccountTabDetails());
    }

    private void updateInvolvementTable(List<EventDto> events, UserDto currentUser) {
        List<EventInvolvementRow> rows = new ArrayList<>();
        for (EventDto event : events) {
            boolean isMarketMaker = currentUser.getMarketMakerEventIds().contains(event.getId());
            boolean isParticipant = currentUser.getParticipatingEventIds().contains(event.getId());
            String role;
            if (isMarketMaker && isParticipant) {
                role = "Market maker + participant";
            } else if (isMarketMaker) {
                role = "Market maker";
            } else if (isParticipant) {
                role = "Participant";
            } else {
                continue;
            }
            rows.add(new EventInvolvementRow(event.getId(), event.getName(), role, event.getMethodType(), event.getStatus()));
        }

        updatingTable = true;
        involvementTable.getItems().setAll(rows);
        if (selectedEventId != null) {
            for (EventInvolvementRow row : rows) {
                if (row.getEventId() == selectedEventId) {
                    involvementTable.getSelectionModel().select(row);
                }
            }
        }
        updatingTable = false;
    }

    @FXML
    private void loadFileButtonClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an events file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        File file = fileChooser.showOpenDialog(loadFileButton.getScene().getWindow());
        if (file == null) {
            return;
        }

        filePathLabel.setText(file.getAbsolutePath());
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("text/xml")))
                .build();

        HttpClientUtil.runPostAsync(Constants.UPLOAD_FILE, body, new ActionCallback("The file was not loaded", responseBody -> {
            marketMainController.refreshNow();
            Alerts.showInfo("File loaded", responseBody);
        }));
    }

    @FXML
    private void loadFundsButtonClicked(ActionEvent event) {
        double amount;
        try {
            amount = Double.parseDouble(fundsAmountTextField.getText().trim());
        } catch (NumberFormatException e) {
            Alerts.showError("Invalid amount", "Please enter the amount of money to load (a positive number)");
            return;
        }
        if (amount <= 0 || Double.isNaN(amount) || Double.isInfinite(amount)) {
            Alerts.showError("Invalid amount", "The amount to load has to be a positive number");
            return;
        }

        String finalUrl = HttpUrl.parse(Constants.LOAD_FUNDS).newBuilder()
                .addQueryParameter("amount", String.valueOf(amount))
                .build()
                .toString();
        HttpClientUtil.runPostAsync(finalUrl, new ActionCallback("Could not load the money", responseBody -> {
            fundsAmountTextField.clear();
            marketMainController.refreshNow();
            Alerts.showInfo("Money loaded", String.format(Locale.US, "%.2f was loaded to your account", amount));
        }));
    }
}
