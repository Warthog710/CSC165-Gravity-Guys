//Import necessary packages
var JavaPackages = new JavaImporter
(
		Packages.ray.rml.Vector3f,
		Packages.ray.rml.Degreef
);

with (JavaPackages)
{
	//Player avatar information
	var updateAvatarPos = true;
	var avatarPos = Vector3f.createFrom(0, 2, -4);
	
	//Default window size... Used only if the dialog box is not implemented
	var windowWidth = 1400;
	var windowHeight = 900;

    //Server info (string, int)
	//var serverAddress = "172.17.50.17";
	var serverAddress = "172.17.50.17";
    var serverPort = 89;

    //Hud position (int, int)
    var hudX = 15;
    var hudY = 15;

    //Tesselation values (int, float, 3x vector3f)
    var tessQuality = 7;
    var tessSubdivisions = 8.0;
	var terrainTessScale = Vector3f.createFrom(50, 100, 50);
	var heightTiling = 4;
	var normalTiling = 4;
	var textureTiling = 4;
	
	//Level values (2x vector3f)
	var levelScale = Vector3f.createFrom(1.4, 1.4, 1.4);
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

	//Level physics planes
	var startPhysicsPlanePos = levelPos.add(startPlatPos);
	startPhysicsPlanePos = startPhysicsPlanePos.add(0, -1, 0);
	var startPhysicsPlaneScale = Vector3f.createFrom(12.8, 1, 6.65);
	var plat1PhysicsPlanePos = levelPos.add(plat1Pos);
	plat1PhysicsPlanePos = plat1PhysicsPlanePos.add(-2.4, -1, 5.5);
	var plat1PhysicsPlaneScale = Vector3f.createFrom(4.45, 1, 12.8);
	var plat2PhysicsPlanePos = levelPos.add(plat2Pos);
	plat2PhysicsPlanePos = plat2PhysicsPlanePos.add(2.4, -1, 5.5);
	var plat2PhysicsPlaneScale = Vector3f.createFrom(4.45, 1, 12.8);
	var wedgePhysicsPlanePos = levelPos.add(wedgePlatPos);
	wedgePhysicsPlanePos = wedgePhysicsPlanePos.add(0, 7.5, 15.3);
	var wedgePhysicsPlaneScale = Vector3f.createFrom(8.3, 12, 1);
	var wedgePhysicsPlaneRotX = Degreef.createFrom(45.8);

	//Visibility of physics planes
	var startPhysicsPlaneVis = false;
	var plat1PhysicsPlaneVis = false;
	var plat2PhysicsPlaneVis = false;
	var wedgePhysicsPlaneVis = false;

	//Physiscs information
	var runPhysSim = true;	

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
	var startPhysicsPlane = "startPhysicsPlane";
	var plat1PhysicsPlane = "plat1PhysicsPlane";
	var plat2PhysicsPlane = "plat2PhysicsPlane";
	var wedgePhysicsPlane = "wedgePhysicsPlane";
}

