package guessmarket.engine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Market implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Event> events;

    public Market() 
    {
        events = new ArrayList<Event>();
    }

    public void addEvent(Event event)
    {
        events.add(event);
    }

    public List<Event> getEvents() 
    {
        return events;
    }

    public List<Event> getActiveEvents()
    {
        List<Event> activeEvents = new ArrayList<Event>();
        for (Event event : events) 
        {
            if (event.isActive()) 
            {
                activeEvents.add(event);
            }
        }
        return activeEvents;
    }

    public Event findById(int id) 
    {
        for (Event event : events)
            {
            if (event.getId() == id)
            {
                return event;
            }
        }
        return null;
    }

    public boolean containsId(int id) 
    {
        return findById(id) != null;
    }
}
