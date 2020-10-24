package a2;

import java.awt.*;
import java.io.*;
import java.util.Random;

import ray.rage.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.controllers.*;
import ray.rml.Vector3f;
import ray.rage.rendersystem.states.*;
import ray.rage.asset.texture.*;

import myGameEngine.ObjectDistance;
import myGameEngine.VerticalLineObject;

//Spawns 10 planets, scattered across the ground plane at varying locations and heights, each planet is in a hierarchy
//so that one single rotation controller can control everything
public class SpawnPlanets 
{
    // String array of all available textures to randomly select from
    static String[] textureList = { "sun.jpg", "jupiter.jpg", "mars.jpg", "ceres.jpg", "neptune.jpg", "earth-day.jpeg",
    "moon.jpeg", "eris.jpg", "haumea.jpg", "makemake.jpg", "mercury.jpg", "saturn.jpg", "venus.jpg",
    "venusSurface.jpg" };

    public void spawnPlanets(Engine eng, SceneManager sm, RotationController rc) throws IOException
    {
        Random myRand = new Random();

        //Create planetGroup node
        sm.getRootSceneNode().createChildNode("planetGroup");

        //Spawn 10 planets all children of the planetGroup
        for (int count = 0; count < 10; count++)
        {
            Entity planetE = sm.createEntity("planet" + Integer.toString(count), "earth.obj");
            SceneNode planetN = sm.getSceneNode("planetGroup").createChildSceneNode("planet" + Integer.toString(count) + "Node");
            planetN.attachObject(planetE);

            //Randomly set scale (1f - 2f)
            float scale = myRand.nextFloat() + 1f;
            planetN.setLocalScale(scale, scale, scale);

            //Randomly set position
            while (true)
            {
                planetN.setLocalPosition(myRand.nextInt(100) - 50, myRand.nextInt(20) + 5, myRand.nextInt(100) - 50);

                // Fix: don't spawn planets near the origin... 
                if (planetN.getLocalPosition().x() < 6 && planetN.getLocalPosition().x() > -6)
                    continue;
                
                if (planetN.getLocalPosition().z() < 6 && planetN.getLocalPosition().z() > -6)
                    continue;

                //If their is a collision with an existing planet returns true
                if (checkPlanetSpawnCollision(sm, planetN))
                    continue;

                break;
            }

            //Assign random texture
            TextureManager tm = eng.getTextureManager();
            Texture planetTexture = tm.getAssetByPath(textureList[myRand.nextInt(14)]);
            RenderSystem rs = sm.getRenderSystem();
            TextureState state = (TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
            state.setTexture(planetTexture);
            planetE.setRenderState(state);

            //Create a point light above the planet
            Light pLight = sm.createLight("light" + Integer.toString(count), Light.Type.POINT);
            pLight.setAmbient(new Color(0.01f, 0.01f, 0.01f));
            pLight.setDiffuse(new Color(0.1f, 0.1f, 0.1f));
            pLight.setSpecular(new Color(1f, 1f, 1f));

            SceneNode pLightNode = planetN.createChildSceneNode("light" + Integer.toString(count) + "Node");
            pLightNode.attachObject(pLight);
            pLightNode.moveUp((planetN.getLocalScale().x() * 2.0f) + 1.0f);

            //Create a vertical line object 
            ManualObject vertLine = new VerticalLineObject(eng).makeLine(planetN.getLocalPosition().y(), count);
            SceneNode lineNode = planetN.createChildSceneNode("lineNode" + Integer.toString(count));
            vertLine.setPrimitive(Primitive.LINES);
            lineNode.attachObject(vertLine);


            //Make it spin!
            rc.addNode(planetN);
        }
    }

    private boolean checkPlanetSpawnCollision(SceneManager sm, SceneNode planet)
    {
        Vector3f planetPos = (Vector3f)planet.getLocalPosition();

        //Iterate through all nodes hanging off the planet group
        for (Node planetNode : sm.getSceneNode("planetGroup").getChildNodes())
        {
            //If the node has the same name as the node that spawned skip it
            if (planetNode.getName() == planet.getName())
                continue;

            //If the spawned planets overlap
            if (ObjectDistance.distanceBetweenVectors(planetPos, (Vector3f)planetNode.getLocalPosition()) <= (planet.getLocalScale().x() * 2.0f)+ planetNode.getLocalScale().x() * 2.0f)
                return true;
        }       
        return false;
    }



}