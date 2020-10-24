package a2;

import ray.rage.scene.*;
import ray.rage.scene.Node.Controller;
import ray.rage.scene.controllers.RotationController;
import ray.rml.*;

public class FollowManager 
{
    private SceneManager sm;
    private SceneNode target;
    private SceneNode follow;
    private boolean hasFollow;
    private RotationController rc;
    private Controller c;

    private Vector3 originalScale, orignalPos;


    private float followDistance = -3.0f;

    public FollowManager(SceneManager sm, SceneNode target, RotationController rc, Controller c)
    {
        this.sm = sm;
        this.target = target;
        this.hasFollow = false;

        this.rc = rc;
        this.c = c;
    }

    public void addFollow(SceneNode follow)
    {
        this.follow = follow;
        this.hasFollow = true;

        //Keep track of original node information
        this.originalScale = follow.getLocalScale();
        this.orignalPos = follow.getLocalPosition();

        //Shrink the scale of the follow node
        follow.setLocalScale(0.25f, 0.25f, 0.25f);

        //Add the node as a child of the target after removing it from the planet group
        sm.getSceneNode("planetGroup").detachChild(follow);
        target.attachChild(follow);
        follow.setLocalPosition(0.0f, .31f, followDistance);

        //Hide the line
        sm.getManualObject("verticalLine" + follow.getName().replace("planet", "").replace("Node", "")).setVisible(false);

        //Add a node controller
        rc.removeNode(follow);
        c.addNode(follow);
    }

    public boolean checkFollow()
    {
        return hasFollow;
    }

    public void restoreNode()
    {
        if (hasFollow)
        {
            //Remove node from controller
            c.removeNode(follow);

            //Detach child from target
            target.detachChild(follow);

            //Restore child to planetGroup
            sm.getSceneNode("planetGroup").attachChild(follow);

            //Restore original scale and position
            follow.setLocalScale(originalScale);
            follow.setLocalPosition(orignalPos);

            //Unhide the line
            sm.getManualObject("verticalLine" + follow.getName().replace("planet", "").replace("Node", "")).setVisible(true);

            //Restore rotation controller
            rc.addNode(follow);

            //Reset the flag
            hasFollow = false;

        }
    }

    public void removeNode()
    {
        //Remove node from the controller
        c.removeNode(follow);

        //Get the planet number
        String pNum = follow.getName().replace("planet", "");
        pNum = pNum.replace("Node", "");

        String planetName = follow.getName();

        //Destroy all the nodes!!!
        sm.destroySceneNode("light" + pNum + "Node");
        sm.destroyLight("light" + pNum);
        sm.destroySceneNode(planetName);  
        sm.destroyEntity(planetName.replace("Node", ""));
        sm.destroySceneNode("lineNode" + pNum);
        sm.destroyManualObject("verticalLine" + pNum); 
        
        //Reset the flag
        hasFollow = false;
    }
  

    
}
