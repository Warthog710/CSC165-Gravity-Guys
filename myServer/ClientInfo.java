package myServer;

import java.util.UUID;

//Holds client info
public class ClientInfo 
{
    protected UUID clientID;
    protected String pos;
    protected long lastKeepAlive;
    protected String rotation;
    protected int score;
    protected String name, textureName;

    protected ClientInfo(UUID clientID, String pos, String rotation, String name, String textureName)
    {
        this.clientID = clientID;
        this.pos = pos;
        this.rotation = rotation;
        this.lastKeepAlive = System.currentTimeMillis();
        this.score = 0;
        this.name = name;
        this.textureName = textureName;
    }
}
