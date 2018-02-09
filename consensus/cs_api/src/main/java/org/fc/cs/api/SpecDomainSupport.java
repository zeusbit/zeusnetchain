package org.fc.cs.api;

import java.util.List;

import org.fc.cs.api.exception.CSException;

/**
 * 参数说明：<br>
 * domains: 设置查询类型，表名等信息<br>
 * entity: 序列化后的数据<br>
 * 
 */
public interface SpecDomainSupport<T> {

	/**
	 * @param dc
	 * @param entity
	 *            pojoJson
	 * @throws CSException
	 */
	Object insert(CSSuportSpec dc, T entity) throws CSException;

	/**
	 * @param dc
	 * @param entities
	 *            serialize list
	 * @throws CSException
	 */
	Object batchInsert(CSSuportSpec dc, List<T> entities) throws CSException;

	
	/**
	 * 
	 * @param dc
	 * @param entity
	 *            ID
	 * @return pojoJson
	 * @throws CSException
	 */
	Object findOne(CSSuportSpec dc, T entity) throws CSException;

	/**
	 * @param dc
	 * @param entity
	 * @return
	 * @throws CSException
	 */
	Object findByExample(CSSuportSpec dc, T entity) throws CSException;

		/**
	 * 
	 * @param dc
	 * @param entity
	 *            pojoJson
	 * @return boolean
	 * @throws CSException
	 */
	Object exists(CSSuportSpec dc, T entity) throws CSException;

	/**
	 * 
	 * @param dc
	 * @throws CSException
	 */
	Object count(CSSuportSpec dc, T entity) throws CSException;


}
