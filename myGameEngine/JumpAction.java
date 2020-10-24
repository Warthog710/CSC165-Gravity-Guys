package myGameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.SceneNode;
import a2.JumpManager;
import net.java.games.input.Event;

public class JumpAction extends AbstractInputAction 
{
    private SceneNode target;
    private JumpManager jm;
    private ChargeCollector cc;
    private long timePressed, duration;

    public JumpAction(SceneNode target, JumpManager jm, ChargeCollector cc)
    {
        this.target = target;
        this.jm = jm;
        this.cc = cc;
    }

    //Record how long the button has been held down
    public void performAction(float time, Event e) 
    {
        //On button press
        if (e.getValue() == 1.0f)
        {
            timePressed = System.currentTimeMillis();

            //Set charge collector
            cc.setTimePressed(timePressed);
        }

        //On button release
        else if (e.getValue() == 0.0f)
        {
            duration = (System.currentTimeMillis() - timePressed);

            //Max jump duration = 2000
            //Note, a max charged jump will make the dolphin go around 30 units up
            if (duration > 2000)
                duration = 2000;

            //If the node is not currently jumping... make it jump
            if (!jm.isJumping(target))
            {
                jm.addNode(target, duration);
            }

            cc.reset();
        }
    }
}
