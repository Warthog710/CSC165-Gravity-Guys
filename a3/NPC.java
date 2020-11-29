package a3;

import java.io.IOException;

import myGameEngine.NetworkedClient;
import myGameEngine.ObjectDistance;
import myGameEngine.ScriptManager;
import ray.ai.behaviortrees.BTAction;
import ray.ai.behaviortrees.BTCompositeType;
import ray.ai.behaviortrees.BTCondition;
import ray.ai.behaviortrees.BTSequence;
import ray.ai.behaviortrees.BTStatus;
import ray.ai.behaviortrees.BehaviorTree;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.scene.Entity;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Matrix3f;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class NPC 
{
    private ScriptManager scriptMan;
    private NetworkedClient nc;
    private SceneNode npcNode, playerNode;
    private BehaviorTree bTree;
    private float blowPower;

    public NPC(SceneManager sm, ScriptManager scriptMan, NetworkedClient nc)  throws IOException
    {
        this.scriptMan = scriptMan;
        this.nc = nc;
        this.blowPower = Float.parseFloat(scriptMan.getValue("blowPower").toString());

        //Create the NPC
        Entity cube = sm.createEntity(scriptMan.getValue("npcName").toString(), "cube.obj");
        cube.setPrimitive(Primitive.TRIANGLES);

        this.npcNode = sm.getRootSceneNode().createChildSceneNode(cube.getName() + "Node");
        npcNode.attachObject(cube);
        npcNode.setLocalScale(.5f, .5f, .5f);
        npcNode.setLocalPosition((Vector3f)scriptMan.getValue("npcStartLocation"));

        //Create the platform
        Entity platform = sm.createEntity("npcPlatform", "cube.obj");
        platform.setPrimitive(Primitive.TRIANGLES);
        SceneNode platformNode = sm.getRootSceneNode().createChildSceneNode(platform.getName() + "Node");
        platformNode.attachObject(platform);
        platformNode.setLocalScale((Vector3f)scriptMan.getValue("platformScale"));
        platformNode.setLocalPosition((Vector3f)scriptMan.getValue("platformPos"));    

        //Save the player node
        playerNode = sm.getSceneNode(this.scriptMan.getValue("avatarName").toString() + "Node");

        // Create behavior tree
        setupBehaviorTree();
    }

    //Update the behavior tree
    public void update(float timeElapsed)
    {
        //If not connected to the server, control the npc yourself
        if (!nc.isConnected)
            bTree.update(timeElapsed);

        //Else the server is controlling the NPC
    }

    //Used by networked client to apply a force to the player
    public void applyBlowForce(float timeElapsed)
    {
        //If movement speed has been updated... grab the new value
        if (scriptMan.scriptUpdate("movementInfo.js"))
            blowPower = Float.parseFloat(scriptMan.getValue("blowPower").toString());

        npcNode.lookAt(playerNode);
        nc.sendNPCRot((Matrix3f)npcNode.getLocalRotation());

        //Get forward axis of the npc, translate to players position
        Vector3f temp = (Vector3f)npcNode.getLocalPosition();
        npcNode.setLocalPosition(playerNode.getLocalPosition());
        Vector3 fwd = npcNode.getLocalForwardAxis().mult(timeElapsed * blowPower);
        npcNode.setLocalPosition(temp);

        //Get player position
        Vector3 playerPos = playerNode.getLocalPosition();

        //Apply physics force
        playerNode.getPhysicsObject().applyForce(fwd.x(), fwd.y(), fwd.z(), playerPos.x(), playerPos.y(), playerPos.z());
        nc.updatePositionOnServer = true;
    }

    //Used by networked client to update orientation and position of the NPC
    public void updateNPCTransform(Vector3f pos, Matrix3f rot)
    {
        npcNode.setLocalPosition(pos);
        npcNode.setLocalRotation(rot);
    }

    private void setupBehaviorTree() 
    {
        bTree = new BehaviorTree(BTCompositeType.SELECTOR);
        bTree.insertAtRoot(new BTSequence(10));
        bTree.insertAtRoot(new BTSequence(20));
        bTree.insert(10, new PlayerInRange(false));
        bTree.insert(10, new BlowPlayerAway());
        bTree.insert(20, new MoveToWaypoint());
    }

    // Condition: Checks if the player is in range
    private class PlayerInRange extends BTCondition 
    {
        private float range = 9f;

        public PlayerInRange(boolean toNegate) 
        {
            super(toNegate);
        }

        @Override
        protected boolean check() 
        {
            //Check to see if the player is in range
            if (ObjectDistance.distanceBetweenVectors(playerNode.getLocalPosition(), npcNode.getLocalPosition()) < range)
                return true;


            return false;

        }
    }

    //Action: If the player is in range, blow the player away
    private class BlowPlayerAway extends BTAction 
    {
        @Override
        protected BTStatus update(float timeElapsed) 
        {
            //If movement speed has been updated... grab the new value
            if (scriptMan.scriptUpdate("movementInfo.js"))
                blowPower = Float.parseFloat(scriptMan.getValue("blowPower").toString());

            npcNode.lookAt(playerNode);

            //Get forward axis of the npc, translate to players position
            Vector3f temp = (Vector3f)npcNode.getLocalPosition();
            npcNode.setLocalPosition(playerNode.getLocalPosition());
            Vector3 fwd = npcNode.getLocalForwardAxis().mult(timeElapsed * blowPower);
            npcNode.setLocalPosition(temp);

            //Get player position
            Vector3 playerPos = playerNode.getLocalPosition();

            //Apply physics force
            playerNode.getPhysicsObject().applyForce(fwd.x(), fwd.y(), fwd.z(), playerPos.x(), playerPos.y(), playerPos.z());
            nc.updatePositionOnServer = true;

            return BTStatus.BH_SUCCESS;
        }        
    }

    //Action: Move toward the waypoint
    private class MoveToWaypoint extends BTAction
    {
        private boolean movingForward;
        private float movementMult;
        private float range = .6f;

        public MoveToWaypoint()
        {
            movementMult = Float.parseFloat(scriptMan.getValue("npcSpeed").toString());   
            movingForward = true;        
        }

        @Override
        protected BTStatus update(float timeElapsed) 
        {
            //If movement speed has been updated... grab the new value
            if (scriptMan.scriptUpdate("movementInfo.js"))
                movementMult = Float.parseFloat(scriptMan.getValue("npcSpeed").toString());

            //Check to see if we should be moving back or forward
            if (ObjectDistance.distanceBetweenVectors((Vector3f)Vector3f.createFrom(0, 10, 10), npcNode.getLocalPosition()) < range)
                movingForward = true;
            else if (ObjectDistance.distanceBetweenVectors((Vector3f)Vector3f.createFrom(0, 10, 28), npcNode.getLocalPosition()) < range)
                movingForward = false;

            Vector3f previousPos = (Vector3f)Vector3f.createFrom(npcNode.getLocalPosition().x(), npcNode.getLocalPosition().y(), npcNode.getLocalPosition().z());
                
            if (movingForward)
                npcNode.setLocalPosition(previousPos.x(), previousPos.y(), previousPos.z() + movementMult * timeElapsed);
            else
                npcNode.setLocalPosition(previousPos.x(), previousPos.y(), previousPos.z() + -movementMult * timeElapsed);

            return BTStatus.BH_SUCCESS;            
        }        
    }    
}
