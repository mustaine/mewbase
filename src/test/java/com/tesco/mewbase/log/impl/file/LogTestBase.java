package com.tesco.mewbase.log.impl.file;

import com.tesco.mewbase.MewbaseTestBase;
import com.tesco.mewbase.bson.BsonObject;
import com.tesco.mewbase.client.MewException;
import com.tesco.mewbase.log.Log;
import com.tesco.mewbase.log.impl.file.faf.AFFileAccess;
import com.tesco.mewbase.server.ServerOptions;
import com.tesco.mewbase.util.AsyncResCF;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.TestContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by tim on 14/10/16.
 */
public class LogTestBase extends MewbaseTestBase {

    protected FileAccess faf;
    protected ServerOptions options;
    protected FileLogManager flm;
    protected Log log;
    protected File logDir;

    @Override
    protected void setup(TestContext context) throws Exception {
        super.setup(context);
        logDir = testFolder.newFolder();
        faf = new AFFileAccess(vertx);
    }

    @Override
    protected void tearDown(TestContext context) throws Exception {
        if (log != null) {
            log.close().thenCompose(v -> {
                AsyncResCF<Void> cf = new AsyncResCF<>();
                vertx.close(cf);
                return cf;
            }).get();
        }
    }

    protected ServerOptions getOptions() {
        return new ServerOptions().setLogsDir(logDir.getPath());
    }

    protected void startLog() throws Exception {
        if (options == null) {
            options = getOptions();
        }
        startLog(options, TEST_CHANNEL_1);
    }

    protected void startLog(ServerOptions options, String channel) throws Exception {
        flm = new FileLogManager(vertx, options, faf);
        flm.createLog(TEST_CHANNEL_1).thenCompose(l -> flm.createLog(TEST_CHANNEL_2)).get();
        log = flm.getLog(channel);
    }

    protected void saveInfo(int fileNumber, int headPos, int fileHeadPos, int lastWrittenPos, boolean shutdown) {
        BsonObject info = new BsonObject();
        info.put("fileNumber", fileNumber);
        info.put("headPos", headPos);
        info.put("fileHeadPos", fileHeadPos);
        info.put("lastWrittenPos", lastWrittenPos);
        info.put("shutdown", shutdown);
        saveFileInfo(info);
    }

    protected void saveFileInfo(BsonObject info) {
        Buffer buff = info.encode();
        File f = new File(options.getLogsDir(), getLogInfoFileName(TEST_CHANNEL_1));
        try {
            if (!f.exists()) {
                if (!f.createNewFile()) {
                    throw new MewException("Failed to create file " + f);
                }
            }
            Files.write(f.toPath(), buff.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);
        } catch (IOException e) {
            throw new MewException(e);
        }
    }

    protected String getLogInfoFileName(String channel) {
        return channel + "-log-info.dat";
    }

    protected String getLogFileName(String channel, int i) {
        return channel + "-" + i + ".log";
    }


    protected BsonObject readInfoFromFile(File infoFile) {
        try {
            byte[] bytes = Files.readAllBytes(infoFile.toPath());
            Buffer buff = Buffer.buffer(bytes);
            return new BsonObject(buff);
        } catch (IOException e) {
            throw new MewException(e);
        }
    }

    protected Buffer readFileIntoBuffer(File f) throws IOException {
        byte[] bytes = Files.readAllBytes(f.toPath());
        return Buffer.buffer(bytes);
    }

    protected File[] listLogFiles(File logDir, String channel) {
        File[] files = logDir.listFiles(file -> {
            String name = file.getName();
            int lpos = name.lastIndexOf("-");
            if (name.endsWith("-log-info.dat")) {
                return false;
            }
            String chName = name.substring(0, lpos);
            return chName.equals(channel);
        });
        return files;
    }

    protected void waitUntil(BooleanSupplier supplier) {
        waitUntil(supplier, 10000);
    }

    protected void waitUntil(BooleanSupplier supplier, long timeout) {
        long start = System.currentTimeMillis();
        while (true) {
            if (supplier.getAsBoolean()) {
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignore) {
            }
            long now = System.currentTimeMillis();
            if (now - start > timeout) {
                throw new IllegalStateException("Timed out");
            }
        }
    }

    protected void assertExists(int fileNumber) {
        File file = new File(logDir, getLogFileName(TEST_CHANNEL_1, fileNumber));
        assertTrue(file.exists());
    }

    protected void assertLogChunkLength(int fileNumber, int length) {
        File file = new File(logDir, getLogFileName(TEST_CHANNEL_1, fileNumber));
        assertEquals(length, file.length());
    }

    protected void assertLogChunkLengthAsync(TestContext testContext, int fileNumber, long length) {
        File file = new File(logDir, getLogFileName(TEST_CHANNEL_1, fileNumber));
        testContext.assertEquals(length, file.length());
    }

    protected void assertNumFiles(String channel, int expected) {
        File[] files = listLogFiles(new File(options.getLogsDir()), channel);
        assertEquals(expected, files.length);
    }

    protected void appendObjectsSequentially(int num, Function<Integer, BsonObject> objectFunction) throws Exception {
        for (int i = 0; i < num; i++) {
            log.append(objectFunction.apply(i)).get();
        }
    }

    protected void appendObjectsConcurrently(int num, Function<Integer, BsonObject> objectFunction) throws Exception {
        List<CompletableFuture<Long>> cfs = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            CompletableFuture<Long> pos = log.append(objectFunction.apply(i));
            cfs.add(pos);
        }
        CompletableFuture<Void> all = CompletableFuture.allOf(cfs.toArray(new CompletableFuture[num]));
        all.get();
    }


}
