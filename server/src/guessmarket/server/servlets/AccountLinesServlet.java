package guessmarket.server.servlets;

import guessmarket.dto.AccountLineDto;
import guessmarket.engine.GuessMarketEngine;
import guessmarket.server.utils.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AccountLinesServlet", urlPatterns = "/account")
public class AccountLinesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = ServletUtils.checkLoggedIn(request, response);
        if (username == null) {
            return;
        }

        GuessMarketEngine engine = ServletUtils.getEngine(getServletContext());
        List<AccountLineDto> lines;
        synchronized (engine) {
            lines = engine.getAccountLines(username);
        }
        ServletUtils.writeJson(response, lines);
    }
}
