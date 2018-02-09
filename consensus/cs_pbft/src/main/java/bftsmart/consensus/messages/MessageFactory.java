
package bftsmart.consensus.messages;

/**
 * This class work as a factory of messages used in the paxos protocol.
 */
public class MessageFactory{

    // constants for messages types
    public static final int PROPOSE = 44781;
    public static final int WRITE    = 44782;
    public static final int ACCEPT  = 44783;

    private int from; // Replica ID of the process which sent this message

    /**
     * Creates a message factory
     * @param from Replica ID of the process which sent this message
     */
    public MessageFactory(int from) {

        this.from = from;

    }

    /**
     * Creates a PROPOSE message to be sent by this process
     * @param id Consensus's execution ID
     * @param epoch Epoch number
     * @param value Proposed value
     * @param proof Proofs from other replicas
     * @return A paxos message of the PROPOSE type, with the specified id, epoch, value, and proof
     */
    public ConsensusMessage createPropose(int id, int epoch, byte[] value) {

        return new ConsensusMessage(PROPOSE, id, epoch, from, value);

    }

    /**
     * Creates a WRITE message to be sent by this process
     * @param id Consensus's execution ID
     * @param epoch Epoch number
     * @param value Write value
     * @return A consensus message of the WRITE type, with the specified id, epoch, and value
     */
    public ConsensusMessage createWrite(int id, int epoch, byte[] value) {

        return new ConsensusMessage(WRITE,id,epoch, from, value);

    }

    /**
     * Creates a WRITE message to be sent by this process
     * @param id Consensus's execution ID
     * @param epoch Epoch number
     * @param value Accepted value
     * @return A consensus message of the ACCEPT type, with the specified id, epoch, and value
     */
    public ConsensusMessage createAccept(int id, int epoch, byte[] value) {

        return new ConsensusMessage(ACCEPT,id,epoch, from, value);

    }


}

