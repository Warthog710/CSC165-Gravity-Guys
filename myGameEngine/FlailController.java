package myGameEngine;

import ray.rage.scene.Node;
import ray.rage.scene.SceneNode;
import ray.rage.scene.controllers.OrbitController;
import ray.rml.Matrix3f;
import ray.rml.Vector3;
import ray.rml.Vector3f;

//Adds a bit of additional functionality to the built-in orbit controller
public class FlailController extends OrbitController 
{
    private SceneNode avatarNode;
    private ScriptManager scriptMan;

    private float cylinderKnockback;

    public FlailController(ScriptManager scriptMan, Node orbitTarget, float orbitalSpeed, float distanceFromTarget, float verticalDistance, boolean faceTarget, SceneNode avatarNode) 
    {
        //Call super
        super(orbitTarget, orbitalSpeed, distanceFromTarget, verticalDistance, faceTarget);

        this.scriptMan = scriptMan;
        this.avatarNode = avatarNode;
        this.cylinderKnockback = Float.parseFloat(scriptMan.getValue("cylinderKnockback").toString());
    }

    @Override
    protected void updateImpl(float elapsedTimeMillis) 
    {
        //Call super
        super.updateImpl(elapsedTimeMillis);

        //Update knockback if an update occured
        if (scriptMan.scriptUpdate("movementInfo.js"))
            cylinderKnockback = Float.parseFloat(scriptMan.getValue("cylinderKnockback").toString());        

        Vector3f pos = (Vector3f)avatarNode.getLocalPosition();      
        float minX = super.controlledNodesList.get(0).getLocalPosition().x() - (super.controlledNodesList.get(0).getLocalScale().x() + .4f);
        float maxX = super.controlledNodesList.get(0).getLocalPosition().x() + (super.controlledNodesList.get(0).getLocalScale().x() + .4f);
        float minY = super.controlledNodesList.get(0).getLocalPosition().y() - (super.controlledNodesList.get(0).getLocalScale().y() + 1);
        float maxY = super.controlledNodesList.get(0).getLocalPosition().y() + (super.controlledNodesList.get(0).getLocalScale().y() + 1);
        float minZ = super.controlledNodesList.get(0).getLocalPosition().z() - (super.controlledNodesList.get(0).getLocalScale().z() + .4f);
        float maxZ = super.controlledNodesList.get(0).getLocalPosition().z() + (super.controlledNodesList.get(0).getLocalScale().z() + .4f);
            
        //Collission with the platform... Avatar should start moving with it... attach as a pseudo child
        if (pos.x() >= minX && pos.x() <= maxX && pos.y() >= minY && pos.y() <= maxY && pos.z() >= minZ && pos.z() <= maxZ)
        {
            //If a collision is detected, push the player away
            Matrix3f rot = (Matrix3f) avatarNode.getLocalRotation();
            avatarNode.lookAt(super.controlledNodesList.get(0));
            Vector3 fwd = avatarNode.getLocalForwardAxis().mult(cylinderKnockback);
            avatarNode.getPhysicsObject().applyForce(fwd.x(), fwd.y(), fwd.z(), 0, 0, 0);  
            avatarNode.setLocalRotation(rot);
        }
    }    
}
