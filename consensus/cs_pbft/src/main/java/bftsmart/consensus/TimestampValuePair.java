
package bftsmart.consensus;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigInteger;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

/**
 * This class associates a timestamp to a value
 * 
 * @author Joao Sousa
 */
public class TimestampValuePair implements Externalizable {

    private int timestamp; // timestamp
    private byte[] value; // value
    private byte[] hashedValue; // hash of the value
    

    /**
     * Constructor
     * @param timestamp Timestamp
     * @param value Value
     */
    public TimestampValuePair(int timestamp, byte[] value) {
        this.timestamp = timestamp;
        this.value = value;

        this.hashedValue = new byte[0];
    }

    /**
     * Empty construtor
     */
    public TimestampValuePair() {
        this.timestamp = -1;
        this.value = new byte[0];

        this.hashedValue = new byte[0];
    }
    /**
     * Set the value's hash
     * @param hashedValue Sintese do valor
     */
    public void setHashedValue(byte[] hashedValue) {
        this.hashedValue = hashedValue;
    }

    /**
     * Get the value's hash
     * @return hash of the value
     */
    public byte[] getHashedValue() {
        return hashedValue;
    }

    /**
     * Get timestamp
     * @return The timestamp
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * Get value
     * @return Value
     */
    public byte[] getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TimestampValuePair) {
            return ((TimestampValuePair) o).timestamp == timestamp &&
                    Arrays.equals(((TimestampValuePair) o).value,value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + timestamp;
        hash = hash * 31 + (new BigInteger(value)).intValue();
        return hash;
    }

    public String toString() {
                
        if (this.value == null || this.value.length == 0) return timestamp + " :: []";
        else return timestamp + " :: " + (this.hashedValue != null && this.hashedValue.length > 0 ? str(this.hashedValue) : str(value));
    }
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException{

        out.writeInt(timestamp);
        out.writeObject(value);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{

        timestamp = in.readInt();
        value = (byte[]) in.readObject();
    }
    
    private String str(byte[] obj) {
        if(obj == null) {
            return "null";
        } else {
            return Base64.encodeBase64String(obj);
        }
    }
}
