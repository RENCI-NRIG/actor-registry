/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orca.registry;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import java.security.cert.X509Certificate;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author anirban
 */
public class XmlrpcHandler {

    private static final String ORCA_ACTOR_REGISTRY_VERSION = "ORCA Actor Registry version 2.1 ";
    // this is the path to orca/registry/registry.properties
	public static final String registryLogProperties="orca.registry.registry";
	public static final String registryStrongCheckingProperty="registry.strongCheck";
    Logger log;
    private boolean strongCheck = true;

    public XmlrpcHandler() {

        ClassLoader loader = this.getClass().getClassLoader();
        Properties p = PropertyLoader.loadProperties(registryLogProperties, loader);
        PropertyConfigurator.configure(p);
        log = Logger.getLogger(XmlrpcHandler.class);
        log.debug("Starting logging for Registry XmlrpcHandler");
        strongCheck = Boolean.getBoolean(p.getProperty(registryStrongCheckingProperty));
        log.info("Strong certificate checking status is set to " + strongCheck);
    }

    /**
     * 
     * @return
     */
    protected String connect(){
        String result = "done connecting";
        //System.out.println("Inside connect");
        log.debug("Inside XmlrpcHandler: connect()");
        DatabaseOperations dbop = new DatabaseOperations();
        dbop.connect();
        return result;
    }

    /** insert version for inserting the actors and their properties
     * 
     * @param act_name
     * @param act_type
     * @param act_guid
     * @param act_desc
     * @param act_soapaxis2url
     * @param act_class
     * @param act_mapper_class
     * @param act_pubkey
     * @param act_cert64
     * @return
     */
    public String insert(String act_name, String act_type, String act_guid, String act_desc, String act_soapaxis2url, String act_class, String act_mapper_class, String act_pubkey, String act_cert64){

        log.info("Inside XmlrpcHandler: insert() - insert actors and properties from " + act_name + "/" + act_guid);

        if (!checkSecure()) {
    		log.error("Client " + RegistryServlet.getClientIpAddress() + " is not using secure channel for 'insert'. Not allowed.");
    		return "Update FAILED: you must use SSL-secured channel to update registry contents";
    	}
        
        if (!clientCertCheck(act_guid)) {
        	log.error("Client " + RegistryServlet.getClientIpAddress() + " with guid " + act_guid + " failed certificate check. Not allowed.");
        	return "Update FAILED: your certificate did not match what is in the database";
        }
    	
        log.debug("Inserting: " + act_name + " " + act_type + " " + act_guid + " " + act_desc);

        DatabaseOperations dbop = new DatabaseOperations();
        return dbop.insert(act_name, act_type, act_guid, act_desc, act_soapaxis2url, act_class, act_mapper_class, act_pubkey, act_cert64);

    }

    /** insert version for inserting abstract rdf, full rdf and allocatable units for existing actors
     * 
     * @param act_guid
     * @param act_abstract_rdf
     * @param act_full_rdf
     * @param act_allocatable_units
     * @return
     */
    public String insert(String act_guid, String act_abstract_rdf, String act_full_rdf, String act_allocatable_units){

        log.info("Inside XmlrpcHandler: insert() - insert abstract rdf, full rdf, allocatable units from " + act_guid);
        
        if (!checkSecure()) {
    		log.info("Client " + RegistryServlet.getClientIpAddress() + " is not using secure channel for 'insert'. Not allowed.");
    		return "Update FAILED: you must use SSL-secured channel to update registry contents";
    	}
        
        if (!clientCertCheck(act_guid)) {
        	log.error("Client " + RegistryServlet.getClientIpAddress() + " with guid " + act_guid + " failed certificate check. Not allowed.");
        	return "Update FAILED: your certificate did not match what is in the database";
        }
        
        //System.out.println("Inside insert Ndl with allocatable units");
        //System.out.println("act_guid:" + act_guid + " act_abstract_rdf:" + act_abstract_rdf + " act_full_rdf:" + act_full_rdf + " act_allocatable_units:" + act_allocatable_units);
        log.debug("Inserting: " + "act_guid:" + act_guid + " act_abstract_rdf:" + act_abstract_rdf + " act_full_rdf:" + act_full_rdf + " act_allocatable_units:" + act_allocatable_units);

        DatabaseOperations dbop = new DatabaseOperations();
        return dbop.insertRdfs(act_guid, act_abstract_rdf, act_full_rdf, act_allocatable_units);
    }

