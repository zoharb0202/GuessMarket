package guessmarket.server.servlets;

import guessmarket.dto.EventStateDto;
import guessmarket.engine.GuessMarketEngine;
import guessmarket.server.constants.Constants;
import guessmarket.server.utils.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "LmsrStateServlet", urlPatterns = "/event/lmsr-state")
public class LmsrStateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (ServletUtils.checkLoggedIn(request, response) == null) {
            return;
        }

        GuessMarketEngine engine = ServletUtils.getEngine(getServletContext());
        try {
            int eventId = ServletUtils.getIntParameter(request, Constants.EVENT_ID);
            EventStateDto result;
            synchronized (engine) {
                result = engine.getLmsrState(eventId);
            }
            ServletUtils.writeJson(response, result);
        } catch (RuntimeException e) {
            ServletUtils.writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
