package myGameEngine;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.UUID;

import a3.MyGame;
import ray.networking.client.GameConnectionClient;
import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.Entity;
import ray.rage.scene.SceneManager;
import ray.rml.Matrix3f;
import ray.rml.Vector3f;

public class NetworkedClient extends GameConnectionClient 
{
    private ScriptManager scriptMan;
    private GhostAvatars ghosts;
    private MyGame myGame;
    private UUID id;
    private float timeSinceLastKeepAlive, timeSinceLastJoin;
    private HashMap<String, Texture> playerTex;
    private Entity avatarEntity;

    //Public boolean to determine whether we are connected to a server
    public boolean isJumping;
    public boolean isConnected;
    public boolean playerWon;
    public boolean stopJump;

    //Creates a UDP client
    public NetworkedClient(InetAddress remoteAddr, int remotePort, GhostAvatars ghosts, ScriptManager scriptMan, MyGame myGame, Entity avatarE) throws IOException 
    {
        super(remoteAddr, remotePort, ProtocolType.UDP);

        this.scriptMan = scriptMan;
        this.myGame = myGame;
        this.ghosts = ghosts;
        this.id = UUID.randomUUID();   
        this.isConnected = false; 
        this.timeSinceLastKeepAlive = 0.0f; 
        this.timeSinceLastJoin = 10000f; 
        this.playerWon = false;
        this.isJumping = false;
        this.avatarEntity = avatarE;

        //Setup textures
        setupTextureMap();

        //Send a join
        sendJOIN(scriptMan.getValue("avatarName").toString() + "Node");

        //Setup shutdown hook
        Runtime.getRuntime().addShutdownHook(new NetworkShutdownHook());
    }

