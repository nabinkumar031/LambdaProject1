package com.lululemon.lambda.fullsync;


import com.amazonaws.services.lambda.runtime.events.S3Event;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.UnsupportedEncodingException;

import static java.net.URLDecoder.decode;

@Getter
@Accessors(fluent = true)
public class S3EventForEfs {
    public static final String FULL_SYNC_S_3_EFS = "FULL_SYNC_S3_EFS";

    private final String sourceBucket;
    private final String key;
    private final StringBuilder trace;

    private S3EventForEfs(String sourceBucket, String key) {
        this.trace = newTrace(sourceBucket, key);
        this.sourceBucket = sourceBucket;
        this.key = key;
    }

    public S3EventForEfs append(String key, String value) {
        appendTrace(this.trace, key, value);
        return this;
    }

    public String formattedTrace(){
        return this.trace.toString();
    }

    public static S3EventForEfs fromS3Event(S3Event event) {
        var record = event.getRecords().stream()
                .findFirst()
                .orElseThrow(() -> new NedappFullSyncException("No Records in events"));
        var sourceBucket = record.getS3().getBucket().getName();

        try {
            var decodedKey = decode(record.getS3().getObject().getKey().replace('+', ' '), "UTF-8");

            return new S3EventForEfs(sourceBucket, decodedKey);
        } catch (UnsupportedEncodingException e) {
            throw new NedappFullSyncException(sourceBucket,e.getMessage(), e);
        }
    }

    private static StringBuilder newTrace(String sourceBucket, String key) {
        var initialTrace = new StringBuilder(FULL_SYNC_S_3_EFS);
        appendTrace(initialTrace, "SrcBucket", sourceBucket);
        appendTrace(initialTrace, "SrcFile", key);
        return initialTrace;
    }

    private static void appendTrace(StringBuilder builder, String key, String value){
        builder.append(key).append("=").append(value).append(" | ");
    }
}
