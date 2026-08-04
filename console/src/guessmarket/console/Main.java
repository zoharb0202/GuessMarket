package guessmarket.console;

import guessmarket.engine.EngineImpl;
import guessmarket.engine.GuessMarketEngine;

public class Main {

    public static void main(String[] args) {
        GuessMarketEngine engine = new EngineImpl();
        ConsoleApp application = new ConsoleApp(engine);
        application.run();
    }
}
