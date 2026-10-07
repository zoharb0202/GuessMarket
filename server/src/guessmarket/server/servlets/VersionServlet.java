package guessmarket.server.servlets;

import guessmarket.engine.GuessMarketEngine;
import guessmarket.server.utils.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "VersionServlet", urlPatterns = "/version")
public class VersionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (ServletUtils.checkLoggedIn(request, response) == null) {
            return;
        }

        GuessMarketEngine engine = ServletUtils.getEngine(getServletContext());
        int version;
        synchronized (engine) {
            version = engine.getVersion();
        }
        ServletUtils.writeJson(response, version);
    }
}
