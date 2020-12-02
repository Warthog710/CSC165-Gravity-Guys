package myServer;

import java.util.UUID;

import myGameEngine.ObjectDistance;
import ray.ai.behaviortrees.*;
import ray.rml.Vector3f;

public class NPCController implements Runnable
{
    private GameServer myGameServer;
    private Vector3f npcPos;
    private BehaviorTree bTree;
    private UUID targetNPC;
    private long lastUpdateTime;

    public NPCController(GameServer myGameServer)
    {
        this.myGameServer = myGameServer;
        this.bTree = new BehaviorTree(BTCompositeType.SELECTOR);
        this.lastUpdateTime = System.currentTimeMillis();
        npcPos = (Vector3f)Vector3f.createFrom(0f, 10f, 10f);

        setupBehaviorTree();
    }

    private void setupBehaviorTree()
    {
        bTree = new BehaviorTree(BTCompositeType.SELECTOR);
        bTree.insertAtRoot(new BTSequence(10));
        bTree.insertAtRoot(new BTSequence(20));
        bTree.insert(10, new PlayerInRange(false));
        bTree.insert(10, new BlowPlayerAway());
        bTree.insert(20, new MoveToWaypoint());
    }

    //NPC Loop!
    @Override
    public void run() 
    {
        while (myGameServer.threadRunning)
        {
            long timeElapsed = System.currentTimeMillis() - lastUpdateTime;

            //Update the behavior tree
            bTree.update(timeElapsed);

            //Update last update
            lastUpdateTime = System.currentTimeMillis();

            //Sleep for 16ms, target update rate is 60 times a second
            try
            {
                Thread.sleep(16);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }

    //Condition: Checks if the player is in range
    private class PlayerInRange extends BTCondition 
    {
        private float range = 9f;

        public PlayerInRange(boolean toNegate) 
        {
            super(toNegate);
        }

        @Override
        protected boolean check() 
        {
            float minRange = -1;

            //Iterate through all players and saving the one that is closest
            for (ClientInfo info : myGameServer.clientInfo.values())
            {
                String[] tokens = info.pos.split(",");             
                Vector3f temp = (Vector3f)Vector3f.createFrom(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));

                if (ObjectDistance.distanceBetweenVectors(temp, npcPos) < minRange || minRange == -1)
                {
                    minRange = ObjectDistance.distanceBetweenVectors(temp, npcPos);
                    targetNPC = info.clientID;                    
                }
            }

            //If the min range is less than range return true, else false
            if (minRange <= range && myGameServer.clientInfo.size() > 0)
                return true;

            return false;
        }
    }

    //Action: If the player is in range, blow the player away
    private class BlowPlayerAway extends BTAction 
    {
        @Override
        protected BTStatus update(float timeElapsed) 
        {
            myGameServer.sendMsgToClient("BLOW," + timeElapsed, targetNPC);
            myGameServer.forwardToClients("ROTATEFAN", targetNPC);

            return BTStatus.BH_SUCCESS;
        }        
    }

    //Action: Move toward the waypoint
    private class MoveToWaypoint extends BTAction
    {
        private boolean movingForward;
        private float movementMult;
        private float range = .6f;

        public MoveToWaypoint()
        {
            movementMult = .001f; 
            movingForward = true;        
        }

        @Override
        protected BTStatus update(float timeElapsed) 
        {
            //Check to see if we should be moving back or forward
            if (ObjectDistance.distanceBetweenVectors((Vector3f)Vector3f.createFrom(0, 10, 10), npcPos) < range)
                movingForward = true;
            else if (ObjectDistance.distanceBetweenVectors((Vector3f)Vector3f.createFrom(0, 10, 28), npcPos) < range)
                movingForward = false;
             
            //Move the NPC
            if (movingForward)
                npcPos = (Vector3f)Vector3f.createFrom(npcPos.x(), npcPos.y(), npcPos.z() + movementMult * timeElapsed);
            else
                npcPos = (Vector3f)Vector3f.createFrom(npcPos.x(), npcPos.y(), npcPos.z() + -movementMult * timeElapsed);

            
            String msg = "NPCPOS," + npcPos.x() + "," + npcPos.y() + "," + npcPos.z();

            //Send NPC info but sync with the gameserver
            myGameServer.sendNPCInfo(msg);

            return BTStatus.BH_SUCCESS;            
        }        
    }    
}
