package myGameEngine;

import java.util.Vector;

import ray.rage.scene.*;
import ray.rml.*;

//Moves all nodes left, then right... or vice versa
public class PlatformController
{
    private PhysicsManager physMan;
    private ScriptManager scriptMan;
    private SceneNode avatarNode;
    private NetworkedClient nc;
    private Vector<SceneNode> controlledNodesList;
    private float cycleTime, totalTime = 0, speed;
    private int moveDir = 1, direction = 0, valueDir = 0, value = 0;

    public MovingWithPlatforms movingWithPlatforms;

    public PlatformController(PhysicsManager physMan, ScriptManager scriptMan, NetworkedClient nc, SceneNode avatarNode)
    {
        this.physMan = physMan;
        this.scriptMan = scriptMan; 
        this.avatarNode = avatarNode;
        this.nc = nc;
        this.movingWithPlatforms = new MovingWithPlatforms(physMan, nc, avatarNode);
        
        //Get initial cycle and speed values
        cycleTime = Float.parseFloat(scriptMan.getValue("platformCycleTime").toString());
        speed = Float.parseFloat(scriptMan.getValue("platformSpeed").toString());
    }

    public void addNodeList(Vector<SceneNode>nodeList)
    {
        controlledNodesList = nodeList;
        movingWithPlatforms.addPlatforms(nodeList);
    }

    public void update(float elapsedTimeMillis)
    {
        totalTime += elapsedTimeMillis;
        int count = 0;

        //Perform collision detection on the platforms
        movingWithPlatforms.moveWithPlatforms();

        //If a script update has occured update the related variables
        if (scriptMan.scriptUpdate("movementInfo.js"))
        {
            cycleTime = Float.parseFloat(scriptMan.getValue("platformCycleTime").toString());
            speed = Float.parseFloat(scriptMan.getValue("platformSpeed").toString());
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
            //The platform must've returned to its original position
            direction = 0;
            valueDir++;
            
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

        //Iterate through all platforms and move
        for (SceneNode node : controlledNodesList)
        {
            if (count % 2 == value)
            {
                Vector3f currentPos = (Vector3f)node.getLocalPosition();
                currentPos = (Vector3f)Vector3f.createFrom(currentPos.x() + speed * elapsedTimeMillis * moveDir, currentPos.y(), currentPos.z());
                node.setLocalPosition(currentPos);
          
                if (movingWithPlatforms.getIsChild())
                {
                    avatarNode.getPhysicsObject().applyForce(0, 12, 0, 0, 0, 0);

                    if (node.getName() == movingWithPlatforms.getParentName())
                    {   
                        avatarNode.setLocalPosition(avatarNode.getLocalPosition().x() + speed * elapsedTimeMillis * moveDir, avatarNode.getLocalPosition().y(), avatarNode.getLocalPosition().z());
                        physMan.updatePhysicsPosition(avatarNode);
                        nc.updatePositionOnServer = true;
                    }
                }
            }
            else
            {
                Vector3f currentPos = (Vector3f)node.getLocalPosition();
                currentPos = (Vector3f)Vector3f.createFrom(currentPos.x() + speed * elapsedTimeMillis * -moveDir, currentPos.y(), currentPos.z());
                node.setLocalPosition(currentPos);
         
                if (movingWithPlatforms.getIsChild())
                {
                    avatarNode.getPhysicsObject().applyForce(0, 12, 0, 0, 0, 0);

                    if (node.getName() == movingWithPlatforms.getParentName())
                    {  
                        avatarNode.setLocalPosition(avatarNode.getLocalPosition().x() + speed * elapsedTimeMillis * -moveDir, avatarNode.getLocalPosition().y(), avatarNode.getLocalPosition().z());
                        physMan.updatePhysicsPosition(avatarNode);
                        nc.updatePositionOnServer = true;
                    }
                }
            }           

            physMan.updatePhysicsPosition(node);        
            count++;
        }        
    } 
    
    public void reset() 
    {
        //Reset all internal variables to default
        cycleTime = Float.parseFloat(scriptMan.getValue("platformCycleTime").toString());
        totalTime = 0;
        speed = Float.parseFloat(scriptMan.getValue("platformSpeed").toString());
        moveDir = 1;
        direction = 0;
        valueDir = 0;
        value = 0;

        //Reset all the platform positions
        for (int index = 1; index <= controlledNodesList.size(); index++)
        {
            String name = "endPlat" + index + "PhysicsPlanePos";
            controlledNodesList.get(index - 1).setLocalPosition((Vector3f)scriptMan.getValue(name));  
            physMan.updatePhysicsPosition(controlledNodesList.get(index - 1));         
        }
    }
}
