# Guess Market - Exercise 1 (Console)

Console application for the Guess Market rolling project.
Events are managed with the LMSR method and are loaded from an XML file.

## Modules

- `engine` - the system engine. Holds the events, the trading logic and the file
  reading. It is passive and contains no printing at all.
- `console` - the user interface. Holds `main`, the menu loop, all the user input
  and every print to the screen.

## Building

Run `build.bat` (needs the JDK on the PATH). It creates `dist\engine.jar`,
`dist\console.jar` and `dist\run.bat`.

## Running

    java -jar console.jar

`console.jar` and `engine.jar` have to sit in the same folder.

## Bonus

Saving and loading the state of the system (menu commands 6 and 7), using java
serialization. State files get the `.gm` extension.
