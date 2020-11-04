package myServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import ray.networking.server.GameConnectionServer;
import ray.networking.server.IClientInfo;

//This game server uses UDP
public class GameServer extends GameConnectionServer<UUID>
{
    protected volatile Map<UUID, ClientInfo> clientInfo;
    protected volatile boolean threadRunning;
    private Thread detectDeadClient;

    //Shutdown hook for hopefully closing the server properly... I think...
    private Runtime current;

    //Call super to create a UDP server
    public GameServer(int localPort) throws IOException 
    {
        super(localPort, ProtocolType.UDP);

        //Synchronized thread safe map
        this.clientInfo = Collections.synchronizedMap(new HashMap<UUID, ClientInfo>());

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
                processWANTDETAILSFOR(UUID.fromString(msgTokens[1]), UUID.fromString(msgTokens[2]), Long.parseLong(msgTokens[3]));
            }

            //Check for UPDATEFOR msg
            if (msgTokens[0].compareTo("UPDATEFOR") == 0)
            {
                //Update details for the client passed
                processUPDATEFOR(msgTokens);
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

                //Send confirmation msg with position of all active clients
                sendConfirmMessage(UUID.fromString(msgTokens[1]));
            }

            //Check for BYE msg
            if (msgTokens[0].compareTo("BYE") == 0)
            {
                processBYE(msg, UUID.fromString(msgTokens[1]));

                System.out.println("Client " + msgTokens[1] + " left");
            }
        }
    }

    //Processes the join msg
    private void processJOIN(String[] msgTokens, InetAddress senderIP, int sndPort)
    {
        try 
        {
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
                clientInfo.put(clientID, new ClientInfo(clientID, pos, rotation));
        
                //If we have more than 1 client... Inform them of the new client
                if (getClients().size() > 1)
                {
                    sendCreateMessages(clientID, pos, rotation);
                }

                //Log join
                System.out.println("Client " + msgTokens[1] + " joined");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //Processes a WANTDETAILSFOR and sends info back about the wanted client
    //! Just position information at the moment.
    private void processWANTDETAILSFOR(UUID clientID, UUID wantID, long lastUpdateTime)
    {
        try
        {
            //If the requested client exists
            if (clientInfo.containsKey(wantID))
            {
                //! This is causing a bug??? HUH?
                //TODO: Fix me!
                //Only send a packet if an update has actually occured
                //if (clientInfo.get(wantID).lastUpdate > lastUpdateTime)
                {
                    String msg = new String("DETAILSFOR," + wantID.toString() + clientInfo.get(wantID).pos + clientInfo.get(wantID).rotation);
                    sendPacket(msg, clientID);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }        
    }

    //Processes a UPDATEFOR msg
    //! Just position information at the moment
    private void processUPDATEFOR(String[] msgTokens)
    {
        UUID updatefor = UUID.fromString(msgTokens[2]);
        long updateTime = Long.parseLong(msgTokens[msgTokens.length - 1]);

        //If msg contains POS
        if (msgTokens[1].compareTo("POS") == 0)
        {
            //If the client exists update it
            if (clientInfo.containsKey(updatefor))
            {
                //Only if the update is not out of date
                if (updateTime > clientInfo.get(updatefor).lastUpdate)
                {
                    String pos = "," + msgTokens[3] + "," + msgTokens[4] + "," + msgTokens[5];
                    clientInfo.get(updatefor).pos = pos;
                    clientInfo.get(updatefor).lastUpdate = System.currentTimeMillis();
                }
            }
        }

        else if (msgTokens[1].compareTo("ORIENT") == 0)
        {
            //If the client exists update it
            if (clientInfo.containsKey(updatefor))
            {
                //Only if the update is not out of date
                if (updateTime > clientInfo.get(updatefor).lastUpdate)
                {
                    String rotation = "," + msgTokens[3] + "," + msgTokens[4] + "," + msgTokens[5] + "," + msgTokens[6] + ","
                            + msgTokens[7] + "," + msgTokens[8] + "," + msgTokens[9] + "," + msgTokens[10] + ","
                            + msgTokens[11];

                    clientInfo.get(updatefor).rotation = rotation;
                    clientInfo.get(updatefor).lastUpdate = System.currentTimeMillis();
                }
            }
        }
        else
        {
            //If the client exists update it
            if (clientInfo.containsKey(updatefor))
            {
                //Only if the update is not out of date
                if (updateTime > clientInfo.get(updatefor).lastUpdate)
                {
                    String pos = "," + msgTokens[3] + "," + msgTokens[4] + "," + msgTokens[5];

                    String rotation = "," + msgTokens[6] + "," + msgTokens[7] + "," + msgTokens[8] + "," + msgTokens[9]
                            + "," + msgTokens[10] + "," + msgTokens[11] + "," + msgTokens[12] + "," + msgTokens[13] + ","
                            + msgTokens[14];

                    clientInfo.get(updatefor).pos = pos;
                    clientInfo.get(updatefor).rotation = rotation;
                    clientInfo.get(updatefor).lastUpdate = System.currentTimeMillis();
                }
            }
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
    
    private void sendCreateMessages(UUID clientID, String position, String rotation)
    {
        //Send a create msg to all clients other than the client that joined
        try
        {
            String msg = new String("CREATE," + clientID.toString());
            msg += position + rotation;
            
            forwardPacketToAll(msg, clientID);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    //Confirms a join request was received.
    //Sends the location of all active clients to the newly joined client
    private void sendConfirmMessage(UUID clientID)
    {
        try
        {
            sendPacket("CONFIRM", clientID);

            //Send the position of all other clients through create msgs
            for (ClientInfo ci : clientInfo.values())
            {
                //Don't send a info back on the client itself
                if (!clientID.equals(ci.clientID))
                {
                    //Send a create msg
                    String msg = new String("CREATE," + ci.clientID.toString());
                    msg += ci.pos + ci.rotation;

                    sendPacket(msg, clientID);
                }
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private class Shutdown extends Thread
    {
        public void run()
        {
            threadRunning = false;
            detectDeadClient.interrupt();
            System.out.println("Server shutting down...");
        }

    }
}
