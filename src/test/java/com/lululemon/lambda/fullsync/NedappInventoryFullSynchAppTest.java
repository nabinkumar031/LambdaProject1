package com.lululemon.lambda.fullsync;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.function.context.FunctionCatalog;

import java.util.List;
import java.util.function.Consumer;

@Disabled
@Slf4j
@SpringBootTest
class NedappInventoryFullSynchAppTest {

    @Autowired
    private FunctionCatalog catalog;

    @Test
    public void testFullSync() throws Exception {
        Consumer<S3Event> consumer = catalog.lookup(Consumer.class, "nedappFullSyncConsumer");

        var oneRecord = new S3EventNotification.S3EventNotificationRecord("us-east-1", "inventoryFullSync", null, null, null, null, null, null, null);
        consumer.accept(new S3Event(List.of(oneRecord)));
    }

}