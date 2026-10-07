package guessmarket.server.servlets;

import guessmarket.dto.ChatEntryDto;
import guessmarket.dto.ChatLinesDto;
import guessmarket.engine.chat.ChatManager;
import guessmarket.server.constants.Constants;
import guessmarket.server.utils.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ChatServlet", urlPatterns = "/chat")
public class ChatServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (ServletUtils.checkLoggedIn(request, response) == null) {
            return;
        }

        int chatVersion;
        try {
            chatVersion = ServletUtils.getIntParameter(request, Constants.CHAT_VERSION);
        } catch (IllegalArgumentException e) {
            ServletUtils.writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
        int currentVersion;
        List<ChatEntryDto> entries;
        synchronized (chatManager) {
            currentVersion = chatManager.getVersion();
            entries = chatManager.getChatEntries(chatVersion);
        }
        ServletUtils.writeJson(response, new ChatLinesDto(entries, currentVersion));
    }
}
