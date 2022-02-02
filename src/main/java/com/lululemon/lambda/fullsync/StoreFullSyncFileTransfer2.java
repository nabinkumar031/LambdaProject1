package com.lululemon.lambda.fullsync;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.lululemon.lambda.fullsync.pojo.S3EventForEfs;
import com.lululemon.lambda.fullsync.util.LuluUtil;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

public class StoreFullSyncFileTransfer2 implements RequestHandler< S3Event, String> {

    //private static final Logger LOGGER = LoggerFactory.getLogger(StoreFullSyncFileTransfer2.class);
    private static final String PIPE = " | ";
    private static final String FULL_SYNC_S3_EFS = "FULL_SYNC_S3_EFS";


    @Override
    public String handleRequest(S3Event event, Context context) {
        LambdaLogger lambdaLogger = context.getLogger();

        String strEFSPath=System.getenv("EFS_FOLDER_PATH");

        try {
            S3EventForEfs eventObj = new S3EventForEfs(event);
            String srcBucket = eventObj.getSourceBucket();
            String key = eventObj.getKey();
            lambdaLogger.log("srcBucket: "+srcBucket);

            S3EventNotification.S3EventNotificationRecord record=eventObj.getRecord();
            lambdaLogger.log("srcBucket1: "+srcBucket);

            String tempKey = key.replace('+', ' ');
            tempKey = URLDecoder.decode(key, "UTF-8");

            lambdaLogger.log("tempKey: "+tempKey);

            AmazonS3 s3Client = new AmazonS3Client();
            String sFileContent = s3Client.getObjectAsString(srcBucket, tempKey);

            String efsFullPath = strEFSPath+key;
            lambdaLogger.log("efsFullPath: "+efsFullPath);
            File efsFile = new File(efsFullPath);
            efsFile.createNewFile();
            FileOutputStream efsFileOutputStream = new FileOutputStream(efsFile, false);
            efsFileOutputStream.write(sFileContent.getBytes(Charset.forName("UTF-8")));
            printLogger(lambdaLogger,srcBucket,key,efsFullPath);
            return "ok";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void printLogger(LambdaLogger lambdaLogger, String srcBucket, String key, String efsFullPath) {
        StringBuilder sBuilder = new StringBuilder(PIPE).append(FULL_SYNC_S3_EFS)
                .append(PIPE);
        LuluUtil.appendEntity(sBuilder,"SrcBucket",srcBucket);
        LuluUtil.appendEntity(sBuilder,"SrcFile",key);
        LuluUtil.appendEntity(sBuilder,"EFSFile",efsFullPath);
        lambdaLogger.log(sBuilder.toString());

    }

}
