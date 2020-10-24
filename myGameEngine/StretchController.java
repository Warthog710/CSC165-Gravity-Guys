package myGameEngine;

import ray.rage.scene.controllers.*;
import ray.rage.scene.*;
import ray.rml.*;

//Streches all child nodes of the assigned node in alternating X and Y directions using scale
public class StretchController extends AbstractController
{
    private float scaleRate = .0015f;
    private float cycleTime = 1000.0f;
    private float totalTime = 0.0f;
    private int direction = 1;

    @Override
    public void updateImpl(float elapsedTimeMillis)
    {
        totalTime += elapsedTimeMillis;
        float scaleAmt;

        //scaleAmt = scaleRate;

        //If half of the cycle time has passed scale negatively
        if (totalTime >= (cycleTime / 2))
            scaleAmt = -1.0f * scaleRate;
        else
            scaleAmt = 1.0f * scaleRate;

        //If the cycle time has passed, flip direction and restart the timer
        if (totalTime > cycleTime)
        {
            direction = -direction;
            totalTime = 0.0f;
        }

        //Cycle through all nodes and apply the modifier
        for (Node myNode : super.controlledNodesList)
        {
            Vector3 curScale = myNode.getLocalScale();

            //If direction > 1 scale in the x direction
            if (direction >= 1)
                curScale = Vector3f.createFrom(curScale.x() + scaleAmt, curScale.y(), curScale.z());
            else
                curScale = Vector3f.createFrom(curScale.x(), curScale.y() + scaleAmt, curScale.z());                        
            
            myNode.setLocalScale(curScale);
        }
    }    
}
