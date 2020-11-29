package myGameEngine;

import ray.rage.scene.Node;
import ray.rage.scene.controllers.OrbitController;

//Adds a bit of additional functionality to the built-in orbit controller
public class FlailController extends OrbitController 
{
    private PhysicsManager physMan;

    public FlailController(Node orbitTarget, float orbitalSpeed, float distanceFromTarget, float verticalDistance, boolean faceTarget, PhysicsManager physMan) 
    {
        //Call super
        super(orbitTarget, orbitalSpeed, distanceFromTarget, verticalDistance, faceTarget);

        this.physMan = physMan;
    }

    @Override
    protected void updateImpl(float elapsedTimeMillis) 
    {
        //Call super
        super.updateImpl(elapsedTimeMillis);

        //Update the physics object position
        physMan.updatePhysicsPosition(super.controlledNodesList.get(0));
    }    
}
