package guessmarket.dto;

import java.util.List;

public class ChatLinesDto
{
    private final List<ChatEntryDto> entries;
    private final int version;

    public ChatLinesDto(List<ChatEntryDto> entries, int version)
    {
        this.entries = entries;
        this.version = version;
    }

    public List<ChatEntryDto> getEntries()
    {
        return entries;
    }

    public int getVersion()
    {
        return version;
    }
}
