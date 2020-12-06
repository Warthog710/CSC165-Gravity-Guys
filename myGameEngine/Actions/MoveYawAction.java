package myGameEngine.Actions;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rml.*;
import myGameEngine.*;
import net.java.games.input.Event;

public class MoveYawAction extends AbstractInputAction 
{
    private OrbitCameraController oc;
    private ScriptManager scriptMan;
    private SceneNode target;
    private float avatarYawSpeed;

    public MoveYawAction(OrbitCameraController oc, SceneNode target, ScriptManager scriptMan) 
    {
        this.oc = oc;
        this.scriptMan = scriptMan;
        this.target = target;
        this.avatarYawSpeed = Float.parseFloat(scriptMan.getValue("avatarYawSpeed").toString());  
    }

    // A full rotation takes 5 sec
    public void performAction(float time, Event e) 
    {
        // Deadzone
        if (e.getValue() > -.2 && e.getValue() < .2)
            return;

        //Updates yaw speed, if a script update occured
        if (scriptMan.scriptUpdate("movementInfo.js"))
            avatarYawSpeed = Float.parseFloat(scriptMan.getValue("avatarYawSpeed").toString());   

        //Global Yaw
        Vector3 worldUp = Vector3f.createFrom(0.0f, 1.0f, 0.0f);
        Matrix3 matRot = Matrix3f.createRotationFrom(Degreef.createFrom(e.getValue() * avatarYawSpeed * time), worldUp);
        target.setLocalRotation(matRot.mult(target.getWorldRotation()));

        //Update orbit azimuth
        oc.updateAzimoth(e.getValue() * avatarYawSpeed * time);
    }
}
