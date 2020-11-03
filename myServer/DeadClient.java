package myServer;

import java.util.UUID;

//This class is ran in a thread alongside the server. It periodicaly detects and removes clients
//that have exceeded the maximum time out. Currently 30sec
public class DeadClient implements Runnable
{
    private GameServer myGameServer;

    public DeadClient(GameServer myGameServer)
    {
        this.myGameServer = myGameServer;
    }

    @Override
    public void run() 
    {
        while (myGameServer.threadRunning)
        {
            long cTime = System.currentTimeMillis();

            for (UUID id : myGameServer.clientInfo.keySet())
            {
                //Remove a client if its been inactive for 1min
                if ((cTime - myGameServer.clientInfo.get(id).lastKeepAlive) >= 30000)
                {
                    //Forcibly say bye
                    System.out.println("Forcibly removed inactive client " + id);
                    myGameServer.processForcedBYE(id);
                }
            }

            //Attempt to sleep for 10 seconds
            try
            {
                Thread.sleep(10000);
            }
            //Catches an interrupt and awakes the thread
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }    
}
