package orca.registry;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.ws.commons.util.Base64;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

/**
 *
 * @author anirban
 */
public class TestClient {


	
	private static final String SSL_KEYSTORE="keystorename";
	private static final String SSL_KEYSTORE_PASSWORD="keystorepass";
	private static final String SSL_KEY_PASSWORD="keypassword";
	
	private static KeyManager[] getKeyManagers(Properties props) throws IOException, GeneralSecurityException {
		// First, get the default KeyManagerFactory.
		String alg = KeyManagerFactory.getDefaultAlgorithm();

		// initialize multi key manager
		MultiKeyStoreManager mkm = new MultiKeyStoreManager();

		// create the other key managers for other keystores
		System.out.println("Loading keystores");
		
		String[] keystoreNames = props.getProperty(SSL_KEYSTORE).split(",");
		
		for (String ksName: keystoreNames) {
        	URL res = MultiKeyStoreManager.class.getResource(ksName + ".jks");
			System.out.println("   loading " + ksName + " from " + res.getPath());
			
			KeyManagerFactory kmFact = KeyManagerFactory.getInstance(alg);   

			FileInputStream fis = new FileInputStream(res.getPath());

			KeyStore ks = KeyStore.getInstance("jks");
			String keyStorePassword = props.getProperty(SSL_KEYSTORE_PASSWORD);

			ks.load(fis, keyStorePassword.toCharArray());
			fis.close();

			String keyPassword = props.getProperty(SSL_KEY_PASSWORD);
			
	 		// Now we initialise the KeyManagerFactory with this KeyStore
			kmFact.init(ks, keyPassword.toCharArray());
			
			X509KeyManager customX509KeyManager = MultiKeyStoreManager.getX509KeyManager(alg, kmFact);
			
			mkm.addKeyManager(ksName, customX509KeyManager);
		}
		
		KeyManager[] km = { mkm };   
		return km;
	}

	private static Map<String, Certificate> actorCerts = new HashMap<String, Certificate>();
	
	private static KeyManager[] getKeyManagers1(Properties props) throws IOException, GeneralSecurityException {
		// initialize multi key manager
		MultiKeyManager mkm = new MultiKeyManager();

		// create the other key managers for other keystores
		System.out.println("Loading keystores");
		
		String[] keystoreNames = props.getProperty(SSL_KEYSTORE).split(",");
		
		for (String ksName: keystoreNames) {
        	URL res = MultiKeyStoreManager.class.getResource(ksName + ".jks");
			System.out.println("   loading " + ksName + " from " + res.getPath());
			
			FileInputStream fis = new FileInputStream(res.getPath());

			KeyStore ks = KeyStore.getInstance("jks");
			String keyStorePassword = props.getProperty(SSL_KEYSTORE_PASSWORD);

			ks.load(fis, keyStorePassword.toCharArray());
			fis.close();
			
			mkm.addPrivateKey(ksName, 
					(PrivateKey)ks.getKey("actorkey", props.getProperty(SSL_KEY_PASSWORD).toCharArray()), 
					ks.getCertificate("actorkey"));
			
			actorCerts.put(ksName, ks.getCertificate("actorkey"));
		}
		
		KeyManager[] km = { mkm };   
		return km;
	}
	
	private static MultiKeyManager initMultiKeyManager(Properties props) throws IOException, GeneralSecurityException {
		// initialize multi key manager
		MultiKeyManager mkm = new MultiKeyManager();

		// create the other key managers for other keystores
		System.out.println("Loading keystores");
		
		String[] keystoreNames = props.getProperty(SSL_KEYSTORE).split(",");
		
		for (String ksName: keystoreNames) {
        	URL res = MultiKeyStoreManager.class.getResource(ksName + ".jks");
			System.out.println("   loading " + ksName + " from " + res.getPath());
			
			FileInputStream fis = new FileInputStream(res.getPath());

			KeyStore ks = KeyStore.getInstance("jks");
			String keyStorePassword = props.getProperty(SSL_KEYSTORE_PASSWORD);

			ks.load(fis, keyStorePassword.toCharArray());
			fis.close();
			
			mkm.addPrivateKey(ksName, 
					(PrivateKey)ks.getKey("actorkey", props.getProperty(SSL_KEY_PASSWORD).toCharArray()), 
					ks.getCertificate("actorkey"));
			
			actorCerts.put(ksName, ks.getCertificate("actorkey"));
		}
		return mkm;
	}
	
//	private static KeyManager[] getKeyManagers2() {
//		MultiKeyManager mkm = new MultiKeyManager();
//		
//		KeyManager[] km = { mkm };
//		return km;
//	}

