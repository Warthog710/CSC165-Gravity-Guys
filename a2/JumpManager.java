package a2;

import ray.rage.scene.*;
import ray.rml.*;

import java.util.ArrayList;
import java.util.HashMap;

import myGameEngine.DetectCollision;

public class JumpManager 
{
    private ArrayList<JumpNode> jumpNodesList = new ArrayList<>();
    SceneManager sm;
    private float basePower = .000045f;
    private long decayRate = 3;
    private float startingPos;

    public JumpManager(SceneManager sm, float startingPos)
    {
        this.startingPos = startingPos;
        this.sm = sm;
    }

    public void addNode(SceneNode node, long duration)
    {
        jumpNodesList.add(new JumpNode(node, duration));

        //Pitch node up 10 degrees
        node.pitch(Degreef.createFrom(-10));
    }

    //A max power jump is around 30.f units up
    public HashMap<SceneNode, Node> update(float timeElapsed)
    {
        HashMap<SceneNode, Node> collisionInfo = new HashMap<>();

        for (int count = 0; count < jumpNodesList.size(); count++)
        {
            Vector3 currentPos = jumpNodesList.get(count).node.getLocalPosition();
            Vector3f newPos = (Vector3f)Vector3f.createFrom(currentPos.x(), currentPos.y() + (basePower * timeElapsed * jumpNodesList.get(count).duration), currentPos.z());
            jumpNodesList.get(count).node.setLocalPosition(newPos);

            jumpNodesList.get(count).duration -= (decayRate * timeElapsed);

            //Check whether the node will collide with a planet, if so... starting moving down
            Node temp = DetectCollision.planetCollisions(sm, newPos);

            if (temp != null)
            {
                jumpNodesList.get(count).node.setLocalPosition(currentPos);

                //If not travelling down...
                if (jumpNodesList.get(count).duration > 0)
                    jumpNodesList.get(count).duration = 0;

                //Record collision info if it hasn't been recorded
                if (!collisionInfo.containsKey(jumpNodesList.get(count).node))
                    collisionInfo.put(jumpNodesList.get(count).node, temp);
            }

            //Check to see if the jumping node has returned to its starting position
            if (jumpNodesList.get(count).node.getLocalPosition().y() <= startingPos)
            {
                newPos = (Vector3f)Vector3f.createFrom(currentPos.x(), startingPos, currentPos.z());
                jumpNodesList.get(count).node.setLocalPosition(newPos);

                //Reset pitch
                jumpNodesList.get(count).node.pitch(Degreef.createFrom(10));

                //Remove the node and duration
                jumpNodesList.remove(count);
            }
        }

        return collisionInfo;
    }

    public boolean isJumping(SceneNode node)
    {
        for (int count = 0; count < jumpNodesList.size(); count++)
        {
            //If the node is in the jumping list return true
            if (jumpNodesList.get(count).node.getName() == node.getName())
            {
                return true;
            }
        }
        return false;
    }

    //Simple private class to hold info about the jumping node
    private class JumpNode
    {
        protected SceneNode node;
        protected long duration;

        protected JumpNode (SceneNode node, long duration)
        {
            this.node = node;
            this.duration = duration;
        }        
    }

    
}
