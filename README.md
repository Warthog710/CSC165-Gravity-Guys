# CSC165-Final-Project

### Objectives Due by 12/12 (Project Submission):

* [ ] Add two different types of light in addition to ambient light, allowing the player to turn one on and off. **(Josh)**
* [x] Update the HUD information (possibly allow the user to toggle the HUD display). **(Quinn)**
* [ ] Get server IP information via command line, or prompt the user for it interactively. **(Quinn)**
* [ ] Make avatar texture better **(Josh)**
* [x] Server only accepts 8 players & automatically assigns an avatar color **(Quinn)**
* [x] Win/loss condition (win a player reaches the finish line, give them a point and start over...) **(Quinn)**
* [ ] Ghost avatars need animation and sound **(Josh)**
* [x] Platforms before finish line need to move back and forth
* [ ] Add keyboard controls **(Anyone)**
* [x] Have orbit camera focus be higher on the avatar **(Anyone)**
* [x] Do something with the ground so it does not look like it ends... **(Anyone)**
* [ ] When the NPC performs a *lookat()* at the player, make it not affect the rotations around the X-axis **(Anyone)**
* [ ] Uncomment code for Full-Screen-Dialog-Box **(Anyone)**
* [ ] Make player guide document **(Quinn & Josh)**

#### How to compile/run:

* ***runGame.bat*** compiles and runs the game. No server parameters are passed as these are now read from the "gameVariables.js" script. If you wish to update these values, change the *serverAddress* & *serverPort* variables.

* ***runServer.bat*** compiles and runs the server. A default port number (89) is passed. If you wish to change this. Change the parameter that is passed inside the batch file.

**IMPORTANT:** Please list sources for textures in source.txt files in the associated directory (if none exists please make one) this will make citing assets at the end much further. Please see the one I made for some textures if you are unsure about formatting.

#### **Basic Networking Implementation Info:**
* Client server info is recorded in the *gameVariables.js* script file. To connect to a different server change these values. Currently N clients are supported. The server faciliates updates between these clients by sending updates when requested. It currently supports both position and rotation.

#### **Basic Scripting Implementation Info:**
* All scripting operations are managed by the  *ScriptManager* class. This class can be passed a script file name (Ex: "test.js") and it will load the file into its local script engine (do this in setup scene). The variables in this file can be called using the *getValue(variableName)* method. When called, the script manager returns an object that will need to be cast to what you are expecting. To check for updates, call *checkUpdates(fileName)*. It returns true if the file was updated (after performing the update) or false if no update was detected.
* **Note:** the ScriptManager assumes all scripts are located in the *scripts* folder!
* **Note 2:** Deprecation warnings for *Nashorn* have been hidden. Ideally GraalVM would be used but this would require packing its source with our game... Not Ideal.
* **IMPORTANT:** Moving forward please try to incorporate game constants into script files. You are free and welcome to make new script files!
