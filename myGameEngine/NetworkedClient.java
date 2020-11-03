package myGameEngine;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.UUID;

import a3.MyGame;
import ray.networking.client.GameConnectionClient;
import ray.rage.scene.SceneManager;
import ray.rml.Matrix3f;
import ray.rml.Vector3f;

public class NetworkedClient extends GameConnectionClient 
{
    private ScriptManager scriptMan;
    private GhostAvatars ghosts;
    private MyGame myGame;
    private UUID id;
    private float timeSinceLastKeepAlive;
    private HashMap<UUID, Long> lastUpdate;

    //Public boolean to determine whether we are connected to a server
    public boolean isConnected;

    //These variables are set to determine what updates need to be sent to the server
    protected boolean updatePositionOnServer = false;  
    protected boolean updateOrientationOnServer = false;

    //Creates a UDP client
    public NetworkedClient(InetAddress remoteAddr, int remotePort, GhostAvatars ghosts, ScriptManager scriptMan, MyGame myGame) throws IOException 
    {
        super(remoteAddr, remotePort, ProtocolType.UDP);

        this.scriptMan = scriptMan;
        this.myGame = myGame;
        this.ghosts = ghosts;
        this.id = UUID.randomUUID();   
        this.isConnected = false; 
        this.timeSinceLastKeepAlive = 0.0f;  
        this.lastUpdate = new HashMap<>();  
    }

    //Overloaded version of processpackets implements additional functionality
    public void processPackets(float timeElapsed) 
    {
        //If the client is connected. Ask for updates and send updates if necessary
        if (isConnected)
        {
            //Ask for details from the server
            sendWANTDETAILSFOR();
                
            //Send an update to the server (only will send if an update has actually occured)
            sendUPDATEFOR(scriptMan.getValue("avatarName").toString() + "Node");
        }
        //Else, try to connect to a server (allows the game to connect to a server even if it starts after...)
        else
        {
            sendJOIN(scriptMan.getValue("avatarName").toString() + "Node");
        }
        
        //Process packets that have arrived
        processPackets();       
        
        //If a certain amount of time has happened... send a keep alive message
        timeSinceLastKeepAlive += timeElapsed;
        
        //Every 10 seconds send a keepAlive if connected
        if (timeSinceLastKeepAlive > 10000f && isConnected)
        {
            sendKeepAlive();
        
            //Reset timer
            timeSinceLastKeepAlive = 0.0f;
        }
    }

    @Override
    protected void processPacket(Object myMsg)
    {
        String msg = (String) myMsg;
        //System.out.println("Received msg: " + msg);
        String[] msgTokens = msg.split(",");

        //If the the msg list contains something...
        if (msgTokens.length > 0)
        {
            //Check for DETAILSFOR msg
            if (msgTokens[0].compareTo("DETAILSFOR") == 0)
            {
                processDETAILSFOR(msgTokens);
            }

            //Check for CREATE msg
            if (msgTokens[0].compareTo("CREATE") == 0)
            {
                processCREATE(msgTokens);                       
            }

            //Check for CONFIRM msg
            if (msgTokens[0].compareTo("CONFIRM") == 0)
            {
                //Server responded and client creation was successful
                //Only do this if I'm not already connected
                //NOTE: It is currently possible to recieve multiple confirm message...
                //NOTE: This is because the game continously attempts to join a server...
                //NOTE: Lets just ignore the others for now... ¯\_(ツ)_/¯
                if (!isConnected)
                {
                    System.out.println("\nConfirm received... Connection successful");
                    isConnected = true;
                }
            }

            //Check for BYE msg
            if (msgTokens[0].compareTo("BYE") == 0)
            {
                processBYE(UUID.fromString(msgTokens[1]));
            }

            //Server forcibly removed this client... Just in case
            if (msgTokens[0].compareTo("FORCEDBYE") == 0)
            {
                processFORCEDBYE();
            }
        }
    } 
    
