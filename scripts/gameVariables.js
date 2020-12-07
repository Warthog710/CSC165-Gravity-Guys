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

	//Start
	//var avatarPos = Vector3f.createFrom(0, 12, 0);

	//Top of wedge
	var avatarPos = Vector3f.createFrom(0, 27, 72);

	//Below wedge
	//var avatarPos = Vector3f.createFrom(0, 15, 50);
	//var avatarPos = Vector3f.createFrom(0, 10, 45);
	
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

    //Tessellation values (int, float, 3x vector3f)
    var tessQuality = 7;
    var tessSubdivisions = 64.0;
	var terrainTessScale = Vector3f.createFrom(800, 100, 800);
	var heightTiling = 64;
	var normalTiling = 64;
	var textureTiling = 64;
	
	//Level values (2x vector3f)
	var levelScale = Vector3f.createFrom(1.4, 1.4, 1.4);
	var levelPos = Vector3f.createFrom(0, 10, 0);
	
	//Level object values
	var startPlatScale = Vector3f.createFrom(1.5, 1, 2);
	var plat1Scale = Vector3f.createFrom(1, 1, 2);
	var plat2Scale = Vector3f.createFrom(1, 1, 2);
	var wishbonePlatScale = Vector3f.createFrom(3, 2.8, 6);
	var wedgePlatScale = Vector3f.createFrom(1, 1, 1);
	var endPlat1Scale = Vector3f.createFrom(0.75, 0.125, 0.5);
	var endPlat2Scale = Vector3f.createFrom(0.75, 0.125, 0.5);
	var endPlat3Scale = Vector3f.createFrom(0.75, 0.125, 0.5);
	var endPlat4Scale = Vector3f.createFrom(0.75, 0.125, 0.5);
	var endPlat5Scale = Vector3f.createFrom(0.75, 0.125, 0.5);
	var finishPlatScale = Vector3f.createFrom(1, 1, 1);
	var startPlatPos = Vector3f.createFrom(0, 0, 0);
	var plat1Pos = Vector3f.createFrom(-5.98, 0, 13.93);
	var plat2Pos = Vector3f.createFrom(5.98, 0, 13.93);
	var wishbonePlatPos = Vector3f.createFrom(0, -0.7, 34.5);
	var wedgePlatPos = Vector3f.createFrom(0, 0, 46.5);
	var endPlat1Pos = Vector3f.createFrom(0, 10, 57);
	var endPlat2Pos = Vector3f.createFrom(-2, 8, 62);
	var endPlat3Pos = Vector3f.createFrom(1, 6, 67);
	var endPlat4Pos = Vector3f.createFrom(4, 4, 72);
	var endPlat5Pos = Vector3f.createFrom(-2, 2, 77);
	var finishPlatPos = Vector3f.createFrom(0, 0, 85);

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

	//End platforms
	var endPlat1PhysicsPlaneScale = Vector3f.createFrom(3.34, .4, 3.2);
	var endPlat2PhysicsPlaneScale = Vector3f.createFrom(3.34, .4, 3.2);
	var endPlat3PhysicsPlaneScale = Vector3f.createFrom(3.34, .4, 3.2);
	var endPlat4PhysicsPlaneScale = Vector3f.createFrom(3.32, .4, 3.2);
	var endPlat5PhysicsPlaneScale = Vector3f.createFrom(3.32, .4, 3.2);
	var endPlat1PhysicsPlanePos = Vector3f.createFrom(0, 23.6, 79.8);
	var endPlat2PhysicsPlanePos = Vector3f.createFrom(-2.8, 20.8, 86.8);
	var endPlat3PhysicsPlanePos = Vector3f.createFrom(1.4, 18, 93.8);
	var endPlat4PhysicsPlanePos = Vector3f.createFrom(5.6, 15.2, 100.8);
	var endPlat5PhysicsPlanePos = Vector3f.createFrom(-2.82, 12.4, 107.8);
	
	//Finish platform
	var finishPlatPhysicsPlanePos = levelPos.add(finishPlatPos);
	finishPlatPhysicsPlanePos = finishPlatPhysicsPlanePos.add(0, -3.45, 34);
	var finishPlatPhysicsPlaneScale = Vector3f.createFrom(7.2533, 3.45, 4.79);

	//Wishbone cylinder
	var wishBoneOnePos = levelPos.add(wishbonePlatPos);
	wishBoneOnePos = wishBoneOnePos.add(-4.6, -.3, 6);
	var wishBoneOneScale = Vector3f.createFrom(1, 10, 1);
	var wishBoneOneRotY = Degreef.createFrom(-24.6);
	var wishBoneTwoPos = levelPos.add(wishbonePlatPos);
	wishBoneTwoPos = wishBoneTwoPos.add(4.6, -.3, 6);
	var wishBoneTwoScale = Vector3f.createFrom(1, 10, 1);
	var wishBoneTwoRotY = Degreef.createFrom(24.6);

	//Wishbone physics plane
	var wishBoneThreePos = levelPos.add(wishbonePlatPos);
	wishBoneThreePos = wishBoneThreePos.add(4.5, -.3, 6);
	var wishBoneThreeScale = Vector3f.createFrom(.5, 1, 9.2);
	var wishBoneThreeRotY = Degreef.createFrom(-24.6);

	var wishBoneFourPos = levelPos.add(wishbonePlatPos);
	wishBoneFourPos = wishBoneFourPos.add(-4.5, -.3, 6);
	var wishBoneFourScale = Vector3f.createFrom(.5, 1, 9.2);
	var wishBoneFourRotY = Degreef.createFrom(24.6);

	//Visibility of physics planes
	var startPhysicsPlaneVis = false;
	var plat1PhysicsPlaneVis = false;
	var plat2PhysicsPlaneVis = false;
	var wedgePhysicsPlaneVis = false;
	var plat3PhysicsPlaneVis = false;
	var plat4PhysicsPlaneVis = false;
	var endPlat1PhysicsPlaneVis = true;
	var endPlat2PhysicsPlaneVis = true;
	var endPlat3PhysicsPlaneVis = true;
	var endPlat4PhysicsPlaneVis = true;
	var endPlat5PhysicsPlaneVis = true;
	var wishBoneOneVis = false;
	var wishBoneTwoVis = false;
	var wishBoneThreeVis = false;
	var wishBoneFourVis = false;
	var finishPlatPhysicsPlaneVis = false;

	//Physiscs information
	var runPhysSim = true; 

	//Moving walls on left platform
	var offset = 4;
	var wallStartingPos = Vector3f.createFrom(8.3, 11, 7.5);
	var wallScale = Vector3f.createFrom(3, 1, .3);

	//Flails
	pillar0Pos = Vector3f.createFrom(-5.5, 11, 9);
	flail0Pos = pillar0Pos.add(0, 0, -1);
	pillar1Pos = Vector3f.createFrom(-11.2, 11, 9);
	flail1Pos = pillar1Pos.add(0, 0, -1);
	pillar2Pos = Vector3f.createFrom(-5.5, 11, 19);
	flail2Pos = pillar2Pos.add(0, 0, -1);
	pillar3Pos = Vector3f.createFrom(-11.2, 11, 19);
	flail3Pos = pillar3Pos.add(0, 0, -1);
	pillar4Pos = Vector3f.createFrom(-5.5, 11, 29);
	flail4Pos = pillar4Pos.add(0, 0, -1);
	pillar5Pos = Vector3f.createFrom(-11.2, 11, 29);
	flail5Pos = pillar5Pos.add(0, 0, -1);
	pillar6Pos = Vector3f.createFrom(-8, 11, 14);
	flail6Pos = pillar6Pos.add(0, 0, -1);
	pillar7Pos = Vector3f.createFrom(-8, 11, 24);
	flail7Pos = pillar7Pos.add(0, 0, -1);

	//Constant across all flails
	flailCubePos = Vector3f.createFrom(0, 0, 1);
	pillarScale = Vector3f.createFrom(.5, 1, .5);
	flailScale = Vector3f.createFrom(.25, .9, .25);
	flailCubeScale = Vector3f.createFrom(.8, .2, 1.5);
	flailSpeed = 15;

	//NPC and platform
	var platformPos = Vector3f.createFrom(0, 9, 19);
	var platformScale = Vector3f.createFrom(1, 1, 10);
	var npcStartLocation = Vector3f.createFrom(0, 10, 10);

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
	var endPlat1Name = "endPlatform1";
	var endPlat2Name = "endPlatform2";
	var endPlat3Name = "endPlatform3";
	var endPlat4Name = "endPlatform4";
	var endPlat5Name = "endPlatform5";
	var finishPlatName = "finishPlatform";
	var startPhysicsPlane = "startPhysicsPlane";
	var plat1PhysicsPlane = "plat1PhysicsPlane";
	var plat2PhysicsPlane = "plat2PhysicsPlane";
	var plat3PhysicsPlane = "plat3PhysicsPlane";
	var plat4PhysicsPlane = "plat3PhysicsPlane";
	var endPlat1PhysicsPlane = "endPlat1PhysicsPlane";
	var endPlat2PhysicsPlane = "endPlat2PhysicsPlane";
	var endPlat3PhysicsPlane = "endPlat3PhysicsPlane";
	var endPlat4PhysicsPlane = "endPlat4PhysicsPlane";
	var endPlat5PhysicsPlane = "endPlat5PhysicsPlane";
	var finishPlatPhysicsPlane = "finishPlatPhysicsPlane";
	var wedgePhysicsPlane = "wedgePhysicsPlane";
	var jumpAnimation = "jumpAnimation";
	var walkAnimation = "walkAnimation";
	var wishBoneOne = "wishBoneOne";
	var wishBoneTwo = "wishBoneTwo";
	var wishBoneThree = "wishBoneThree";
	var wishBoneFour = "wishBoneFour";
	var npcName = "npc";
	var pillarName = "pillar";
	var flailName = "flail";
	var flailCubeName = "flailCube";

}

