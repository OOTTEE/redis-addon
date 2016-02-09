/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.redis.internal;

import org.seedstack.seed.SeedException;
import org.seedstack.seed.transaction.spi.TransactionalLink;
import redis.clients.jedis.Jedis;

import java.util.ArrayDeque;
import java.util.Deque;

class RedisLink<T> implements TransactionalLink<T> {
    private final ThreadLocal<Deque<Holder>> perThreadObjectContainer = new ThreadLocal<Deque<Holder>>() {
        @Override
        protected Deque<Holder> initialValue() {
            return new ArrayDeque<Holder>();
        }
    };

    public T get() {
        Holder holder = this.perThreadObjectContainer.get().peek();

        if (holder == null || holder.attached == null) {
            throw SeedException.createNew(RedisErrorCodes.ACCESSING_REDIS_OUTSIDE_TRANSACTION);
        }

        return holder.attached;
    }

    void push(Jedis jedis) {
        this.perThreadObjectContainer.get().push(new Holder(jedis));
    }

    Jedis pop() {
        return this.perThreadObjectContainer.get().pop().jedis;
    }

    Holder getHolder() {
        return this.perThreadObjectContainer.get().peek();
    }

    final class Holder {
        final Jedis jedis;
        T attached;

        Holder(Jedis jedis) {
            this.jedis = jedis;
        }
    }
}
