/*
 * Copyright 2012-2013 the original author or authors.
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

package org.springframework.boot.autoconfigure.amqp;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link RabbitTemplate}.
 * 
 * @author Greg Turnquist
 */
@Configuration
@ConditionalOnClass({ RabbitTemplate.class })
@EnableConfigurationProperties
public class RabbitTemplateAutoConfiguration {

	@Bean
	@ConditionalOnExpression("${spring.rabbitmq.dynamic:true}")
	@ConditionalOnMissingBean(AmqpAdmin.class)
	public AmqpAdmin amqpAdmin(CachingConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}

	@Configuration
	@ConditionalOnMissingBean(RabbitTemplate.class)
	protected static class RabbitTemplateCreator {

		@Autowired
		CachingConnectionFactory connectionFactory;

		@Bean
		public RabbitTemplate rabbitTemplate() {
			return new RabbitTemplate(this.connectionFactory);
		}

	}

	@Configuration
	@ConditionalOnMissingBean(ConnectionFactory.class)
	@EnableConfigurationProperties(RabbitConnectionFactoryProperties.class)
	protected static class RabbitConnectionFactoryCreator {

		@Autowired
		private RabbitConnectionFactoryProperties config;

		@Bean
		public CachingConnectionFactory connectionFactory() {
			CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
					this.config.getHost());
			connectionFactory.setPort(this.config.getPort());
			if (this.config.getUsername() != null) {
				connectionFactory.setUsername(this.config.getUsername());
			}
			if (this.config.getPassword() != null) {
				connectionFactory.setPassword(this.config.getPassword());
			}
			return connectionFactory;
		}
	}

	@ConfigurationProperties(name = "spring.rabbitmq")
	public static class RabbitConnectionFactoryProperties {

		private String host = "localhost";

		private int port = 5672;

		private String username;

		private String password;

		private boolean dynamic = true;

		public String getHost() {
			return this.host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return this.port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getUsername() {
			return this.username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return this.password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public boolean isDynamic() {
			return this.dynamic;
		}

		public void setDynamic(boolean dynamic) {
			this.dynamic = dynamic;
		}

	}
}
