package myGameEngine;

import ray.physics.PhysicsObject;
import ray.rage.scene.SkeletalEntity;
import ray.rage.scene.SkeletalEntity.EndType;

public class AnimationManager 
{
	private SkeletalEntity SEntity;
	private ScriptManager scriptMan;
	private boolean isWalking, isJumping, goingDown;
	private PhysicsObject PObject;
	private SoundManager soundMan;

    public AnimationManager(SkeletalEntity SEntity, PhysicsObject PObject, ScriptManager scriptMan, SoundManager soundMan)
    {
    	this.PObject = PObject;
    	this.SEntity = SEntity;
        this.scriptMan = scriptMan;
        this.soundMan = soundMan;
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
    	soundMan.playJump();
    	isJumping = true;
    }
    
    public void playWalk() {
    	if (isJumping || isWalking)
    		return;
    	else if (!isWalking) {
    		SEntity.playAnimation(scriptMan.getValue("walkAnimation").toString(), 0.9f, EndType.LOOP, 0);
    		soundMan.playWalk();
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
    		goingDown = false;
    		isJumping = false;
    	}
    }
    
    private void checkWalking() {
    	//If the player is jumping, checkJumping will handle stopping animations
    	if (isJumping)
    		return;
    	//Else, if the player is not moving fast enough along the ground, then stop the walk animation
    	else if (Math.abs(PObject.getLinearVelocity()[0]) < 0.1f && Math.abs(PObject.getLinearVelocity()[2]) < 0.1f) {
    		SEntity.stopAnimation();
    		soundMan.stopWalk();
    		isWalking = false;
    	}
    }
    

    
}
