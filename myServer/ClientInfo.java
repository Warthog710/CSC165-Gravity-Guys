package myServer;

import java.util.UUID;

//Holds client info
public class ClientInfo 
{
    protected UUID clientID;
    protected String pos;
    protected long lastKeepAlive;
    protected long lastUpdate;
    protected String rotation;

    protected ClientInfo(UUID clientID, String pos, String rotation)
    {
        this.clientID = clientID;
        this.pos = pos;
        this.rotation = rotation;
        this.lastKeepAlive = System.currentTimeMillis();
        this.lastUpdate = System.currentTimeMillis();
    }
}
