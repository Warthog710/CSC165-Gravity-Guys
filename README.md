# CSC165-Final-Project

### How to compile/run:

* ***runGame.bat*** compiles and runs the game. No server parameters are passed as these are now read from the "gameVariables.js" script. If you wish to update these values, change the *serverAddress* & *serverPort* variables.

* ***runServer.bat*** compiles and runs the server. A default port number (89) is passed. If you wish to change this. Change the parameter that is passed inside the batch file.


#### **Basic Networking Implementation Info:**
**Client Features:**
- [x] Client can join server
- [x] Client can send updates to the server update its position (currently only avatar position)
- [x] Client can ask WANTSDETAILSFOR to getall currenntly active clients information (currently only avatar position)
- [x] Client can say BYE and leave the server
- [x] Client can join a server even if the server is not active during startup (server polling)
- [x] Client reads serverIP and serverPort number from the gameVariables script

**Server Features:**
- [x] Server holds the info of all clients inside of a Hashmap
- [x] Server sends CREATE messages to all other clients when a new client joins
- [x] Server sends BYE message to all other clients when a client leaves
- [x] Server informs a newly joined client that connection was sucessfull with a CONFIRM response
- [x] Server informs a newly joined client of all other clients information through a series of CREATE messages
- [x] Server sends a DETAILSFOR msg in response to a WANTSDETAILSFOR if the user the client existed exists

**TODO:**
- [ ] A client only sends BYE if the "ESC" key is used to exit the game. This needs to be called in all cases.
- [ ] If a client fails to say bye, the server is stuck with that client... Implement a timeout?
- [ ] Instead of requesting details for a single active client. Perform one request and the server responds with new details for all active clients. Only if an update has occured since the last time the specific client has asked...
- [ ] Pass a timestamp in the packet since you cannot rely on order in UDP. This prevents overwriting a newer position with an older position
- [ ] Merge all calls to NetworkedClient in MyGame.update() to a single call... This will clean up the code a bit.
- [ ] Have game server print out public IP

#### **Basic Scripting Implementation Info:**
* All scripting operations are managed by the  *ScriptManager* class. This class can be passed a script file name (Ex: "test.js") and it will load the file into its local script engine (do this in setup scene). The variables in this file can be called using the *getValue(fileName, variableName)* method. When called, the script manager automatically checks for an updated value before returning.
* **Note:** the ScriptManager assumes all scripts are located in the *scripts* folder!
* **Note 2:** Deprecation warnings for *Nashorn* have been hidden. Ideally GraalVM would be used but this would require packing its source with our game... Not Ideal.
* **IMPORTANT:** Moving forward please try to incorporate game constants into script files. You are free and welcome to make new script files!