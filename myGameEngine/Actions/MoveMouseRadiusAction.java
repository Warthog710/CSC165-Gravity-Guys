package myGameEngine.Actions;

import myGameEngine.*;
import ray.input.action.AbstractInputAction;
import net.java.games.input.Event;

public class MoveMouseRadiusAction extends AbstractInputAction 
{
    //private OrbitCameraController oc;

    public MoveMouseRadiusAction(OrbitCameraController oc) 
    {
        //this.oc = oc;
    }

    // Do mouse stuff here
    public void performAction(float time, Event e) 
    {
        
        System.out.println("mouse radius: " + e.getValue());
        //oc.mouseRadiusAction(time, -e.getValue());
    }
}