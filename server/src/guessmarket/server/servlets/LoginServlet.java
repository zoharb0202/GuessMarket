package guessmarket.server.servlets;

import guessmarket.engine.GuessMarketEngine;
import guessmarket.server.constants.Constants;
import guessmarket.server.utils.ServletUtils;
import guessmarket.server.utils.SessionUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        if (usernameFromSession != null) {
            ServletUtils.writeText(response, usernameFromSession);
            return;
        }

        String usernameFromParameter = request.getParameter(Constants.USERNAME);
        if (usernameFromParameter == null || usernameFromParameter.trim().isEmpty()) {
            ServletUtils.writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Please enter a user name");
            return;
        }
        String username = usernameFromParameter.trim();

        GuessMarketEngine engine = ServletUtils.getEngine(getServletContext());
        synchronized (engine) {
            if (engine.isUserExists(username)) {
                ServletUtils.writeError(response, HttpServletResponse.SC_CONFLICT,
                        "The user name '" + username + "' is already taken. Please choose a different name");
                return;
            }
            try {
                engine.addUser(username);
            } catch (RuntimeException e) {
                ServletUtils.writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                return;
            }
        }

        request.getSession(true).setAttribute(Constants.USERNAME, username);
        ServletUtils.writeText(response, username);
    }
}
