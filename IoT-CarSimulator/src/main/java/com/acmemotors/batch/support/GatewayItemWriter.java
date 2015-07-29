/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.acmemotors.batch.support;

import com.acmemotors.service.RequestGateway;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;

/**
 * A Spring Batch {@link org.springframework.batch.item.ItemWriter} used to send each item
 * to a given destination via MessagingGateway.
 *
 * @author mminella
 */
public class GatewayItemWriter implements ItemWriter<String>, InitializingBean {

	@Autowired
	private RequestGateway gateway;

	@Override
	public void write(List<? extends String> items) throws Exception {
    for (String item : items)
    {
      gateway.send(item);
    }
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(gateway, "A RequestGateway is required");
	}

	public void setGateway(RequestGateway gateway) {
		this.gateway = gateway;
	}
}

