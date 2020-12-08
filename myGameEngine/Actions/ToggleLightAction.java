package myGameEngine.Actions;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import a3.MyGame;
import myGameEngine.*;
import net.java.games.input.Event;

public class ToggleLightAction extends AbstractInputAction 
{
    private SceneNode target;
    private ScriptManager scriptMan;
    private MyGame game;
    private boolean lightEnabled;

    public ToggleLightAction(SceneNode target, ScriptManager scriptMan, MyGame game) 
    {
        this.target = target;
        this.scriptMan = scriptMan;
        this.game = game;
        this.lightEnabled = false;
    }

    //Check if the player is close enough to the light, and if so, toggle it
    public void performAction(float time, Event e) 
    {
    	Light light = game.getEngine().getSceneManager().getLight(scriptMan.getValue("lampLightName").toString());
        SceneNode lightN = game.getEngine().getSceneManager().getSceneNode(scriptMan.getValue("lampLightName").toString() + "Node");
        
    	//Check if the player is close to the light
        if (target.getWorldPosition().sub(lightN.getWorldPosition()).length() < 8.0f) 
        {
        	//Flip the boolean and set light visibility to new value
        	lightEnabled = !lightEnabled;
        	light.setVisible(lightEnabled);
        }
    }
}
