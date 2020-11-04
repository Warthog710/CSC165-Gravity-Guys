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
	var terrainTessScale = Vector3f.createFrom(200, 600, 200);
	var heightTiling = 4;
	var normalTiling = 4;
	var textureTiling = 4;
	
	//Level values (2x vector3f)
	var levelScale = Vector3f.createFrom(1, 1, 1);
	var levelPos = Vector3f.createFrom(0, 20, 0);
	
	//Level object values
	var startPlatScale = Vector3f.createFrom(1.5, 1, 2);
	var plat1Scale = Vector3f.createFrom(1, 1, 2);
	var plat2Scale = Vector3f.createFrom(1, 1, 2);
	var wishbonePlatScale = Vector3f.createFrom(6, 3, 3);
	var wedgePlatScale = Vector3f.createFrom(1, 1, 1);
	var startPlatPos = Vector3f.createFrom(0, 0, 0);
	var plat1Pos = Vector3f.createFrom(-5.98, 0, 13.93);
	var plat2Pos = Vector3f.createFrom(5.98, 0, 13.93);
	var wishbonePlatPos = Vector3f.createFrom(0, -0.7, 30.8);
	var wedgePlatPos = Vector3f.createFrom(0, 0, 46.5);
	

    //! DO NOT CHANGE DURING RUNTIME
    var terrainName = "terrainTess";
    var waterName = "waterTess";
    var avatarName = "playerAvatar";
	var levelName = "levelOne";
	var startPlatName = "startingPlatform";
	var plat1Name = "platform1";
	var plat2Name = "platform2";
	var wishbonePlatName = "wishbonePlatform";
	var wedgePlatName = "wedgePlatform";
}

