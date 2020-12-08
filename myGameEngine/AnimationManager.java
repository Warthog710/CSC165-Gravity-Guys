package myGameEngine;

import ray.physics.PhysicsObject;
import ray.rage.scene.SceneNode;
import ray.rage.scene.SkeletalEntity;
import ray.rage.scene.SkeletalEntity.EndType;

public class AnimationManager 
{
	private SkeletalEntity SEntity;
	private ScriptManager scriptMan;
	private boolean isWalking, isJumping, goingDown;
	private PhysicsObject PObject;
	private SceneNode node;
	private SoundManager soundMan;
	private NetworkedClient nc;

    public AnimationManager(SkeletalEntity SEntity, PhysicsObject PObject, SceneNode node, ScriptManager scriptMan, SoundManager soundMan, NetworkedClient nc)
    {
    	this.PObject = PObject;
    	this.SEntity = SEntity;
        this.scriptMan = scriptMan;
		this.soundMan = soundMan;
		this.nc = nc;
		this.node = node;
        isWalking = false;
        isJumping = false;
        goingDown = false;
    }
    
    public void playJump() {
    	if (isJumping)
    		return;
    	//Jumping overrides walking
    	else if (isWalking) {
    		SEntity.stopAnimation();
    		isWalking = false;
    	}
    	SEntity.playAnimation(scriptMan.getValue("jumpAnimation").toString(), 0.7f, EndType.PAUSE, 0);
    	soundMan.playJump(node);
		soundMan.stopWalk(node);
		nc.isJumping = true;
    	isJumping = true;
    }
    
    public void playWalk() {
    	if (isJumping || isWalking)
    		return;
    	else if (!isWalking) {
    		SEntity.playAnimation(scriptMan.getValue("walkAnimation").toString(), 0.9f, EndType.LOOP, 0);
			soundMan.playWalk(node);
    		isWalking = true;
    	}
    }
    
    public void checkAnimations() {
    	//check if the player's walk or jump animation should end
    	checkJumping();
    	checkWalking();
    }
    
    private void checkJumping() {
    	if (PObject.getLinearVelocity()[1] < -2f) {
    		goingDown = true;
    	}
    	else if (goingDown && Math.abs(PObject.getLinearVelocity()[1]) < 0.3f) {
			SEntity.stopAnimation();
			nc.stopJump = true;
    		goingDown = false;
    		isJumping = false;
    		isWalking = false;
    	}
    }
    
    private void checkWalking() {
    	//If the player is jumping, checkJumping will handle stopping animations
    	if (isJumping)
    		return;
    	//Else, if the player is not moving fast enough along the ground or is falling, then stop the walk animation
    	else if ((Math.abs(PObject.getLinearVelocity()[0]) < 0.1f && Math.abs(PObject.getLinearVelocity()[2]) < 0.1f) || PObject.getLinearVelocity()[1] < -2f) {
    		SEntity.stopAnimation();
    		soundMan.stopWalk(node);
    		isWalking = false;
    	}
    	else
    		playWalk();
    	
    }

    
}
