package myGameEngine;

import ray.rage.scene.SceneNode;
import ray.rml.Matrix3f;
import ray.rml.Vector3f;

public class WinCondition 
{
    private int score;
    private SceneNode avatarNode;
    private NetworkedClient nc;
    private ScriptManager scriptMan;
    private PhysicsManager physMan;
    private float minX, maxX, minY, maxY, minZ, maxZ;
    private String winningPlayer, lastWinner;
    private float timeSinceWin;

    private boolean playerAtFinish;

    public WinCondition(SceneNode avatarNode, ScriptManager scriptMan, NetworkedClient nc, PhysicsManager physMan)
    {
        this.score = 0;
        this.playerAtFinish = false;
        this.avatarNode = avatarNode;
        this.nc = nc;
        this.scriptMan = scriptMan;
        this.physMan = physMan;
        this.timeSinceWin = 0;
        this.winningPlayer = "";
        this.lastWinner = "";

        String name = scriptMan.getValue("finishPlatPhysicsPlane").toString();
        Vector3f pos = (Vector3f)scriptMan.getValue(name + "Pos");
        Vector3f scale = (Vector3f)scriptMan.getValue(name + "Scale");       

        //For detecting if the player is on the finish platform
        minX = pos.x() - scale.x();
        maxX = pos.x() + scale.x();
        minY = pos.y() - (scale.y() + 1);
        maxY = pos.y() + (scale.y() + 1);
        minZ = pos.z() - scale.z();
        maxZ = pos.z() + scale.z();
    }

    public void update(float timeElapsed)
    {
        //If the player has not reached the finish yet...
        if (!playerAtFinish)
        {
            Vector3f pos = (Vector3f)avatarNode.getLocalPosition();

            if (pos.x() >= minX && pos.x() <= maxX && pos.y() >= minY && pos.y() <= maxY && pos.z() >= minZ && pos.z() <= maxZ)
            {
                System.out.println("Player reached finish");
                playerAtFinish = true;
                score++;
                

                //If connected to a server... send the win message
                if (nc.isConnected)
                    nc.playerWon = true;
            }
        }

        if (playerAtFinish)
        {
            timeSinceWin += timeElapsed;

            //If the client is not connected to the server reset after 10 seconds
            if (!nc.isConnected && timeSinceWin >= 10000)
            {
                winningPlayer = "You!";
                lastWinner = "You!";

                resetGame();
            }            
        }
    }

    //Increments the score, called by networked client when a "FIRSTWINNER" msg is recieved so the first winner gets two points
    public void incrementScore()
    {
        score++;
    }

    //Returns a string to be used on the HUD
    public String getHudString()
    {
        return ("Score: " + score + "     Winning Player: " + winningPlayer + "     Last Winner: " + lastWinner);
    }

    //Sets the last and current winner
    public void setWinners(String winningPlayer, String lastWinner)
    {
        this.winningPlayer = winningPlayer;
        this.lastWinner = lastWinner;
    }

    //Called by networked client when a "RESETGAME" message is recieved
    public void resetGame()
    {
        //Reset position
        avatarNode.setLocalPosition((Vector3f)scriptMan.getValue("avatarPos"));

        //Reset orientation
        float[] rot = { 1, 0, 0, 0, 1, 0, 0, 0, 1};
        avatarNode.setLocalRotation(Matrix3f.createFrom(rot));
        
        //Update physics transforms
        physMan.updatePhysicsTransforms(avatarNode);

        //Reset ability to win
        this.playerAtFinish = false;
        this.timeSinceWin = 0;
    }
}
