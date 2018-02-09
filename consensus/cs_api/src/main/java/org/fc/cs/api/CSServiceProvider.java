package org.fc.cs.api;


public abstract interface CSServiceProvider {
	public abstract String getProviderid();

	public abstract String[] getContextConfigs();

	public abstract SpecDomainSupport getDaoByBeanName(SpecDomainSupport oJdao);
}
