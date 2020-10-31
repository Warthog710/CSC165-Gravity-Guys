//Import necessary packages
var JavaPackages = new JavaImporter
(
    Packages.ray.rml.Vector3f,
    java.awt.Color
);

with (JavaPackages)
{
    //Set default location of the player avatar during world load
    var avatarPos = Vector3f.createFrom(0, .31, 0);

    //Ambient light color
    var ambColor = new Color(.3, .3, .3);

    //Server info
    var serverAddress = "172.17.50.17";
    var serverPort = 89;

    //Hud position
    var hudX = 15;
    var hudY = 15;
}

