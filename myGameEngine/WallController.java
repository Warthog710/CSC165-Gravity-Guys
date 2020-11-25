package myGameEngine;

import java.util.Vector;

import ray.rage.scene.controllers.*;
import ray.rage.scene.*;
import ray.rml.*;

//TODO: Make the server control the walls if the client is connected to one
//IDEA: May just make the server send a sync msg when a new client joins?

//Streches all child nodes of the assigned node in alternating X and Y directions using scale
public class WallController extends AbstractController
{
    private PhysicsManager physMan;
    private ScriptManager scriptMan;
    private float cycleTime, totalTime = 0, speed;
    private int moveDir = 1, direction = 0, valueDir = 0, value = 0, offset = 0;

    public WallController(PhysicsManager physMan, ScriptManager scriptMan)
    {
        this.physMan = physMan;
        this.scriptMan = scriptMan; 
        
        //Get initial cycle and speed values
        cycleTime = Float.parseFloat(scriptMan.getValue("wallCycleTime").toString());
        speed = Float.parseFloat(scriptMan.getValue("wallSpeed").toString());
    }

    public void addNodeList(Vector<SceneNode>nodeList)
    {
        for (SceneNode node : nodeList)
        {
            addNode(node);
        }
    }

    @Override
    public void updateImpl(float elapsedTimeMillis)
    {
        totalTime += elapsedTimeMillis;
        int count = 0;

        //If a script update has occured update the related variables
        if (scriptMan.scriptUpdate("movementInfo.js"))
        {
            cycleTime = Float.parseFloat(scriptMan.getValue("wallCycleTime").toString());
            speed = Float.parseFloat(scriptMan.getValue("wallSpeed").toString());
        }

        if (totalTime >= cycleTime)
        {
            //Swap the direction
            totalTime = 0;
            moveDir = moveDir * -1;
            direction++;
        }

        if (direction == 2)
        {
            //The wall must've returned to its original position
            direction = 0;
            valueDir++;

            //Reset all walls to original pos (prevents wall drift)
            for (Node node : super.controlledNodesList)
            {
                Vector3f sPos = ((Vector3f)scriptMan.getValue("wallStartingPos"));
                sPos = (Vector3f)Vector3f.createFrom(sPos.x(), sPos.y(), sPos.z() + offset);
                offset += Integer.parseInt(this.scriptMan.getValue("offset").toString());
                node.setLocalPosition(sPos);
            }

            //Reset offset for next reset
            offset = 0;
            
            //If this code has executed twice, change the value so that all walls reverse directions
            if (valueDir == 2)
            {
                valueDir = 0;
                value = 0;
            }
            else
            {
                value = 1;
            }
        }

        //Iterate through all walls and move
        for (Node node : super.controlledNodesList)
        {
            if (count % 2 == value)
            {
                Vector3f currentPos = (Vector3f)node.getLocalPosition();
                currentPos = (Vector3f)Vector3f.createFrom(currentPos.x() + speed * elapsedTimeMillis * moveDir, currentPos.y(), currentPos.z());
                node.setLocalPosition(currentPos);
            }
            else
            {
                Vector3f currentPos = (Vector3f)node.getLocalPosition();
                currentPos = (Vector3f)Vector3f.createFrom(currentPos.x() + speed * elapsedTimeMillis * -moveDir, currentPos.y(), currentPos.z());
                node.setLocalPosition(currentPos);        
            } 

            physMan.updatePhysicsPosition(node);
            count++;
        } 
    } 
    
    public void reset() 
    {
        //Reset all internal variables to default
        cycleTime = 2000f; //!Get from script
        totalTime = 0;
        speed = .001f; //!Get from script
        moveDir = 1;
        direction = 0;
        valueDir = 0;
        value = 0;
        offset = 0;
    }
}
