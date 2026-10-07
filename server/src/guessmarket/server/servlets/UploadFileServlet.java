package guessmarket.server.servlets;

import guessmarket.engine.GuessMarketEngine;
import guessmarket.engine.exception.InvalidFileException;
import guessmarket.server.constants.Constants;
import guessmarket.server.utils.ServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "UploadFileServlet", urlPatterns = "/upload-file")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 5, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5)
public class UploadFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = ServletUtils.checkLoggedIn(request, response);
        if (username == null) {
            return;
        }

        Part filePart;
        try {
            filePart = request.getPart(Constants.FILE);
        } catch (IllegalStateException e) {
            ServletUtils.writeError(response, HttpServletResponse.SC_BAD_REQUEST, "The file is too big to be uploaded");
            return;
        }
        if (filePart == null) {
            ServletUtils.writeError(response, HttpServletResponse.SC_BAD_REQUEST, "No file was uploaded");
            return;
        }

        GuessMarketEngine engine = ServletUtils.getEngine(getServletContext());
        int addedEvents;
        try (InputStream content = filePart.getInputStream()) {
            synchronized (engine) {
                addedEvents = engine.loadEventsFile(content, filePart.getSubmittedFileName(), username);
            }
        } catch (InvalidFileException e) {
            ServletUtils.writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        String eventsText = addedEvents == 1 ? "1 new event was" : addedEvents + " new events were";
        ServletUtils.writeText(response, "The file is valid and was loaded. " + eventsText
                + " added to the system and you are the market maker of them");
    }
}
