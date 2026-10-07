package guessmarket.client.component.login;

import guessmarket.client.component.main.AppMainController;
import guessmarket.client.util.Constants;
import guessmarket.client.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LoginController {

    @FXML private TextField userNameTextField;
    @FXML private Label errorMessageLabel;
    @FXML private Button loginButton;

    private AppMainController appMainController;

    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    @FXML
    public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
    }

    @FXML
    private void loginButtonClicked(ActionEvent event) {
        String userName = userNameTextField.getText().trim();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with an empty user name");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.LOGIN)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();

        loginButton.setDisable(true);
        HttpClientUtil.runPostAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    errorMessageProperty.set("The server could not be reached. Make sure the server is up and try again");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        loginButton.setDisable(false);
                        errorMessageProperty.set(responseBody);
                    });
                } else {
                    Platform.runLater(() -> appMainController.switchToMarket(responseBody));
                }
            }
        });
    }

    @FXML
    private void userNameKeyTyped(KeyEvent event) {
        errorMessageProperty.set("");
    }

    @FXML
    private void quitButtonClicked(ActionEvent e) {
        Platform.exit();
    }

    public void setAppMainController(AppMainController appMainController) {
        this.appMainController = appMainController;
    }
}
