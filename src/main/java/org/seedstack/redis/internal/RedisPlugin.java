/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.redis.internal;

import com.google.common.collect.Lists;
import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.context.InitContext;
import io.nuun.kernel.api.plugin.request.ClasspathScanRequest;
import org.seedstack.redis.RedisConfig;
import org.seedstack.redis.RedisExceptionHandler;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.core.internal.AbstractSeedPlugin;
import org.seedstack.seed.crypto.spi.SSLProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

import javax.net.ssl.SSLContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RedisPlugin extends AbstractSeedPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisPlugin.class);
    private final Map<String, JedisPool> jedisPools = new HashMap<String, JedisPool>();
    private final Map<String, Class<? extends RedisExceptionHandler>> exceptionHandlerClasses = new HashMap<String, Class<? extends RedisExceptionHandler>>();

    @Override
    public String name() {
        return "redis";
    }

    @Override
    protected Collection<Class<?>> dependencies() {
        return Lists.newArrayList(SSLProvider.class);
    }

    @Override
    public Collection<ClasspathScanRequest> classpathScanRequests() {
        return classpathScanRequestBuilder()
                .subtypeOf(RedisExceptionHandler.class)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public InitState initialize(InitContext initContext) {
        RedisConfig redisConfig = getConfiguration(RedisConfig.class);

        if (redisConfig.getClients().isEmpty()) {
            LOGGER.info("No Redis client configured, Redis support disabled");
            return InitState.INITIALIZED;
        }

        for (Map.Entry<String, RedisConfig.ClientConfig> clientEntry : redisConfig.getClients().entrySet()) {
            String clientName = clientEntry.getKey();
            RedisConfig.ClientConfig clientConfig = clientEntry.getValue();

            Class<? extends RedisExceptionHandler> exceptionHandlerClass = clientConfig.getExceptionHandler();
            if (exceptionHandlerClass != null) {
                exceptionHandlerClasses.put(clientName, exceptionHandlerClass);
            }

            try {
                jedisPools.put(clientName, createJedisPool(clientConfig, initContext.dependency(SSLProvider.class)));
            } catch (Exception e) {
                throw SeedException.wrap(e, RedisErrorCode.UNABLE_TO_CREATE_CLIENT).put("clientName", clientName);
            }
        }

        return InitState.INITIALIZED;
    }

    @Override
    public Object nativeUnitModule() {
        return new RedisModule(jedisPools, exceptionHandlerClasses);
    }

    @Override
    public void stop() {
        for (Map.Entry<String, JedisPool> jedisPoolEntry : jedisPools.entrySet()) {
            LOGGER.info("Shutting down {} Jedis pool", jedisPoolEntry.getKey());
            try {
                jedisPoolEntry.getValue().close();
            } catch (Exception e) {
                LOGGER.error(String.format("Unable to properly close %s Jedi pool", jedisPoolEntry.getKey()), e);
            }
        }
    }

    private JedisPool createJedisPool(RedisConfig.ClientConfig clientConfig, SSLProvider sslProvider) {
        if (sslProvider.sslContext().isPresent()) {
            SSLContext sslContext = sslProvider.sslContext().get();
            return new JedisPool(
                    clientConfig.getPoolConfig(),
                    clientConfig.getUri(),
                    clientConfig.getTimeout(),
                    sslContext.getSocketFactory(),
                    sslContext.getSupportedSSLParameters(),
                    null // default
            );
        } else {
            return new JedisPool(clientConfig.getPoolConfig(), clientConfig.getUri(), clientConfig.getTimeout());
        }
    }
}
