package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rml.*;

import net.java.games.input.Event;

public class MoveYawKeyboardAction extends AbstractInputAction 
{
    private OrbitCameraController oc;
    private SceneNode target;


    public MoveYawKeyboardAction(OrbitCameraController oc, SceneNode target) 
    {
        this.oc = oc;
        this.target = target;
    }

    // A full rotation takes 5 sec
    public void performAction(float time, Event e) 
    {
        //If Q, move yaw left
        if (e.getComponent().toString() == "Q")
        {
            // Global Yaw
            Vector3 worldUp = Vector3f.createFrom(0.0f, 1.0f, 0.0f);
            Matrix3 matRot = Matrix3f.createRotationFrom(Degreef.createFrom(.072f * time), worldUp);
            target.setLocalRotation(matRot.mult(target.getWorldRotation()));

            //Update orbit azimuth
            oc.updateAzimoth(.072f * time);
        }

        //E must've been pressed
        else
        {
            // Global Yaw
            Vector3 worldUp = Vector3f.createFrom(0.0f, 1.0f, 0.0f);
            Matrix3 matRot = Matrix3f.createRotationFrom(Degreef.createFrom(-.072f * time), worldUp);
            target.setLocalRotation(matRot.mult(target.getWorldRotation()));
            
            //Update orbit azimuth
            oc.updateAzimoth(-.072f * time);
        }
    }
}