
package bftsmart.statemanagement;

import bftsmart.tom.core.DeliveryThread;
import bftsmart.tom.core.TOMLayer;

/**
 * TODO: Don't know if this class will be used. For now, leave it here
 *
 *  Check if the changes for supporting dynamicity are correct
 *  
 * @author Joao Sousa
 */
public interface StateManager {

    public void requestAppState(int cid);
    
    public void analyzeState(int cid);

    public void stateTimeout();
    
    public void init(TOMLayer tomLayer, DeliveryThread dt);
    
    public void SMRequestDeliver(SMMessage msg, boolean isBFT);
    
    public void SMReplyDeliver(SMMessage msg, boolean isBFT);

    public void askCurrentConsensusId();
    
    public void currentConsensusIdAsked(int sender);
    
    public void currentConsensusIdReceived(SMMessage msg);
    
    public void setLastCID(int lastCID);
    
    public int getLastCID();
    
    public boolean isRetrievingState();
}
