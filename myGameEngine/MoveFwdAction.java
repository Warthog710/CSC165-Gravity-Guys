package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import net.java.games.input.Event;

public class MoveFwdAction extends AbstractInputAction 
{
    private SceneNode target;
    private NetworkedClient nc;

    public MoveFwdAction(SceneNode target, NetworkedClient nc) 
    {
        this.target = target;
        this.nc = nc;
    }

    // Move forward or backwards 5.0f every 1000ms or 1 second (assuming axis value = 1)
    public void performAction(float time, Event e) 
    {
        // Deadzone
        if (e.getValue() > -.2 && e.getValue() < .2)
            return;

        //Move forward .005f units every 1ms
        target.moveForward(-(time * e.getValue()) / 200);

        //Check if dolphin left the world plane...
        if (Math.abs(target.getLocalPosition().x()) > 50 || Math.abs(target.getLocalPosition().z()) > 50)
        {
            //Don't make the move
            target.moveForward((time * e.getValue()) / 200);
        }

        //Tell the networked client that an update is required
        nc.updatePositionOnServer = true;
    }
}
