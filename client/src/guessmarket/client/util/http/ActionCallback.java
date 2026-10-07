package guessmarket.client.util.http;

import guessmarket.client.util.Alerts;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Consumer;

public class ActionCallback implements Callback {

    private final String errorTitle;
    private final Consumer<String> onSuccess;

    public ActionCallback(String errorTitle, Consumer<String> onSuccess) {
        this.errorTitle = errorTitle;
        this.onSuccess = onSuccess;
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        Platform.runLater(() ->
                Alerts.showError(errorTitle, "The server could not be reached: " + e.getMessage()));
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        String responseBody = response.body().string();
        if (response.code() != 200) {
            Platform.runLater(() -> Alerts.showError(errorTitle, responseBody));
        } else {
            Platform.runLater(() -> onSuccess.accept(responseBody));
        }
    }
}
