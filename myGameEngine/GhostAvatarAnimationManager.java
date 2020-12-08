package myGameEngine;

import java.util.UUID;

import ray.rage.scene.SceneNode;
import ray.rage.scene.SkeletalEntity;
import ray.rage.scene.SkeletalEntity.EndType;

public class GhostAvatarAnimationManager 
{
    protected SkeletalEntity ghost;
    private UUID ghostId;
    protected boolean isJumping, isWalking;
    private SoundManager soundMan;
    private SceneNode ghostN;

    public GhostAvatarAnimationManager(SkeletalEntity ghost, SceneNode ghostN, UUID ghostId, SoundManager soundMan)
    {
        this.ghost = ghost;
        this.ghostN = ghostN;
        this.ghostId = ghostId;
        this.isJumping = false;
        this.isWalking = false;
        this.soundMan = soundMan;
        soundMan.addGhost(ghostN);
    }

    //Plays jump for the ghost avatar
    public void playJump()
    {
        if (isJumping)
            return;

        //If the avatar is walking
        if (isWalking)
        {
            ghost.stopAnimation();
            isWalking = false;
        }

        //Play the jump animation
        ghost.playAnimation("ghostJump" + ghostId, .9f, EndType.PAUSE, 0);
        soundMan.playJump(ghostN);
		soundMan.stopWalk(ghostN);
        isJumping = true;
    }

    //Stops the jump for the ghost avatar
    public void stopJump()
    {
        ghost.stopAnimation();
        isJumping = false;
        isWalking = false;
    }

    public void playWalk()
    {
        if (isJumping || isWalking)
            return;

        ghost.playAnimation("ghostWalk" + ghostId, .9f, EndType.LOOP, 0);
        soundMan.playWalk(ghostN);
        isWalking = true;
    }

    public void update()
    {
        ghost.update();
    }      
}
