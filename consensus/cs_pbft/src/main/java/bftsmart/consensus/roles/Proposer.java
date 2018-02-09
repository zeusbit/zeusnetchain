
package bftsmart.consensus.roles;

import bftsmart.communication.ServerCommunicationSystem;
import bftsmart.consensus.messages.MessageFactory;
import bftsmart.reconfiguration.ServerViewController;

/**
 * This class represents the proposer role in the consensus protocol.
 **/
public class Proposer {

    private MessageFactory factory; // Factory for PaW messages
    private ServerCommunicationSystem communication; // Replicas comunication system
    private ServerViewController controller;

    /**
     * Creates a new instance of Proposer
     * 
     * @param communication Replicas communication system
     * @param factory Factory for PaW messages
     * @param verifier Proof verifier
     * @param conf TOM configuration
     */
    public Proposer(ServerCommunicationSystem communication, MessageFactory factory,
            ServerViewController controller) {
        this.communication = communication;
        this.factory = factory;
        this.controller = controller;
    }

    /**
     * This method is called by the TOMLayer (or any other)
     * to start the consensus instance.
     *
     * @param cid ID for the consensus instance to be started
     * @param value Value to be proposed
     */
    public void startConsensus(int cid, byte[] value) {
        //******* EDUARDO BEGIN **************//
        communication.send(this.controller.getCurrentViewAcceptors(),
                factory.createPropose(cid, 0, value));
        //******* EDUARDO END **************//
    }
}
