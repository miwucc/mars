/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 *
 * 2017年3月2日 	
 */
package com.tsixi.mars.mongo;

import java.io.Serializable;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

/**
 *
 * mongo db 数据访问基类
 * 
 * @scene 1.0
 * 
 * @author Alan
 *
 */
public abstract class MarsMongoDao<T, ID extends Serializable> extends
		SimpleMongoRepository<T, ID> {

	protected MongoTemplate mongoTemplate;
	protected Class<T> clazz;

	public MarsMongoDao(Class<T> clazz, MongoTemplate mongoTemplate) {
		super(new MongoRepositoryFactory(mongoTemplate)
				.getEntityInformation(clazz), mongoTemplate);
		this.mongoTemplate = mongoTemplate;
		this.clazz = clazz;
	}
}
