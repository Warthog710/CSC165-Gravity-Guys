package myGameEngine;

import ray.physics.PhysicsEngine;
import ray.physics.PhysicsEngineFactory;
import ray.physics.PhysicsObject;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Matrix4;
import ray.rml.Matrix4f;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class PhysicsManager 
{
    private PhysicsEngine physicsEng;
    private ScriptManager scriptMan;

    public PhysicsManager(float gravity, ScriptManager scriptMan)
    {
        float[] grav = {0, gravity, 0};
        physicsEng = PhysicsEngineFactory.createPhysicsEngine("ray.physics.JBullet.JBulletPhysicsEngine");
        physicsEng.initSystem();
        physicsEng.setGravity(grav);

        this.scriptMan = scriptMan;
    }
    
    //Creates a sphere in the physics world 
    public void createSpherePhysicsObject(SceneNode node, float mass, float bounciness, float friction, float damping)
    {
        double[] temp = toDoubleArray(node.getLocalTransform().toFloatArray());
        
        PhysicsObject physObj = physicsEng.addSphereObject(physicsEng.nextUID(), mass, temp, 1.0f);
        physObj.setBounciness(bounciness);
        physObj.setFriction(friction);
        physObj.setDamping(damping, damping);
        node.setPhysicsObject(physObj);
    }

    public void createCubePhysicsObject(SceneNode node, float mass, float bounciness, float friction, float damping)
    {
    	double[] originalTransform = toDoubleArray(node.getLocalTransform().toFloatArray());
        
        double[] newTransform = {1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0,
        		0.0, 0.0, 1.0, 0.0, originalTransform[12], originalTransform[13], originalTransform[14], 1.0};
        
        float[] size = node.getLocalScale().toFloatArray();
        
        //Cube primitive is 2f
        size[0] = 2f * size[0];
        size[1] = 2f * size[1];
        size[2] = 2f * size[2];
  
        PhysicsObject physObj = physicsEng.addBoxObject(physicsEng.nextUID(), mass, newTransform, size);
        physObj.setBounciness(bounciness);
        physObj.setFriction(friction);
        physObj.setDamping(damping, damping);
        node.setPhysicsObject(physObj);
    }

    public void createStaticGroundPlane(SceneNode node, float bounciness, float friction, float damping, Vector3f scale)
    {
        double[] temp = toDoubleArray(node.getWorldTransform().toFloatArray());
        float[] up = {0, 1, 0};

        PhysicsObject gndPlaneP = physicsEng.addStaticPlaneObject(physicsEng.nextUID(), temp, up, 0.0f);
        gndPlaneP.setBounciness(bounciness);
        gndPlaneP.setFriction(friction);
        gndPlaneP.setDamping(damping, damping);
        gndPlaneP.setTransform(temp);

        //node.setLocalScale(scale);
        node.setPhysicsObject(gndPlaneP);      
    }

    public void updatePhysicsObjects(SceneManager sm)
    {
        for (SceneNode node : sm.getSceneNodes())
        {
            if (node.getPhysicsObject() != null)
            {
                Matrix4 mat = Matrix4f.createFrom(toFloatArray(node.getPhysicsObject().getTransform()));
                node.setLocalPosition(mat.value(0, 3), mat.value(1, 3), mat.value(2, 3));
            }
        }
    }

    public PhysicsEngine getPhysicsEngine()
    {
        return physicsEng;

    }

    public void updatePhysicsTransforms(SceneNode node)
    {
        if (node.getPhysicsObject() != null)
            node.getPhysicsObject().setTransform(toDoubleArray(node.getLocalTransform().toFloatArray()));
    }

    private double[] toDoubleArray(float[] fArray)
    {
        if (fArray == null)
            return null;

        double[] dArray = new double[fArray.length];

        for (int count = 0; count < fArray.length; count++)
            dArray[count] = (double)fArray[count];

        return dArray;          
    }

    private float[] toFloatArray(double[] dArray)
    {
        if (dArray == null)
            return null;

        float[] fArray = new float[dArray.length];

        for (int count = 0; count < dArray.length; count++)
            fArray[count] = (float)dArray[count];

        return fArray;          
    }
}
