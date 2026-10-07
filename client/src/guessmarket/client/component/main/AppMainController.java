package guessmarket.client.component.main;

import guessmarket.client.component.login.LoginController;
import guessmarket.client.component.market.MarketMainController;
import guessmarket.dto.UserDto;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import static guessmarket.client.util.Constants.*;

public class AppMainController {

    @FXML private Label userGreetingLabel;
    @FXML private Label balanceLabel;
    @FXML private Label connectionLabel;
    @FXML private ComboBox<String> skinComboBox;
    @FXML private CheckBox animationsCheckBox;
    @FXML private AnchorPane mainPanel;

    private Parent loginComponent;
    private LoginController loginController;

    private Parent marketComponent;
    private MarketMainController marketMainController;

    private String currentUserName;
    private double lastBalance;

    @FXML
    public void initialize() {
        userGreetingLabel.setText("Please log in");
        skinComboBox.setItems(FXCollections.observableArrayList("Default", "Dark", "Sunset"));
        skinComboBox.getSelectionModel().selectFirst();
        skinComboBox.setOnHidden(e -> applySkin(skinComboBox.getValue()));
        skinComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!skinComboBox.isShowing()) {
                applySkin(newValue);
            }
        });

        loadLoginPage();
        loadMarketPage();
    }

    private void loadLoginPage() {
        URL loginPageUrl = getClass().getResource(LOGIN_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPageUrl);
            loginComponent = fxmlLoader.load();
            loginController = fxmlLoader.getController();
            loginController.setAppMainController(this);
            setMainPanelTo(loginComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMarketPage() {
        URL marketPageUrl = getClass().getResource(MARKET_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(marketPageUrl);
            marketComponent = fxmlLoader.load();
            marketMainController = fxmlLoader.getController();
            marketMainController.setAppMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMainPanelTo(Parent pane) {
        mainPanel.getChildren().clear();
        mainPanel.getChildren().add(pane);
        AnchorPane.setBottomAnchor(pane, 1.0);
        AnchorPane.setTopAnchor(pane, 1.0);
        AnchorPane.setLeftAnchor(pane, 1.0);
        AnchorPane.setRightAnchor(pane, 1.0);
    }

    public void switchToMarket(String userName) {
        currentUserName = userName;
        userGreetingLabel.setText("Hello " + userName);
        balanceLabel.setText(String.format(Locale.US, "Balance: %.2f", 0.0));
        setMainPanelTo(marketComponent);
        playFade(marketComponent, Duration.millis(800));
        marketMainController.setActive();
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void updateCurrentUser(UserDto user) {
        if (user == null) {
            return;
        }
        String text = String.format(Locale.US, "Balance: %.2f", user.getBalance());
        if (user.isBlocked()) {
            text += "  (BLOCKED - load funds to your account to continue)";
        }
        balanceLabel.setText(text);
        balanceLabel.setStyle(user.isBlocked() ? "-fx-text-fill: red; -fx-font-weight: bold;" : "");
        if (user.getBalance() != lastBalance) {
            lastBalance = user.getBalance();
            playPulse(balanceLabel);
        }
    }

    public void setConnectionProblem(String problem) {
        connectionLabel.setText(problem == null ? "" : "Problem talking to the server: " + problem);
    }

    public void close() {
        if (marketMainController != null) {
            marketMainController.close();
        }
    }

    private void applySkin(String skinName) {
        if (mainPanel.getScene() == null) {
            return;
        }
        ObservableList<String> stylesheets = mainPanel.getScene().getStylesheets();
        stylesheets.clear();
        if ("Dark".equals(skinName)) {
            stylesheets.add(getClass().getResource(DARK_SKIN_CSS_LOCATION).toExternalForm());
        } else if ("Sunset".equals(skinName)) {
            stylesheets.add(getClass().getResource(SUNSET_SKIN_CSS_LOCATION).toExternalForm());
        }
    }

    public boolean isAnimationsOn() {
        return animationsCheckBox.isSelected();
    }

    public void playFade(Node node, Duration duration) {
        if (!isAnimationsOn()) {
            node.setOpacity(1);
            return;
        }
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void playPulse(Node node) {
        if (!isAnimationsOn()) {
            return;
        }
        ScaleTransition pulse = new ScaleTransition(Duration.millis(250), node);
        pulse.setFromX(1);
        pulse.setFromY(1);
        pulse.setToX(1.3);
        pulse.setToY(1.3);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(2);
        pulse.play();
    }
}
