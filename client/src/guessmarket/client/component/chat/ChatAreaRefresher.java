package guessmarket.client.component.chat;

import guessmarket.client.util.Constants;
import guessmarket.client.util.http.HttpClientUtil;
import guessmarket.dto.ChatLinesDto;
import javafx.beans.property.IntegerProperty;
import okhttp3.HttpUrl;
import okhttp3.Response;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static guessmarket.client.util.Constants.GSON_INSTANCE;

public class ChatAreaRefresher extends TimerTask {

    private final IntegerProperty chatVersion;
    private final Consumer<ChatLinesDto> chatLinesConsumer;

    public ChatAreaRefresher(IntegerProperty chatVersion, Consumer<ChatLinesDto> chatLinesConsumer) {
        this.chatVersion = chatVersion;
        this.chatLinesConsumer = chatLinesConsumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.CHAT_LINES_LIST)
                .newBuilder()
                .addQueryParameter("chatversion", String.valueOf(chatVersion.get()))
                .build()
                .toString();

        try (Response response = HttpClientUtil.runSync(finalUrl)) {
            String rawBody = response.body().string();
            if (response.isSuccessful()) {
                ChatLinesDto chatLines = GSON_INSTANCE.fromJson(rawBody, ChatLinesDto.class);
                chatLinesConsumer.accept(chatLines);
            }
        } catch (IOException | RuntimeException ignored) {
        }
    }
}
