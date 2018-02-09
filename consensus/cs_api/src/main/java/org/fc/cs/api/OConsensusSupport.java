package org.fc.cs.api;

import java.lang.reflect.Method;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.fc.cs.api.exception.CSException;
import onight.tfw.outils.serialize.SerializerUtil;

@Data
@Slf4j
public class OConsensusSupport<T> implements CSServiceProvider {
	private CSSuportSpec serviceSpec;
	private String domainName;
	private Class<T> domainClazz;

	private Class keyClazz;

	private String keyField;
	private List<Method> keyMethods;

	OConsensusSupport  csSupport;
	public void setCSsupport(OConsensusSupport support) {
			csSupport =  support;
	}

	

	public OConsensusSupport(CSSuportSpec serviceSpec, Class domainClazz, Class keyClazz) {
		super();
		this.serviceSpec = serviceSpec;
		this.domainName = domainClazz.getSimpleName().toLowerCase();
		this.domainClazz = domainClazz;
		this.keyClazz = keyClazz;
	}

	public OConsensusSupport() {
		super();
		this.serviceSpec = CSSuportSpec.NONE_CS;
	}

	
}

