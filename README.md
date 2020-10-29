# CSC165-Final-Project

### How to compile/run:

* ***runGame.bat*** compiles and runs the game. Default parameters are passed inside this file. Most likely you will have to change these to properly interact with the server. For example, the first parameter "server IP" will need to be set to the IP the server prints when it starts.

* ***runServer.bat*** compiles and runs the server. A default port number (89) is passed. If you wish to change this. Change the parameter that is passed inside the batch file.


#### **Basic Networking Implementation Info:**
**Client Features:**
- [x] Client can join server
- [x] Client can send updates to the server update its position (currently only avatar position)
- [x] Client can ask WANTSDETAILSFOR to getall currenntly active clients information (currently only avatar position)
- [x] Client can say BYE and leave the server
- [x] Client can join a server even if the server is not active during startup (server polling)

**Server Features:**
- [x] Server holds the info of all clients inside of a Hashmap
- [x] Server sends CREATE messages to all other clients when a new client joins
- [x] Server sends BYE message to all other clients when a client leaves
- [x] Server informs a newly joined client that connection was sucessfull with a CONFIRM response
- [x] Server informs a newly joined client of all other clients information through a series of CREATE messages
- [x] Server sends a DETAILSFOR msg in response to a WANTSDETAILSFOR if the user the client existed exists

**TODO:**
- [ ] Have the game server save its IP and port # in a file. This way a client can grab that file if no parameters are passed. If parameters are passed, don't read from the file. If no file exists... Assume no server exists.
- [ ] If no parmaters are passed errors occur. Fail gracefully? Or assume no server exists.
- [ ] A client only sends BYE if the "ESC" key is used to exit the game. This needs to be called in all cases.
- [ ] If a client fails to say bye, the server is stuck with that client... Implement a timeout?
- [ ] Instead of requesting details for a single active client. Perform one request and the server responds with new details for all active clients. Only if an update has occured since the last time the specific client has asked...
- [ ] Pass a timestamp in the packet since you cannot rely on order in UDP. This prevents overwriting a newer position with an older position

