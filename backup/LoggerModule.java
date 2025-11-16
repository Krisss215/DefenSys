package com.guardian.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggerModule {
    private static final Logger LOG = LoggerFactory.getLogger("ElderGuardianAgent");

    private LoggerModule() {}

    public static void log(String msg) {
        LOG.info(msg);
    }

    public static void error(String msg, Throwable t) {
        LOG.error(msg, t);
    }
}
