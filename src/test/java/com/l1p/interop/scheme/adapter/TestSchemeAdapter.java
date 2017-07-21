package com.l1p.interop.scheme.adapter;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mule.tck.junit4.FunctionalTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import io.restassured.response.Response;

public class TestSchemeAdapter extends FunctionalTestCase {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private static WireMockServer wireMockServer;
	
	@Rule
	public WireMockRule mockReceiverSchemeAdapter = new WireMockRule(8090);
	
	@Rule
    public WireMockRule dfspAPIService = new WireMockRule(8010);
	
	@Rule
    public WireMockRule dfspQuoteService = new WireMockRule(8020);
	
	@Rule
    public WireMockRule ilpService = new WireMockRule(3045);
	
	
	@Override
	protected String getConfigResources() {
		return "test-resources.xml,scheme-adapter-api.xml,scheme-adapter.xml";
	}
	
	@BeforeClass
	public static void initEnv() {
		System.setProperty("MULE_ENV", "test");
	}
	
	@Test
	public void testQuery() throws Exception {
		String dfspReceiverResponseJson = loadResourceAsString("test_data/schemeAdapterReceiverMockResponse.json");
		mockReceiverSchemeAdapter.stubFor(get(urlPathMatching("/scheme/adapter/v1/receivers/.*")).willReturn(aResponse().withBody(dfspReceiverResponseJson)));
    	
		given().
    		contentType("application/json").
    		queryParam("receiver", "localhost:8090/scheme/adapter/v1/receivers/123456").
        when().
        	get("http://localhost:8088/scheme/adapter/v1/query").
        then().
        	statusCode(200).
        	body("id", equalTo("http://ec2-35-166-180-190.us-west-2.compute.amazonaws.com:8088/ilp/ledger/v1/accounts/alice")).
        	body("name", equalTo("alice")).
        	body("balance", equalTo("1000.00")).
        	body("currencyCode", equalTo("USD")).
        	body("currencySymbol", equalTo("$")).	
        	body("is_disabled", equalTo(false)).
        	body("ledger", equalTo("http://ec2-35-166-180-190.us-west-2.compute.amazonaws.com:8088/ilp/ledger/v1"));
		
	}
	
