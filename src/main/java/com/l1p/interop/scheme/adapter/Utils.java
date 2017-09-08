package com.l1p.interop.scheme.adapter;

import org.codehaus.jackson.map.ObjectMapper;
import org.mule.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.restassured.path.json.JsonPath;

import java.io.IOException;

public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static String getPaymentId(String payload){
        return JsonPath.from(payload).getString("paymentId");
    }

    public static String getData(String payload){
        String data =  JsonPath.from(payload).getString("data");
        return data.replace("\"","\\\"");

    }


}
