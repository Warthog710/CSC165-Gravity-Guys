package myGameEngine;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.scene.SkeletalEntity;
import ray.rml.Matrix3f;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class GhostAvatars
{
    protected Vector<UUID> activeGhosts;
    protected HashMap<UUID, Vector3f> previousPos;
    protected HashMap<UUID, GhostAvatarAnimationManager> ghostAnim;
    private SceneManager sm;
    private SoundManager soundMan;
    
    //Class constructor
    public GhostAvatars(SceneManager sm)
    {
        this.sm = sm;
        this.activeGhosts = new Vector<>();
        this.ghostAnim = new HashMap<>();
    }
    
    //Add the sound manager to be passed to the GhostAvatarAnimationManager
    public void addSoundManager(SoundManager soundMan) {
    	this.soundMan = soundMan;
    }

    //Creates a ghost avatar... eventually have them choose an avatar and pass it...
    public void addGhost(UUID ghostID, Vector3f pos, Matrix3f rotation, Texture texture) throws IOException
    {
        //Create entity (cube for now)
        SkeletalEntity ghostE = sm.createSkeletalEntity("ghostEntity" + ghostID.toString(), "player.rkm", "player.rks");
        TextureState tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		tstate.setTexture(texture);
        ghostE.setRenderState(tstate);
        
        ghostE.loadAnimation("ghostJump" + ghostID.toString(), "jump.rka");
        ghostE.loadAnimation("ghostWalk" + ghostID.toString(), "newWalk.rka");   

        //Create scenenode (hanging off root for now)
        SceneNode ghostN = sm.getRootSceneNode().createChildSceneNode("ghostNode" + ghostID.toString());
        ghostN.attachObject(ghostE);
        ghostN.setLocalScale(0.25f, 0.25f, 0.25f);

        //Set position & rotation
        ghostN.setLocalPosition(pos);
        ghostN.setLocalRotation(rotation);
        
        //Setup animation manager for that specific ghost
        ghostAnim.put(ghostID, new GhostAvatarAnimationManager(ghostE, ghostN, ghostID, soundMan));  

        //Add to active ghosts
        activeGhosts.add(ghostID);
    }
    
    //Removes a ghost from the world
    public void removeGhost(UUID ghostID)
    {
    	//Remove sound object
        soundMan.removeGhost(sm.getSceneNode("ghostNode" + ghostID.toString()));
        
        sm.destroySceneNode("ghostNode" + ghostID.toString());

        //Delete from active ghosts
        activeGhosts.remove(ghostID);
        
        System.out.println("Deleted ghost avatar " + ghostID);
    }

    public void updateGhostPosition(UUID ghostID, Vector3f pos, Matrix3f rotation)
    {
        //Update previous pos with previous pos
        Vector3 delta = pos.sub(sm.getSceneNode("ghostNode" + ghostID.toString()).getLocalPosition());
        double distance = Math.sqrt(delta.x() * delta.x() + delta.z() * delta.z());
        float verticalSpeed = pos.y() - sm.getSceneNode("ghostNode" + ghostID.toString()).getLocalPosition().y();

        //If not enough change stop the walking animation
        if (Math.abs(distance) < .03f || verticalSpeed < -.2f)
        {
            ghostAnim.get(ghostID).isWalking = false;

            //If not jumping... stop animation and walking noise
            if (!ghostAnim.get(ghostID).isJumping) {
            	ghostAnim.get(ghostID).ghost.stopAnimation();
            	soundMan.stopWalk(sm.getSceneNode("ghostNode" + ghostID.toString()));
            }
                
        }
        //Play the walk animation if the change is big enough
        else
            ghostAnim.get(ghostID).playWalk();


        //Update pos and rot
        sm.getSceneNode("ghostNode" + ghostID.toString()).setLocalPosition(pos);
        sm.getSceneNode("ghostNode" + ghostID.toString()).setLocalRotation(rotation);
    }

    public void jumpGhost(UUID ghostID)
    {
        //Only if the ghost exists
        if (activeGhosts.contains(ghostID))
        {
            ghostAnim.get(ghostID).playJump();
        }
    }

    public void stopGhostJump(UUID ghostID)
    {
        //Only if the ghost
        if (activeGhosts.contains(ghostID))
        {
            ghostAnim.get(ghostID).stopJump();
        }
    }

    public void update()
    {
        for (UUID id : activeGhosts)
        {
            try
            {
                ghostAnim.get(id).update();
            }
            catch (Exception e)
            {
                //Do nothing...
                //Sometimes an exception occurs when the game shuts down
                //due to ongoing updates to the skeleton... Just ignore those for now
                //I know this is a bad way to handle it!!!
            }
        }
    }
}
