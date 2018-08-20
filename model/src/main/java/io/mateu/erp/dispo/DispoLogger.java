package io.mateu.erp.dispo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispoLogger {

    static final Logger logger = LoggerFactory.getLogger(DispoLogger.class);

    public static boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    public static void trace(String msg) {
        logger.trace(msg);
    }

}
