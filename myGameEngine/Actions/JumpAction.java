package myGameEngine.Actions;

import ray.input.action.AbstractInputAction;
import ray.physics.PhysicsObject;
import ray.rage.scene.*;
import a3.MyGame;
import myGameEngine.*;
import net.java.games.input.Event;

public class JumpAction extends AbstractInputAction 
{
    private SceneNode target;
    private ScriptManager scriptMan;
    private AnimationManager animMan;
    private MyGame game;
    private float movementMult;
    private PhysicsManager physMan;

    public JumpAction(SceneNode target, ScriptManager scriptMan, AnimationManager animMan, MyGame game, PhysicsManager physMan) 
    {
        this.target = target;
        this.scriptMan = scriptMan;
        this.game = game;
        this.animMan = animMan;
        this.physMan = physMan;
        this.movementMult = Float.parseFloat(scriptMan.getValue("jumpMultiplier").toString());
    }

    // Move forward or backwards 5.0f every 1000ms or 1 second (assuming axis value = 1)
    public void performAction(float time, Event e) 
    {
        //Updates forward speed, if a script update occured
        if (scriptMan.scriptUpdate("movementInfo.js"))
            movementMult = Float.parseFloat(scriptMan.getValue("jumpMultiplier").toString()); 

        //If the player is currently a "child" of the moving platform... detach it...
        if (game.pc.movingWithPlatforms.getIsChild())
        {
            game.pc.movingWithPlatforms.setIsChildFalse();
            target.setLocalPosition(target.getLocalPosition().x(), target.getLocalPosition().y() + 1f, target.getLocalPosition().z());
            physMan.updatePhysicsPosition(target);

            //Set linear velocity in the Y direction to zero so the jump takes effect
            float[] temp = {target.getPhysicsObject().getLinearVelocity()[0], 0, target.getPhysicsObject().getLinearVelocity()[2]};
            target.getPhysicsObject().setLinearVelocity(temp);
        }

        PhysicsObject targ = target.getPhysicsObject();
        
        //Check if the player is on ground and can initiate a jump
        if (Math.abs(targ.getLinearVelocity()[1]) <= 0.5) 
        {
        	//Apply an upward force to do a jump
            animMan.playJump();
            targ.applyForce(0f, movementMult, 0f, 0f, 0f, 0f);
        }

        //Update height
        game.updateVerticalPosition();        
    }
}
