package main.de.mj.bb.core.playerobject;

import java.util.UUID;

public interface UserInterface {
    String getName();

    UUID getUUID();

    boolean isAFK();
}