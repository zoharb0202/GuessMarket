package guessmarket.server.servlets;

import guessmarket.dto.TradeResultDto;
import guessmarket.engine.GuessMarketEngine;
import guessmarket.server.constants.Constants;
import guessmarket.server.utils.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "SubmitOrderServlet", urlPatterns = "/trade/order")
public class SubmitOrderServlet extends HttpServlet {

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
            String side = ServletUtils.getParameter(request, Constants.SIDE);
            int quantity = ServletUtils.getIntParameter(request, Constants.QUANTITY);
            double price = ServletUtils.getDoubleParameter(request, Constants.PRICE);
            TradeResultDto result;
            synchronized (engine) {
                result = engine.submitOrder(username, eventId, optionIndex, side, quantity, price);
            }
            ServletUtils.writeJson(response, result);
        } catch (RuntimeException e) {
            ServletUtils.writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
