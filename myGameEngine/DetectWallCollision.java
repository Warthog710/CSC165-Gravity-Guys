package myGameEngine;

import java.util.Vector;

import ray.rage.scene.SceneNode;
import ray.rml.Vector3f;

//Adds a bit of additional functionality to the built-in orbit controller
public class DetectWallCollision
{
    Vector<SceneNode> wallList;


    public DetectWallCollision(Vector<SceneNode> wallList)
    {
        this.wallList = wallList;
    } 

    public boolean wallCollision(Vector3f pos)
    {

        for (SceneNode node : wallList)
        {
            float minX = node.getLocalPosition().x() - node.getLocalScale().x();
            float maxX = node.getLocalPosition().x() + node.getLocalScale().x();
            float minY = node.getLocalPosition().y() - node.getLocalScale().y();
            float maxY = node.getLocalPosition().y() + node.getLocalScale().y();
            float minZ = node.getLocalPosition().z() - node.getLocalScale().z();
            float maxZ = node.getLocalPosition().z() + node.getLocalScale().z();

            //What... it works? Just don't look too close...
            if (pos.x() >= minX && pos.x() <= maxX && pos.y() >= minY && pos.y() <= maxY && pos.z() >= minZ && pos.z() <= maxZ)
                return true;
        }     
        return false;
    }    
}
