package xyz.rexhaif.jstore;

import org.mapdb.DB;
import org.mapdb.DBMaker;

public class Consts {

    public static final int PORT = 8080;

    public static final DBMaker.Maker dbConfig =
            DBMaker
                    .memoryDB()
                    .cleanerHackEnable()
                    .closeOnJvmShutdown()
                    .executorEnable();

}
