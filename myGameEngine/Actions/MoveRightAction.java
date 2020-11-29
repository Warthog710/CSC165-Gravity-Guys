package myGameEngine.Actions;

import ray.input.action.AbstractInputAction;
import ray.physics.PhysicsObject;
import ray.rage.scene.*;
import ray.rml.Vector3;
import a3.MyGame;
import myGameEngine.*;
import net.java.games.input.Event;

public class MoveRightAction extends AbstractInputAction 
{
    private SceneNode target;
    private NetworkedClient nc;
    private ScriptManager scriptMan;
    private AnimationManager animMan;
    private MyGame game;
    private float movementMult;

    public MoveRightAction(SceneNode target, NetworkedClient nc, ScriptManager scriptMan, AnimationManager animMan, MyGame game) 
    {
        this.target = target;
        this.nc = nc;
        this.scriptMan = scriptMan;
        this.game = game;
        this.animMan = animMan;
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

        //Get the physics object of the node and apply a right/left force
        PhysicsObject targ = target.getPhysicsObject();
        Vector3 right = target.getLocalRightAxis().mult(-time * e.getValue() * movementMult);
        //Vector3 pos = target.getLocalPosition();
        targ.applyForce(right.x(), right.y(), right.z(), 0f, 0f, 0f);
        
        animMan.playWalk();
        
        //Update avatar vertical position
        game.updateVerticalPosition();

        //Tell the networked client that an update is required
        nc.updatePositionOnServer = true;
    }
}