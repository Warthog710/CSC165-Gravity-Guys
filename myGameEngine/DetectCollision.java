package myGameEngine;

import java.util.Iterator;

import ray.rage.scene.Node;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.*;

//This class is able to detect collision on a perfect sphere ONLY!!!
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

    public static boolean towerCollisions(Vector3f dolphinPos)
    {
        //Check the 4 tower feet
        Vector3f footOne = (Vector3f)Vector3f.createFrom(-3.14f, 0f, -3.07f);
        Vector3f footTwo = (Vector3f)Vector3f.createFrom(-3.14f, 0f, 3.07f);
        Vector3f footThree = (Vector3f)Vector3f.createFrom(3.14f, 0f, 3.07f);
        Vector3f footFour = (Vector3f)Vector3f.createFrom(3.14f, 0f, -3.07f);

        float footRadius = 1.3f;

        if (ObjectDistance.distanceBetweenVectors(dolphinPos, footOne) < footRadius)
            return true;

        if (ObjectDistance.distanceBetweenVectors(dolphinPos, footTwo) < footRadius)
            return true;

        if (ObjectDistance.distanceBetweenVectors(dolphinPos, footThree) < footRadius)
            return true;

        if (ObjectDistance.distanceBetweenVectors(dolphinPos, footFour) < footRadius)
            return true;


        return false;
    }

    //Checks for collisions between the lasers and carried planets
    public static boolean carriedPlanetCollisions(SceneNode dolphinNode, Vector3f laserPos)
    {
        //Check planets being carried
        Iterator<Node> myNodes = dolphinNode.getChildNodes().iterator();

        //Only if a child exists...
        while (myNodes.hasNext())
        {
            Node temp = myNodes.next();

            //If its a planet... (could be: planet, light, or vertical line)
            if (temp.getName().contains("planet"))
            {                
                if (ObjectDistance.distanceBetweenVectors((Vector3f)temp.getWorldPosition(), laserPos) < 2.3f * temp.getLocalScale().x())
                    return true;
            }
        }
        
        return false;
    }
}
