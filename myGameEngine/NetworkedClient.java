package myGameEngine;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import a3.MyGame;
import ray.networking.client.GameConnectionClient;
import ray.rage.scene.SceneManager;
import ray.rml.Vector3f;

public class NetworkedClient extends GameConnectionClient 
{
    private GhostAvatars ghosts;
    private MyGame myGame;
    private UUID id;

    //Public boolean to determine whether we are connected to a server
    public boolean isConnected;

    //These variables are set to determine what updates need to be sent to the server
    boolean updatePositionOnServer = false;  

    //Creates a UDP client
    public NetworkedClient(InetAddress remoteAddr, int remotePort, GhostAvatars ghosts, MyGame myGame) throws IOException 
    {
        super(remoteAddr, remotePort, ProtocolType.UDP);

        this.myGame = myGame;
        this.ghosts = ghosts;
        this.id = UUID.randomUUID();   
        this.isConnected = false;     
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
            msg += "," + sm.getSceneNode(nodeName).getLocalPosition().z();

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
            for (UUID wantID : ghosts.activeGhosts)
            {
                String msg = new String("WANTDETAILSFOR," + id.toString() + "," + wantID.toString());            
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
        if (!updatePositionOnServer)
            return;

        SceneManager sm = myGame.getEngine().getSceneManager();

        //Attempt to send an update
        try
        {
            String msg = new String("UPDATEFOR," + id.toString());
            msg += "," + sm.getSceneNode(nodeName).getLocalPosition().x();
            msg += "," + sm.getSceneNode(nodeName).getLocalPosition().y();
            msg += "," + sm.getSceneNode(nodeName).getLocalPosition().z();

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

    private void processCREATE(String[] msgTokens)
    {
        //Fixes a weird networking bug where multiple ghosts with the same ID were being added...
        if (ghosts.activeGhosts.contains(UUID.fromString(msgTokens[1])))
            return;

        Vector3f ghostPos = (Vector3f) Vector3f.createFrom(Float.parseFloat(msgTokens[2]),
        Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
  
        //Attemp to create a new ghost
        try
        {
            ghosts.addGhost(UUID.fromString(msgTokens[1]), ghostPos); 
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }  
    }

    //! Only processes position information at the moment
    private void processDETAILSFOR(String[] msgTokens)
    {
        Vector3f ghostPos = (Vector3f) Vector3f.createFrom(Float.parseFloat(msgTokens[2]),
        Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));

        UUID detailsFor = UUID.fromString(msgTokens[1]);

        //If the ghost exists... update it
        if (ghosts.activeGhosts.contains(detailsFor))
        {
            ghosts.updateGhostPosition(detailsFor, ghostPos);
        }
    }

    private void processBYE(UUID leavingID)
    {
        ghosts.removeGhost(leavingID);
    }
}
