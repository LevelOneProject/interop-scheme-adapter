package com.l1p.interop.scheme.adapter;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mule.tck.junit4.FunctionalTestCase;


import com.github.tomakehurst.wiremock.junit.WireMockRule;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class TestSchemeAdapter extends FunctionalTestCase {
	
	@Rule
    public WireMockRule wireMockRule = new WireMockRule(8010);
	
	
	@Override
	protected String getConfigResources() {
		return "test-resources.xml,scheme-adapter-api.xml,scheme-adapter.xml";
	}
	
	@BeforeClass
	public static void initEnv() {
		System.setProperty("MULE_ENV", "test");
		System.setProperty("spring.profiles.active", "test");
	}
	
	
	@Test
	public void testQuotes() throws Exception {
		String dfspQuoteResponseJson = loadResourceAsString("test_data/dfspQuoteResponse.json");
		givenThat(post(urlMatching("/v1/quote")).willReturn(aResponse().withBody(dfspQuoteResponseJson)));

		String proxyQuoteRequestJson = loadResourceAsString("test_data/proxyQuoteRequest.json");
		Response response =
        	given().
            	contentType("application/json").
            	body(proxyQuoteRequestJson).
            when().
            	post("http://localhost:8088/scheme/adapter/v1/quotes");

	    assertEquals("payeeFee",(String)response.jsonPath().get("payeeFee.amount"),"1");
	    assertEquals("payeeCommission",(String)response.jsonPath().get("payeeCommission.amount"),"1");
		
	}
	

}
