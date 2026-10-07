package guessmarket.client.component.chat;

import guessmarket.client.util.Constants;
import guessmarket.client.util.http.ActionCallback;
import guessmarket.client.util.http.HttpClientUtil;
import guessmarket.dto.ChatEntryDto;
import guessmarket.dto.ChatLinesDto;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import okhttp3.HttpUrl;

import java.util.Timer;

import static guessmarket.client.util.Constants.REFRESH_RATE;

public class ChatAreaController {

    private static final String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %s: %s%n";

    @FXML private TextArea mainChatLinesTextArea;
    @FXML private TextField chatLineTextField;

    private final IntegerProperty chatVersion = new SimpleIntegerProperty();
    private ChatAreaRefresher chatAreaRefresher;
    private Timer timer;

    @FXML
    private void sendButtonClicked(ActionEvent event) {
        String chatLine = chatLineTextField.getText().trim();
        if (chatLine.isEmpty()) {
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.SEND_CHAT_LINE)
                .newBuilder()
                .addQueryParameter("chatline", chatLine)
                .build()
                .toString();

        HttpClientUtil.runPostAsync(finalUrl, new ActionCallback("The message was not sent", responseBody -> {}));
        chatLineTextField.clear();
    }

    private void updateChatLines(ChatLinesDto chatLines) {
        if (chatLines.getVersion() == chatVersion.get()) {
            return;
        }

        StringBuilder newLines = new StringBuilder();
        for (ChatEntryDto entry : chatLines.getEntries()) {
            long time = entry.getTime();
            newLines.append(String.format(CHAT_LINE_FORMATTING, time, time, time, entry.getUserName(), entry.getText()));
        }

        Platform.runLater(() -> {
            chatVersion.set(chatLines.getVersion());
            mainChatLinesTextArea.appendText(newLines.toString());
        });
    }

    public void startListRefresher() {
        chatAreaRefresher = new ChatAreaRefresher(chatVersion, this::updateChatLines);
        timer = new Timer();
        timer.schedule(chatAreaRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void close() {
        if (chatAreaRefresher != null && timer != null) {
            chatAreaRefresher.cancel();
            timer.cancel();
        }
    }
}
