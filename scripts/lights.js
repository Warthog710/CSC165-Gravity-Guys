//Import necessary packages
var JavaPackages = new JavaImporter
(
    Packages.ray.rage.scene.SceneManager,  
    Packages.ray.rage.scene.Light,  
    Packages.ray.rage.scene.Light.Type,  
    Packages.ray.rage.scene.Light.Type.POINT, 
    Packages.ray.rml.Vector3f, 
    Packages.java.awt.Color
);

with (JavaPackages)
{
    //Create point light
    var lightName = "pLight1";
    var pLightPos = Vector3f.createFrom(0, 8, 0);
    var pLight = sm.createLight("pLight1", Light.Type.POINT);
    pLight.setAmbient(new Color(.1, .1, .1));
    pLight.setDiffuse(new Color(.7, .7, .7));
    pLight.setSpecular(new Color(1, 1, 1));
    pLight.setRange(30);
        

    //Ambient light color
    var ambColor = new Color(.3, .3, .3);
}