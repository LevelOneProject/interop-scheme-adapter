package com.l1p.interop.scheme.adapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.interledger.InterledgerAddress;
import org.interledger.InterledgerAddressBuilder;
import org.interledger.codecs.CodecContext;
import org.interledger.codecs.CodecContextFactory;
import org.interledger.ipr.InterledgerPaymentRequest;
import org.interledger.ipr.InterledgerPaymentRequestBuilder;

public class IlpCryptoLib {
	
	private static Logger log = LogManager.getLogger(IlpCryptoLib.class);
	
	public String cryptoEncode (String destAddr, String destAmnt){
		
		log.info("received destination address: " + destAddr + " and destination amount: " + destAmnt);
		
		InterledgerAddress destinationAddress = InterledgerAddressBuilder.builder().value(destAddr).build();
	    long destinationAmount = Long.parseLong(destAmnt);
	    ZonedDateTime expiresAt = ZonedDateTime.now().plusMinutes(1);
	    byte[] receiverSecret = new byte[32];
	    String paymentId = UUID.randomUUID().toString();

	    InterledgerPaymentRequestBuilder builder = new InterledgerPaymentRequestBuilder(
	        destinationAddress,
	        destinationAmount,
	        expiresAt,
	        receiverSecret);

	    builder.setEncrypted(false);
	    builder.getPskMessageBuilder().addPublicHeader("Payment-Id", paymentId);

	    CodecContext context = CodecContextFactory.interledger();
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	    try {
	      context.write(InterledgerPaymentRequest.class, builder.getIpr(), outputStream);
	    } catch (IOException e) {
	      throw new RuntimeException("Error encoding Interledger Packet Request.", e);
	    }

	    String cryptoEncode = Base64.getUrlEncoder().encodeToString(outputStream.toByteArray());
	    
	    log.info("returning the following crypto encode value: " + cryptoEncode);
	    
	    return cryptoEncode;
	}

}
