package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.physics.PhysicsObject;
import ray.rage.scene.*;
import ray.rage.scene.SkeletalEntity.EndType;
import ray.rml.Vector3;
import a3.MyGame;
import net.java.games.input.Event;

public class JumpAction extends AbstractInputAction 
{
    private SceneNode target;
    private NetworkedClient nc;
    private ScriptManager scriptMan;
    private AnimationManager animMan;
    private MyGame game;
    private float movementMult;

    public JumpAction(SceneNode target, NetworkedClient nc, ScriptManager scriptMan, AnimationManager animMan, MyGame game) 
    {
        this.target = target;
        this.nc = nc;
        this.scriptMan = scriptMan;
        this.game = game;
        this.animMan = animMan;
        this.movementMult = Float.parseFloat(scriptMan.getValue("jumpMultiplier").toString());
    }

    // Move forward or backwards 5.0f every 1000ms or 1 second (assuming axis value = 1)
    public void performAction(float time, Event e) 
    {
        //Updates forward speed, if a script update occured
        if (scriptMan.scriptUpdate("movementInfo.js"))
            movementMult = Float.parseFloat(scriptMan.getValue("jumpMultiplier").toString()); 

        PhysicsObject targ = target.getPhysicsObject();
        
        //Check if the player is on ground and can initiate a jump
        if (Math.abs(targ.getLinearVelocity()[1]) <= 0.5) {
        	//Apply an upward force to do a jump
        	SkeletalEntity playerSE = (SkeletalEntity) game.getEngine().getSceneManager().getEntity(scriptMan.getValue("avatarName").toString());
            //playerSE.stopAnimation();
            animMan.playJump();
        	Vector3 pos = target.getLocalPosition();
            targ.applyForce(0f, movementMult, 0f, pos.x(), pos.y(), pos.z());
        }

        //Update height
        game.updateVerticalPosition();
        
        nc.updatePositionOnServer = true;
    }
}
