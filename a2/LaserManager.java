package a2;

import ray.rage.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rml.*;

import java.awt.Color;
import java.util.ArrayList;

import myGameEngine.DetectCollision;
import myGameEngine.LaserObject;

public class LaserManager 
{
    private ArrayList<SceneNode> laserList;
    private FollowManager player1FM, player2FM;
    private LaserObject laserCreator;
    private Engine eng;
    private int laserOffset;

    public LaserManager(Engine eng, FollowManager player1FM, FollowManager player2FM)
    {
        this.eng = eng;
        this.laserList = new ArrayList<SceneNode>();
        this.laserOffset = 0;
        this.player1FM = player1FM;
        this.player2FM = player2FM;

        try 
        {
            this.laserCreator = new LaserObject(eng);            
        } catch (Exception e) {
            System.out.println("Error creating laser object...");
        }
    }

    public void addLaser(SceneNode originNode)
    {
        ManualObject laser;
        Light laserLight = eng.getSceneManager().createLight("laserLight" + Integer.toString(laserOffset), Light.Type.POINT);

        //Make the correct laser/light color
        if (originNode.getName().contains("One"))
        {
            laser = laserCreator.makeLaser(true, laserOffset);
            laserLight.setDiffuse(Color.GREEN);
        }
        else
        {
            laser =laserCreator.makeLaser(false, laserOffset);
            laserLight.setDiffuse(Color.RED);
        }

        laser.setPrimitive(Primitive.LINES);
        SceneNode laserNode = eng.getSceneManager().getRootSceneNode().createChildSceneNode("laserNode" + Integer.toString(laserOffset));
        laserNode.attachObject(laser);

        //Add a light to the laser  
        laserLight.setRange(1.0f);
        laserLight.setSpecular(Color.WHITE);
        SceneNode laserLightNode = laserNode.createChildSceneNode("laserLightNode" + Integer.toString(laserOffset));
        laserLightNode.attachObject(laserLight); 
        
        //Position the laser
        laserNode.setLocalPosition(originNode.getLocalPosition());
        originNode.moveForward(1.0f);
        laserNode.lookAt(originNode);
        originNode.moveBackward(1.0f); 

        //Add the laser to the list
        laserList.add(laserNode);        
        
        //Increment offset
        laserOffset++;
    }

    public void update(float timeElapsed)
    {
        //Move all the lasers!!!
        for (int index = 0; index < laserList.size(); index++)
        {
            laserList.get(index).moveForward(timeElapsed / 50);

            //If the laser has moved beyond a certain range... remove it
            if (Math.abs(laserList.get(index).getLocalPosition().x()) > 60 || Math.abs(laserList.get(index).getLocalPosition().z()) > 60)
                removeLaser(index);

            //If the laser collides with the tower remove it
            else if (DetectCollision.towerCollisions((Vector3f)laserList.get(index).getLocalPosition()))
                removeLaser(index);

            //If the laser hits the planet the enemy dolphin is carrying
            else if (DetectCollision.carriedPlanetCollisions(eng.getSceneManager().getSceneNode("playerOneDolphinNode"), (Vector3f)laserList.get(index).getLocalPosition()))
            {
                player1FM.restoreNode();
                removeLaser(index);
            }

            //If the laser hits the planet the other enemy dolphin is carrying
            else if (DetectCollision.carriedPlanetCollisions(eng.getSceneManager().getSceneNode("playerTwoDolphinNode"), (Vector3f)laserList.get(index).getLocalPosition()))
            {
                player2FM.restoreNode();
                removeLaser(index);
            }

        }

    }

    private void removeLaser(int index)
    {
        //Get the laser number
        String lNum = laserList.get(index).getName().replace("laserNode", "");

        //Destroy the nodes!!!
        eng.getSceneManager().destroySceneNode("laserLightNode" + lNum);
        eng.getSceneManager().destroyLight("laserLight" + lNum);
        eng.getSceneManager().destroySceneNode("laserNode" + lNum);
        eng.getSceneManager().destroyManualObject("laser" + lNum);

        //Remove the laser from the list
        laserList.remove(index);
    }
}
