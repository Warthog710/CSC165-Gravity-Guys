package myGameEngine;

import ray.rage.scene.controllers.*;
import ray.rage.scene.*;
import ray.rml.*;

//Streches all child nodes of the assigned node in alternating X and Y directions using scale
public class HeightController extends AbstractController
{
    private float movementSpeed = 0.005f;
    private float timeElapsed = 0.0f;
    private float cycleTime = 1000f;
    

    //Make an node jump up and down
    @Override
    public void updateImpl(float elapsedTimeMillis)
    {
        timeElapsed += elapsedTimeMillis;

        //If the time has elapsed... reverse the direction
        if (timeElapsed > cycleTime)
        {
            movementSpeed = -movementSpeed;
            timeElapsed = 0.0f;
        }


        //Iterate through all assigned nodes
        for (Node myNode : super.controlledNodesList)
        {
            Vector3 currentPos = myNode.getLocalPosition();
            Vector3f newPos = (Vector3f)Vector3f.createFrom(currentPos.x(), currentPos.y() + movementSpeed, currentPos.z());
            myNode.setLocalPosition(newPos);            
        }
    }    
}
