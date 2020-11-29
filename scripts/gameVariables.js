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
	var avatarPos = Vector3f.createFrom(0, 12, 0);
	
	//Default window size... Used only if the dialog box is not implemented
	var windowWidth = 1400;
	var windowHeight = 900;

    //Server info (string, int)
	//var serverAddress = "172.17.50.17";
	var serverAddress = "192.168.68.106";
    var serverPort = 89;

    //Hud position (int, int)
    var hudX = 15;
    var hudY = 15;

    //Tesselation values (int, float, 3x vector3f)
    var tessQuality = 7;
    var tessSubdivisions = 16.0;
	var terrainTessScale = Vector3f.createFrom(200, 100, 200);
	var heightTiling = 16;
	var normalTiling = 16;
	var textureTiling = 16;
	
	//Level values (2x vector3f)
	var levelScale = Vector3f.createFrom(1.4, 1.4, 1.4);
	var levelPos = Vector3f.createFrom(0, 10, 0);
	
	//Level object values
	var startPlatScale = Vector3f.createFrom(1.5, 1, 2);
	var plat1Scale = Vector3f.createFrom(1, 1, 2);
	var plat2Scale = Vector3f.createFrom(1, 1, 2);
	var wishbonePlatScale = Vector3f.createFrom(3, 2.8, 6);
	var wedgePlatScale = Vector3f.createFrom(1, 1, 1);
	var startPlatPos = Vector3f.createFrom(0, 0, 0);
	var plat1Pos = Vector3f.createFrom(-5.98, 0, 13.93);
	var plat2Pos = Vector3f.createFrom(5.98, 0, 13.93);
	var wishbonePlatPos = Vector3f.createFrom(0, -0.7, 34.5);
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
	var plat3PhysicsPlanePos = levelPos.add(wedgePlatPos);
	plat3PhysicsPlanePos = plat3PhysicsPlanePos.add(0, -1, 4);
	var plat3PhysicsPlaneScale = Vector3f.createFrom(8.25, 1, 2.2);
	var plat4PhysicsPlanePos = levelPos.add(wedgePlatPos);
	plat4PhysicsPlanePos = plat4PhysicsPlanePos.add(0, 15.5, 25.2);
	var plat4PhysicsPlaneScale = Vector3f.createFrom(8.25, 1, 2.1);

	var wishBoneOnePos = levelPos.add(wishbonePlatPos);
	wishBoneOnePos = wishBoneOnePos.add(-4.6, -.3, 6);
	var wishBoneOneScale = Vector3f.createFrom(1, 10, 1);
	var wishBoneOneRotY = Degreef.createFrom(-24.6);

	var wishBoneTwoPos = levelPos.add(wishbonePlatPos);
	wishBoneTwoPos = wishBoneTwoPos.add(4.6, -.3, 6);
	var wishBoneTwoScale = Vector3f.createFrom(1, 10, 1);
	var wishBoneTwoRotY = Degreef.createFrom(24.6);


	//Visibility of physics planes
	var startPhysicsPlaneVis = false;
	var plat1PhysicsPlaneVis = false;
	var plat2PhysicsPlaneVis = false;
	var wedgePhysicsPlaneVis = false;
	var plat3PhysicsPlaneVis = false;
	var plat4PhysicsPlaneVis = false;
	var wishBoneOneVis = false;
	var wishBoneTwoVis = false;

	//Physiscs information
	var runPhysSim = true; 

	//Moving walls on left platform
	var offset = 4;
	var wallStartingPos = Vector3f.createFrom(8.3, 11, 7.5);
	var wallScale = Vector3f.createFrom(3, 1, .3);

	//NPC and platform
	var platformPos = Vector3f.createFrom(0, 9, 19);
	var platformScale = Vector3f.createFrom(1, 1, 10);
	var npcStartLocation = Vector3f.createFrom(0, 10.5, 10);

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
	var plat3PhysicsPlane = "plat3PhysicsPlane";
	var plat4PhysicsPlane = "plat3PhysicsPlane";
	var wedgePhysicsPlane = "wedgePhysicsPlane";
	var jumpAnimation = "jumpAnimation";
	var walkAnimation = "walkAnimation";
	var wishBoneOne = "wishBoneOne";
	var wishBoneTwo = "wishBoneTwo";
	var npcName = "npc";
}

