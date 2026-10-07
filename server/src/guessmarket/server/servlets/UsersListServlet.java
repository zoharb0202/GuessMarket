package guessmarket.server.servlets;

import guessmarket.dto.UserDto;
import guessmarket.engine.GuessMarketEngine;
import guessmarket.server.utils.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "UsersListServlet", urlPatterns = "/users")
public class UsersListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (ServletUtils.checkLoggedIn(request, response) == null) {
            return;
        }

        GuessMarketEngine engine = ServletUtils.getEngine(getServletContext());
        List<UserDto> users;
        synchronized (engine) {
            users = engine.getAllUsers();
        }
        ServletUtils.writeJson(response, users);
    }
}
