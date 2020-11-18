# CSC165-Final-Project

#### MileStone #2 ToDo: **(Due before 11/20)**

- [x] Add collision on game platforms
- [x] Rework how moving acts so it uses forces instead of updating transforms. **(Josh)**
- [x] Implement jumping using the physics engine **(Josh)**
- [ ] Add player avatar with some form of limited animation **(Josh)**
- [ ] Rework how the "wishbone" is modelled so as to make collision detection more elegant. **(Josh)**
- [ ] Change how level starts/ends so as to end on some form of terrain to fulfill terrain requirement (this will require working out how to update the physics height...)
- [x] Make level bigger...
- [ ] At the top and bottom of the wedge we need regular platforms **(Josh)**

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
