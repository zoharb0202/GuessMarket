package guessmarket.engine.model;

import java.io.Serializable;

public interface UserLookup extends Serializable
{
    User find(String name);
}
