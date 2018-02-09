package org.fc.cs.api;
public interface CASExecutor {
	public Object doInTransaction();

	public Object lockBeforeExec();
}
