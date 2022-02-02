package com.lululemon.lambda.fullsync.pojo;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class S3EventWithContext {
    public static final String FULL_SYNC_S_3_EFS = "FULL_SYNC_S3_EFS";
    private String sourceBucket;
    private S3Event s3Event;
    private S3EventNotification.S3EventNotificationRecord record;
    private final String key;
    private final Context context;
    private final StringBuilder trace;

    public S3EventWithContext(S3Event event, Context context) {
        this.trace = new StringBuilder(FULL_SYNC_S_3_EFS);
        this.context = context;
        this.s3Event = event;
        this.record=event.getRecords().get(0);
        this.sourceBucket = record.getS3().getBucket().getName();
        this.key = record.getS3().getObject().getKey();


    }

    public S3EventWithContext append(String key, String value) {
        this.trace.append(key).append("=").append(value).append(" | ");
        return this;
    }

    public String formattedTrace(){
        return this.trace.toString();
    }
}
