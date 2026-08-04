package guessmarket.engine.dto;

import java.util.List;

public class EventDto {
    private final int id;
    private final String name;
    private final String description;
    private final int commissionPercent;
    private final String commissionDescription;
    private final List<String> optionNames;
    private final String status;

    public EventDto(int id, String name, String description, int commissionPercent, String commissionDescription, List<String> optionNames, String status)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.commissionPercent = commissionPercent;
        this.commissionDescription = commissionDescription;
        this.optionNames = optionNames;
        this.status = status;
    }

    public int getId() 
    {
        return id;
    }

    public String getName() 
    {
        return name;
    }

    public String getDescription() 
    {
        return description;
    }

    public int getCommissionPercent() 
    {
        return commissionPercent;
    }

    public String getCommissionDescription() 
    {
        return commissionDescription;
    }

    public List<String> getOptionNames() 
    {
        return optionNames;
    }

    public String getStatus()
    {
        return status;
    }
}
