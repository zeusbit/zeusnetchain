package org.fc.cs.api;

import lombok.Data;

@Data
public class CSSuportSpec {

	/** 共识方案 none,pbft,raft,dpos等... */
	private String target = "none";
	/** 存储策略 **/

	public static CSSuportSpec NONE_CS = new CSSuportSpec();
	public static CSSuportSpec PBFT_CS = new CSSuportSpec("pbft");
	public static CSSuportSpec RAFT_CS = new CSSuportSpec("raft");
	public static CSSuportSpec POS_CS = new CSSuportSpec("pos");
	public static CSSuportSpec DAG_CS = new CSSuportSpec("dag");
	public static CSSuportSpec POWPOS_CS = new CSSuportSpec("powpos");
	public static CSSuportSpec RMQ_CS = new CSSuportSpec("rabbitmq");


	public CSSuportSpec() {
	}

	

	public CSSuportSpec(String target) {
		super();
		this.target = target;
	};

}
