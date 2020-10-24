package myGameEngine;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import ray.rage.*;
import ray.rage.rendersystem.shader.*;
import ray.rage.scene.*;
import ray.rage.util.BufferUtil;
import ray.rage.rendersystem.states.*;
import ray.rage.asset.texture.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.DataSource;

public class GroundPlaneObject 
{
    private SceneManager sm;
    private Engine engine;

    public GroundPlaneObject(SceneManager sm, Engine engine)
    {
        this.sm = sm;
        this.engine = engine;
    }

    public ManualObject makeObject(String name) throws IOException
    {
        ManualObject groundPlane = sm.createManualObject(name);
        ManualObjectSection groundPlaneSec = groundPlane.createManualSection(name + "Section");
        groundPlane.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));

        float[] vertices = new float[] 
        { 
            -50f, 0f, -50f, 50f, 0f, 50f, 50f, 0f, -50f,
            -50f, 0f, -50f, -50f, 0f, 50f, 50f, 0f, 50f
        };

        float[] texcoords = new float[] 
        { 

            0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f 
        };

        float[] normals = new float[] 
        {
            -50f, 1f, -50f, 50f, 1f, 50f, 50f, 1f, -50f,
            -50f, 1f, -50f, -50f, 1f, 50f, 50f, 1f, 50f
        };

        int[] indices = new int[] { 0,1,2,3,4,5};
        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
        FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
        FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
        IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
        groundPlaneSec.setVertexBuffer(vertBuf);
        groundPlaneSec.setTextureCoordsBuffer(texBuf);
        groundPlaneSec.setNormalsBuffer(normBuf);
        groundPlaneSec.setIndexBuffer(indexBuf);
        Texture tex = engine.getTextureManager().getAssetByPath("oceanWater.jpg");
        TextureState texState = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
        texState.setTexture(tex);
        FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);
        groundPlane.setDataSource(DataSource.INDEX_BUFFER);
        groundPlane.setRenderState(texState);
        groundPlane.setRenderState(faceState);

        //Skybox fix
        RenderSystem rs = sm.getRenderSystem();
        ZBufferState zstate = (ZBufferState) rs.createRenderState(RenderState.Type.ZBUFFER);
        zstate.setTestEnabled(true);
        groundPlane.setRenderState(zstate);
        return groundPlane;
    }
}