package myGameEngine;

import java.util.Iterator;

import ray.rage.scene.Node;
import ray.rage.scene.SceneManager;
import ray.rml.*;

//! This class is able to detect collision on a perfect sphere ONLY!!!
public class DetectCollision 
{
    public static Node planetCollisions(SceneManager sm, Vector3f newPos) 
    {
        //Distance where collision occurs in an earth.obj
        float colSphere = 2.0f;

        //Grab an iterator of all planet nodes
        Iterator<Node> myNodes = sm.getSceneNode("planetGroup").getChildNodes().iterator();

        //Iterate through all nodes
        while (myNodes.hasNext())
        {
            Node temp = myNodes.next();

            //Verify its a planet node
            if (temp.getName().contains("planet"))
            {
                // If the new position is within a certain range of the object...
                if (ObjectDistance.distanceBetweenVectors(newPos, (Vector3f) temp.getLocalPosition()) < colSphere * temp.getLocalScale().x())
                    return temp;

            }
        }
        return null;
    }
}
