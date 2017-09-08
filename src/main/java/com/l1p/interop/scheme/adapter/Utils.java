package com.l1p.interop.scheme.adapter;

import com.jayway.jsonpath.JsonPath;
import org.codehaus.jackson.map.ObjectMapper;
import org.mule.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static String getPaymentId(String payload){
        return JsonPath.parse(payload).read("paymentId");
    }

    public static String getData(String payload) throws IOException {
        log.info("Payload received: {}",payload);
        Object dataObj =  JsonPath.read(payload,"$.data");
        return new ObjectMapper().writeValueAsString(dataObj);
        //return data.replace("\"","\\\"");

    }

    public static void main(String[] args) throws IOException {
        String data = "{\"paymentId\":\"b19aef1d-80e5-4caa-8421-06c3fd198846\",\"expiresAt\":\"2017-09-08T18:47:08.918Z\",\"payeeFee\":{\"amount\":0,\"currency\":\"TZS\"},\"payeeCommission\":{\"amount\":0,\"currency\":\"TZS\"},\"data\":{\"paymentId\":\"b19aef1d-80e5-4caa-8421-06c3fd198846\",\"identifier\":\"27213971461\",\"identifierType\":\"eur\",\"destinationAccount\":\"http://ec2-35-166-236-69.us-west-2.compute.amazonaws.com:8088/ilp/ledger/v1/accounts/alice\",\"currency\":\"TZS\",\"fee\":0,\"commission\":0,\"transferType\":\"p2p\",\"amount\":\"1200\",\"params\":{\"peer\":{\"identifier\":\"14325603\",\"identifierType\":\"eur\"}},\"isDebit\":false,\"expiresAt\":\"2017-09-08T18:47:08.918Z\"}}";
        log.info("Result {}",getData(data));
    }


}
