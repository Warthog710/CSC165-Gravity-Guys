package myServer;

import java.util.UUID;

public class ResetGame implements Runnable
{
    private GameServer gs;
    private UUID lastWinner;
    private UUID currentWinner;

    public ResetGame(GameServer gs, UUID lastWinner)
    {
        this.gs = gs;
        this.lastWinner = lastWinner;
    }

    @Override
    public void run() 
    {
        int score = -1;
        System.out.println("Reseting Game in 10 seconds...");

        try
        {
            Thread.sleep(10000);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }

        //After 10 seconds have passed send the reset msg to all clients... along with the current winner and the last winner
        //Get the player with the max score as the current winner
        for (UUID id : gs.clientInfo.keySet())
        {
            if (gs.clientInfo.get(id).score > score)
            {
                currentWinner = id;
                score = gs.clientInfo.get(id).score;
            }
        }

        gs.sendMsgToAll("RESETGAME," + gs.clientInfo.get(currentWinner).name + "," + gs.clientInfo.get(lastWinner).name);
        gs.playerWonThisRound = false;
        System.out.println("Game reset!");   
    }

    
}
