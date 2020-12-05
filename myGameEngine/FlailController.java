package myGameEngine;

import java.util.Vector;

import ray.rage.scene.Node;
import ray.rage.scene.SceneNode;
import ray.rage.scene.controllers.OrbitController;
import ray.rml.Matrix3f;
import ray.rml.Vector3;
import ray.rml.Vector3f;

//Adds a bit of additional functionality to the built-in orbit controller
public class FlailController extends OrbitController 
{
    private PhysicsManager physMan;
    private SceneNode node;
    private SceneNode avatarNode;

    public FlailController(Node orbitTarget, float orbitalSpeed, float distanceFromTarget, float verticalDistance, boolean faceTarget, PhysicsManager physMan, SceneNode avatarNode, SceneNode flail) 
    {
        //Call super
        super(orbitTarget, orbitalSpeed, distanceFromTarget, verticalDistance, faceTarget);

        this.physMan = physMan;
        this.avatarNode = avatarNode;
        this.node = flail;
    }

    @Override
    protected void updateImpl(float elapsedTimeMillis) 
    {
        //Call super
        super.updateImpl(elapsedTimeMillis);

        //Update the physics object position
        physMan.updatePhysicsPosition(super.controlledNodesList.get(0));

        /*Vector3f pos = (Vector3f)avatarNode.getLocalPosition();      
        float minX = node.getLocalPosition().x() - (node.getLocalScale().x());
        float maxX = node.getLocalPosition().x() + (node.getLocalScale().x());
        float minY = node.getLocalPosition().y() - (node.getLocalScale().y());
        float maxY = node.getLocalPosition().y() + (node.getLocalScale().y());
        float minZ = node.getLocalPosition().z() - (node.getLocalScale().z());
        float maxZ = node.getLocalPosition().z() + (node.getLocalScale().z());
        //System.out.println(node.getName());
            
        //Collission with the platform... Avatar should start moving with it... attach as a pseudo child
        if (pos.x() >= minX && pos.x() <= maxX && pos.y() >= minY && pos.y() <= maxY && pos.z() >= minZ && pos.z() <= maxZ)
        {
            System.out.println("Flail collission");
            //If a collision is detected, push the player away
            //Matrix3f rot = (Matrix3f) avatarNode.getLocalRotation();
            //avatarNode.lookAt(node);
            //Vector3 fwd = avatarNode.getLocalForwardAxis().mult(-200);
            //avatarNode.getPhysicsObject().applyForce(fwd.x(), fwd.y(), fwd.z(), 0, 0, 0);  
            //avatarNode.setLocalRotation(rot);
        }*/
    }    
}
