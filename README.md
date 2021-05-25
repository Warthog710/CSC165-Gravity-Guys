# Gravity Guys

#### Description:
Gravity Guys is a 3D platformer with multiplayer networking with support for 8 players. It was developed in RAGE (Raymond's Awesome Game Engine). This is a barebones Java game engine built at Sacramento State University. Gravity Guys includes keyboard and gamepad support, accurate physics through the Bullet physics engine, and 3D positional sound.

#### How to Play:
The goal of our game is to reach the finish line. However, in your way are obstacles such as moving walls, rotating flails, and falling balls (and much more). Avoid these obstacles and navigate to the finish line. If at any time you fall off the platforms you can reset yourself back to the beginning using the reset player button found under our game controls. If the player is not connected to the server, reaching the finish line will reward a single point. After 10 seconds the game will reset (keeping your score) allowing you to go again. If the player is connected to the server, the first player to reach the finish line (assuming multiple players) will be awarded two points. After 10 seconds the game will reset like before. However, if other players are able to reach the finish line within the 10 second timer, they will be awarded a single point. After the game resets, the clients will display the current winner and the last player who won along with your current score.

#### Controls:
| Action | Keyboard/Mouse | Gamepad |
| :---: | :---: | :---: |
| Move Player Backward | W | Y Axis (Left Stick) |
| Move Player Forward | S | Y Axis (Left Stick) |
| Move Player Left | A | X Axis (Left Stick) |
| Move Player Right | D | X Axis (Left Stick) |
| Yaw Player Left | Q | Z Axis (Left Trigger) |
| Yaw Player Right | E | Z Axis (Right Trigger) |
| Move Camera Azimuth | Mouse X Axis | RX Axis (Right Stick) |
| Move Camera Elevation | Mouse Y Axis | RY Axis (Reft Stick) |
| Adjust Camera Radius | Scroll Wheel | POV Hat (Fwd/Bwd) |
| Jump | Space | Button 1 (A) |
| Reset Player | R | Button 6 (Right Button) |
| Toggle Light | F | Button 2 (B) |

#### How to Compile/Run:
*This game requires the presence of the RAGE game engine on the target machine. Please refer to the RAGE installation instructions in the next section of this README to fulfill that requirement.*
The game can be compiled and ran from the command windows by opening a CMD window inside the game directory and running the following commands:
 * ``javac a3\*.java <IP Address> <Port Number>``
 * ``java -Dsun.java2d.d3d=false -Dsun.java2d.uiScale=1 a3.MyGame``
 
Alternatively you can just use the provided batch files to perform this operation. Run *compile.bat* and *runGame.bat*. The latter batch file can be modified with the disered server IP and port number.

In order to establish networking the server must be run. This can be done by opening a CMD windows inside the game directory and running the following commands:
 * ``javac myServer\*.java``
 * ``java myServer.NetworkingServer <Port Number>``
 
Alternatively, you can just use the provided batch files to perform this operation. Run *compile.bat* and then *runServer.bat*. The latter batch file can be modified with the desired game server’s port number.

If you wish to connect a client to the server, you must include the server’s IP and port number either in the command line or inside the batch file used to run the client. If the game fails to parse the provided IP address and port number, it will default to port 89, and use your local IP address.

#### Installing RAGE
Please refer to the linked installation instructions below. The download for the RAGE game engine itself is also listed below.  
<a href="https://drive.google.com/file/d/1y6SOtXK0ixuxccEluz6XWlzsKh9JBxRr/view?usp=sharing">Installation Instructions</a>  
<a href="https://drive.google.com/file/d/1JazzoRfVDTn2vhEDjat2fxyHJgV_gBYk/view?usp=sharing">RAGE Game Engine</a><br><br>
Please note, this version of the RAGE game engine works with Java 11, in particular Java 11.0.8 (x64). It is recommended that the Java 11 JDK is installed on the target machine.

#### Link:
The player guide can be viewed below:  
<a href="https://drive.google.com/file/d/1-mMwuFQ0m4TVA8FujMjL1c7Y7HRdOU7b/view?usp=sharing">Player Guide</a>

