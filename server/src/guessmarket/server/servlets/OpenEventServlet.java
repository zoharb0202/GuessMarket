package guessmarket.server.servlets;

import guessmarket.engine.GuessMarketEngine;
import guessmarket.server.constants.Constants;
import guessmarket.server.utils.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "OpenEventServlet", urlPatterns = "/event/open")
public class OpenEventServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = ServletUtils.checkLoggedIn(request, response);
        if (username == null) {
            return;
        }

        GuessMarketEngine engine = ServletUtils.getEngine(getServletContext());
        try {
            int eventId = ServletUtils.getIntParameter(request, Constants.EVENT_ID);
            synchronized (engine) {
                engine.openEvent(username, eventId);
            }
            ServletUtils.writeText(response, "The event was opened and trading in it can start");
        } catch (RuntimeException e) {
            ServletUtils.writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
