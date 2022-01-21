package com.lululemon.lambda.fullsync.pojo;


import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class S3EventObj {

    @Getter
    @Setter
    private String sourceBucket;
    private S3Event s3Event;
    @Getter
    private S3EventNotification.S3EventNotificationRecord record;
    @Getter
    private String key;


    public S3EventObj(S3Event event) {
        this.s3Event = event;
        this.record=event.getRecords().get(0);
        this.sourceBucket = record.getS3().getBucket().getName();
        this.key = record.getS3().getObject().getKey();
    }


}
