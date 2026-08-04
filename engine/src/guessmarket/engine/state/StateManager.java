package guessmarket.engine.state;

import guessmarket.engine.exception.StateFileException;
import guessmarket.engine.model.Market;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Saves the whole market to a file and reads it back, using java serialization.
 * The user gives a path without an extension and the manager adds it by itself.
 */
public class StateManager {
    private static final String STATE_SUFFIX = ".gm";

    public void save(Market market, String path) throws StateFileException {
        File file = buildFile(path);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(market);
        } catch (IOException e) {
            throw new StateFileException("The state could not be written to '" + file.getPath() + "': " + e.getMessage());
        }
    }

    public Market load(String path) throws StateFileException {
        File file = buildFile(path);
        if (!file.exists()) {
            throw new StateFileException("There is no saved state at '" + file.getPath() + "'.");
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (Market) in.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new StateFileException("The file '" + file.getPath() + "' is not a state file of Guess Market.");
        }
    }

    public String getFileName(String path) throws StateFileException {
        return buildFile(path).getPath();
    }

    private File buildFile(String path) throws StateFileException {
        if (path.isEmpty()) {
            throw new StateFileException("No path was entered.");
        }

        // the user is asked for a path without an extension, but if the extension
        // is written anyway it should not be added twice
        String fullPath = path;
        if (!fullPath.toLowerCase().endsWith(STATE_SUFFIX)) {
            fullPath += STATE_SUFFIX;
        }
        return new File(fullPath);
    }
}
