package guessmarket.console;

import guessmarket.engine.dto.EventDto;
import guessmarket.engine.dto.EventStateDto;
import guessmarket.engine.dto.OptionStateDto;
import guessmarket.engine.dto.PurchaseResultDto;
import guessmarket.engine.dto.TradeDto;

import java.util.List;

/*
  The only place in the program that writes to the screen.
 */
public class Printer {

    public void printWelcome()
    {
        System.out.println("Welcome to Guess Market!");
    }

    public void printMessage(String message)
    {
        System.out.println(message);
    }

    public void printMenu()
    {
        System.out.println();
        System.out.println("---Main menu---");
        MenuOption[] options = MenuOption.values();
        for (int i = 0; i < options.length; i++)
            {
            System.out.println((i + 1) + ". " + options[i].getDescription());
        }
    }

    public void printEvents(List<EventDto> events)
    {
        for (int i = 0; i < events.size(); i++) 
        {
            EventDto event = events.get(i);
            System.out.println();
            System.out.println((i + 1) + ") Event number: " + event.getId());
            System.out.println("   Name: " + event.getName());
            System.out.println("   Description: " + event.getDescription());
            System.out.println("   Commission: " + event.getCommissionPercent() + "% (taken " + event.getCommissionDescription() + ")");
            System.out.println("   Options: " + joinOptionNames(event.getOptionNames()));
            System.out.println("   Status: " + event.getStatus());
        }
        System.out.println();
    }

    public void printCurrentState(List<OptionStateDto> options)
    {
        System.out.println("Current state:");
        for (int i = 0; i < options.size(); i++) 
        {
            OptionStateDto option = options.get(i);
            System.out.println("   " + (i + 1) + ". " + option.getName()
                    + " - value: " + money(option.getValue())
                    + " , shares bought so far: " + option.getSharesBought());
        }
    }

    public void printEventState(EventStateDto state) 
    {
        System.out.println();
        System.out.println("Trading state of event number " + state.getEventId() + " - " + state.getEventName());
        printCurrentState(state.getOptions());
        System.out.println("The account of the event holds: " + money(state.getAccountBalance()));
        System.out.println("Commission that was collected so far: " + money(state.getCollectedCommission()));
        printTrades(state.getTrades());

        if (state.isClosed())
        {
            System.out.println("The event is closed. The winning option is: " + state.getWinningOptionName());
            System.out.println("Shares that were bought in this event:");
            for (OptionStateDto option : state.getOptions()) 
            {
                System.out.println("   " + option.getName() + " - " + option.getSharesBought() + " shares");
            }
        }
    }

    public void printPurchaseResult(PurchaseResultDto result)
    {
        System.out.println("The purchase was completed");
        System.out.println("Paid for the shares: " + money(result.getSharesCost()));
        System.out.println("Paid for the commission: " + money(result.getCommission()));
        System.out.println("Total that was paid: " + money(result.getTotalPaid()));
    }

    private void printTrades(List<TradeDto> trades) 
    {
        System.out.println("Trading history (from the last purchase to the first one):");
        if (trades.isEmpty())
        {
            System.out.println("   Nothing was bought in this event yet");
            return;
        }
        for (TradeDto trade : trades)
            {
            System.out.println("   " + trade.getOptionName() + " - " + trade.getQuantity()
                    + " shares - paid: " + money(trade.getPricePaid()));
        }
    }

    private String joinOptionNames(List<String> names)
    {
        String line = "";
        for (int i = 0; i < names.size(); i++)
            {
            if (i > 0) {
                line += " , ";
            }
            line += names.get(i);
        }
        return line;
    }

    private String money(double value)
    {
        return String.format("%.2f", value);
    }
}
