package guessmarket.client.component.events;

import guessmarket.client.component.details.EventDetailsController;
import guessmarket.client.component.market.MarketData;
import guessmarket.client.component.market.MarketMainController;
import guessmarket.client.component.rows.EventRow;
import guessmarket.dto.EventDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventsController {

    @FXML private VBox filtersBox;
    @FXML private TableView<EventRow> eventsTable;
    @FXML private Parent eventDetailsComponent;
    @FXML private EventDetailsController eventDetailsComponentController;

    private MarketMainController marketMainController;

    private final ObservableList<EventRow> eventsData = FXCollections.observableArrayList();
    private FilteredList<EventRow> eventsFiltered;

    private final List<ToggleButton> methodToggles = new ArrayList<>();
    private final List<ToggleButton> statusToggles = new ArrayList<>();
    private final List<ToggleButton> commissionToggles = new ArrayList<>();

    private volatile Integer selectedEventId;
    private boolean updatingTable;

    @FXML
    public void initialize() {
        filtersBox.getChildren().addAll(
                buildToggleRow("Method", new String[]{"LMSR", "Order Book"}, methodToggles),
                buildToggleRow("Status", new String[]{"Not started", "Active", "Closed"}, statusToggles),
                buildToggleRow("Commission", new String[]{"on-close", "on-purchase"}, commissionToggles));

        eventsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        eventsTable.setPlaceholder(new Label("No events yet. Upload an events file from the Account tab"));

        TableColumn<EventRow, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> c.getValue().nameProperty());
        TableColumn<EventRow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> c.getValue().statusProperty());
        TableColumn<EventRow, String> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(c -> c.getValue().methodTypeProperty());
        TableColumn<EventRow, String> commissionCol = new TableColumn<>("Commission");
        commissionCol.setCellValueFactory(c -> c.getValue().commissionProperty());
        TableColumn<EventRow, String> balanceCol = new TableColumn<>("Account balance");
        balanceCol.setCellValueFactory(c -> c.getValue().accountBalanceProperty());
        eventsTable.getColumns().addAll(nameCol, statusCol, methodCol, commissionCol, balanceCol);

        eventsFiltered = new FilteredList<>(eventsData, row -> true);
        eventsTable.setItems(eventsFiltered);

        eventsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> {
            if (updatingTable) {
                return;
            }
            selectedEventId = newRow == null ? null : newRow.getId();
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

    public void update(MarketData data) {
        List<EventRow> rows = new ArrayList<>();
        for (EventDto event : data.getEvents()) {
            rows.add(new EventRow(event));
        }

        updatingTable = true;
        eventsData.setAll(rows);
        selectRow(selectedEventId);
        updatingTable = false;

        eventDetailsComponentController.update(data.getEventsTabDetails());
    }

    private void selectRow(Integer eventId) {
        if (eventId == null) {
            return;
        }
        for (EventRow row : eventsFiltered) {
            if (row.getId() == eventId) {
                eventsTable.getSelectionModel().select(row);
                return;
            }
        }
    }

    private HBox buildToggleRow(String label, String[] values, List<ToggleButton> registry) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().add(new Label(label + ":"));
        for (String value : values) {
            ToggleButton button = new ToggleButton(value);
            button.setSelected(true);
            button.selectedProperty().addListener((obs, wasSelected, isSelected) -> refreshEventsFilter());
            registry.add(button);
            box.getChildren().add(button);
        }
        return box;
    }

    private void refreshEventsFilter() {
        Set<String> methods = selectedValues(methodToggles);
        Set<String> statuses = selectedValues(statusToggles);
        Set<String> commissions = selectedValues(commissionToggles);

        eventsFiltered.setPredicate(row ->
                methods.contains(row.getMethodTypeRaw())
                        && statuses.contains(row.getStatusRaw())
                        && commissions.contains(row.getCommissionTypeRaw()));
    }

    private Set<String> selectedValues(List<ToggleButton> toggles) {
        Set<String> result = new HashSet<>();
        for (ToggleButton toggle : toggles) {
            if (toggle.isSelected()) {
                result.add(toggle.getText());
            }
        }
        return result;
    }
}
