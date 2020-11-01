package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import a3.MyGame;
import net.java.games.input.Event;

public class MoveRightAction extends AbstractInputAction 
{
    private SceneNode target;
    private NetworkedClient nc;
    private MyGame game;

    public MoveRightAction(SceneNode target, NetworkedClient nc, MyGame game) 
    {
        this.target = target;
        this.nc = nc;
        this.game = game;
    }

    // Move left or right 5.0f every 1000ms or 1 second (assuming axis value = 1)
    public void performAction(float time, Event e) 
    {
        // Deadzone
        if (e.getValue() > -.2 && e.getValue() < .2)
            return;

        //Move right .005f units every 1ms
        target.moveRight(-(time * e.getValue()) / 200);
        game.updateVerticalPosition();
        
        //Check if dolphin left the world plane...
        if (Math.abs(target.getLocalPosition().x()) > 50 || Math.abs(target.getLocalPosition().z()) > 50)
        {
            //Don't make the move
            target.moveRight((time * e.getValue()) / 200);
        }

        //Tell the networked client that an update is required
        nc.updatePositionOnServer = true;
    }
}