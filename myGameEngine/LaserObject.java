package myGameEngine;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import ray.rage.*;
import ray.rage.rendersystem.shader.*;
import ray.rage.scene.*;
import ray.rage.util.BufferUtil;
import ray.rage.rendersystem.states.*;
import ray.rage.asset.material.Material;
import ray.rage.asset.texture.*;
import ray.rage.rendersystem.*;

public class LaserObject
{
    private Engine eng;
    private Material mat;
    private Texture greenTex, redTex;

    public LaserObject (Engine eng) throws IOException
    {
        this.eng = eng;
        this.mat = eng.getMaterialManager().getAssetByPath("default.mtl");
        this.greenTex = eng.getTextureManager().getAssetByPath("laserGreen.jpg");
        this.redTex = eng.getTextureManager().getAssetByPath("laserRed.jpg");
    }

    //If color == true a green laser is made
    //If color == false a red laser is made
    public ManualObject makeLaser(boolean color, int offset)
    {
        ManualObject laser = eng.getSceneManager().createManualObject("laser" + Integer.toString(offset));
        ManualObjectSection laserSec = laser.createManualSection("laserSection" + Integer.toString(offset));
        laser.setGpuShaderProgram(eng.getSceneManager().getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));

        float[] vertices = new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, .5f};

        float[] texcoords = new float[] { 0.0f, 0.0f, 0.0f, 1.0f};

        float[] normals = new float[] { 1.0f, 1.0f, 0.0f, -1.0f, -1.0f, .5f};

        int[] indices = new int[] {0, 1};

        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
        FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
        FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
        IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);

        laserSec.setVertexBuffer(vertBuf);
        laserSec.setTextureCoordsBuffer(texBuf);
        laserSec.setNormalsBuffer(normBuf);
        laserSec.setIndexBuffer(indexBuf);

        TextureState laserState = (TextureState) eng.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
        
        //Set color based on the boolean
        //Note: Emissive gives weird behavior once multiple objects are added to the scene... Leaving it out for now
        if (color)
        {
            laserState.setTexture(greenTex);
            //mat.setEmissive(Color.GREEN);
        }
        else
        {
            laserState.setTexture(redTex);
            //mat.setEmissive(Color.RED);
        }
        
        laser.setRenderState(laserState);
        laser.setMaterial(mat);

        //Skybox fix
        RenderSystem rs = eng.getRenderSystem();
        ZBufferState zstate = (ZBufferState) rs.createRenderState(RenderState.Type.ZBUFFER);
        zstate.setTestEnabled(true);
        laser.setRenderState(zstate);

        return laser;
    }    
}