	private SSLContext sc = null;
	
//	private void configureSSL1() {
//		
//        // Install the all-trusting trust manager
//        try {
//        	sc = SSLContext.getInstance("TLS");
//        	// Create empty HostnameVerifier
//        	HostnameVerifier hv = new HostnameVerifier() {
//        		public boolean verify(String arg0, SSLSession arg1) {
//        			return true;
//        		}
//        	};
//
//        	Properties props = new Properties();
//        	
//        	String kss = "cc748912-d46d-423a-a3d7-24062b81c596,91aa8f2f-0cd5-4180-a81b-cbb4cd1755b2,42ba8543-71e2-4043-98f8-61dfee56f3de";
//        	
//        	props.setProperty(SSL_KEYSTORE, kss);
//        	props.setProperty(SSL_KEYSTORE_PASSWORD, "clientkeystorepass");
//        	props.setProperty(SSL_KEY_PASSWORD, "clientkeypass");
//        	
//        	sc.init(getKeyManagers1(props), trustAllCerts, new java.security.SecureRandom());
//
//        	HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//        	HttpsURLConnection.setDefaultHostnameVerifier(hv);
//        	//SSLContext.setDefault(context);
//        } catch (NoSuchAlgorithmException e1) {
//        	System.err.println("No such algm: " + e1);
//        } catch (KeyManagementException e2) {
//        	System.err.println("KeyManagement exception: " + e2);
//        } catch (Exception e3) {
//        	System.err.println("Other exception: " + e3);
//        }
//	}
//	
//	private void configureSSL2() {
//		try {
//			sc = SSLContext.getInstance("TLS");
//			
//			sc.init(getKeyManagers2(), trustAllCerts, new java.security.SecureRandom());
//			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//		} catch (NoSuchAlgorithmException e1) {
//			System.err.println("No such algm: " + e1);
//		} catch (KeyManagementException e2) {
//			System.err.println("KeyManagement exception: " + e2);
//		} catch (Exception e3) {
//			System.err.println("Other exception: " + e3);
//		}
//	}
	
	// implement a factory for SSL contexts for actors
	private class MultiKeySSLContextFactory implements ContextualSSLProtocolSocketFactory.SSLContextFactory {
		private MultiKeyManager mkm;
		
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() {
						return new X509Certificate[0];
					}

					public void checkClientTrusted(X509Certificate[] certs, String authType) {
						// Trust always
					}

