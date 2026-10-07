package guessmarket.server.utils;

import com.google.gson.Gson;
import guessmarket.engine.EngineImpl;
import guessmarket.engine.GuessMarketEngine;
import guessmarket.engine.chat.ChatManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class ServletUtils {

    private static final String ENGINE_ATTRIBUTE_NAME = "engine";
    private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";

    private static final Object engineLock = new Object();
    private static final Object chatManagerLock = new Object();

    private static final Gson gson = new Gson();

    public static GuessMarketEngine getEngine(ServletContext servletContext) {
        synchronized (engineLock) {
            if (servletContext.getAttribute(ENGINE_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(ENGINE_ATTRIBUTE_NAME, new EngineImpl());
            }
        }
        return (GuessMarketEngine) servletContext.getAttribute(ENGINE_ATTRIBUTE_NAME);
    }

    public static ChatManager getChatManager(ServletContext servletContext) {
        synchronized (chatManagerLock) {
            if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
            }
        }
        return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
    }

    public static String checkLoggedIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "You are not logged in. Please log in first");
        }
        return username;
    }

    public static void writeJson(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(data));
            out.flush();
        }
    }

    public static void writeText(HttpServletResponse response, String text) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(text);
            out.flush();
        }
    }

    public static void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        writeText(response, message);
    }

    public static String getParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("The parameter '" + name + "' is missing");
        }
        return value.trim();
    }

    public static int getIntParameter(HttpServletRequest request, String name) {
        String value = getParameter(request, name);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The parameter '" + name + "' has to be a whole number, but it is '" + value + "'");
        }
    }

    public static double getDoubleParameter(HttpServletRequest request, String name) {
        String value = getParameter(request, name);
        double number;
        try {
            number = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The parameter '" + name + "' has to be a number, but it is '" + value + "'");
        }
        if (Double.isNaN(number) || Double.isInfinite(number)) {
            throw new IllegalArgumentException("The parameter '" + name + "' has to be a number, but it is '" + value + "'");
        }
        return number;
    }
}
