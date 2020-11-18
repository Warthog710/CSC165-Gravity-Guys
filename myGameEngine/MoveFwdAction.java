package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.physics.PhysicsObject;
import ray.rage.scene.*;
import ray.rml.Vector3;
import a3.MyGame;
import net.java.games.input.Event;

public class MoveFwdAction extends AbstractInputAction 
{
    private SceneNode target;
    private NetworkedClient nc;
    private ScriptManager scriptMan;
    private PhysicsManager physMan;
    private MyGame game;
    private float movementMult;

    public MoveFwdAction(SceneNode target, NetworkedClient nc, ScriptManager scriptMan, PhysicsManager physMan, MyGame game) 
    {
        this.target = target;
        this.nc = nc;
        this.scriptMan = scriptMan;
        this.game = game;
        this.physMan = physMan;
        this.movementMult = Float.parseFloat(scriptMan.getValue("forwardMovementMultiplier").toString());
    }

    // Move forward or backwards 5.0f every 1000ms or 1 second (assuming axis value = 1)
    public void performAction(float time, Event e) 
    {
        // Deadzone
        if (e.getValue() > -.2 && e.getValue() < .2)
            return;

        //Updates forward speed, if a script update occured
        if (scriptMan.scriptUpdate("movementInfo.js"))
            movementMult = Float.parseFloat(scriptMan.getValue("forwardMovementMultiplier").toString()); 
        
        //Get the physics object of the node and apply a forward/backward force
        PhysicsObject targ = target.getPhysicsObject();
        Vector3 forward = target.getLocalForwardAxis().mult(-time * e.getValue() * movementMult);
        Vector3 pos = target.getLocalPosition();
        targ.applyForce(forward.x(), forward.y(), forward.z(), 
        		pos.x(), pos.y(), pos.z());

        //target.getPhysicsObject().applyForce(10, 0, 0, pos.x(), pos.y(), pos.z());

        //Update height
        game.updateVerticalPosition();
        
        nc.updatePositionOnServer = true;
    }
}
