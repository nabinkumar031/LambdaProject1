package com.lululemon.lambda.fullsync;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.lululemon.lambda.fullsync.pojo.S3EventWithContext;
import com.lululemon.lambda.fullsync.util.LuluUtil;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.Consumer;

@Slf4j
public class NedappFullSyncConsumer implements Consumer<S3EventWithContext> {
    private final S3Client s3Client;
    private final String strEFSPath;

    public NedappFullSyncConsumer(S3Client s3Client, String strEFSPath1) {
        this.s3Client = s3Client;
        this.strEFSPath = strEFSPath1;
    }

    @Override
    public void accept(S3EventWithContext s3Event) {
        //            S3EventObj eventObj = new S3EventObj(event);
//            String srcBucket = eventObj.getSourceBucket();
//            String key = eventObj.getKey();
//            lambdaLogger.log("srcBucket: "+srcBucket);
//
//            S3EventNotification.S3EventNotificationRecord record=eventObj.getRecord();
//            lambdaLogger.log("srcBucket1: "+srcBucket);
//
//            String tempKey = key.replace('+', ' ');
//            tempKey = URLDecoder.decode(key, "UTF-8");
//
//            lambdaLogger.log("tempKey: "+tempKey);
//
//            AmazonS3 s3Client = new AmazonS3Client();

//
        try (FileOutputStream efsFileOutputStream = new FileOutputStream(createEfsFile(s3Event), false)) {
            String sFileContent = s3Client.get(srcBucket, tempKey);
            efsFileOutputStream.write(sFileContent.getBytes(Charset.forName("UTF-8")));

            s3Event.context().getLogger().log(s3Event.formattedTrace());
        } catch (IOException e) {
            s3Event.append("Error", e.getMessage());
            log.error(s3Event.formattedTrace(), e);
        }
    }

    private File createEfsFile(S3EventWithContext s3Event) throws IOException {
        String efsFullPath = strEFSPath + s3Event.key();
        s3Event.append("efsFullPath:", efsFullPath);
        File efsFile = new File(efsFullPath);
        efsFile.createNewFile();
        return efsFile;
    }

    private String createLogEntry(LambdaLogger lambdaLogger, String srcBucket, String key, String efsFullPath) {
        StringBuilder sBuilder = new StringBuilder(PIPE).append(FULL_SYNC_S3_EFS)
                .append(PIPE);
        LuluUtil.appendEntity(sBuilder, "SrcBucket", srcBucket);
        LuluUtil.appendEntity(sBuilder, "SrcFile", key);
        LuluUtil.appendEntity(sBuilder, "EFSFile", efsFullPath);
        lambdaLogger.log(sBuilder.toString());

    }

}
