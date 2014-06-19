/*
 * Copyright 2010-2014  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */
package org.covito.kit.cache.ehcache;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;

import org.covito.kit.cache.common.AbstractCacheImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

/**
 * EhCache包装类
 * <p>
 * 功能详细描述
 * </p>
 * 
 * @author covito
 * @version [v1.0, 2014年6月10日]
 */
public class EhCacheWrp extends AbstractCacheImpl {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Ehcache cache;

	/**
	 * Constructor
	 * 
	 * @param cacheManager
	 * @param cache
	 */
	public EhCacheWrp(EhCacheManager cacheManager, Ehcache cache) {
		Assert.notNull(cache, "Ehcache must not be null");
		Status status = cache.getStatus();
		Assert.isTrue(Status.STATUS_ALIVE.equals(status),
				"An 'alive' Ehcache is required - current cache is " + status.toString());
		this.cacheManager = cacheManager;
		this.cache = cache;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @author covito
	 * @return
	 */
	@Override
	public String getName() {
		return cache.getName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @author covito
	 * @return
	 */
	@Override
	public Object getNativeCache() {
		return this.cache;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @author covito
	 * @param key
	 * @return
	 */
	@Override
	public ValueWrapper get(Object key) {
		Element ele = this.cache.get(key);
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("----->Get cache by name {}, key {}", getName(), key);
			if (ele != null) {
				this.logger.debug("<----- Cache  result {}", ele.getObjectValue());
			}
		}
		return ((ele != null) ? new SimpleValueWrapper(fromStoreValue(ele.getObjectValue())) : null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @author covito
	 * @param key
	 * @param value
	 */
	@Override
	public void put(Object key, Object value) {
		if (this.logger.isDebugEnabled()){
			this.logger.debug("Put into cache {} by key {}, value {}", new Object[] { getName(),
					key, value });
		}
		this.cache.put(new Element(key, toStoreValue(value)));
		putObject(key, value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @author covito
	 * @param key
	 */
	@Override
	public void evict(Object key) {
		evictObject(key);
		Boolean result = Boolean.valueOf(this.cache.remove(key));
		if (this.logger.isDebugEnabled()){
			this.logger.debug("Evict from cache {} by key {}, RESULT = {}", new Object[] {
					getName(), key, result });
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @author covito
	 */
	@Override
	public void clear() {
		this.cache.removeAll();
	}

}