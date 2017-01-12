/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.redis.internal;

import org.seedstack.redis.Redis;
import org.seedstack.shed.reflect.StandardAnnotationResolver;

import java.lang.reflect.Method;

class RedisResolver extends StandardAnnotationResolver<Method, Redis> {
    static RedisResolver INSTANCE = new RedisResolver();

    private RedisResolver() {
        // no external instantiation allowed
    }
}
