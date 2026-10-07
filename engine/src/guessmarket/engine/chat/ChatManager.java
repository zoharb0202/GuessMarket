package guessmarket.engine.chat;

import guessmarket.dto.ChatEntryDto;

import java.util.ArrayList;
import java.util.List;

public class ChatManager {

    private final List<ChatEntryDto> chatEntries = new ArrayList<>();

    public synchronized void addChatLine(String username, String text) {
        chatEntries.add(new ChatEntryDto(username, text, System.currentTimeMillis()));
    }

    public synchronized List<ChatEntryDto> getChatEntries(int fromIndex) {
        if (fromIndex < 0 || fromIndex > chatEntries.size()) {
            fromIndex = 0;
        }
        return new ArrayList<>(chatEntries.subList(fromIndex, chatEntries.size()));
    }

    public synchronized int getVersion() {
        return chatEntries.size();
    }
}
