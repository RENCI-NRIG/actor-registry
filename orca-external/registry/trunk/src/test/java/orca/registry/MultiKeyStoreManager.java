package orca.registry;

import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509KeyManager;

/**
 * Combines default keystoremanager with custom keystoremanager
 * @author ibaldin (with code cribbed from StackOverflow)
 *
 */
class MultiKeyStoreManager implements X509KeyManager {
	private static final String ACTORKEY_ALIAS = "actorkey";
	private final X509KeyManager jvmKeyManager;
	private final Map<String, X509KeyManager> customKeyManagers = new HashMap<String, X509KeyManager>();
	private static ThreadLocal<String> guid = new ThreadLocal<String>();

	public MultiKeyStoreManager() {
		String alg = KeyManagerFactory.getDefaultAlgorithm();
		X509KeyManager km = null;
		try {
			// default key manager
			KeyManagerFactory dkmFact = KeyManagerFactory.getInstance(alg); 
			dkmFact.init(null,null);  

			km = getX509KeyManager(alg, dkmFact);
		} catch (Exception e) {
			;
		}
		jvmKeyManager = km;
	}
	
	public synchronized void addKeyManager(String g, X509KeyManager km) {
		if ((g != null) && (km != null)) {
			customKeyManagers.put(g, km);
			// set to last invoker 
			guid.set(g);
		}
	}
	
	private synchronized X509KeyManager getKeyManager(String guid) {
		if (guid != null)
			return customKeyManagers.get(guid);
		return null;
	}

	public static void setCurrentGuid(String g) {
		if (g != null)
			guid.set(g);
	}
	
	public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
		// always return 'actorkey' for clients
		return ACTORKEY_ALIAS;
	}

	public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
		// just return whatever default key manager says
		return jvmKeyManager.chooseServerAlias(keyType, issuers, socket);
	}

	/**
	 * Get cert chain based on selected keystore
	 */
	public X509Certificate[] getCertificateChain(String alias) {
		// pick the right key store/manager and return the key chain
		X509KeyManager selectedKm = getKeyManager(guid.get());
		
		X509Certificate[] chain = selectedKm.getCertificateChain(alias);
		
		if( chain == null || chain.length == 0) {
			chain = jvmKeyManager.getCertificateChain(alias);
		} 
		return chain;
	}

	/**
	 * collect all aliases
	 */
	public String[] getClientAliases(String keyType, Principal[] issuers) {
		// use set to enforce uniqueness
		Set<String> aliases = new HashSet<String>();
		for(Entry<String,X509KeyManager> e: customKeyManagers.entrySet()) {
			String[] cAliases = e.getValue().getClientAliases(keyType, issuers);
			aliases.addAll(Arrays.asList(cAliases));			
		}
		String[] jAliases = jvmKeyManager.getClientAliases(keyType, issuers);
		aliases.addAll(Arrays.asList(jAliases));
		
		return (String[])aliases.toArray();
	}

	/**
	 * Get private key from custom key store or JVM
	 */
	public PrivateKey getPrivateKey(String alias) {
		// pick the right key store/manager and return the key chain
		X509KeyManager selectedKm = getKeyManager(guid.get());
		
		PrivateKey key = selectedKm.getPrivateKey(alias);
		
		if( key == null ) {
			return jvmKeyManager.getPrivateKey(alias);
		} 
		return key;
	}

	/**
	 * Only JVM aliases are returned
	 */
	public String[] getServerAliases(String keyType, Principal[] issuers) {
		// only return JVM aliases here
		return jvmKeyManager.getServerAliases(keyType, issuers);
	}
	
	/**
	 * Find a X509 Key Manager compatible with a particular algorithm
	 * @param algorithm
	 * @param kmFact
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static X509KeyManager getX509KeyManager(String algorithm, KeyManagerFactory kmFact)
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

}
