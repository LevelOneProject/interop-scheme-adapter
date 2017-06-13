package com.l1p.interop.scheme.adapter;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mule.tck.junit4.FunctionalTestCase;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

//import com.github.tomakehurst.wiremock.junit.WireMockRule;
//import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

//import io.restassured.RestAssured;
//import io.restassured.response.Response;
//import static io.restassured.RestAssured.given;

//import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class TestSchemeAdapter extends FunctionalTestCase {
	
	//@Rule
    //public WireMockRule wireMockRule = new WireMockRule(8010);
	
	private final String serviceHost = "http://localhost:8088";
	
	WebResource webService;
	
	@Override
	protected String getConfigResources() {
		return "test-resources.xml,scheme-adapter-api.xml,scheme-adapter.xml";
	}
	
	@BeforeClass
	public static void initEnv() {
		System.setProperty("MULE_ENV", "test");
		System.setProperty("spring.profiles.active", "test");
	}
	
	@Before
	public void initSslClient() throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException,
			KeyManagementException {
		ClientConfig config = new DefaultClientConfig();
		webService = Client.create(config).resource(serviceHost);
	}
	
	@Test
	public void exampleTest() throws Exception {
		String proxyQuoteRequestJson = loadResourceAsString("test_data/proxyQuoteRequest.json");
		String dfspQuoteResponseJson = loadResourceAsString("test_data/dfspQuoteResponse.json");
		//givenThat(post(urlMatching("/v1/quote")).willReturn(aResponse().withBody(dfspQuoteResponseJson)));

	    //Thread.sleep(15000);
//	    Response response =
//        	given().
//            	contentType("application/json").
//            	body(proxyQuoteRequestJson).
//            when().
//            	post("http://localhost:8088/scheme/adapter/v1/quotes");

		ClientResponse clientResponse = postRequest( "/scheme/adapter/v1/quotes", proxyQuoteRequestJson );
	}
	
	private ClientResponse postRequest( String path, String requestData ) {
		return webService.path( path ).type( "application/json").post(ClientResponse.class, requestData);
	}

}