    //Sets up all the possible player textures
    private void setupTextureMap()
    {
        playerTex = new HashMap<>();

        try
        {
            Texture temp = myGame.getEngine().getTextureManager().getAssetByPath("greenPlayer.png");
            playerTex.put("greenPlayer.png", temp);

            temp = myGame.getEngine().getTextureManager().getAssetByPath("orangePlayer.png");
            playerTex.put("orangePlayer.png", temp);

            temp = myGame.getEngine().getTextureManager().getAssetByPath("redPlayer.png");
            playerTex.put("redPlayer.png", temp);

            temp = myGame.getEngine().getTextureManager().getAssetByPath("pinkPlayer.png");
            playerTex.put("pinkPlayer.png", temp);

            temp = myGame.getEngine().getTextureManager().getAssetByPath("bluePlayer.png");
            playerTex.put("bluePlayer.png", temp);

            temp = myGame.getEngine().getTextureManager().getAssetByPath("brownPlayer.png");
            playerTex.put("brownPlayer.png", temp);

            temp = myGame.getEngine().getTextureManager().getAssetByPath("yellowPlayer.png");
            playerTex.put("yellowPlayer.png", temp);

            temp = myGame.getEngine().getTextureManager().getAssetByPath("purplePlayer.png");
            playerTex.put("purplePlayer.png", temp);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

    }

    //Overloaded version of processpackets implements additional functionality
    public void processPackets(float timeElapsed) 
    {
        timeSinceLastJoin += timeElapsed;
        timeSinceLastKeepAlive += timeElapsed;


        //If the client is connected. Ask for updates and send updates if necessary
        if (isConnected)
        {
            //Ask for details from the server
            sendWANTDETAILSFOR();
                
            //Send an update to the server (only will send if an update has actually occured)
            sendUPDATEFOR(scriptMan.getValue("avatarName").toString() + "Node");
        }
        //Else, try to connect to a server (allows the game to connect to a server even if it starts after...)
        else if(timeSinceLastJoin > 10000f)
        {
            sendJOIN(scriptMan.getValue("avatarName").toString() + "Node");
            timeSinceLastJoin = 0.0f;
        }
        
        //Process packets that have arrived
        processPackets();    
         
        //Every 10 seconds send a keepAlive if connected
        if (timeSinceLastKeepAlive > 10000f && isConnected)
        {
            sendKeepAlive();
        
            //Reset timer
            timeSinceLastKeepAlive = 0.0f;
        }

        //If the player is jumping send a msg
        if (isJumping)
        {
            sendJump();
            isJumping = false;
        }

        //If the player stopped jumping send a msg
        if (stopJump)
        {
            sendStopJump();
            stopJump = false;
        }

        //If the player has won... send a win message
        if (playerWon)
        {
            sendWinMsg();
            playerWon = false;
        }

        //Tell ghosts to update
        ghosts.update();
    }

    @Override
    protected void processPacket(Object myMsg)
    {
        String msg = (String) myMsg;

        //! Its possible to receive a null packet??? Handle this...
        try
        {
            String[] msgTokens = msg.split(",");

            //If the the msg list contains something...
            if (msgTokens.length > 0)
            {
                //Check for DETAILSFOR msg
                if (msgTokens[0].compareTo("DETAILSFOR") == 0)
                {
                    processDETAILSFOR(msgTokens);
                }

                if (msgTokens[0].compareTo("NPCPOS") == 0)
                {
                    processNPCPOS(msgTokens);
                }

                if (msgTokens[0].compareTo("BLOW") == 0)
                {
                    myGame.npc.applyBlowForce(Float.parseFloat(msgTokens[1]));
                }

                if (msgTokens[0].compareTo("ROTATEFAN") == 0)
                {
                    myGame.npc.rotateFan();
                }

                if (msgTokens[0].compareTo("JUMPFOR") == 0)
                {
                    ghosts.jumpGhost(UUID.fromString(msgTokens[1]));
                }

                if (msgTokens[0].compareTo("STOPJUMPFOR") == 0)
                {
                    ghosts.stopGhostJump(UUID.fromString(msgTokens[1]));
                }

                //Check for NEWBALL msg
                if (msgTokens[0].compareTo("NEWBALL") == 0)
                {
                    processNEWBALL(msgTokens);
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
                        //Set the avatar texture as requested by the server
                        System.out.println("\nConfirm received, Connection successful");
                        TextureState tState = (TextureState)myGame.getEngine().getSceneManager().getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
                        tState.setTexture(playerTex.get(msgTokens[2]));
                        avatarEntity.setRenderState(tState);

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

                //Server wants all client to sync
                if (msgTokens[0].compareTo("SYNC") == 0)
                {
                    //Sync the walls and platforms
                    myGame.platformWalls.resetWalls();
                    myGame.pc.reset();
                }

                if (msgTokens[0].compareTo("FIRSTWINNER") == 0)
                {
                    myGame.wc.incrementScore();
                }

                if (msgTokens[0].compareTo("RESETGAME") == 0)
                {
                    System.out.println("Game Reset!");
                    myGame.wc.setWinners(msgTokens[1], msgTokens[2]);
                    myGame.wc.resetGame();
                    myGame.platformWalls.resetWalls();
                    myGame.pc.reset();
                }
            }
        }

        //! Handles a null packet
        catch (Exception e)
        {
            //Don't process this null packet
            return;
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
            for (UUID wantID : ghosts.activeGhosts)
            {
                //Send last update time. Server only returns an update if something has happened
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

        String msg;
        SceneManager sm = myGame.getEngine().getSceneManager();

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

        //Attempt to send the update
        try
        {
            sendPacket(msg);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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

    public void sendNPCRot(Matrix3f npcRot)
    {
        //If I am not connected don't
        if (!isConnected)
            return;

        float[] temp = npcRot.toFloatArray();
        String msg = "NPCROT" +"," + temp[0] + "," + temp[1] + "," + temp[2] + "," + temp[3] + "," + temp[4] + "," + temp[5] + ","
                + temp[6] + "," + temp[7] + "," + temp[8];

        try 
        {
            sendPacket(msg);            
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    //Sends a msg to the server saying a player won
    public void sendWinMsg()
    {
        String msg = "WIN," + this.id;

        try
        {
            sendPacket(msg);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendJump()
    {
        String msg = "JUMP," + this.id;

        try
        {
            sendPacket(msg);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendStopJump()
    {
        String msg = "STOPJUMP," + this.id;

        try
        {
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
            ghosts.addGhost(UUID.fromString(msgTokens[1]), ghostPos, rotation, playerTex.get(msgTokens[15])); 
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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
    }

    private void processBYE(UUID leavingID)
    {
        //If the ghost exists remove the ghost
        if (ghosts.activeGhosts.contains(leavingID))
            ghosts.removeGhost(leavingID);
    }

    private void processFORCEDBYE()
    {
        for (int count = 0; count < ghosts.activeGhosts.size(); count++)
        {
            ghosts.removeGhost(ghosts.activeGhosts.get(count));
        }
    }

    private void processNEWBALL(String[] msgTokens)
    {
        Vector3f pos = (Vector3f)Vector3f.createFrom(Float.parseFloat(msgTokens[1]), Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]));
        float radius = Float.parseFloat(msgTokens[4]);
        myGame.bouncyBalls.addBall(pos, radius);
    }

    private void processNPCPOS(String[] msgTokens)
    {
        Vector3f npcPos = (Vector3f)Vector3f.createFrom(Float.parseFloat(msgTokens[1]), Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]));

        float[] temp = { Float.parseFloat(msgTokens[4]), Float.parseFloat(msgTokens[5]), Float.parseFloat(msgTokens[6]),
                Float.parseFloat(msgTokens[7]), Float.parseFloat(msgTokens[8]), Float.parseFloat(msgTokens[9]),
                Float.parseFloat(msgTokens[10]), Float.parseFloat(msgTokens[11]), Float.parseFloat(msgTokens[12]) };

        Matrix3f rot =(Matrix3f)Matrix3f.createFrom(temp);            
        
        myGame.npc.updateNPCTransform(npcPos, rot);
    }

    //Shutdown hook ensures that "BYE" is sent to the server (most of the time...)
    private class NetworkShutdownHook extends Thread
    {    
        //Executes a bye to the server
        public void run()
        {
            sendBYE();
        }     
    }
}