    /** insert version for inserting abstract rdf, full rdf for existing actors
     * 
     * @param act_guid
     * @param act_abstract_rdf
     * @param act_full_rdf
     * @return
     */
    public String insert(String act_guid, String act_abstract_rdf, String act_full_rdf){

        log.info("Inside XmlrpcHandler: insert() - insert abstract rdf, full rdf from " + act_guid);
        
        if (!checkSecure()) {
    		log.info("Client " + RegistryServlet.getClientIpAddress() + " is not using secure channel for 'insert'. Not allowed.");
    		return "Update FAILED: you must use SSL-secured channel to update registry contents";
    	}
        
        if (!clientCertCheck(act_guid)) {
        	log.error("Client " + RegistryServlet.getClientIpAddress() + " with guid " + act_guid + " failed certificate check. Not allowed.");
        	return "Update FAILED: your certificate did not match what is in the database";
        }
        
        //System.out.println("Inside insert Ndl");
        //System.out.println("act_guid:" + act_guid + " act_abstract_rdf:" + act_abstract_rdf + " act_full_rdf:" + act_full_rdf);
        log.debug("Inserting: " + "act_guid:" + act_guid + " act_abstract_rdf:" + act_abstract_rdf + " act_full_rdf:" + act_full_rdf);

        DatabaseOperations dbop = new DatabaseOperations();
        return dbop.insertRdfs(act_guid, act_abstract_rdf, act_full_rdf);
    }

    /** insert version to insert periodic heartbeats, which change the last update date for an actor
     * 
     * @param act_guid
     * @return
     */
    public String insert(String act_guid){

        log.info("Inside XmlrpdHandler: insert() - insert heartbeats from " + act_guid);
        
        if (!checkSecure()) {
    		log.info("Client " + RegistryServlet.getClientIpAddress() + " is not using secure channel for 'insert'. Not allowed.");
    		return "Update FAILED: you must use SSL-secured channel to update registry contents";
    	}
        
        if (!clientCertCheck(act_guid)) {
        	log.error("Client " + RegistryServlet.getClientIpAddress() + " with guid " + act_guid + " failed certificate check. Not allowed.");
        	return "Update FAILED: your certificate did not match what is in the database";
        }
        
        log.debug("Inserting: heartbeats for act_guid: " + act_guid);
        DatabaseOperations dbop = new DatabaseOperations();
        return dbop.insertHeartbeat(act_guid);
    }

    /**
     * Return a map of all known actors indexed by GUID. 
     * @return
     */
    public Map<String, Map<String, String>> getActors(boolean essentialOnly) {
        log.info("Inside XmlrpdHandler: getActorsMap()");

        DatabaseOperations dbop = new DatabaseOperations();
        return dbop.queryMap(DatabaseOperations.QUERY_ACTORS, true, essentialOnly);
    }
    
    /**
     * return a map of actors other than those listed indexed by GUID. 
     * @param guids
     * @return
     */
    public Map<String, Map<String, String>> getActorsOtherThan(List<String> guids, boolean essentialOnly) {
    	DatabaseOperations dbop = new DatabaseOperations();
    	
    	Map<String, Map<String, String>> res = dbop.queryMap(DatabaseOperations.QUERY_ACTORS, true, essentialOnly);
    	
    	// filter out known guids
    	for(String g: guids) 
    		res.remove(g.trim());
    	
    	return res;
    }
    
