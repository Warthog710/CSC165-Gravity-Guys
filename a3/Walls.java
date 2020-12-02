package a3;

import java.io.IOException;
import java.util.Vector;

import myGameEngine.PhysicsManager;
import myGameEngine.ScriptManager;
import myGameEngine.WallController;
import ray.rage.Engine;
import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.Entity;
import ray.rage.scene.Node;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3f;

public class Walls 
{
    private Vector<SceneNode> wallList;
    private ScriptManager scriptMan;
    private PhysicsManager physMan;
    private SceneManager sm;
    private WallController wc;
    
    public Walls(ScriptManager scriptMan, PhysicsManager physMan, Engine eng)
    {
        this.scriptMan = scriptMan;
        this.physMan = physMan;
        this.sm = eng.getSceneManager();
        this.wallList = new Vector<>();

        try
        {
            Entity wallE;
            SceneNode wallN;
            int offset = 0;

            Vector3f startPos = (Vector3f)this.scriptMan.getValue("wallStartingPos");
            Vector3f wallScale = (Vector3f)this.scriptMan.getValue("wallScale");

            Vector<Vector3f> startingPos = new Vector<>();

            Texture wallTex = eng.getTextureManager().getAssetByPath("walls.png");
            TextureState wallTexState = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
            wallTexState.setTexture(wallTex);

            //Create 6 walls
            for (int count = 0; count < 6; count++)
            {
                wallE = this.sm.createEntity("wall" + count, "customCube.obj");
                wallE.setPrimitive(Primitive.TRIANGLES);
                wallE.setRenderState(wallTexState);
                wallN = this.sm.getRootSceneNode().createChildSceneNode(wallE.getName() + "Node");
                wallN.attachObject(wallE);

                wallN.setLocalPosition(startPos.x(), startPos.y(), startPos.z() +  offset);
                wallN.setLocalScale(wallScale);

                //Make it a physics object and update the offset
                this.physMan.createCubePhysicsObject(wallN, 0, 0, 1, .9f);
                offset += Integer.parseInt(this.scriptMan.getValue("offset").toString());
                startingPos.add((Vector3f)Vector3f.createFrom(startPos.x(), startPos.y(), startPos.z() +  offset));

                //Add to wall list
                wallList.add(wallN);
            }

            //Create a wall controller and add the list
            wc = new WallController(physMan, scriptMan, sm.getSceneNode(scriptMan.getValue("avatarName").toString() + "Node"));
            wc.addNodeList(wallList);
            sm.addController(wc);
        }
        catch (IOException e)
        {
            System.out.println("Failed to create walls: " + e.getMessage());            
        }
    }

    public void resetWalls()
    {
        //Reset the wall controller
        wc.reset();

        //Reset all wall posisitions
        Vector3f startPos = (Vector3f)this.scriptMan.getValue("wallStartingPos");
        int offset = 0;

        for (SceneNode node : wallList)
        {
            node.setLocalPosition(startPos.x(), startPos.y(), startPos.z() +  offset);
            physMan.updatePhysicsPosition((Node)node);
            offset += Integer.parseInt(this.scriptMan.getValue("offset").toString());
        }
    }
    
    public Vector<SceneNode> getWalls()
    {
        return wallList;
    }
}
