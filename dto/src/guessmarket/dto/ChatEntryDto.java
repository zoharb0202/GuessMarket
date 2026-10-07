package guessmarket.dto;

public class ChatEntryDto
{
    private final String userName;
    private final String text;
    private final long time;

    public ChatEntryDto(String userName, String text, long time)
    {
        this.userName = userName;
        this.text = text;
        this.time = time;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getText()
    {
        return text;
    }

    public long getTime()
    {
        return time;
    }
}
