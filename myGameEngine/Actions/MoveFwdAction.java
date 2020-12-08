package myGameEngine.Actions;

import ray.input.action.AbstractInputAction;
import ray.physics.PhysicsObject;
import ray.rage.scene.*;
import ray.rml.Vector3;
import ray.rml.Vector3f;
import a3.MyGame;
import myGameEngine.*;
import net.java.games.input.Event;

public class MoveFwdAction extends AbstractInputAction 
{
    private SceneNode target;
    private ScriptManager scriptMan;
    private AnimationManager animMan;
    private MyGame game;
    private float movementMult;

    public MoveFwdAction(SceneNode target, ScriptManager scriptMan, AnimationManager animMan, MyGame game) 
    {
        this.target = target;
        this.scriptMan = scriptMan;
        this.animMan = animMan;
        this.game = game;
        this.movementMult = Float.parseFloat(scriptMan.getValue("forwardMovementMultiplier").toString());
    }

    // Move forward or backwards 5.0f every 1000ms or 1 second (assuming axis value = 1)
    public void performAction(float time, Event e) 
    {        
    	float keyValue = e.getValue();
        // Deadzone
        if (keyValue > -.2 && keyValue < .2) {
        	return;
        }
        if (e.getComponent().getIdentifier() == net.java.games.input.Component.Identifier.Key.W) {
        	keyValue = -keyValue;
		}

        //Updates forward speed, if a script update occured
        if (scriptMan.scriptUpdate("movementInfo.js"))
            movementMult = Float.parseFloat(scriptMan.getValue("forwardMovementMultiplier").toString()); 
        
        //Get the physics object of the node and apply a forward/backward force
        PhysicsObject targ = target.getPhysicsObject();
        Vector3 forward = target.getLocalForwardAxis().mult(-time * keyValue * movementMult);
        //Vector3 pos = target.getLocalPosition();

        //If the new vector collides with a wall don't move
        if (game.collision.wallCollision((Vector3f)forward))
            return;

        targ.applyForce(forward.x(), forward.y(), forward.z(), 0f, 0f, 0f);
        
        animMan.playWalk();       
        
        //Update height
        game.updateVerticalPosition();        
    }
}
