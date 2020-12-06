package myGameEngine.Actions;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rml.Matrix3f;
import ray.rml.Vector3;
import myGameEngine.*;
import net.java.games.input.Event;

public class ResetPlayerAction extends AbstractInputAction 
{
    private SceneNode target;
    private ScriptManager scriptMan;
    private PhysicsManager physMan;


    public ResetPlayerAction(SceneNode target, ScriptManager scriptMan, PhysicsManager physMan) 
    {
        this.target = target;
        this.scriptMan = scriptMan;
        this.physMan = physMan;
    }

    // Move left or right 5.0f every 1000ms or 1 second (assuming axis value = 1)
    public void performAction(float time, Event e) 
    {
        //Reset position
        target.setLocalPosition((Vector3)scriptMan.getValue("avatarPos"));

        //Reset orientation
        float[] rot = { 1, 0, 0, 0, 1, 0, 0, 0, 1};
        target.setLocalRotation(Matrix3f.createFrom(rot));

        //Update physics transforms
        physMan.updatePhysicsTransforms(target);
    }
}