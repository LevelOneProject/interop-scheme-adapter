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
    public WireMockRule dfspQuote = new WireMockRule(8010);
	
	@Rule
    public WireMockRule ilpService = new WireMockRule(3045);
	
	
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
		dfspQuote.stubFor(post(urlMatching("/v1/quote")).willReturn(aResponse().withBody(dfspQuoteResponseJson)));
		
		String ilpServiceMockJson = loadResourceAsString("test_data/ilpServiceCreateIPRMockResponse.json");
		ilpService.stubFor(post(urlMatching("/createIPR")).willReturn(aResponse().withHeader("Content-Type", "application/json").withBody(ilpServiceMockJson)));

		String proxyQuoteRequestJson = loadResourceAsString("test_data/proxyQuoteRequest.json");
		Response response =
        	given().
            	contentType("application/json").
            	body(proxyQuoteRequestJson).
            when().
            	post("http://localhost:8088/scheme/adapter/v1/quotes");
		
		
	    assertEquals("payeeFee","1",(String)response.jsonPath().get("payeeFee.amount"));
	    assertEquals("payeeCommission","1",(String)response.jsonPath().get("payeeCommission.amount"));
	    assertEquals("ipr","Aojf9Pq9_RKgnS3mzvYnZAXvJuvjWnw6r-JXdwitLmHygdQBgdEAAAAAAAAEsDZsZXZlbG9uZS5kZnNwMS5hbGljZS5TdXVPNUdhaDUxSXM3VzVyUkdXdVBnTWVSdGtKOXZPelGBj1BTSy8xLjAKTm9uY2U6IHRsNF93NVRfaGhLM0FFcWJ3Ukg3VVEKRW5jcnlwdGlvbjogbm9uZQpQYXltZW50LUlkOiAxMTBlYzU4YS1hMGYyLTRhYzQtODM5My1jODY2ZDgxM2I4ZDEKCkV4cGlyZXMtQXQ6IDIwMTctMDYtMjBUMDA6MDA6MDEuMDAwWgoKAA==",(String)response.jsonPath().get("ipr"));
		
	}
	

}
