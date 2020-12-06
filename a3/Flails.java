package a3;

import java.io.IOException;

import myGameEngine.FlailController;
import myGameEngine.PhysicsManager;
import myGameEngine.ScriptManager;
import ray.rage.Engine;
import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.Entity;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3f;

public class Flails 
{
    private Engine eng;
    private SceneManager sm;
    private PhysicsManager physMan;
    private ScriptManager scriptMan;

    public Flails(Engine eng, PhysicsManager physMan, ScriptManager scriptMan)
    {
        this.eng = eng;
        this.sm = eng.getSceneManager();
        this.physMan = physMan;
        this.scriptMan = scriptMan;

        try
        {
            setupFlails();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void setupFlails() throws IOException
    {
        String pillar = scriptMan.getValue("pillarName").toString();
        String flail = scriptMan.getValue("flailName").toString();
        String flailCube = scriptMan.getValue("flailCubeName").toString();

        Texture redCube = eng.getTextureManager().getAssetByPath("redCube.png");
        Texture pillarTex = eng.getTextureManager().getAssetByPath("pillar.png");
        TextureState redCubeState = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
        TextureState pillarTexState = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
        redCubeState.setTexture(redCube);
        pillarTexState.setTexture(pillarTex);

        for (int index = 0; index < 8; index++)
        {
            //Create the flail (composed of 3 entities)
            Entity pillarE = sm.createEntity(pillar + index, "cylinder.obj");
            pillarE.setPrimitive(Primitive.TRIANGLES);
            pillarE.setRenderState(pillarTexState);
            SceneNode pillarN = sm.getRootSceneNode().createChildSceneNode(pillarE.getName() + "Node");
            pillarN.attachObject(pillarE);
            pillarN.setLocalPosition((Vector3f)scriptMan.getValue(pillar + index + "Pos"));
            pillarN.setLocalScale((Vector3f)scriptMan.getValue(pillar + "Scale"));
            physMan.createFlailPhysicsObject(pillarN, 0, 0, 1, .99f);

            Entity flailE = sm.createEntity(flail + index, "cylinder.obj");
            flailE.setPrimitive(Primitive.TRIANGLES);
            SceneNode flailN = sm.getRootSceneNode().createChildSceneNode(flailE.getName() + "Node");
            flailN.attachObject(flailE);
            flailN.setLocalPosition((Vector3f)scriptMan.getValue(flail + index + "Pos"));
            flailN.setLocalScale((Vector3f)scriptMan.getValue(flail + "Scale"));

            Entity cubeE = sm.createEntity(flailCube + index, "cube.obj");
            cubeE.setPrimitive(Primitive.TRIANGLES);
            cubeE.setRenderState(redCubeState);
            SceneNode cubeN = flailN.createChildSceneNode(cubeE.getName() + "Node");
            cubeN.attachObject(cubeE);
            cubeN.setLocalPosition((Vector3f)scriptMan.getValue(flailCube + "Pos"));
            cubeN.setLocalScale((Vector3f)scriptMan.getValue(flailCube + "Scale"));

            //Each flail has its own controller 
            FlailController fc = new FlailController(scriptMan, pillarN, Float.parseFloat(scriptMan.getValue("flailSpeed").toString()), 1f, 0f, true, sm.getSceneNode(scriptMan.getValue("avatarName").toString() + "Node"));
            fc.addNode(flailN);
            sm.addController(fc);
        }
    }    
}
