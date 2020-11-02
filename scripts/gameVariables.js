//Import necessary packages
var JavaPackages = new JavaImporter
(
        Packages.ray.rml.Vector3f
);

with (JavaPackages)
{
    //Player avatar information
    var avatarPos = Vector3f.createFrom(0, 0, 0);

    //Server info (string, int)
    var serverAddress = "172.17.50.17";
    var serverPort = 89;

    //Hud position (int, int)
    var hudX = 15;
    var hudY = 15;

    //Tesselation values (int, float, 3x vector3f)
    var tessQuality = 9;
    var tessSubdivisions = 8.0;
    var terrainTessScale = Vector3f.createFrom(50, 400, 40);
    var waterTessScale = Vector3f.createFrom(27, 10, 20);
    var waterPos = Vector3f.createFrom(0, 1.7, 0);
	
	//Level values (2x vector3f)
	var levelScale = Vector3f.createFrom(1, 1, 1);
	var levelPos = Vector3f.createFrom(0, 0, 30);

    //! DO NOT CHANGE DURING RUNTIME
    var terrainName = "terrainTess"
    var waterName = "waterTess"
    var avatarName = "playerAvatar"
	var levelName = "levelOne"
}

