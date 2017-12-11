package io.mateu.erp.log;

import io.mateu.erp.model.util.Helper;

public class Logger {
    public static void log(String coordinate1, String coordinate2, String coordinate3, String coordinate4, String type, String msg) {

        try {
            Helper.transact("log", (em) -> {
                em.persist(new LogEntry(coordinate1, coordinate2, coordinate3, coordinate4, type, msg));
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }
}
