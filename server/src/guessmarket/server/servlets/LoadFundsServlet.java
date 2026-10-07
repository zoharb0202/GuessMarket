package guessmarket.server.servlets;

import guessmarket.engine.GuessMarketEngine;
import guessmarket.server.constants.Constants;
import guessmarket.server.utils.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "LoadFundsServlet", urlPatterns = "/load-funds")
public class LoadFundsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = ServletUtils.checkLoggedIn(request, response);
        if (username == null) {
            return;
        }

        GuessMarketEngine engine = ServletUtils.getEngine(getServletContext());
        try {
            double amount = ServletUtils.getDoubleParameter(request, Constants.AMOUNT);
            synchronized (engine) {
                engine.loadFunds(username, amount);
            }
            ServletUtils.writeText(response, "The money was loaded to your account");
        } catch (RuntimeException e) {
            ServletUtils.writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
