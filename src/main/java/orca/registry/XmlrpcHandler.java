/*
* Copyright (c) 2011 RENCI/UNC Chapel Hill 
*
* @author Anirban Mandal, Ilia Baldine
*
* Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
* and/or hardware specification (the "Work") to deal in the Work without restriction, including 
* without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or 
* sell copies of the Work, and to permit persons to whom the Work is furnished to do so, subject to 
* the following conditions:  
* The above copyright notice and this permission notice shall be included in all copies or 
* substantial portions of the Work.  
*
* THE WORK IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
* OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
* OUT OF OR IN CONNECTION WITH THE WORK OR THE USE OR OTHER DEALINGS 
* IN THE WORK.
*/

package orca.registry;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author anirban, ibaldin
 */
public class XmlrpcHandler {

    private static final String ORCA_ACTOR_REGISTRY_VERSION = "ORCA Actor Registry version 2.2";
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
        strongCheck = new Boolean(p.getProperty(registryStrongCheckingProperty));
        log.info("Strong certificate checking status is set to " + strongCheck);
    }

    /**
     * 
     * @return
     */
    protected String connect(){
        String result = "STATUS: SUCCESS; done connecting";
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

        log.info("Inside XmlrpcHandler: insert() - insert actors and properties for " + act_name + "/" + act_guid);

        log.debug("Checking secure");
        if (!checkSecure()) {
    		log.error("Client " + RegistryServlet.getClientIpAddress() + " is not using secure channel for 'insert'. Not allowed.");
    		return "STATUS: ERROR; you must use SSL-secured channel to update registry contents";
    	}
        
        log.debug("Checking cert in param");
        if (strongCheck && !DatabaseOperations.compareCertsBase64(act_cert64, getActorCerts())) {
        	log.error("Client " + RegistryServlet.getClientIpAddress() + "/" +  act_guid + " presented an SSL cert that does not match the one in act_cert64 of 'insert'. Not allowed.");
        	log.debug("Client " + act_guid + " presented certificate of " + getActorCerts()[0].getSubjectX500Principal());
        	return "STATUS: ERROR; your SSL cert does not match the one presented as act_cert64 parameter";
        }
        
        log.debug("Checking cert in db");
        if (!clientCertCheck(act_guid)) {
        	log.error("Client " + RegistryServlet.getClientIpAddress() + "/" + act_guid + " failed certificate check. Not allowed.");
        	return "STATUS: ERROR; your SSL cert did not match what is already in the database";
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
    		return "STATUS: ERROR; you must use SSL-secured channel to update registry contents";
    	}
        
        if (!clientCertCheck(act_guid)) {
        	log.error("Client " + RegistryServlet.getClientIpAddress() + " with guid " + act_guid + " failed certificate check. Not allowed.");
        	return "STATUS: ERROR; your SSL cert did not match what is in the database";
        }
        
        //System.out.println("Inside insert Ndl with allocatable units");
        //System.out.println("act_guid:" + act_guid + " act_abstract_rdf:" + act_abstract_rdf + " act_full_rdf:" + act_full_rdf + " act_allocatable_units:" + act_allocatable_units);
        log.debug("Inserting abstract RDF for act_guid:" + act_guid + " allocatable units: " + act_allocatable_units);

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
    		return "STATUS: ERROR; you must use SSL-secured channel to update registry contents";
    	}
        
        if (!clientCertCheck(act_guid)) {
        	log.error("Client " + RegistryServlet.getClientIpAddress() + " with guid " + act_guid + " failed certificate check. Not allowed.");
        	return "STATUS ERROR; your SSL cert did not match what is in the database";
        }
        
        //System.out.println("Inside insert Ndl");
        //System.out.println("act_guid:" + act_guid + " act_abstract_rdf:" + act_abstract_rdf + " act_full_rdf:" + act_full_rdf);
        log.debug("Inserting abstract RDF for act_guid:" + act_guid);

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
    		return "STATUS ERROR; you must use SSL-secured channel to update registry contents";
    	}
        
        if (!clientCertCheck(act_guid)) {
        	log.error("Client " + RegistryServlet.getClientIpAddress() + " with guid " + act_guid + " failed certificate check. Not allowed.");
        	return "STATUS ERROR; your SSL cert did not match what is in the database";
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
        log.info("Inside XmlrpdHandler: getActorsMap() from " + RegistryServlet.getThreadRequest().getRemoteHost());

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
        log.info("Inside XmlrpdHandler: getBrokersMap() from " + RegistryServlet.getThreadRequest().getRemoteHost());

        DatabaseOperations dbop = new DatabaseOperations();
        return dbop.queryMap(DatabaseOperations.QUERY_BROKERS, true, essentialOnly);
    }

    /**
     * Return a map containing information on all valid SMs indexed by GUID. 
     * is returned
     * @return
     */
    public Map<String, Map<String, String>> getSMs(boolean essentialOnly) {
        log.info("Inside XmlrpdHandler: getSMsMap() from " + RegistryServlet.getThreadRequest().getRemoteHost());

        DatabaseOperations dbop = new DatabaseOperations();
        return dbop.queryMap(DatabaseOperations.QUERY_SMS, true, essentialOnly);
    }

    /** Return a map of all valid AMs indexed by GUID. 
     * 
     * @return
     */
    public Map<String, Map<String, String>> getAMs(boolean essentialOnly) {
        log.info("Inside XmlrpdHandler: getAMsMap() from " + RegistryServlet.getThreadRequest().getRemoteHost());

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
     * Image-registry-related functions
     */

    /**
     * Get all available images
     */
    public List<Map<String, String>> getAllImages() {
    	log.info("Inside XmlrpcHandler: getAllImages() from " + RegistryServlet.getThreadRequest().getRemoteHost());
    	
    	DatabaseOperations dbop = new DatabaseOperations();
    	return dbop.queryImageList();
    }
    
    /**
     * Get the default image, if available
     * @return
     */
    public List<Map<String, String>> getDefaultImage() {
    	log.info("Inside XmlrpcHandler: getDefaultImage() from " + RegistryServlet.getThreadRequest().getRemoteHost());
    	
    	DatabaseOperations dbop = new DatabaseOperations();
    	return dbop.queryDefaultImage();
    }
    
    /**
     * Controller-registry-related functions
     */
    
    /**
     * Get all available controllers
     */
    public List<Map<String, String>> getAllControllers() {
    	log.info("Inside XmlrpcHandler: getAllControllers() from " + RegistryServlet.getThreadRequest().getRemoteHost());
    	
    	DatabaseOperations dbop = new DatabaseOperations();
    	return dbop.queryControllerList();
    }
    
    
    /**
     * Tester function for certs. Stateless.
     * @param guid - actor guid
     * @param cert64 - actor cert 64-encoded
     * @return - status string
     */
    public String testSSLCert(String guid, String cert64) {
    	String status = "STATUS for " + guid + " : ";
    	
    	if (checkSecure()) {
    		status += "Security = SSL USED; ";
    		if ((getActorCerts() == null) || (getActorCerts().length == 0)) {
    			status += "Certificate = NONE; ";
    		} else {
    			if (DatabaseOperations.compareCertsBase64(cert64, getActorCerts())) {
    				status += "Certificate = MATCH; ";
    			} else {
    				status += "Certificate = MISMATCH; ";
    			}
    		}
    	} else {
    		status += "Security = NO SSL USED; ";
    	}
    	return status;
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
    			log.info("Actor " + act_guid + " did not present an SSL client certificate, strong checking is disabled, proceeding.");
    			return true;
    		} else {
    			log.error("Actor " + act_guid + " did not present an SSL client certificate, strong checking is enabled, blocking");
    			return false;
    		}
    	}
    	
    	// compare the cert
    	DatabaseOperations dbop = new DatabaseOperations();
    	// returns true if actor is not in db or if there is a match, false otherwise
    	return dbop.checkCert(act_guid, chain);
    }
}
