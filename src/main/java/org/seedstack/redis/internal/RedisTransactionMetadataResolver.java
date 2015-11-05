/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.redis.internal;

import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.redis.RedisExceptionHandler;
import org.seedstack.seed.core.utils.SeedReflectionUtils;
import org.seedstack.redis.Redis;
import org.seedstack.seed.transaction.spi.TransactionMetadata;
import org.seedstack.seed.transaction.spi.TransactionMetadataResolver;

/**
 * This {@link TransactionMetadataResolver} resolves metadata for transactions marked
 * with {@link Redis}.
 *
 * @author adrien.lauer@mpsa.com
 */
class RedisTransactionMetadataResolver implements TransactionMetadataResolver {
    static String defaultClient;

    @Override
    public TransactionMetadata resolve(MethodInvocation methodInvocation, TransactionMetadata defaults) {
        Redis redis = SeedReflectionUtils.getMethodOrAncestorMetaAnnotatedWith(methodInvocation.getMethod(), Redis.class);

        if (redis != null || RedisTransactionHandler.class.equals(defaults.getHandler()) || RedisPipelinedTransactionHandler.class.equals(defaults.getHandler())) {
            TransactionMetadata result = new TransactionMetadata();

            result.setExceptionHandler(RedisExceptionHandler.class);
            result.setResource(redis == null ? defaultClient : redis.value());

            if (redis != null) {
                result.setHandler(redis.pipelined() ? RedisPipelinedTransactionHandler.class : RedisTransactionHandler.class);
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