package myServer;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.UUID;

import ray.networking.server.GameConnectionServer;
import ray.networking.server.IClientInfo;

//TODO: Handle condition that a client exits without saying goodbye... Timeout?

//This game server uses UDP
public class GameServer extends GameConnectionServer<UUID>
{
    HashMap<UUID, ClientInfo> clientInfo;

    //Call super to create a UDP server
    public GameServer(int localPort) throws IOException 
    {
        super(localPort, ProtocolType.UDP);
        this.clientInfo = new HashMap<>();
        System.out.println("Game Server Created at " +  InetAddress.getLocalHost().getHostAddress().trim() + " and port " + localPort);

        //Put that information in a file
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
    private void processWANTDETAILSFOR(UUID clientID, UUID wantID)
    {
        try
        {
            //If the requested client exists
            if (clientInfo.containsKey(wantID))
            {
                String msg = new String("DETAILSFOR," + wantID.toString() + clientInfo.get(wantID).pos);
                sendPacket(msg, clientID);
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

        //If the client exists
        if (clientInfo.containsKey(updateFor))
        {
            clientInfo.get(updateFor).pos = pos;
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

                //Forward BYE msg to all other clients
                forwardPacketToAll(msg, leavingID);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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
}
