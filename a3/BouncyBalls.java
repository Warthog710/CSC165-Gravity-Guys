package a3;

import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import myGameEngine.NetworkedClient;
import myGameEngine.PhysicsManager;
import ray.rage.Engine;
import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.Entity;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3f;

//Manages all the balls either being spawned by the server... Or by the game if no
//server is connected
public class BouncyBalls 
{
    private PhysicsManager physMan;
    private SceneManager sm;
    private NetworkedClient nc;
    private int ballOffset = 0;
    private Vector<SceneNode> ballList;
    private float totalTime = 0.0f;
    private Texture sphereTex;

    public BouncyBalls(PhysicsManager physMan, Engine eng, NetworkedClient nc)
    {
        this.physMan = physMan;
        this.sm = eng.getSceneManager();
        this.nc = nc;
        this.ballList = new Vector<>();

        //Preload texture
        try
        {
            sphereTex = eng.getTextureManager().getAssetByPath("waterHeight2.png");
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

    }

    public void addBall(Vector3f startingPos, float radius)
    {
        try
        {
            Entity sphereE = sm.createEntity("ball" + ballOffset, "sphere.obj");
            sphereE.setPrimitive(Primitive.TRIANGLES);

            SceneNode sphereN = sm.getRootSceneNode().createChildSceneNode(sphereE.getName() + "Node");
            sphereN.attachObject(sphereE);
            sphereN.setLocalPosition(startingPos);
            sphereN.setLocalScale(radius, radius, radius);
            physMan.createSpherePhysicsObject(sphereN, 10f, 1f, 1f, .9f);
            ballList.add(sphereN);

            //Temp texture
            TextureState sphereTexState = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
            sphereTexState.setTexture(sphereTex);
            sphereE.setRenderState(sphereTexState);

            ballOffset++;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void update(float elapsedTime)
    {
        //If I am not connected... spawn a ball every 5sec
        if (!nc.isConnected)
        {
            totalTime += elapsedTime;

            //If 5sec has passed
            if (totalTime >= 5000f)
            {
                //Random number between 0 and 8;
                double rand = Math.random() * 8;

                //If == 1 make rand negative
                if (new Random().nextInt(2) == 1)
                    rand = rand * -1;

                addBall((Vector3f)Vector3f.createFrom((float)rand, 40, 68), (float)(Math.random() + 1));
                totalTime = 0;
            }
        }

        //Loop through spawned balls and remove the balls that have hit the ground plane
        for (int index = 0; index < ballList.size(); index++)
        {
            if (ballList.get(index).getLocalPosition().y() <= 2)
            {
                physMan.getPhysicsEngine().removeObject(ballList.get(index).getPhysicsObject().getUID());
                sm.destroyEntity(ballList.get(index).getAttachedObject(0).getName());
                sm.destroySceneNode(ballList.get(index));
                ballList.remove(ballList.get(index));
            }
        }
    }
    
}
