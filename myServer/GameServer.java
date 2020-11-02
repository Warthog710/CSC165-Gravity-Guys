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
        
                //Add the client to the HashMap
                clientInfo.put(clientID, new ClientInfo(clientID, pos));
        
                //If we have more than 1 client... Inform them of the new client
                if (getClients().size() > 1)
                {
                    sendCreateMessages(clientID, pos);
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
                //Only send a packet if an update has actually occured
                if (clientInfo.get(wantID).lastUpdate > lastUpdateTime)
                {
                    String msg = new String("DETAILSFOR," + wantID.toString() + clientInfo.get(wantID).pos);
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
        String pos = "," + msgTokens[2] + "," + msgTokens[3] + "," + msgTokens[4];
        UUID updateFor = UUID.fromString(msgTokens[1]);
        Long updateTime = Long.parseLong(msgTokens[5]);

        //If the client exists update it
        if (clientInfo.containsKey(updateFor))
        {
            //Only update the client if the currently held location is out of date
            if (updateTime > clientInfo.get(updateFor).lastUpdate)
            {
                clientInfo.get(updateFor).pos = pos;
                clientInfo.get(updateFor).lastUpdate = System.currentTimeMillis();
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
        clientInfo.get(UUID.fromString(msgTokens[1])).lastKeepAlive = System.currentTimeMillis();
    }
    
    private void sendCreateMessages(UUID clientID, String position)
    {
        //Send a create msg to all clients other than the client that joined
        try
        {
            String msg = new String("CREATE," + clientID.toString());
            msg += position;

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
                    msg += ci.pos;

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
