package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import net.java.games.input.Event;

public class MoveFwdAction extends AbstractInputAction 
{
    private SceneNode target;
    private NetworkedClient nc;
    private ScriptManager scriptMan;

    public MoveFwdAction(SceneNode target, NetworkedClient nc, ScriptManager scriptMan) 
    {
        this.target = target;
        this.nc = nc;
        this.scriptMan = scriptMan;
    }

    // Move forward or backwards 5.0f every 1000ms or 1 second (assuming axis value = 1)
    public void performAction(float time, Event e) 
    {
        // Deadzone
        if (e.getValue() > -.2 && e.getValue() < .2)
            return;

        //Get movement multiplier
        float movementMult = Float.parseFloat(scriptMan.getValue("movementInfo.js", "forwardMovementMultiplier").toString());

        //Move forward .005f units every 1ms
        target.moveForward(-(time * e.getValue()) * movementMult / 200);

        //Tell the networked client that an update is required
        nc.updatePositionOnServer = true;
    }
}
