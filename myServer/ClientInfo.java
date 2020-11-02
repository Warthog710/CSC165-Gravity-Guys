package myServer;

import java.util.UUID;

//Holds client info
public class ClientInfo 
{
    protected UUID clientID;
    protected String pos;
    protected long lastKeepAlive;
    protected long lastUpdate;

    protected ClientInfo(UUID clientID, String pos)
    {
        this.clientID = clientID;
        this.pos = pos;
        this.lastKeepAlive = System.currentTimeMillis();
        this.lastUpdate = System.currentTimeMillis();
    }
}
