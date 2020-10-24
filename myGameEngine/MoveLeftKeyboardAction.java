package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rml.Vector3f;
import net.java.games.input.Event;

public class MoveLeftKeyboardAction extends AbstractInputAction 
{
    private SceneNode target;

    public MoveLeftKeyboardAction(SceneNode target) 
    {
        this.target = target;
    }

    // Move left 5.0f every 1000ms or 1 second (assuming axis value = 1)
    public void performAction(float time, Event e) 
    {
        target.moveLeft(-time / 200);

        //Check for collission
        if (DetectCollision.towerCollisions((Vector3f)target.getLocalPosition()))
        {
            //If a collision occurs... don't make the move
            target.moveLeft(time / 200);
        }
        //Check if dolphin left the world plane...
        else if (Math.abs(target.getLocalPosition().x()) > 50 || Math.abs(target.getLocalPosition().z()) > 50)
        {
            //Don't make the move
            target.moveLeft(time / 200);
        }          
    }
}