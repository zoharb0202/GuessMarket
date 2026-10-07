package guessmarket.server.servlets;

import guessmarket.engine.GuessMarketEngine;
import guessmarket.server.constants.Constants;
import guessmarket.server.utils.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "CloseEventServlet", urlPatterns = "/event/close")
public class CloseEventServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = ServletUtils.checkLoggedIn(request, response);
        if (username == null) {
            return;
        }

        GuessMarketEngine engine = ServletUtils.getEngine(getServletContext());
        try {
            int eventId = ServletUtils.getIntParameter(request, Constants.EVENT_ID);
            int optionIndex = ServletUtils.getIntParameter(request, Constants.OPTION_INDEX);
            synchronized (engine) {
                engine.closeEvent(username, eventId, optionIndex);
            }
            ServletUtils.writeText(response, "The event was closed and the winners were paid");
        } catch (RuntimeException e) {
            ServletUtils.writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
