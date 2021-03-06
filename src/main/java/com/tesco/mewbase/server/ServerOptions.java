package com.tesco.mewbase.server;

import com.tesco.mewbase.auth.MewbaseAuthProvider;
import com.tesco.mewbase.auth.impl.NoAuthAuthProvider;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServerOptions;

import java.util.Arrays;

/**
 * Created by tim on 22/09/16.
 */
public class ServerOptions {

    public static final String DEFAULT_HOST = "0.0.0.0";
    public static final int DEFAULT_PORT = 7451;
    public static final String DEFAULT_DOCS_DIR = "mewdata/docs";
    public static final String DEFAULT_LOGS_DIR = "mewdata/eventlogs";
    public static final int DEFAULT_MAX_LOG_CHUNK_SIZE = 4 * 10 * 1024 * 1024;
    public static final int DEFAULT_PREALLOCATE_SIZE = 0;
    public static final int DEFAULT_MAX_RECORD_SIZE = 4 * 1024 * 1024;
    public static final int DEFAULT_READ_BUFFER_SIZE = 4 * 1024;

    private NetServerOptions netServerOptions = new NetServerOptions().setPort(DEFAULT_PORT).setHost(DEFAULT_HOST);
    private String docsDir = DEFAULT_DOCS_DIR;
    private MewbaseAuthProvider authProvider = new NoAuthAuthProvider();
    private String logsDir = DEFAULT_LOGS_DIR;
    private int maxLogChunkSize = DEFAULT_MAX_LOG_CHUNK_SIZE;
    private int preallocateSize = DEFAULT_PREALLOCATE_SIZE;
    private int maxRecordSize = DEFAULT_MAX_RECORD_SIZE;
    private int readBufferSize = DEFAULT_READ_BUFFER_SIZE;

    public ServerOptions() {
    }

    public ServerOptions(JsonObject jsonObject) {
        JsonObject nso = jsonObject.getJsonObject("netServerOptions");
        this.netServerOptions = nso == null ? new NetServerOptions() : new NetServerOptions(nso);
        this.docsDir = jsonObject.getString("docsDir", DEFAULT_DOCS_DIR);
        this.logsDir = jsonObject.getString("logsDir", DEFAULT_LOGS_DIR);
        this.maxLogChunkSize = jsonObject.getInteger("maxLogChunkSize", DEFAULT_MAX_LOG_CHUNK_SIZE);
        this.preallocateSize = jsonObject.getInteger("preallocateSize", DEFAULT_PREALLOCATE_SIZE);
        this.maxRecordSize = jsonObject.getInteger("maxRecordSize", DEFAULT_MAX_RECORD_SIZE);
        this.readBufferSize = jsonObject.getInteger("readBufferSize", DEFAULT_READ_BUFFER_SIZE);
    }

    public NetServerOptions getNetServerOptions() {
        return netServerOptions;
    }

    public ServerOptions setNetServerOptions(NetServerOptions netServerOptions) {
        this.netServerOptions = netServerOptions;
        return this;
    }

    public String getDocsDir() {
        return docsDir;
    }

    public ServerOptions setDocsDir(String docsDir) {
        this.docsDir = docsDir;
        return this;
    }

    public MewbaseAuthProvider getAuthProvider() {
        return authProvider;
    }

    public ServerOptions setAuthProvider(MewbaseAuthProvider authProvider) {
        this.authProvider = authProvider;
        return this;
    }

    public String getLogsDir() {
        return logsDir;
    }

    public ServerOptions setLogsDir(String logDir) {
        this.logsDir = logDir;
        return this;
    }

    public int getMaxLogChunkSize() {
        return maxLogChunkSize;
    }

    public ServerOptions setMaxLogChunkSize(int maxLogChunkSize) {
        this.maxLogChunkSize = maxLogChunkSize;
        return this;
    }

    public int getMaxRecordSize() {
        return maxRecordSize;
    }

    public ServerOptions setMaxRecordSize(int maxRecordSize) {
        this.maxRecordSize = maxRecordSize;
        return this;
    }

    public int getPreallocateSize() {
        return preallocateSize;
    }

    public ServerOptions setPreallocateSize(int preallocateSize) {
        this.preallocateSize = preallocateSize;
        return this;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public ServerOptions setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerOptions that = (ServerOptions)o;

        if (maxLogChunkSize != that.maxLogChunkSize) return false;
        if (preallocateSize != that.preallocateSize) return false;
        if (maxRecordSize != that.maxRecordSize) return false;
        if (readBufferSize != that.readBufferSize) return false;
        if (netServerOptions != null ? !netServerOptions.equals(that.netServerOptions) : that.netServerOptions != null)
            return false;
        if (docsDir != null ? !docsDir.equals(that.docsDir) : that.docsDir != null) return false;
        if (authProvider != null ? !authProvider.equals(that.authProvider) : that.authProvider != null) return false;
        return logsDir != null ? logsDir.equals(that.logsDir) : that.logsDir == null;

    }

}
