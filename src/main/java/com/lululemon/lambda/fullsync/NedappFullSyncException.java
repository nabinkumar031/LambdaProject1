package com.lululemon.lambda.fullsync;

import lombok.Getter;
import lombok.experimental.Accessors;

import static com.lululemon.lambda.fullsync.S3EventForEfs.FULL_SYNC_S_3_EFS;

@Accessors(fluent = true)
@Getter
public class NedappFullSyncException extends RuntimeException {
    private final transient String bucket;

    public NedappFullSyncException(String bucket, String cause, Throwable throwable) {
        super(cause, throwable);
        this.bucket = bucket;
    }

    public NedappFullSyncException(String cause) {
        super(cause);
        this.bucket = null;
    }

    public String formattedTrace() {
        return new StringBuilder(FULL_SYNC_S_3_EFS)
                .append("SrcBucket")
                .append("=")
                .append(this.bucket)
                .append(" | ")
                .append("Error")
                .append("=")
                .append(this.getMessage())
                .append(" | ")

                .toString();
    }
}
