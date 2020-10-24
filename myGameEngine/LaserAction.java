package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.SceneNode;
import a2.LaserManager;
import net.java.games.input.Event;

public class LaserAction extends AbstractInputAction 
{
    private SceneNode target;
    private LaserManager lm;

    public LaserAction(SceneNode target, LaserManager lm)
    {
        this.target = target;
        this.lm = lm;
    }

    //Record how long the button has been held down
    public void performAction(float time, Event e) 
    {
        //Add a laser to the laser manager
        lm.addLaser(target);
    }

}