	@Test
	public void testReceivers() throws Exception {
		
		String dfspReceiverResponseJson = loadResourceAsString("test_data/dfspReceiverMockResponse.json");
		dfspAPIService.stubFor(get(urlPathMatching("/receivers/.*")).willReturn(aResponse().withBody(dfspReceiverResponseJson)));
    	
		given().
    		contentType("application/json").
    	when().
        	get("http://localhost:8088/scheme/adapter/v1/receivers/123456").
        then().
        	statusCode(200).
        	body("id", equalTo("http://ec2-35-166-189-14.us-west-2.compute.amazonaws.com:8088/ilp/ledger/v1/accounts/alice")).
        	body("name", equalTo("alice")).
        	body("balance", equalTo("1000.00")).
        	body("currencyCode", equalTo("USD")).
        	body("currencySymbol", equalTo("$")).
        	body("is_disabled", equalTo(false)).
        	body("ledger", equalTo("http://ec2-35-166-189-14.us-west-2.compute.amazonaws.com:8088/ilp/ledger/v1"));
	}
	
	
	@Test
	@Ignore
	public void testQuotes() throws Exception {
		String dfspQuoteResponseJson = loadResourceAsString("test_data/dfspQuoteMockResponse.json");
		dfspQuoteService.stubFor(post(urlMatching("/v1/quote")).willReturn(aResponse().withBody(dfspQuoteResponseJson)));
		
		String ilpServiceMockCreateIPRMockJson = loadResourceAsString("test_data/ilpServiceCreateIPRMockResponse.json");
		ilpService.stubFor(post(urlMatching("/createIPR")).willReturn(aResponse().withHeader("Content-Type", "application/json").withBody(ilpServiceMockCreateIPRMockJson)));

		String ilpServiceMockQuoteIPRMockJson = loadResourceAsString("test_data/ilpServiceQuoteIPRMockResponse.json");
		ilpService.stubFor(get(urlPathMatching("/quoteIPR")).willReturn(aResponse().withHeader("Content-Type", "application/json").withBody(ilpServiceMockQuoteIPRMockJson)));
		
		String proxyQuoteRequestJson = loadResourceAsString("test_data/proxyQuoteRequest.json");
		Response response =
        	given().
            	contentType("application/json").
            	body(proxyQuoteRequestJson).
            when().
            	post("http://localhost:8088/scheme/adapter/v1/quotes");
		
		logger.info("Response: "+response.asString());
		
	    assertEquals("receiveAmount","10",(String)response.jsonPath().get("receiveAmount.amount"));
		assertEquals("payeeFee","1",(String)response.jsonPath().get("payeeFee.amount"));
	    assertEquals("payeeCommission","1",(String)response.jsonPath().get("payeeCommission.amount"));
	    assertEquals("ipr","Aojf9Pq9_RKgnS3mzvYnZAXvJuvjWnw6r-JXdwitLmHygdQBgdEAAAAAAAAEsDZsZXZlbG9uZS5kZnNwMS5hbGljZS5TdXVPNUdhaDUxSXM3VzVyUkdXdVBnTWVSdGtKOXZPelGBj1BTSy8xLjAKTm9uY2U6IHRsNF93NVRfaGhLM0FFcWJ3Ukg3VVEKRW5jcnlwdGlvbjogbm9uZQpQYXltZW50LUlkOiAxMTBlYzU4YS1hMGYyLTRhYzQtODM5My1jODY2ZDgxM2I4ZDEKCkV4cGlyZXMtQXQ6IDIwMTctMDYtMjBUMDA6MDA6MDEuMDAwWgoKAA==",(String)response.jsonPath().get("ipr"));
		assertEquals("expiresAt","2017-06-14T00:00:01.000Z",(String)response.jsonPath().get("expiresAt"));
	}
	
	
	@Test
	public void testPayments() throws Exception {

		String proxyPaymentRequestJson = loadResourceAsString("test_data/proxyPaymentRequest.json");
		String paymentMockResponseJson = loadResourceAsString("test_data/paymentMockResponse.json");
		
		ilpService.stubFor(post(urlMatching("/payIPR")).willReturn(aResponse().withBody(paymentMockResponseJson).withStatus(201)).atPriority(2));
		
		Response response =
        	given().
            	contentType("application/json").
            	body(proxyPaymentRequestJson).
            when().
            	post("http://localhost:8088/scheme/adapter/v1/payments");
				
		logger.info("Response: "+response.asString());
		
	    assertEquals("paymentId","123456",(String)response.jsonPath().get("paymentId"));
		assertEquals("connectorAccount","123456",(String)response.jsonPath().get("connectorAccount"));
	    assertEquals("status","1",(String)response.jsonPath().get("status"));
	    assertEquals("rejectionMessage","rejection message",(String)response.jsonPath().get("rejectionMessage"));
		assertEquals("fulfillment","fulfillment",(String)response.jsonPath().get("fulfillment"));
		
	}

	
	@Test
	public void testIlpAddress() throws Exception {

		String ilpAddressResponseJson = loadResourceAsString("test_data/ilpAddressMockResponse.json");
		ilpService.stubFor(get(urlMatching("/ilpAddress.*")).willReturn(aResponse().withBody(ilpAddressResponseJson).withStatus(200)));
    	
		given().
    		contentType("application/json").
    		queryParam("account", "123459").
    	when().
        	get("http://localhost:8088/scheme/adapter/v1/ilpAddress").
        then().
        	statusCode(200).
        	body("ilpAddress", equalTo("ok"));
		
	}
	
	
	@Test
	public void testNotifications() throws Exception {
		
		String notificationRequestBodyJson = loadResourceAsString("test_data/notificationMockRequestBody.json");

    	given().
        	contentType("application/json").
        	body(notificationRequestBodyJson).
        when().
        	post("http://localhost:8088/scheme/adapter/v1/notifications").
        then().
        	statusCode(200);

	}
	
	
	@Test
	public void testHealth() throws Exception {
		
		String healthMockResponseJson = loadResourceAsString("test_data/healthMockResponse.json");
		dfspAPIService.stubFor(get(urlPathMatching("/health")).willReturn(aResponse().withBody(healthMockResponseJson)));
    	
		given().
    		contentType("application/json").
    	when().
        	get("http://localhost:8088/scheme/adapter/v1/health").
        then().
        	statusCode(200).
        	body("status", equalTo("ok"));
		
	}

}
