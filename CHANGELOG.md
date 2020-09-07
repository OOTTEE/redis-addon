# Version 2.0.2 (2020-08-31)

* [brk] Renamed `url` config option to `uri`. The user, password, port and db can be specified directly in this URI (`redis://[[username:]password@]host[:port][/database]`).
* [new] SeedStack SSL context is used for SSL connections. 
* [new] New configuration options.

# Version 2.0.1 (2020-08-31)

* [chg] Update Jedis to 3.3.0

# Version 2.0.0 (2017-01-13)

* [brk] Update to new configuration system.

# Version 1.0.2 (2016-04-26)

* [chg] Update for SeedStack 16.4.
* [fix] Correctly cleanup `ThreadLocal` in `RedisLink`.

# Version 1.0.1 (2016-02-09)

* [fix] Flawed release process made this add-on unusable by clients.

# Version 1.0.0 (2015-07-30)

* [new] Initial Open-Source release.
