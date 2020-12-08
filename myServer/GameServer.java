package myServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import ray.networking.server.GameConnectionServer;
import ray.networking.server.IClientInfo;
import ray.rml.Matrix3f;

//This game server uses UDP
public class GameServer extends GameConnectionServer<UUID>
{
    protected volatile Map<UUID, ClientInfo> clientInfo;
    protected volatile boolean threadRunning, playerWonThisRound;
    private Thread detectDeadClient, ballSpawner, npcControl, resetGame;
    private AvatarTextureManager avatarTexMan;
    private Matrix3f npcRot;

    //Shutdown hook for hopefully closing the server properly... I think...
    private Runtime current;

    //Call super to create a UDP server
    public GameServer(int localPort) throws IOException 
    {
        super(localPort, ProtocolType.UDP);

        //Synchronized thread safe map
        this.clientInfo = Collections.synchronizedMap(new HashMap<UUID, ClientInfo>());
        this.playerWonThisRound = false;
        this.avatarTexMan = new AvatarTextureManager();

        //Get public IP using a web service
        URL myIp = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(myIp.openStream()));
        String IP = in.readLine();

        //Print startup info
        System.out.println("Game server created...");
        System.out.println("Public: " + IP + ":" + localPort);
        System.out.println("Local: " +  InetAddress.getLocalHost().getHostAddress().trim() + ":" + localPort);

        //Create a thread to detect clients that need to be removed
        threadRunning = true;
        Runnable runnable = new DeadClient(this);
        detectDeadClient = new Thread(runnable);
        detectDeadClient.start();

        //Create a thread to spawn the balls
        Runnable balls = new BallSpawner(this);
        ballSpawner = new Thread(balls);
        ballSpawner.start(); 
        
        //Create a thread to control the NPC
        Runnable npc = new NPCController(this);
        npcControl = new Thread(npc);
        npcControl.start();

        //Intilize npc rotation
        float[] rotVal = { 1, 0, 0, 0, 1, 0, 0, 0, 1 };
        npcRot = (Matrix3f)Matrix3f.createTransposeFrom(rotVal);

