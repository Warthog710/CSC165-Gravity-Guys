package a3;

import java.io.IOException;

import myGameEngine.NetworkedClient;
import myGameEngine.ObjectDistance;
import myGameEngine.PhysicsManager;
import myGameEngine.ScriptManager;
import myGameEngine.SoundManager;
import ray.ai.behaviortrees.BTAction;
import ray.ai.behaviortrees.BTCompositeType;
import ray.ai.behaviortrees.BTCondition;
import ray.ai.behaviortrees.BTSequence;
import ray.ai.behaviortrees.BTStatus;
import ray.ai.behaviortrees.BehaviorTree;
import ray.rage.Engine;
import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.Entity;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Degreef;
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
    private SoundManager soundMan;

    public NPC(Engine eng, ScriptManager scriptMan, NetworkedClient nc, SoundManager soundMan, PhysicsManager physMan)  throws IOException
    {
        this.scriptMan = scriptMan;
        this.nc = nc;
        this.blowPower = Float.parseFloat(scriptMan.getValue("blowPower").toString());
        this.soundMan = soundMan;
        SceneManager sm = eng.getSceneManager();

        Texture platformTex = eng.getTextureManager().getAssetByPath("npcPlatform.png");
        TextureState platformTexState = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
        platformTexState.setTexture(platformTex);

        Entity fanE = sm.createEntity(scriptMan.getValue("npcName").toString(), "fanBase.obj");
        fanE.setPrimitive(Primitive.TRIANGLES);

        this.npcNode = sm.getRootSceneNode().createChildSceneNode(fanE.getName() + "Node");
        this.npcNode.attachObject(fanE);

        Entity fanBladeE = sm.createEntity("fanBlade", "fanBlades.obj");
        fanBladeE.setPrimitive(Primitive.TRIANGLES);

        SceneNode fanBladeN = this.npcNode.createChildSceneNode("fanBladeNode");
        fanBladeN.attachObject(fanBladeE);
        fanBladeN.setLocalPosition(0, 2.3f, .4f);
        this.npcNode.setLocalScale(.5f, .5f, .5f);
        this.npcNode.setLocalPosition((Vector3f)scriptMan.getValue("npcStartLocation"));

        //Create the platform
        Entity platform = sm.createEntity("npcPlatform", "customCube.obj");
        platform.setPrimitive(Primitive.TRIANGLES);
        platform.setRenderState(platformTexState);
        SceneNode platformNode = sm.getRootSceneNode().createChildSceneNode(platform.getName() + "Node");
        platformNode.attachObject(platform);
        platformNode.setLocalScale((Vector3f)scriptMan.getValue("platformScale"));
        platformNode.setLocalPosition((Vector3f)scriptMan.getValue("platformPos"));
        physMan.createCubePhysicsObject(platformNode, 0f, 1f, 1f, .99f);    

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

        //Look at player in the x and z direction, but don't pitch it up or down
        //by having it look straight ahead at its own y value
        npcNode.lookAt(playerNode.getWorldPosition().x(), npcNode.getWorldPosition().y(), playerNode.getWorldPosition().z());
        nc.sendNPCRot((Matrix3f)npcNode.getLocalRotation());

        //Get forward axis of the npc, translate to players position
        Vector3f temp = (Vector3f)npcNode.getLocalPosition();
        npcNode.setLocalPosition(playerNode.getLocalPosition());
        Vector3 fwd = npcNode.getLocalForwardAxis().mult(timeElapsed * blowPower);
        npcNode.setLocalPosition(temp);

        //Rotate the child fan blades of the fan
        npcNode.getChild("fanBladeNode").roll(Degreef.createFrom(10f));
        
        //Play wind sound effect
        soundMan.playWind();

        //Apply physics force
        playerNode.getPhysicsObject().applyForce(fwd.x(), fwd.y(), fwd.z(), 0f, 0f, 0f);
    }

    //Used by networked client to update orientation and position of the NPC
    public void updateNPCTransform(Vector3f pos, Matrix3f rot)
    {
        npcNode.setLocalPosition(pos);
        npcNode.setLocalRotation(rot);
        //soundMan.stopWind();
    }

    public void rotateFan()
    {
        npcNode.getChild("fanBladeNode").roll(Degreef.createFrom(10f));
        soundMan.playWind();
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
            
            //Look at player in the x and z direction, but don't pitch it up or down
            //by having it look straight ahead at its own y value
            npcNode.lookAt(playerNode.getWorldPosition().x(), npcNode.getWorldPosition().y(), playerNode.getWorldPosition().z());

            //Get forward axis of the npc, translate to players position
            Vector3f temp = (Vector3f)npcNode.getLocalPosition();
            npcNode.setLocalPosition(playerNode.getLocalPosition());
            Vector3 fwd = npcNode.getLocalForwardAxis().mult(timeElapsed * blowPower);
            npcNode.setLocalPosition(temp);

            //Rotate the child fan blades of the fan
            npcNode.getChild("fanBladeNode").roll(Degreef.createFrom(10f));

            //Apply physics force
            playerNode.getPhysicsObject().applyForce(fwd.x(), fwd.y(), fwd.z(), 0f, 0f, 0f);
            
            //Play wind sound effect
            soundMan.playWind();

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
            
            //Stop wind sound effect
            //soundMan.stopWind();
            
            return BTStatus.BH_SUCCESS;            
        }        
    }    
}
