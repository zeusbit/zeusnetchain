package org.fc.brewchain.peer;

import org.fc.brewchain.bcapi.crypto.EncHelper;
import org.fc.brewchain.bcapi.crypto.KeyPair;

public class TestEnc {
	public static void main(String[] args) {
		KeyPair pair = EncHelper.newKeyPair();
		System.out.println(pair);
		// org.fc.brewchain.bcapi.crypto.EncHelper.new
	}
}
