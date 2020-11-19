package myServer;

import java.util.Random;

//Every 5 sec this thread spawns a new ball on every client at the same location
public class BallSpawner implements Runnable
{
    private GameServer myGameServer;

    public BallSpawner(GameServer myGameServer)
    {
        this.myGameServer = myGameServer;
    }

    @Override
    public void run() 
    {
        while (myGameServer.threadRunning)
        {
            //Generate a random number between -8 and 8
            float rand = (float)(Math.random() * 8);
            if (new Random().nextInt(2) == 1)
                rand = rand * -1;

            //Generate msg
            String msg = "NEWBALL," + rand + ",40,68," + (float)(Math.random() + 1);

            //Send msg to all clients
            myGameServer.sendMsgToAll(msg);        

            //Attempt to sleep for 5 seconds
            try
            {
                Thread.sleep(5000);
            }
            //Catches an interrupt and awakes the thread
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }       
}
