package myGameEngine;

import java.util.Vector;

import ray.rage.scene.SceneNode;
import ray.rml.Vector3f;

public class MovingWithPlatforms 
{
    private PhysicsManager physMan;
    private NetworkedClient nc;
    private Vector<SceneNode> platforms = new Vector<>();
    private SceneNode avatarNode;
    private String parentName;

    private boolean isChild = false;

    public MovingWithPlatforms(PhysicsManager physMan, NetworkedClient nc, SceneNode avatarNode)
    {
        this.physMan = physMan;
        this.nc = nc;
        this.avatarNode = avatarNode;
    }

    public void addPlatforms (Vector<SceneNode> platforms)
    {
        this.platforms = platforms;
    }

    public void moveWithPlatforms()
    {
        Vector3f pos = (Vector3f)avatarNode.getLocalPosition();

        //Check to see if the player is "colliding" with a bounding box above the platform
        for (SceneNode node : platforms)
        {
            float minX = node.getLocalPosition().x() - node.getLocalScale().x();
            float maxX = node.getLocalPosition().x() + node.getLocalScale().x();
            float minY = node.getLocalPosition().y() - node.getLocalScale().y();
            float maxY = node.getLocalPosition().y() + node.getLocalScale().y();
            float minZ = node.getLocalPosition().z() - node.getLocalScale().z();
            float maxZ = node.getLocalPosition().z() + node.getLocalScale().z();


            //Collission with the platform... Avatar should start moving with it... attach as a pseudo child
            if (pos.x() >= minX && pos.x() <= maxX && pos.y() >= minY && pos.y() <= maxY && pos.z() >= minZ && pos.z() <= maxZ)
            {
                if (isChild)
                {
                    avatarNode.setLocalPosition(avatarNode.getLocalPosition().x(), maxY, avatarNode.getLocalPosition().z());
                    nc.updatePositionOnServer = true;
                }

                if (!isChild)
                {
                    parentName = node.getName();
                    isChild = true;  
                    avatarNode.setLocalPosition(avatarNode.getLocalPosition().x(), maxY, avatarNode.getLocalPosition().z());

                    float[] temp = {avatarNode.getPhysicsObject().getLinearVelocity()[0], 0, avatarNode.getPhysicsObject().getLinearVelocity()[2]};
                    avatarNode.getPhysicsObject().setLinearVelocity(temp);
                    physMan.updatePhysicsPosition(avatarNode);
                    nc.updateOrientationOnServer = true;
                }      

                return;
            }        
        }

        if (isChild)
            isChild = false;
    }
    
    public boolean getIsChild()
    {
        return isChild;
    }

    public void setIsChildFalse()
    {
        isChild = false;
    }

    public String getParentName()
    {
        return parentName;
    }
}
