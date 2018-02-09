
package bftsmart.statemanagement;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import bftsmart.communication.SystemMessage;
import bftsmart.reconfiguration.views.View;
import bftsmart.tom.util.TOMUtil;

/**
 * This class represents a message used in the state transfer protocol
 * 
 * @author Joao Sousa
 */
public abstract class SMMessage extends SystemMessage {

    private ApplicationState state; // State log
    private View view;
    private int cid; // Consensus ID up to which the sender needs to be updated
    private int type; // Message type
    private int regency; // Current regency
    private int leader; // Current leader
    public final boolean TRIGGER_SM_LOCALLY; // indicates that the replica should
                                             // initiate the SM protocol locally

    /**
     * Constructs a SMMessage
     * @param sender Process Id of the sender
     * @param cid Consensus ID up to which the sender needs to be updated
     * @param type Message type
     * @param replica Replica that should send the state
     * @param state State log
     */
    protected SMMessage(int sender, int cid, int type, ApplicationState state, View view, int regency, int leader) {
        super(sender);
        this.state = state;
        this.view = view;
        this.cid = cid;
        this.type = type;
        this.sender = sender;
        this.regency = regency;
        this.leader = leader;

        if (type == TOMUtil.TRIGGER_SM_LOCALLY && sender == -1) this.TRIGGER_SM_LOCALLY = true;
        else this.TRIGGER_SM_LOCALLY  = false;

    }

    protected SMMessage() {
        this.TRIGGER_SM_LOCALLY = false;
    }
    /**
     * Retrieves the state log
     * @return The state Log
     */
    public ApplicationState getState() {
        return state;
    }
    
    /**
     * Retrieves the state log
     * @return The state Log
     */
    public View getView() {
        return view;
    }
    
    /**
     * Retrieves the type of the message
     * @return The type of the message
     */
    public int getType() {
        return type;
    }

    /**
     * Retrieves the consensus ID up to which the sender needs to be updated
     * @return The consensus ID up to which the sender needs to be updated
     */
    public int getCID() {
        return cid;
    }

    /**
     * Retrieves the regency that the replica had when sending the state
     * @return The regency that the replica had when sending the state
     */
    public int getRegency() {
        return regency;
    }
    
    /**
     * Retrieves the leader that the replica had when sending the state
     * @return The leader that the replica had when sending the state
     */
    public int getLeader() {
        return leader;
    }
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException{
        super.writeExternal(out);
        out.writeInt(sender);
        out.writeInt(cid);
        out.writeInt(type);
        out.writeInt(regency);
        out.writeInt(leader);
        out.writeObject(state);
        out.writeObject(view);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        super.readExternal(in);
        sender = in.readInt();
        cid = in.readInt();
        type = in.readInt();
        regency = in.readInt();
        leader = in.readInt();
        state = (ApplicationState) in.readObject();
        view = (View) in.readObject();
    }
}
