package guessmarket.console;

import java.util.Scanner;

/**
 * Collects the input from the user and keeps asking until the input makes sense.
 */
public class InputReader {
    private final Scanner scanner;

    public InputReader() {
        scanner = new Scanner(System.in);
    }

    public int readNumberInRange(String message, int min, int max) {
        return readNumber(message, min, max, "Please enter a number between " + min + " and " + max + ".");
    }

    public int readPositiveNumber(String message) {
        return readNumber(message, 1, Integer.MAX_VALUE, "Please enter a whole number that is bigger than 0.");
    }

    public String readPath(String message) {
        System.out.print(message);
        String input = scanner.nextLine().trim();

        // windows copies a path together with the quotes around it, so they are removed
        if (input.length() > 1 && input.startsWith("\"") && input.endsWith("\"")) {
            input = input.substring(1, input.length() - 1).trim();
        }
        return input;
    }

    private int readNumber(String message, int min, int max, String rangeMessage) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Nothing was entered. " + rangeMessage);
                continue;
            }

            try {
                int number = Integer.parseInt(input);
                if (number < min || number > max) {
                    System.out.println("The number " + number + " can not be used here. " + rangeMessage);
                    continue;
                }
                return number;
            } catch (NumberFormatException e) {
                System.out.println("'" + input + "' is not a whole number. " + rangeMessage);
            }
        }
    }
}
