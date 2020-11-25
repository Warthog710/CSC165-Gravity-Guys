package myGameEngine;

import java.io.IOException;
import java.util.UUID;
import java.util.Vector;

import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.scene.SkeletalEntity;
import ray.rml.Matrix3f;
import ray.rml.Vector3f;

public class GhostAvatars
{
    protected Vector<UUID> activeGhosts;
    private SceneManager sm;
    private Texture ghostTex;

    //TODO: Ghost avatars need animations

    //Class constructor
    public GhostAvatars(SceneManager sm)
    {
        this.sm = sm;
        this.activeGhosts = new Vector<>();

        try 
        {
            this.ghostTex = sm.getTextureManager().getAssetByPath("newPlayer.png");
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    //Creates a ghost avatar... eventually have them choose an avatar and pass it...
    public void addGhost(UUID ghostID, Vector3f pos, Matrix3f rotation) throws IOException
    {
        //Create entity (cube for now)
        SkeletalEntity ghostE = sm.createSkeletalEntity("ghostEntity" + ghostID.toString(), "player.rkm", "player.rks");
        TextureState tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		tstate.setTexture(ghostTex);
	    ghostE.setRenderState(tstate);

        //Create scenenode (hanging off root for now)
        SceneNode ghostN = sm.getRootSceneNode().createChildSceneNode("ghostNode" + ghostID.toString());
        ghostN.attachObject(ghostE);
        ghostN.setLocalScale(0.25f, 0.25f, 0.25f);

        //Set position & rotation
        ghostN.setLocalPosition(pos);
        ghostN.setLocalRotation(rotation);

        //Add to active ghosts
        activeGhosts.add(ghostID);
    }
    
    //Removes a ghost from the world
    public void removeGhost(UUID ghostID)
    {
        sm.destroySceneNode("ghostNode" + ghostID.toString());
        sm.destroyEntity("ghostEntity" + ghostID.toString());

        //Delete from active ghosts
        activeGhosts.remove(ghostID);

        System.out.println("Deleted ghost avatar " + ghostID);
    }

    public void updateGhostPosition(UUID ghostID, Vector3f pos, Matrix3f rotation)
    {
        sm.getSceneNode("ghostNode" + ghostID.toString()).setLocalPosition(pos);
        sm.getSceneNode("ghostNode" + ghostID.toString()).setLocalRotation(rotation);
    }
}
