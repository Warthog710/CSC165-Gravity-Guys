package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import a3.MyGame;
import net.java.games.input.Event;

public class MoveRightAction extends AbstractInputAction 
{
    private SceneNode target;
    private NetworkedClient nc;
    private ScriptManager scriptMan;
    private PhysicsManager physMan;
    private MyGame game;
    private float movementMult;

    public MoveRightAction(SceneNode target, NetworkedClient nc, ScriptManager scriptMan, PhysicsManager physMan, MyGame game) 
    {
        this.target = target;
        this.nc = nc;
        this.scriptMan = scriptMan;
        this.game = game;
        this.physMan = physMan;
        this.movementMult = Float.parseFloat(scriptMan.getValue("horizontalMovementMultiplier").toString());
    }

    // Move left or right 5.0f every 1000ms or 1 second (assuming axis value = 1)
    public void performAction(float time, Event e) 
    {
        // Deadzone
        if (e.getValue() > -.2 && e.getValue() < .2)
            return;

        //Updates horizontal speed, if a script update occured
        if (scriptMan.scriptUpdate("movementInfo.js"))
            movementMult = Float.parseFloat(scriptMan.getValue("horizontalMovementMultiplier").toString()); 

        //Move right .005f units every 1ms
        target.moveRight(-(time * e.getValue()) * movementMult / 200);

        //Update physics world
        physMan.updatePhysicsTransforms(target);

        //Update avatar vertical position
        game.updateVerticalPosition();

        //Tell the networked client that an update is required
        nc.updatePositionOnServer = true;
    }
}