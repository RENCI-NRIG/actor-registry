package orca.registry;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.net.URL;
import java.util.Map;
import java.util.Vector;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 *
 * @author anirban
 */
public class TestClient {

	public static void main(String[] args) {

		//String serverHostname = "geni.renci.org";
		//String serverHostname = "http://geni.renci.org:8080/registry/";
		//String serverHostname = "http://geni-test.renci.org:11080/registry/";
		String serverHostname = "http://geni.renci.org:11080/registry/";
		//int port = 19999;

		try{
			String result;
			System.out.println("Starting xml-rpc client");
			//XmlRpcClient client = new XmlRpcClient("http://" + serverHostname + ":" + port + "/");
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL(serverHostname));
			XmlRpcClient client = new XmlRpcClient();
			client.setConfig(config);

			Vector params = new Vector();

			if(params == null) System.out.println("Null params for xml-rpc call");

//			System.out.println("=====================================================");
//			System.out.println("Obtaining information about all ORCA Actors");
//			System.out.println("=====================================================");
//			result = (String) client.execute("registryService.getActors", params);
//			System.out.println(result);
//
//			params.clear();
//			System.out.println("=====================================================");
//			System.out.println("Obtaining information about all ORCA Brokers");
//			System.out.println("=====================================================");
//			result = (String) client.execute("registryService.getBrokers", params);
//			System.out.println(result);
//
//			params.clear();
//			System.out.println("=====================================================");
//			System.out.println("Obtaining information about all ORCA Aggregate Managers");
//			System.out.println("=====================================================");
//			result = (String) client.execute("registryService.getAMs", params);
//			System.out.println(result);
//
//			params.clear();
//			System.out.println("=====================================================");
//			System.out.println("Obtaining information about all ORCA Service Managers");
//			System.out.println("=====================================================");
//			result = (String) client.execute("registryService.getSMs", params);
//			System.out.println(result);
			
			Map<String, Map<String, String>> mapResult = (Map<String, Map<String, String>>) client.execute("registryService.getSMsMap", params);
			
			System.out.println(mapResult.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error in XmlrpcClient");

		}
	}

}
