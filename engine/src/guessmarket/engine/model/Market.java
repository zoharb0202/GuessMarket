package guessmarket.engine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Market implements Serializable, UserLookup
{
    private static final long serialVersionUID = 1L;

    private final List<Event> events;
    private final Map<String, User> users;

    public Market()
    {
        events = new ArrayList<Event>();
        users = new LinkedHashMap<String, User>();
    }

    public void addEvent(Event event)
    {
        events.add(event);
    }

    public List<Event> getEvents()
    {
        return events;
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

    public Event findByName(String name)
    {
        for (Event event : events)
        {
            if (event.getName().equalsIgnoreCase(name))
            {
                return event;
            }
        }
        return null;
    }

    public int nextEventId()
    {
        int max = 0;
        for (Event event : events)
        {
            max = Math.max(max, event.getId());
        }
        return max + 1;
    }

    public void addUser(User user)
    {
        users.put(user.getName().toLowerCase(), user);
    }

    public List<User> getUsers()
    {
        return new ArrayList<User>(users.values());
    }

    public boolean containsUser(String name)
    {
        return users.containsKey(name.toLowerCase());
    }

    @Override
    public User find(String name)
    {
        if (name == null)
        {
            return null;
        }
        return users.get(name.toLowerCase());
    }
}
