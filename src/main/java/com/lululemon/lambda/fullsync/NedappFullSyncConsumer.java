package com.lululemon.lambda.fullsync;

import com.lululemon.lambda.fullsync.pojo.S3EventForEfs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

@Slf4j
@Component
public class NedappFullSyncConsumer implements Consumer<S3EventForEfs> {
    private final S3Client s3Client;
    private final String strEFSPath;

    public NedappFullSyncConsumer(S3Client s3Client, @Value("#{System.getenv(\"EFS_FOLDER_PATH\")}") String strEFSPath1) {
        this.s3Client = s3Client;
        this.strEFSPath = strEFSPath1;
    }

    @Override
    public void accept(S3EventForEfs s3Event) {
        try (FileOutputStream efsFileOutputStream = new FileOutputStream(createEfsFile(s3Event), false)) {
            efsFileOutputStream.write(readObjectFromEvent(s3Event));
            log.info(s3Event.formattedTrace());
        } catch (IOException e) {
            s3Event.append("Error", e.getMessage());
            log.error(s3Event.formattedTrace(), e);
        }
    }

    private byte[] readObjectFromEvent(S3EventForEfs s3Event) {
        return s3Client.getObjectAsBytes(requestBuilder -> requestBuilder
                        .bucket(s3Event.sourceBucket())
                        .key(s3Event.key()))
                .asByteArray();
    }

    private File createEfsFile(S3EventForEfs s3Event) throws IOException {
        String efsFullPath = strEFSPath + s3Event.key();
        s3Event.append("efsFullPath:", efsFullPath);
        File efsFile = new File(efsFullPath);
        efsFile.createNewFile();
        return efsFile;
    }

//    private String createLogEntry(LambdaLogger lambdaLogger, String srcBucket, String key, String efsFullPath) {
//        StringBuilder sBuilder = new StringBuilder(PIPE).append(FULL_SYNC_S3_EFS)
//                .append(PIPE);
//        LuluUtil.appendEntity(sBuilder, "SrcBucket", srcBucket);
//        LuluUtil.appendEntity(sBuilder, "SrcFile", key);
//        LuluUtil.appendEntity(sBuilder, "EFSFile", efsFullPath);
//        lambdaLogger.log(sBuilder.toString());
//
//    }

}
