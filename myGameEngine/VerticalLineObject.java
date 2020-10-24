package myGameEngine;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import ray.rage.*;
import ray.rage.rendersystem.shader.*;
import ray.rage.scene.*;
import ray.rage.util.BufferUtil;
import ray.rage.rendersystem.states.*;
import ray.rage.rendersystem.*;

public class VerticalLineObject
{
    private Engine eng;

    public VerticalLineObject (Engine eng)
    {
        this.eng = eng;
    }

    public ManualObject makeLine(float height, int offset) throws IOException
    {
        ManualObject verticalLine = eng.getSceneManager().createManualObject("verticalLine" + Integer.toString(offset));
        ManualObjectSection verticalLineSec = verticalLine.createManualSection("verticalLineSec" + Integer.toString(offset));
        verticalLine.setGpuShaderProgram(eng.getSceneManager().getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));

        float[] vertices = new float[] { 0.0f, 0.0f, 0.0f, 0.0f, -height, 0.0f};

        float[] texcoords = new float[] { 0.0f, 0.0f, 0.0f, 1.0f};

        float[] normals = new float[] { 1.0f, 1.0f, 0.0f, -1.0f, -height, 0.0f};

        int[] indices = new int[] {0, 1};

        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
        FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
        FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
        IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);

        verticalLineSec.setVertexBuffer(vertBuf);
        verticalLineSec.setTextureCoordsBuffer(texBuf);
        verticalLineSec.setNormalsBuffer(normBuf);
        verticalLineSec.setIndexBuffer(indexBuf);

        TextureState verticalLineState = (TextureState) eng.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
        verticalLineState.setTexture(eng.getTextureManager().getAssetByPath("flatOrange.jpg"));
       
        verticalLine.setRenderState(verticalLineState);
        verticalLine.setMaterial(eng.getMaterialManager().getAssetByPath("default.mtl"));

        //Skybox fix
        RenderSystem rs = eng.getRenderSystem();
        ZBufferState zstate = (ZBufferState) rs.createRenderState(RenderState.Type.ZBUFFER);
        zstate.setTestEnabled(true);
        verticalLine.setRenderState(zstate);

        return verticalLine;
    }    
}