    public void sendJOIN(String nodeName)
    {
        //If I've already joined a server... 
        if (isConnected)
        {
            System.out.println("A server has already been successfully joined...");
            return;
        }

        SceneManager sm = myGame.getEngine().getSceneManager();    
        try
        {
            String msg = new String("JOIN," + id.toString());
            msg += "," + sm.getSceneNode(nodeName).getLocalPosition().x();
            msg += "," + sm.getSceneNode(nodeName).getLocalPosition().y();
            msg += "," + sm.getSceneNode(nodeName).getLocalPosition().z() + ",";

            
            //Pack rotation matrix
            float[] temp = sm.getSceneNode(nodeName).getLocalRotation().toFloatArray();
            for (int count = 0; count < sm.getSceneNode(nodeName).getLocalRotation().toFloatArray().length; count++)
                    msg += temp[count] + ",";

            msg += System.currentTimeMillis();

            sendPacket(msg);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //Asks the server to send details for all other clients
    public void sendWANTDETAILSFOR()
    {
        //If I'm not connected to server... don't try it
        if (!isConnected)
            return;

        try
        {
            for (UUID wantID : lastUpdate.keySet())
            {
                //Send last update time. Server only returns an update if something has happened
                String msg = new String("WANTDETAILSFOR," + id.toString() + "," + wantID.toString() + "," + lastUpdate.get(wantID));            
                sendPacket(msg);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendUPDATEFOR(String nodeName)
    {
        //If I'm not connected to a server... don't try it
        if (!isConnected)
            return;

        //If no movement has taken place... don't send an update
        if (!updatePositionOnServer && !updateOrientationOnServer)
            return;

        SceneManager sm = myGame.getEngine().getSceneManager();
        String msg;

        //if just no movement send just orientation
        if (!updatePositionOnServer)
        {
            msg = new String("UPDATEFOR," + "ORIENT," + id.toString() + ",");

            //Pack rotation matrix
            float[] temp = sm.getSceneNode(nodeName).getLocalRotation().toFloatArray();
            for (int count = 0; count < sm.getSceneNode(nodeName).getLocalRotation().toFloatArray().length; count++)
                msg += temp[count] + ",";

            msg += System.currentTimeMillis();

            updateOrientationOnServer = false;;
        }

        else if (!updateOrientationOnServer)
        {
            //Build msg
            msg = new String("UPDATEFOR," + "POS," + id.toString());
            msg += "," + sm.getSceneNode(nodeName).getLocalPosition().x();
            msg += "," + sm.getSceneNode(nodeName).getLocalPosition().y();
            msg += "," + sm.getSceneNode(nodeName).getLocalPosition().z();
            msg += "," + System.currentTimeMillis();

            updatePositionOnServer = false;
        }

        else
        {
            //Build msg
            msg = new String("UPDATEFOR," + "BOTH," + id.toString());
            msg += "," + sm.getSceneNode(nodeName).getLocalPosition().x();
            msg += "," + sm.getSceneNode(nodeName).getLocalPosition().y();
            msg += "," + sm.getSceneNode(nodeName).getLocalPosition().z();
            msg += ",";

            //Pack rotation matrix
            float[] temp = sm.getSceneNode(nodeName).getLocalRotation().toFloatArray();
            for (int count = 0; count < sm.getSceneNode(nodeName).getLocalRotation().toFloatArray().length; count++)
                msg += temp[count] + ",";
            
            msg += System.currentTimeMillis();

            updatePositionOnServer = false;
            updateOrientationOnServer = false;
        }

        //Attempt to send the update
        try
        {
            sendPacket(msg);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //Update has been sent
        updatePositionOnServer = false;
    }

    public void sendBYE()
    {
        //If I'm not connected to a server... don't try it
        if (!isConnected)
            return;

        try
        {
            String msg = new String("BYE," + id.toString());
            sendPacket(msg);
            isConnected = false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }        
    }

    private void sendKeepAlive()
    {
        //If I'm not connected to a server... don't try it
        if (!isConnected)
            return;
        
        //Send a keep alive
        try
        {
            String msg = new String("KEEPALIVE," + id.toString());
            sendPacket(msg);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void processCREATE(String[] msgTokens)
    {
        //Fixes a weird networking bug where multiple ghosts with the same ID were being added...
        if (ghosts.activeGhosts.contains(UUID.fromString(msgTokens[1])))
            return;

        Vector3f ghostPos = (Vector3f) Vector3f.createFrom(Float.parseFloat(msgTokens[2]),
        Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));

        float[] temp = new float[9];

        //Iterate through msg and get the rotation matrix float array
        for (int count = 0; count < 9; count++)
            temp[count] = Float.parseFloat(msgTokens[count + 5]);

        Matrix3f rotation = (Matrix3f)Matrix3f.createFrom(temp);  
  
        //Attempt to create a new ghost
        try
        {
            ghosts.addGhost(UUID.fromString(msgTokens[1]), ghostPos, rotation); 
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }  

        //Record ghost with new update time
        lastUpdate.put(UUID.fromString(msgTokens[1]), System.currentTimeMillis());
    }

    //? Processes both position and rotation
    private void processDETAILSFOR(String[] msgTokens)
    {
        Vector3f ghostPos = (Vector3f) Vector3f.createFrom(Float.parseFloat(msgTokens[2]),
        Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));

        float[] temp = new float[9];

        //Iterate through msg and get the rotation matrix float array
        for (int count = 0; count < 9; count++)
            temp[count] = Float.parseFloat(msgTokens[count + 5]);

        Matrix3f rotation = (Matrix3f)Matrix3f.createFrom(temp);            

        UUID detailsFor = UUID.fromString(msgTokens[1]);

        //If the ghost exists... update it
        if (ghosts.activeGhosts.contains(detailsFor))
            ghosts.updateGhostPosition(detailsFor, ghostPos, rotation);

        //Update last update time
        if (lastUpdate.containsKey(detailsFor))
            lastUpdate.put(detailsFor, System.currentTimeMillis());
    }

    private void processBYE(UUID leavingID)
    {
        //If the ghost exists remove the ghost
        if (ghosts.activeGhosts.contains(leavingID))
            ghosts.removeGhost(leavingID);

        //Also remove if it exists from the update tracker
        if (lastUpdate.containsKey(leavingID))
            lastUpdate.remove(leavingID);
    }

    private void processFORCEDBYE()
    {
        for (int count = 0; count < ghosts.activeGhosts.size(); count++)
        {
            ghosts.removeGhost(ghosts.activeGhosts.get(count));
        }

        //Empty last update
        lastUpdate.clear();
    }
}
