/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.redis.internal;

import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.redis.Redis;
import org.seedstack.redis.RedisExceptionHandler;
import org.seedstack.seed.transaction.spi.TransactionMetadata;
import org.seedstack.seed.transaction.spi.TransactionMetadataResolver;

import java.util.Optional;

/**
 * This {@link TransactionMetadataResolver} resolves metadata for transactions marked
 * with {@link Redis}.
 */
class RedisTransactionMetadataResolver implements TransactionMetadataResolver {
    static String defaultClient;

    @Override
    public TransactionMetadata resolve(MethodInvocation methodInvocation, TransactionMetadata defaults) {
        Optional<Redis> redisOptional = RedisResolver.INSTANCE.apply(methodInvocation.getMethod());

        if (redisOptional.isPresent() || RedisTransactionHandler.class.equals(defaults.getHandler()) || RedisPipelinedTransactionHandler.class.equals(defaults.getHandler())) {
            TransactionMetadata result = new TransactionMetadata();

            result.setExceptionHandler(RedisExceptionHandler.class);
            result.setResource(redisOptional.isPresent() ? redisOptional.get().value() : defaultClient);

            if (redisOptional.isPresent()) {
                result.setHandler(redisOptional.get().pipelined() ? RedisPipelinedTransactionHandler.class : RedisTransactionHandler.class);
            } else if (RedisTransactionHandler.class.equals(defaults.getHandler())) {
                result.setHandler(RedisTransactionHandler.class);
            } else if (RedisPipelinedTransactionHandler.class.equals(defaults.getHandler())) {
                result.setHandler(RedisPipelinedTransactionHandler.class);
            }

            return result;
        }

        return null;
    }
}