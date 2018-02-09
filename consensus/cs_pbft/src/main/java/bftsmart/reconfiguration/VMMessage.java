
package bftsmart.reconfiguration;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import bftsmart.communication.SystemMessage;

/**
 *
 * @author eduardo
 */
public class VMMessage extends SystemMessage{
    private ReconfigureReply reply;
    
    public VMMessage(){}
    
    public VMMessage(ReconfigureReply reply){
        super();
        this.reply = reply;
    }
    
     public VMMessage(int from, ReconfigureReply reply){
         super(from);
         this.reply = reply;
    }
     
     
      // Implemented method of the Externalizable interface
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(reply);
    }

    // Implemented method of the Externalizable interface
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.reply = (ReconfigureReply) in.readObject();
    }

    public ReconfigureReply getReply() {
        return reply;
    }
}
