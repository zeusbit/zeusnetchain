package org.fc.cs.api;

public interface ICSClient {

	public abstract void onCSServiceReady(CSSuportSpec dao);
	
	public abstract void onCSServiceAllReady();

}
