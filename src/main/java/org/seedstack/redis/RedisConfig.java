/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.redis;

import org.seedstack.coffig.Config;
import org.seedstack.coffig.SingleValue;
import org.seedstack.seed.validation.NotBlank;
import redis.clients.jedis.JedisPoolConfig;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Config("redis")
public class RedisConfig {
    private Map<String, ClientConfig> clients = new HashMap<>();
    private String defaultClient;

    public Map<String, ClientConfig> getClients() {
        return Collections.unmodifiableMap(clients);
    }

    public RedisConfig addClient(String name, ClientConfig clientConfig) {
        this.clients.put(name, clientConfig);
        return this;
    }

    public String getDefaultClient() {
        return defaultClient;
    }

    public RedisConfig setDefaultClient(String defaultClient) {
        this.defaultClient = defaultClient;
        return this;
    }

    public static class ClientConfig {
        @NotBlank
        @SingleValue
        private String url;
        @NotNull
        private JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        private Class<? extends RedisExceptionHandler> exceptionHandler;

        public String getUrl() {
            return url;
        }

        public ClientConfig setUrl(String url) {
            this.url = url;
            return this;
        }

        public JedisPoolConfig getJedisPoolConfig() {
            return jedisPoolConfig;
        }

        public ClientConfig setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {
            this.jedisPoolConfig = jedisPoolConfig;
            return this;
        }

        public Class<? extends RedisExceptionHandler> getExceptionHandler() {
            return exceptionHandler;
        }

        public ClientConfig setExceptionHandler(Class<? extends RedisExceptionHandler> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }
    }
}