					public void checkServerTrusted(X509Certificate[] certs, String authType) {
						// Trust always
						MessageDigest md = null;
						try {
							md = MessageDigest.getInstance("MD5");

							if (certs.length == 0) 
								throw new CertificateException();

							//	byte[] certDigest = md.digest(certs[0].getEncoded());
							//	if (!Arrays.equals(certDigest, registryCertDigest)) {
							//		System.err.println("Certificate presented by registry does not match local copy, communications with registry is not possible");
							//		sslError = true;
							//		throw new CertificateException();
							//	}
						} catch (NoSuchAlgorithmException e) {

						} catch (Exception e) {
							System.err.println("Unable to compare server certificate digest to the existing registry digest: " + e.toString());
						}
					}
				}
		};
		
		MultiKeySSLContextFactory(MultiKeyManager m) {
			mkm = m;
		}
		
		@Override
		public SSLContext createSSLContext() {
	    	// create our own context for this host/port 
	    	SSLContext sslcontext = null;
	    	try {
	    		sslcontext = SSLContext.getInstance("TLS");
	    		KeyManager[] kms = { mkm };
	    		sslcontext.init(kms, trustAllCerts, new java.security.SecureRandom());
	    	} catch (NoSuchAlgorithmException e1) {
	    		;
	    	} catch (KeyManagementException e2) {
	    		;
	    	}
	        return sslcontext;
		}
		
	}
	private void simpleTest(final String serverHostname) {
		try{
			String result;

			System.out.println("Starting xml-rpc client");
			
        	Properties props = new Properties();
        	
        	String kss = "cc748912-d46d-423a-a3d7-24062b81c596,91aa8f2f-0cd5-4180-a81b-cbb4cd1755b2,42ba8543-71e2-4043-98f8-61dfee56f3de";
        	
        	props.setProperty(SSL_KEYSTORE, kss);
        	props.setProperty(SSL_KEYSTORE_PASSWORD, "clientkeystorepass");
        	props.setProperty(SSL_KEY_PASSWORD, "clientkeypass");
			
        	final MultiKeyManager mkm = initMultiKeyManager(props);
        	ContextualSSLProtocolSocketFactory regSslFact = 
        		new ContextualSSLProtocolSocketFactory();
        	
        	regSslFact.addHostContextFactory(new MultiKeySSLContextFactory(mkm), "127.0.0.1", 20443);
        	
			Protocol reghhttps = new Protocol("https", (ProtocolSocketFactory)regSslFact, 443); 
			Protocol.registerProtocol("https", reghhttps);
			
			XmlRpcClient client = new XmlRpcClient();
			
			// use a custom transport factory so we can use the protocol above (if you use
			// the default XmlRpcSunHttpTransportFactory, the above is ignored.
			XmlRpcCommonsTransportFactory f = new XmlRpcCommonsTransportFactory(client);
			client.setTransportFactory(f);
			
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL(serverHostname));
			client.setConfig(config);
			
			Vector<Object> params = new Vector<Object>();

			if (params == null) 
				System.out.println("Null params for xml-rpc call");

			System.out.println("Calling getRegistryVersion");
			
			result = (String)client.execute("registryService.getRegistryVersion", params);

			System.out.println("Registry version is " + result);
			
			System.out.println("Calling testSSLCert serially");
			
			for(Entry<String, Certificate> er: actorCerts.entrySet()) {
				// now call cert tests
				
				mkm.setCurrentGuid(er.getKey());
				params.clear();
				params.add(er.getKey());
				params.add(Base64.encode(er.getValue().getEncoded()));
				
				result = (String)client.execute("registryService.testSSLCert", params);
				System.out.println("Returned: " + result);
			}
			
			System.out.println("Calling testSSLCert parallel");
			
			int sleep = 3000;
			for(Entry<String, Certificate> er: actorCerts.entrySet()) {
				final String fguid = er.getKey();
				final String fcert = Base64.encode(er.getValue().getEncoded());
				final int fsleep = sleep;
				sleep -= 1000;
				Thread t = new Thread() {
					@Override
					public void run() {
//						try{
//							Thread.sleep(fsleep);
//						} catch (Exception e) {
//							;
//						}
						
						XmlRpcClient client1 = new XmlRpcClient();
						
						// use a apache's transport factory so our
						// protocol implementation is used
						XmlRpcCommonsTransportFactory f = new XmlRpcCommonsTransportFactory(client1);
						client1.setTransportFactory(f);
						
						XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
						try {
							config.setServerURL(new URL(serverHostname));
						} catch (Exception e) {
							;
						}
						client1.setConfig(config);
						
						Vector<Object> params = new Vector<Object>();
						params.add(fguid);
						params.add(fcert);
						mkm.setCurrentGuid(fguid);
						try {
							String result = (String)client1.execute("registryService.testSSLCert", params);
							System.out.println("Returned: " + result);
						} catch (Exception e) {
							System.out.println("Parallel exception " + e);
						}
					}
				};
				t.start();
			}
			
		} catch (Exception e) {
			System.err.println("Error in XmlrpcClient: " + e);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {

		String serverHostname = "https://localhost:20443/registry/";

		//System.setProperty("javax.net.debug", "all");
		//System.out.println("SSL DEBUG: " + System.getProperty("javax.net.debug"));
		
		TestClient tc = new TestClient();
		tc.simpleTest(serverHostname);
		
	}

}
