package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rml.*;

import net.java.games.input.Event;

public class MoveYawAction extends AbstractInputAction 
{
    private OrbitCameraController oc;
    private SceneNode target;


    public MoveYawAction(OrbitCameraController oc, SceneNode target) 
    {
        this.oc = oc;
        this.target = target;
    }

    // A full rotation takes 5 sec
    public void performAction(float time, Event e) 
    {
        // Deadzone
        if (e.getValue() > -.2 && e.getValue() < .2)
            return;

        // Global Yaw
        Vector3 worldUp = Vector3f.createFrom(0.0f, 1.0f, 0.0f);
        Matrix3 matRot = Matrix3f.createRotationFrom(Degreef.createFrom(e.getValue() * .072f * time), worldUp);
        target.setLocalRotation(matRot.mult(target.getWorldRotation()));

        //Update orbit azimuth
        oc.updateAzimoth(e.getValue() *.072f * time);
    }
}
