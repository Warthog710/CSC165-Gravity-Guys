package myGameEngine;

import net.java.games.input.Controller;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.rage.scene.*;
import ray.rml.*;

public class OrbitCameraController 
{
    private SceneNode cameraN;
    private SceneNode target;
    private float azimuth;
    private float elevation;
    private float radius;
    private float currentPitch;
    private boolean isPitched;


    private Action orbitAroundAction, orbitElevationAction, orbitRadiusAction;

    public OrbitCameraController(SceneNode cameraN, SceneNode target, InputManager im)
    {
        this.cameraN = cameraN;
        this.target = target;
        this.azimuth = 180.0f;
        this.elevation = 20.0f;
        this.radius = 2.0f;
        this.currentPitch = 0.0f;
        this.isPitched = false;

        updateCameraPosition();
    }

    public void updateCameraPosition()
    {
        double theta = Math.toRadians(azimuth);
        double phi = Math.toRadians(elevation);
        double x = radius * Math.cos(phi) * Math.sin(theta);
        double y = radius * Math.sin(phi);
        double z = radius * Math.cos(phi) * Math.cos(theta);
        cameraN.setLocalPosition(Vector3f.createFrom((float)x, (float)y, (float)z).add(target.getWorldPosition()));
        cameraN.lookAt(target, Vector3f.createFrom(0.0f, 1.0f, 0.0f));

        //If the camera is pitched, pitch it...
        cameraN.pitch(Degreef.createFrom(-currentPitch));

    }

    public void updateAzimoth(float value)
    {
        azimuth += value;
        azimuth = azimuth % 360;
        updateCameraPosition();
    }

    public void setupInputs(InputManager im, Controller controller)
    {
        orbitAroundAction = new OrbitAroundAction();
        orbitElevationAction = new OrbitElevationAction();
        orbitRadiusAction = new OrbitRadiusAction();

        //If the controller passed is a gamepad
        if (controller.getType() == Controller.Type.GAMEPAD)
        {
            im.associateAction(controller, net.java.games.input.Component.Identifier.Axis.RX, orbitAroundAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
            im.associateAction(controller, net.java.games.input.Component.Identifier.Axis.RY, orbitElevationAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(controller, net.java.games.input.Component.Identifier.Axis.POV, orbitRadiusAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        }
    }

    public void mouseAzimuthAction(float value)
    {
        float rotateAmount = value * .072f;

        azimuth += rotateAmount;
        azimuth = azimuth % 360;
        updateCameraPosition();
    }

    public void mouseElevationAction(float value)
    {
        //If camera is not pitched, move the elevation
        if (!isPitched)
        {
            float elevationAmount = value *.072f;

            //If move would make the dolphin go above 90 degrees, don't make the move
            if ((elevation - elevationAmount) > 90)
            {
                return;
            }

            //If the camera would hit the ground plane, set it to the pitch state
            if ((elevation - elevationAmount) < -5)
            {
                elevationAmount = 0;
                isPitched = true;

                //Set initial pitch of 1.1 degree
                currentPitch = 1.1f;
            }

            elevation -= elevationAmount;
        }
        //Else the camera must be pitched... adjust that
        else
        {
            float pitchAmount = -value * .072f;

            //If pitch would be less than 1 degree reset pitch state and pitch
            if ((currentPitch - pitchAmount) < 1)
            {
                currentPitch = 0;
                isPitched = false;
                return;
            }

            //If the pitch owuld be greater than 90... don't do it
            if ((currentPitch - pitchAmount) > 90)
            {
                currentPitch = 90;
                return;
            }

            //Make the move
            currentPitch -= pitchAmount;              
        }
        updateCameraPosition();
    }

    public void mouseRadiusAction(float time, float value)
    {
        //Floats for zoom max and zoom min. The camera will never go beyond these
        float ZOOM_MAX = 10.0f;
        float ZOOM_MIN = 1.0f;        
        float radiusAmount = value * .004f * time;

        //If zoom would be beyond less than zero or greater than max... don't do it
        if ((radius + radiusAmount) < ZOOM_MIN || (radius + radiusAmount) > ZOOM_MAX)
        {
            return;
        }

        radius += radiusAmount;
        updateCameraPosition();
    }

    private class OrbitAroundAction extends AbstractInputAction
    {
        public void performAction(float time, net.java.games.input.Event evt)
        {
            float rotateAmount = 0.0f;

            // Deadzone
            if (evt.getValue() > -.2 && evt.getValue() < .2)
                return;

            rotateAmount = evt.getValue() *.072f * time;

            azimuth += rotateAmount;
            azimuth = azimuth % 360;
            updateCameraPosition();
        }
    }

    private class OrbitElevationAction extends AbstractInputAction
    {
        public void performAction(float time, net.java.games.input.Event evt)
        {
            //Deazone
            if (evt.getValue() > -.2 && evt.getValue() < .2)
                return;

            //If camera is not pitched, move the elevation
            if (!isPitched)
            {
                float elevationAmount = evt.getValue() *.072f * time;

                //If move would make the dolphin go above 90 degrees, don't make the move
                if ((elevation - elevationAmount) > 90)
                {
                    return;
                }

                //If the camera would hit the ground plane, set it to the pitch state
                if ((elevation - elevationAmount) < -1)
                {
                    elevationAmount = 0;
                    isPitched = true;

                    //Set initial pitch of 1.1 degree
                    currentPitch = 1.1f;
                }

                elevation -= elevationAmount;
            }
            //Else the camera must be pitched... adjust that
            else
            {
                float pitchAmount = -evt.getValue() * .072f * time;

                //If pitch would be less than 1 degree reset pitch state and pitch
                if ((currentPitch - pitchAmount) < 1)
                {
                    currentPitch = 0;
                    isPitched = false;
                    return;
                }

                //If the pitch owuld be greater than 90... don't do it
                if ((currentPitch - pitchAmount) > 90)
                {
                    currentPitch = 90;
                    return;
                }

                //Make the move
                currentPitch -= pitchAmount;              
            }
            updateCameraPosition();
        }
    }

    private class OrbitRadiusAction extends AbstractInputAction
    {
        public void performAction(float time, net.java.games.input.Event evt)
        {
            //Floats for zoom max and zoom min. The camera will never go beyond these
            float ZOOM_MAX = 10.0f;
            float ZOOM_MIN = 1.0f;

            float radiusAmount = 0.0f;

            //POV hat forward button is pressed
            if (evt.getValue() == .25)
            {
                radiusAmount = -.004f * time;
            }

            //POV hat backward button is pressed
            else if (evt.getValue() == .75)
            {
                radiusAmount = .004f * time;
            }

            //If zoom would be beyond less than zero or greater than max... don't do it
            if ((radius + radiusAmount) < ZOOM_MIN || (radius + radiusAmount) > ZOOM_MAX)
            {
                return;
            }

            radius += radiusAmount;
            updateCameraPosition();
        }
    }  
}
