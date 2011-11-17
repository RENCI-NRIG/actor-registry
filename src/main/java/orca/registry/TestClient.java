package orca.registry;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.Vector;

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

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 *
 * @author anirban
 */
public class TestClient {
	
	/**
	 * Combines default keystoremanager with custom keystoremanager
	 * @author ibaldin
	 *
	 */
	private static class MultiKeyStoreManager implements X509KeyManager {
		private final X509KeyManager jvmKeyManager;
		private final X509KeyManager customKeyManager;

		public MultiKeyStoreManager(X509KeyManager jvmKeyManager, X509KeyManager customKeyManager ) {
			this.jvmKeyManager = jvmKeyManager;
			this.customKeyManager = customKeyManager;  
		}

		public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
			// try the first key manager
			String alias = customKeyManager.chooseClientAlias(keyType, issuers, socket);
			if( alias == null ) {
				alias = jvmKeyManager.chooseClientAlias(keyType, issuers, socket);
				System.out.println("Reverting to JVM CLIENT alias : " + alias);
			}
			
			alias = "actorkey";
			return alias;

		}

		public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
			// try the first key manager
			String alias = customKeyManager.chooseServerAlias(keyType, issuers, socket);
			if( alias == null ) {
				alias =  jvmKeyManager.chooseServerAlias(keyType, issuers, socket);
				System.out.println("Reverting to JVM Server alias : " + alias);
			} 
			return alias;
		}

		public X509Certificate[] getCertificateChain(String alias) {
			
			X509Certificate[] chain = customKeyManager.getCertificateChain(alias);
			if( chain == null || chain.length == 0) {
				System.out.println("Reverting to JVM Chain : " + alias);
				chain = jvmKeyManager.getCertificateChain(alias);
			} 
			return chain;
		}

		public String[] getClientAliases(String keyType, Principal[] issuers) {
			String[] cAliases = customKeyManager.getClientAliases(keyType, issuers);
			String[] jAliases = jvmKeyManager.getClientAliases(keyType, issuers);
			System.out.println("Supported Client Aliases Custom: " + cAliases.length + " JVM : " + jAliases.length);
			String[] ret = new String[cAliases.length + jAliases.length];
			System.arraycopy(cAliases, 0, ret, 0, cAliases.length);
			System.arraycopy(jAliases, 0, ret, cAliases.length, jAliases.length);
			return ret;
		}

		public PrivateKey getPrivateKey(String alias) {
			PrivateKey key = customKeyManager.getPrivateKey(alias);
			if( key == null ) {
				System.out.println("Reverting to JVM Key : " + alias);
				return jvmKeyManager.getPrivateKey(alias);
			} else {
				return key;
			}
		}

		public String[] getServerAliases(String keyType, Principal[] issuers) {
			String[] cAliases = customKeyManager.getServerAliases(keyType, issuers);
			String[] jAliases = jvmKeyManager.getServerAliases(keyType, issuers);
			System.out.println("Supported Server Aliases Custom: " + cAliases.length + " JVM : " + jAliases.length);
			String[] ret = new String[cAliases.length + jAliases.length];
			System.arraycopy(cAliases, 0, ret, 0, cAliases.length);
			System.arraycopy(jAliases, 0, ret, cAliases.length, jAliases.length);
			return ret;
		}

	}
	
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
	
	private static final String SSL_KEYSTORE="keystorename";
	private static final String SSL_KEYSTORE_PASSWORD="keystorepass";
	private static final String SSL_KEY_PASSWORD="keypassword";
	
	private static KeyManager[] getKeyManagers(Properties props) throws IOException, GeneralSecurityException {
		// First, get the default KeyManagerFactory.
		String alg = KeyManagerFactory.getDefaultAlgorithm();
		KeyManagerFactory kmFact = KeyManagerFactory.getInstance(alg);   
		// Next, set up the KeyStore to use. We need to load the file into
		// a KeyStore instance.
		FileInputStream fis = new FileInputStream(props.getProperty(SSL_KEYSTORE));
		System.out.println("Loading keystore");
		KeyStore ks = KeyStore.getInstance("jks");
		String keyStorePassword = props.getProperty(SSL_KEYSTORE_PASSWORD);

		ks.load(fis, keyStorePassword.toCharArray());
		fis.close();

//		try {
//			Key k = ks.getKey("actorkey", "clientkeypass".toCharArray());
//			System.out.println("Retrieved key " + k);
//		} catch (UnrecoverableKeyException e) {
//			System.err.println("Unable to recover actorkey key");
//		}

		String keyPassword = props.getProperty(SSL_KEY_PASSWORD);
		
 		// Now we initialise the KeyManagerFactory with this KeyStore
		kmFact.init(ks, keyPassword.toCharArray());

		// default
		KeyManagerFactory dkmFact = KeyManagerFactory.getInstance(alg); 
		dkmFact.init(null,null);  

		// Get the first X509KeyManager in the list
		X509KeyManager customX509KeyManager = getX509KeyManager(alg, kmFact);
		X509KeyManager jvmX509KeyManager = getX509KeyManager(alg, dkmFact);

		KeyManager[] km = { new MultiKeyStoreManager(jvmX509KeyManager, customX509KeyManager) };   
		System.out.println("Number of key managers registered:" + km.length);  
		return km;
	}

	/**
	 * Find a X509 Key Manager compatible with a particular algorithm
	 * @param algorithm
	 * @param kmFact
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static X509KeyManager getX509KeyManager(String algorithm, KeyManagerFactory kmFact)
	throws NoSuchAlgorithmException {
		KeyManager[] keyManagers = kmFact.getKeyManagers();

		if (keyManagers == null || keyManagers.length == 0) {
			throw new NoSuchAlgorithmException("The default algorithm :" + algorithm + " produced no key managers");
		}

		X509KeyManager x509KeyManager = null;

		for (int i = 0; i < keyManagers.length; i++) {
			if (keyManagers[i] instanceof X509KeyManager) {
				x509KeyManager = (X509KeyManager) keyManagers[i];
				break;
			}
		}

		if (x509KeyManager == null) {
			throw new NoSuchAlgorithmException("The default algorithm :"+ algorithm + " did not produce a X509 Key manager");
		}
		return x509KeyManager;
	}

	private void configureSSL() {
		// TODO: ARGH!
		// first need to create an ephemeral keystore from all actorkeys in the container
		
		// then use a custom X509ExtendedKeyManager to select appropriate alias through a
		// back channel or perhaps via ThreadLocal
		
		// create SSL context with a combined keystore and this new extended keystore manager
		
        // Install the all-trusting trust manager
        try {
        	SSLContext sc = SSLContext.getInstance("TLS");
        	// Create empty HostnameVerifier
        	HostnameVerifier hv = new HostnameVerifier() {
        		public boolean verify(String arg0, SSLSession arg1) {
        			return true;
        		}
        	};

        	Properties props = new Properties();
        	URL res = getClass().getResource("cc748912-d46d-423a-a3d7-24062b81c596.jks");
        	
        	props.setProperty(SSL_KEYSTORE, res.getFile());
        	props.setProperty(SSL_KEYSTORE_PASSWORD, "clientkeystorepass");
        	props.setProperty(SSL_KEY_PASSWORD, "clientkeypass");
        	
        	sc.init(getKeyManagers(props), trustAllCerts, new java.security.SecureRandom());

        	HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        	HttpsURLConnection.setDefaultHostnameVerifier(hv);
        	//SSLContext.setDefault(context);
        } catch (NoSuchAlgorithmException e1) {
        	System.err.println("No such algm: " + e1);
        } catch (KeyManagementException e2) {
        	System.err.println("KeyManagement exception: " + e2);
        } catch (Exception e3) {
        	System.err.println("Other exception: " + e3);
        }
	}
	
	private void simpleTest(String serverHostname) {
		try{
			String result;
			System.out.println("Starting xml-rpc client");
			//XmlRpcClient client = new XmlRpcClient("http://" + serverHostname + ":" + port + "/");
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL(serverHostname));
			XmlRpcClient client = new XmlRpcClient();
			client.setConfig(config);

			Vector<Object> params = new Vector<Object>();

			if(params == null) System.out.println("Null params for xml-rpc call");

			result = (String)client.execute("registryService.getRegistryVersion", params);
			System.out.println("Registry version is " + result);
			
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
			
//			Map<String, Map<String, String>> mapResult = (Map<String, Map<String, String>>) client.execute("registryService.getSMsMap", params);
//			
//			System.out.println(mapResult.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error in XmlrpcClient");

		}
	}
	
	public static void main(String[] args) {

		//String serverHostname = "geni.renci.org";
		//String serverHostname = "http://geni.renci.org:8080/registry/";
		//String serverHostname = "http://geni-test.renci.org:11080/registry/";
		String serverHostname = "https://localhost:11443/registry/";

		//System.setProperty("javax.net.debug", "all");
		//System.out.println("SSL DEBUG: " + System.getProperty("javax.net.debug"));
		
		TestClient tc = new TestClient();
		tc.configureSSL();
		tc.simpleTest(serverHostname);
		
	}

}