        //Intilize shutdown hook
        current = Runtime.getRuntime();
        current.addShutdownHook(new Shutdown());
    }

    @Override
    public void processPacket(Object myMsg, InetAddress senderIP, int sndPort)
    {
        String msg = (String) myMsg;
        //System.out.println("Received Msg: " + msg);
        String[] msgTokens = msg.split(",");

        //If the msg contains something
        if (msgTokens.length > 0)
        {
            //Check for WANTDETAILSFOR msg
            if (msgTokens[0].compareTo("WANTDETAILSFOR") == 0)
            {
                //Send back details for the requested client
                processWANTDETAILSFOR(UUID.fromString(msgTokens[1]), UUID.fromString(msgTokens[2]));
            }

            //Check for UPDATEFOR msg
            if (msgTokens[0].compareTo("UPDATEFOR") == 0)
            {
                //Update details for the client passed
                processUPDATEFOR(msgTokens);
            }

            //Check for NPC rotation update
            if (msgTokens[0].compareTo("NPCROT") == 0)
            {
                processNPCRot(msgTokens);
            }

            //Check for JUMP msg
            if (msgTokens[0].compareTo("JUMP") == 0)
            {
                forwardToClients("JUMPFOR," + msgTokens[1], UUID.fromString(msgTokens[1]));
            }

            if (msgTokens[0].compareTo("STOPJUMP") == 0)
            {
                forwardToClients("STOPJUMPFOR," + msgTokens[1], UUID.fromString(msgTokens[1]));
            }

            //Check for KEEPALIVE msg
            if (msgTokens[0].compareTo("KEEPALIVE") == 0)
            {
                processKEEPALIVE(msgTokens);
            }

            //Check for JOIN msg
            if (msgTokens[0].compareTo("JOIN") == 0)
            {
                //Process the join
                processJOIN(msgTokens, senderIP, sndPort);
            }

            //Check for BYE msg
            if (msgTokens[0].compareTo("BYE") == 0)
            {
                processBYE(msg, UUID.fromString(msgTokens[1]));

                System.out.println("Client " + msgTokens[1] + " left");
            }

            //Win msg
            if (msgTokens[0].compareTo("WIN") == 0)
            {
                //If this is the first player to win this round send a msg back
                if (!playerWonThisRound)
                {
                    //Increment score by two for the first winner
                    clientInfo.get(UUID.fromString(msgTokens[1])).score += 2;
    
                    sendMsgToClient("FIRSTWINNER", UUID.fromString(msgTokens[1]));
                    System.out.println("Client: " + msgTokens[1] + " won the round");
                    playerWonThisRound = true;

                    //Start the thread to reset the game
                    Runnable rst = new ResetGame(this, UUID.fromString(msgTokens[1]));
                    resetGame = new Thread(rst);
                    resetGame.start();
                }
                else
                {
                    clientInfo.get(UUID.fromString(msgTokens[1])).score++;
                    System.out.println("Client: " + msgTokens[1] + " got to the end");
                }
            }
        }
    }

    //Processes the join msg
    private void processJOIN(String[] msgTokens, InetAddress senderIP, int sndPort)
    {
        try 
        {
            //If 8 clients already exist, don't join this client
            if (clientInfo.size() == 8)
                return;

            //If the client doesn't already exists...
            if (!clientInfo.containsKey(UUID.fromString(msgTokens[1])))
            {
                IClientInfo client = getServerSocket().createClientInfo(senderIP, sndPort);
                UUID clientID = UUID.fromString(msgTokens[1]);
                addClient(client, clientID);
        
                String pos = "," + msgTokens[2] + "," + msgTokens[3] + "," + msgTokens[4];
                String rotation = "," + msgTokens[5] + "," + msgTokens[6] + "," + msgTokens[7] + "," + msgTokens[8]
                + "," + msgTokens[9] + "," + msgTokens[10] + "," + msgTokens[11] + "," + msgTokens[12] + ","
                + msgTokens[13];
        
                //Add the client to the HashMap
                AvatarTexture temp = avatarTexMan.getRandomUnusedTexture();
                clientInfo.put(clientID, new ClientInfo(clientID, pos, rotation, temp.name, temp.textureName));
        
                //If we have more than 1 client... Inform them of the new client
                if (getClients().size() > 1)
                {
                    sendCreateMessages(clientID, pos, rotation, temp);
                }

                //Log join                
                System.out.println("Client " + msgTokens[1] + " joined");

                //Send confirmation msg with position of all active clients
                sendConfirmMessage(UUID.fromString(msgTokens[1]), temp);

                //Since a new clients joined... ask all the clients to Sync
                sendMsgToAll("SYNC");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //Processes a WANTDETAILSFOR and sends info back about the wanted client
    private void processWANTDETAILSFOR(UUID clientID, UUID wantID)
    {
        try
        {
            //If the requested client exists
            if (clientInfo.containsKey(wantID))
            {
                //Send the update
                String msg = new String("DETAILSFOR," + wantID.toString() + clientInfo.get(wantID).pos + clientInfo.get(wantID).rotation);
                    sendPacket(msg, clientID);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }        
    }

    //Processes a UPDATEFOR msg
    private void processUPDATEFOR(String[] msgTokens)
    {
        UUID updatefor = UUID.fromString(msgTokens[2]);

        //If the client exists update it
        if (clientInfo.containsKey(updatefor))
        {

            String pos = "," + msgTokens[3] + "," + msgTokens[4] + "," + msgTokens[5];

            String rotation = "," + msgTokens[6] + "," + msgTokens[7] + "," + msgTokens[8] + "," + msgTokens[9]
                    + "," + msgTokens[10] + "," + msgTokens[11] + "," + msgTokens[12] + "," + msgTokens[13] + ","
                    + msgTokens[14];

            clientInfo.get(updatefor).pos = pos;
            clientInfo.get(updatefor).rotation = rotation;
        }
    }

    //Process a BYE msg
    private void processBYE(String msg, UUID leavingID)
    {
        try
        {
            //If the client exists
            if (clientInfo.containsKey(leavingID))
            {
                //Remove client from clientInfo
                avatarTexMan.reclaimTexture(clientInfo.get(leavingID).textureName);
                clientInfo.remove(leavingID);

                //Remove from server
                removeClient(leavingID);

                //Forward BYE msg to all other clients
                forwardPacketToAll(msg, leavingID);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //Called by the thread to forcibly make a client leave
    protected void processForcedBYE(UUID leavingID)
    {
        //Send forced bye in case client is still listening...
        try
        {
            sendPacket("FORCEDBYE", leavingID);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //Inform other clients of the change
        String msg = new String("BYE," + leavingID.toString());
        processBYE(msg, leavingID);
    }

    //Tells the server to keep a client active (client sends this msg every 10 seconds)
    private void processKEEPALIVE(String[] msgTokens)
    {
        //Update time of last keep alive
        if (clientInfo.containsKey(UUID.fromString(msgTokens[1])))
            clientInfo.get(UUID.fromString(msgTokens[1])).lastKeepAlive = System.currentTimeMillis();

    }

    private void processNPCRot(String[] msgTokens)
    {
        float[] temp = { Float.parseFloat(msgTokens[1]), Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]),
            Float.parseFloat(msgTokens[4]), Float.parseFloat(msgTokens[5]), Float.parseFloat(msgTokens[6]),
            Float.parseFloat(msgTokens[7]), Float.parseFloat(msgTokens[8]), Float.parseFloat(msgTokens[9]) };

        npcRot =(Matrix3f)Matrix3f.createFrom(temp);   
    }
    
    private void sendCreateMessages(UUID clientID, String position, String rotation, AvatarTexture avTex)
    {
        //Send a create msg to all clients other than the client that joined
        try
        {
            String msg = new String("CREATE," + clientID.toString());
            msg += position + rotation;
            msg += "," + avTex.name + "," + avTex.textureName;
            
            forwardPacketToAll(msg, clientID);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    //Confirms a join request was received.
    //Sends the location of all active clients to the newly joined client
    private void sendConfirmMessage(UUID clientID, AvatarTexture avTex)
    {
        try
        {
            sendPacket("CONFIRM," + avTex.name + "," + avTex.textureName, clientID);

            //Send the position of all other clients through create msgs
            for (ClientInfo ci : clientInfo.values())
            {
                //Don't send a info back on the client itself
                if (!clientID.equals(ci.clientID))
                {
                    //Send a create msg
                    String msg = new String("CREATE," + ci.clientID.toString());
                    msg += ci.pos + ci.rotation + "," + ci.name + "," + ci.textureName;

                    sendPacket(msg, clientID);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    protected void sendMsgToAll(String msg)
    {
        try 
        {
            sendPacketToAll(msg);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    protected void sendNPCInfo(String msg)
    {
        try 
        {
            //Also send NPC rotation
            float[] temp = npcRot.toFloatArray();
            msg += "," + temp[0] + "," + temp[1] + "," + temp[2] + "," + temp[3] + "," + temp[4] + "," + temp[5] + ","
                    + temp[6] + "," + temp[7] + "," + temp[8];

            sendPacketToAll(msg);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    protected void sendMsgToClient(String msg, UUID client)
    {
        try
        {
            sendPacket(msg, client);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    protected void forwardToClients(String msg, UUID client)
    {
        try
        {
            forwardPacketToAll(msg, client);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void sendPacketToAll(Serializable object) throws IOException 
    {
        synchronized(this)
        {
            super.sendPacketToAll(object);
        }
    }

    @Override
    protected void sendPacket(Serializable object, UUID clientUID) throws IOException 
    {
        synchronized(this)
        {
            super.sendPacket(object, clientUID);
        }
    }

    @Override
    protected void forwardPacketToAll(Serializable object, UUID originalClientUID) throws IOException
    {
        synchronized(this)
        {
            super.forwardPacketToAll(object, originalClientUID);
        }
    }
    
    private class Shutdown extends Thread
    {
        public void run()
        {
            threadRunning = false;
            detectDeadClient.interrupt();
            ballSpawner.interrupt();
            npcControl.interrupt();
            System.out.println("Server shutting down...");
        }
    }
}
