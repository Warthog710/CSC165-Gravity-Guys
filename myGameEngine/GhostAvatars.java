package myGameEngine;

import java.io.IOException;
import java.util.UUID;
import java.util.Vector;

import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.scene.Entity;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3f;

public class GhostAvatars
{
    protected Vector<UUID> activeGhosts;
    private SceneManager sm;

    //Class constructor
    public GhostAvatars(SceneManager sm)
    {
        this.sm = sm;
        this.activeGhosts = new Vector<>();
    }

    //Creates a ghost avatar... eventually have them choose an avatar and pass it...
    public void addGhost(UUID ghostID, Vector3f pos) throws IOException
    {
        //Create entity (cube for now)
        Entity ghostE = sm.createEntity("ghostEntity" + ghostID.toString(), "cube.obj");
        ghostE.setPrimitive(Primitive.TRIANGLES);

        //Create scenenode (hanging off root for now)
        SceneNode ghostN = sm.getRootSceneNode().createChildSceneNode("ghostNode" + ghostID.toString());
        ghostN.attachObject(ghostE);
        ghostN.setLocalScale(0.5f, 0.5f, 0.5f);

        //Set position
        ghostN.setLocalPosition(pos);

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

    public void updateGhostPosition(UUID ghostID, Vector3f pos)
    {
        sm.getSceneNode("ghostNode" + ghostID.toString()).setLocalPosition(pos);
    }
}
