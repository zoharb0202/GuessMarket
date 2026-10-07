package guessmarket.server.servlets;

import guessmarket.engine.chat.ChatManager;
import guessmarket.server.constants.Constants;
import guessmarket.server.utils.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "SendChatServlet", urlPatterns = "/chat/send")
public class SendChatServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = ServletUtils.checkLoggedIn(request, response);
        if (username == null) {
            return;
        }

        String chatLine = request.getParameter(Constants.CHAT_LINE);
        if (chatLine == null || chatLine.trim().isEmpty()) {
            ServletUtils.writeError(response, HttpServletResponse.SC_BAD_REQUEST, "An empty message cannot be sent");
            return;
        }

        ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
        chatManager.addChatLine(username, chatLine.trim());
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
