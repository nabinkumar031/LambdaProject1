package com.lululemon.lambda.fullsync.util;

public class LuluUtil {

    public static void appendEntity(StringBuilder sBuilder, String name, String value) {
        if (sBuilder == null) {
            sBuilder = new StringBuilder();
        }
        sBuilder.append(name).append("=").append(value).append(" | ");

    }
}