    /**
     * return a map of valid and verified (admin-approved) actors other than those listed indexed by GUID
     * @param guids
     * @param essentialOnly
     * @return
     */
    public Map<String, Map<String, String>> getActorsVerifiedOtherThan(List<String> guids, boolean essentialOnly) {
    	DatabaseOperations dbop = new DatabaseOperations();
    	
    	Map<String, Map<String, String>> res = dbop.queryMap(DatabaseOperations.QUERY_ACTORS_VERIFIED, true, essentialOnly);
    	
    	// filter out known guids
    	for(String g: guids) 
    		res.remove(g.trim());
    	
    	return res;
    }
    
    
    /**
     * Return a map containing information on all valid brokers indexed by GUID.  
     * @return
     */
    public Map<String, Map<String, String>> getBrokers(boolean essentialOnly) {
        log.info("Inside XmlrpdHandler: getBrokersMap()");

        DatabaseOperations dbop = new DatabaseOperations();
        return dbop.queryMap(DatabaseOperations.QUERY_BROKERS, true, essentialOnly);
    }

    /**
     * Return a map containing information on all valid SMs indexed by GUID. 
     * is returned
     * @return
     */
    public Map<String, Map<String, String>> getSMs(boolean essentialOnly) {
        log.info("Inside XmlrpdHandler: getSMsMap()");

        DatabaseOperations dbop = new DatabaseOperations();
        return dbop.queryMap(DatabaseOperations.QUERY_SMS, true, essentialOnly);
    }

    /** Return a map of all valid AMs indexed by GUID. 
     * 
     * @return
     */
    public Map<String, Map<String, String>> getAMs(boolean essentialOnly) {
        log.info("Inside XmlrpdHandler: getAMsMap()");

        DatabaseOperations dbop = new DatabaseOperations();
        return dbop.queryMap(DatabaseOperations.QUERY_AMS, true, essentialOnly);
    }
    
    /**
     * Get registry version
     * @return
     */
    public String getRegistryVersion() {

    	return ORCA_ACTOR_REGISTRY_VERSION;
    }

    /**
     * Retrieve certificate chain if available (null if not)
     * @return
     */
    protected X509Certificate[] getActorCerts() {
    	if (checkSecure()) {
        	HttpServletRequest pRequest = RegistryServlet.getThreadRequest();
        	Object certChain = pRequest.getAttribute("javax.servlet.request.X509Certificate");
    		if (certChain != null) {
    			return (X509Certificate[])certChain;
    		}
    		else {
    			return null;
    		}
    	}
    	return null;
    }
    
    /**
     * Check if the client is using secure channel
     * @return
     */
    protected boolean checkSecure() {
    	// if comms are secure, session id will be set
    	HttpServletRequest pRequest = RegistryServlet.getThreadRequest();
    	String sslSessionId = (String)pRequest.getAttribute("javax.servlet.request.ssl_session");

    	// if ssl session id is present, client using SSL/TLS
    	if (sslSessionId == null) {
    		return false;
    	}
    	return true;
    }
    
    /**
     * Check client certificate against the database
     * @param act_guid
     * @return
     */
    protected boolean clientCertCheck(String act_guid) {
    	// get the cert chains from SSL
    	X509Certificate[] chain = getActorCerts();
    	
    	if ((chain == null) || (chain.length == 0)) {
    		if (!strongCheck) {
    			log.info("Actor " + act_guid + " did not present a valid certificate, strong checking is disabled, proceeding.");
    			return true;
    		} else {
    			log.error("Actor " + act_guid + " did not present a valid certificate, strong checking is enabled, blocking");
    			return false;
    		}
    	}
    	
    	// compare the cert
    	DatabaseOperations dbop = new DatabaseOperations();
    	// returns true if actor is not in db or if there is a match, false otherwise
    	return dbop.checkCert(act_guid, chain);
    }
}
