/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orca.registry;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author anirban
 */
public class DatabaseOperations {

    private static final String STATUS_SUCCESS = "STATUS: SUCCESS";
	private static final String REGISTRY_DB_URL = "registry.dbUrl";
	private static final String REGISTRY_PASSWORD = "registry.password";
	private static final String REGISTRY_USERNAME = "registry.username";
	private static final String TRUE_STRING = "True";
	private static final String FALSE_STRING = "False";
	
	public static final String QUERY_AMS = "ams";
	public static final String QUERY_BROKERS = "brokers";
	public static final String QUERY_SMS = "sms";
	public static final String QUERY_ACTORS_VERIFIED = "actors_verified";
	public static final String QUERY_ACTORS = "actors";
    
	private static final String SOAPAXIS2_PROTOCOL = "soapaxis2";
	
	public static final String ActorAllocunits = "ALLOCUNITS";
	public static final String ActorFullRDF = "FULLRDF";
	public static final String ActorAbstractRDF = "ABSRDF";
	public static final String ActorCert64 = "CERT";
	public static final String ActorPubkey = "PUBKEY";
	public static final String ActorMapperclass = "MAPPERCLASS";
	public static final String ActorClazz = "CLASS";
	public static final String ActorLocation = "LOCATION";
	public static final String ActorProtocol = "PROTOCOL";
	public static final String ActorType = "TYPE";
	public static final String ActorGuid = "GUID";
	public static final String ActorName = "NAME";
	public static final String ActorDesc = "DESC";
	public static final String ActorLastUpdate = "LASTUPDATE";
	public static final String ActorProduction = "PRODUCTION";
	public static final String ActorVerified = "VERIFIED";
	
	private String userName = "registry";
    private String password = "registry";
    private String url = "jdbc:mysql://localhost:3306/ActorRegistry";
    Logger log;

    public DatabaseOperations()  {
        log = Logger.getLogger(DatabaseOperations.class);
        log.debug("Starting logging for Registry DatabaseOperations");
        ClassLoader loader = this.getClass().getClassLoader();
        Properties p = PropertyLoader.loadProperties(XmlrpcHandler.registryLogProperties, loader);
        PropertyConfigurator.configure(p);
        
        if (p.getProperty(REGISTRY_USERNAME) != null)
        	userName=p.getProperty(REGISTRY_USERNAME);
        if (p.getProperty(REGISTRY_PASSWORD) != null)
        	password=p.getProperty(REGISTRY_PASSWORD);
        if (p.getProperty(REGISTRY_DB_URL) != null)
        	url=p.getProperty(REGISTRY_DB_URL);
    }


    public void connect(){

       Connection conn = null;

       try{
            log.debug("Inside DatabaseOperations: connect()");
            //System.out.println("Trying to get a new instance");
            log.debug("Inside DatabaseOperations: connect() - Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            //System.out.println("Trying to get a database connection");
            log.debug("Inside DatabaseOperations: connect() - Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            //System.out.println ("Database connection established");
            log.debug("Inside DatabaseOperations: connect() - Database connection established");
        }
        catch(Exception e){
            //System.err.println ("Cannot connect to database server");
            log.error("Cannot connect to database server");
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    //System.out.println ("Database connection terminated");
                    log.error("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }

    }

    public void testQuery(String query){

        Connection conn = null;

        try{

            log.debug("Inside DatabaseOperations: testQuery()");
            //System.out.println("Trying to get a new instance");
            log.debug("Inside DatabaseOperations: testQuery() - Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            //System.out.println("Trying to get a database connection");
            log.debug("Inside DatabaseOperations: testQuery() - Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            //System.out.println ("Database connection established");
            log.debug("Inside DatabaseOperations: testQuery() - Database connection established");

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet srs = stmt.executeQuery("SELECT * FROM TestActors");

            while (srs.next()) {
                String act_name = srs.getString("act_name");
                String act_guid = srs.getString("act_guid");
                //System.out.println("Actor Name: " + act_name + " | Actor GUID: " + act_guid );
                log.debug("Actor Name: " + act_name + " | Actor GUID: " + act_guid );
            }

        }
        catch(Exception e){
            //System.err.println ("Cannot query the database server");
            log.error("Cannot query the database server");
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    //System.out.println ("Database connection terminated");
                    log.error("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }

    }

    /**
     *  insert version for inserting the actors and their properties
     * @param act_name
     * @param act_type
     * @param act_guid
     * @param act_desc
     * @param act_soapaxis2url
     * @param act_class
     * @param act_mapper_class
     * @param act_pubkey
     * @param act_cert64
     */
    public String insert(String act_name, String act_type, String act_guid, String act_desc, String act_soapaxis2url, String act_class, String act_mapper_class, String act_pubkey, String act_cert64){

    	if ((act_name == null) || (act_type == null) || (act_guid == null) || 
    			(act_soapaxis2url == null) || (act_class == null) || (act_mapper_class == null) ||
    			(act_pubkey == null) || (act_cert64 == null))
    		return "STATUS: ERROR; invalid insert parameters";
    			
    	if (act_desc == null) {
    		act_desc = "No description";
    	}
    	
        log.debug("Inside DatabaseOperations: insert() - inserting actors and their properties");
        String status = STATUS_SUCCESS;
        Connection conn = null;

        try{            
            // Query the Actors table to find out if act_guid already present
            // If act_guid already present, check if the ip address of the client 
            // matches the IP address returned by InetAddress.getByName(act_soapaxis2url - the extracted portion of soapaxis2url)
            // If it matches, execute an 'Update' command for that row, OR, delete that row and insert this new row
            // Set new timestamp for that row
            
            String clientIP = RegistryServlet.getClientIpAddress();
            //System.out.println("clientIP = " + clientIP);
            log.debug("DatabaseOperations: insert() -  clientIP = " + clientIP);

            if(clientIP == null){
                //System.out.println("Can't get IP address of client; Insert failed");
                log.error("DatabaseOperations: insert() -  Can't get IP address of client; Insert failed");
                return "STATUS: ERROR; Can't get IP address of client; Insert failed";
            }

            String[] splitSoapUrl = act_soapaxis2url.split("//");
            String noHttp = splitSoapUrl[1];
            String[] splitNoHttp = noHttp.split(":");
            String ipSoapUrl = splitNoHttp[0];

            //System.out.println("ip in SoapUrl = " + ipSoapUrl);
            log.debug("DatabaseOperations: insert() -  ip in SoapUrl = " + ipSoapUrl);

            String humanReadableIP = null;
            String numericIP = null;
            try {
                InetAddress address = InetAddress.getByName(ipSoapUrl);
                //System.out.println("humanreadable IP/numeric IP = " + address.toString());
                //log.debug("humanreadable IP/numeric IP = " + address.toString());
                String[] splitResultGetByName = address.toString().split("/");
                humanReadableIP = splitResultGetByName[0];
                numericIP = splitResultGetByName[1];
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            	return "STATUS: ERROR; Exception encountered";
            }

            boolean insertEntry = false;
            String act_production_deployment = FALSE_STRING;
            if(clientIP.equalsIgnoreCase(numericIP)){
                insertEntry = true;
                act_production_deployment = TRUE_STRING;
            }
            else{
                if (ipSoapUrl.equalsIgnoreCase("localhost")){ // Special check: if the soapaxis url is localhost (implying test deployment) set production deployment as false
                    insertEntry = true;
                    act_production_deployment = FALSE_STRING;
                }
                else {
                    //System.out.println("Can't verify the identity of the client; client IP doesn't match with IP in SOAP-Axis URL of the Actor; It is also not a test deployment. INSERT Failed !!!");
                    log.error("Can't verify the identity of the client; client IP doesn't match with IP in SOAP-Axis URL of the Actor; It is also not a test deployment. INSERT Failed !!!");
                    return "STATUS: ERROR; Can't verify the identity of the client; client IP doesn't match with IP in SOAP-Axis URL of the Actor;";
                }
            }

            boolean actorExists = checkExistingGuid(act_guid);

            //System.out.println("Trying to get a new instance");
            log.debug("Inside DatabaseOperations: insert() - Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            //System.out.println("Trying to get a database connection");
            log.debug("Inside DatabaseOperations: insert() - Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            //System.out.println ("Database connection established");
            log.debug("Inside DatabaseOperations: insert() - Database connection established");

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String act_last_update = sdf.format(cal.getTime());
            
            if(insertEntry){ // valid client trying to insert new entry or trying to update an existing entry

                if(!actorExists) { // New actor
                	PreparedStatement pStat = conn.prepareStatement("INSERT into `Actors` ( `act_name` , `act_guid` , `act_type`, `act_desc`, `act_soapaxis2url`, `act_class`, `act_mapper_class`, `act_pubkey`, `act_cert64`, `act_production_deployment`, `act_last_update`, `act_verified`) values " +
                			 "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                	pStat.setString(1, act_name);
                	pStat.setString(2, act_guid);
                	pStat.setString(3, act_type);
                	pStat.setString(4, act_desc);
                	pStat.setString(5, act_soapaxis2url);
                	pStat.setString(6, act_class);
                	pStat.setString(7, act_mapper_class);
                	pStat.setString(8, act_pubkey);
                	pStat.setString(9, act_cert64);
                	pStat.setString(10, act_production_deployment);
                	pStat.setString(11, act_last_update);
                	pStat.setString(12, FALSE_STRING);
                	pStat.execute();
                }
                else{ // Existing actor
                    // get ALL known entries
                	Map<String, Map<String, String>> res = queryMap(act_guid, false, false);
                	
                	// update if necessary: only location  and description can be updated
                	if (!res.get(ActorLocation).equals(act_soapaxis2url) || !res.get(ActorDesc).equals(act_desc)) {
                		PreparedStatement pStat = conn.prepareStatement("UPDATE Actors SET act_soapaxis2url = ?, act_desc = ?, act_last_update = ? WHERE act_guid = ?");
                		pStat.setString(1, act_soapaxis2url);
                		pStat.setString(2, act_desc);
                		pStat.setString(3, act_last_update);
                		pStat.setString(4, act_guid);
                		pStat.execute();
                		
                	} else {
                		// if any other mismatch - return error
                		if (!res.get(ActorName).equals(act_name) || !res.get(ActorClazz).equals(act_class) || 
                				!res.get(ActorMapperclass).equals(act_mapper_class) || 
                				!res.get(ActorPubkey).equals(act_pubkey) || !res.get(ActorCert64).equals(act_cert64))
                			status = "STATUS: ERROR; Mimatch to previous registration for this guid. Please change the guid and generate new certificate;";
                		else
                			// otherwise simply insert heartbeat for this guid
                			insertHeartbeat(act_guid);
                	}
                }
            }
        }
        catch(Exception e){
            //System.err.println ("Error inserting into Actors table");
            log.error("DatabaseOperations: insert() - Error inserting into Actors table");
            status = "STATUS: ERROR; Exception encountered during insert";
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    //System.out.println ("Database connection terminated");
                    log.debug("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }
        return status;
    }

    /**
     * insert version for inserting abstract rdf, full rdf and allocatable units for existing actors
     * @param act_guid
     * @param act_abstract_rdf
     * @param act_full_rdf
     * @param act_allocatable_units
     */
    public String insertRdfs(String act_guid, String act_abstract_rdf, String act_full_rdf, String act_allocatable_units){

    	if ((act_guid==null) || (act_full_rdf == null) || (act_abstract_rdf==null) || (act_allocatable_units==null))
    		return "STATUS: ERROR; Invalid parameters";
    	
        log.debug("Inside DatabaseOperations: insertRdfs() - inserting abstract rdf, full rdf and allocatable units");

        Connection conn = null;
        String status = STATUS_SUCCESS;
        try {
            String clientIP = RegistryServlet.getClientIpAddress();
            //System.out.println("clientIP = " + clientIP);
            log.debug("DatabaseOperations: inserRdfst() -  clientIP = " + clientIP);

            if(clientIP == null){
                //System.out.println("Can't get IP address of client; Insert failed");
                log.error("DatabaseOperations: insertRdfs() - Can't get IP address of client; Insert failed");
                return "STATUS: ERROR; Can't get IP address of client";
            }

            String act_soapaxis2url = getSoapAxis2Url(act_guid);
            if(act_soapaxis2url == null){
                //System.out.println("Actor with guid: " + act_guid + " doesn't have a soapaxis2url");
                log.error("DatabaseOperations: insertRdfs() - " + "Actor with guid: " + act_guid + " doesn't have a soapaxis2url; Insert failed");
                return "STATUS: ERROR; Actor missing soapaxis2 URL";
            }

            boolean insertEntry = checkIP(clientIP, act_soapaxis2url);

            if (!checkExistingGuid(act_guid)) {
            	log.error("DatabaseOperations: insertRdfs() - " + "Actor with guid: " + act_guid + " actor entry does not exist");
            	return "STATUS: ERROR; unknown actor " + act_guid;
            }
            
            //System.out.println("Trying to get a new instance");
            log.debug("Inside DatabaseOperations: insertRdfs() - Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            //System.out.println("Trying to get a database connection");
            log.debug("Inside DatabaseOperations: insertRdfs() - Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            //System.out.println ("Database connection established");
            log.debug("Inside DatabaseOperations: insertRdfs() - Database connection established");

            if(insertEntry) { // valid client trying to update an existing entry with rdfs

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String act_last_update = sdf.format(cal.getTime());

                PreparedStatement pStat = conn.prepareStatement("UPDATE Actors SET act_abstract_rdf= ?, act_full_rdf= ?, act_allocatable_units= ?, act_last_update= ? WHERE act_guid= ?");
                pStat.setString(1, act_abstract_rdf);
                pStat.setString(2, act_full_rdf);
                pStat.setString(3, act_allocatable_units);
                pStat.setString(4, act_last_update);
                pStat.setString(5, act_guid);
                pStat.execute();
            }
            
        }
        catch(Exception e){
            //System.err.println ("Error inserting Ndl into Actors table");
            log.error("Inside DatabaseOperations: insertRdfs() - Exception while inserting Ndl into Actors table");
            status = "STATUS: ERROR; Exception encountered while inserting NDL";
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    //System.out.println ("Database connection terminated");
                    log.debug("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }
        return status;
    }

    /**
     *  insert version for inserting abstract rdf, full rdf for existing actors
     * @param act_guid
     * @param act_abstract_rdf
     * @param act_full_rdf
     */
    public String insertRdfs(String act_guid, String act_abstract_rdf, String act_full_rdf){

       	if ((act_guid==null) || (act_full_rdf == null) || (act_abstract_rdf==null))
    		return "STATUS: ERROR; Invalid parameters";
 
    	log.debug("Inside DatabaseOperations: insertRdfs() - inserting abstract rdf and full rdf");

        Connection conn = null;
        String status = STATUS_SUCCESS;
        
        try{
            String clientIP = RegistryServlet.getClientIpAddress();
            //System.out.println("clientIP = " + clientIP);
            log.debug("DatabaseOperations: insertRdfs() -  clientIP = " + clientIP);

            if(clientIP == null){
                //System.out.println("Can't get IP address of client; Insert failed");
                log.error("DatabaseOperations: insertRdfs() - Can't get IP address of client; Insert failed");
                return "STATUS: ERROR; Can't get IP address of client";
            }

            String act_soapaxis2url = getSoapAxis2Url(act_guid);
            if(act_soapaxis2url == null){
                //System.out.println("Actor with guid: " + act_guid + " doesn't have a soapaxis2url");
                log.error("DatabaseOperations: insertRdfs() - " + "Actor with guid: " + act_guid + " doesn't have a soapaxis2url; Insert failed");
                return "STATUS: ERROR; Actor does not have soapaxis2 URL";
            }

            boolean insertEntry = checkIP(clientIP, act_soapaxis2url);

            if (!checkExistingGuid(act_guid)) {
            	log.error("DatabaseOperations: insertRdfs() - " + "Actor with guid: " + act_guid + " actor entry does not exist");
            	return "STATUS: ERROR; unknown actor " + act_guid;
            }
            
            //System.out.println("Trying to get a new instance");
            log.debug("Inside DatabaseOperations: insertRdfs() - Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            //System.out.println("Trying to get a database connection");
            log.debug("Inside DatabaseOperations: insertRdfs() - Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            //System.out.println ("Database connection established");
            log.debug("Inside DatabaseOperations: insertRdfs() - Database connection established");

            if(insertEntry){ // valid client trying to update an existing entry with rdfs

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String act_last_update = sdf.format(cal.getTime());

                PreparedStatement pStat = conn.prepareStatement("UPDATE Actors SET act_abstract_rdf= ?, act_full_rdf= ?, act_last_update= ?, WHERE act_guid= ?");
                pStat.setString(1, act_abstract_rdf);
                pStat.setString(2, act_full_rdf);
                pStat.setString(3, act_last_update);
                pStat.execute();
            }
        }
        catch(Exception e){
            //System.err.println ("Error inserting Ndl into Actors table");
            log.error("Inside DatabaseOperations: insertRdfs() - Exception while inserting Ndl into Actors table");
            status = "STATUS: ERROR; Exception encountered while inserting Ndl";
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    //System.out.println ("Database connection terminated");
                    log.debug("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }
        return status;
    }


    // insert version for heartbeats; The method name is confusing; the semantic is to insert the most recent last update date for the actor
    public String insertHeartbeat(String act_guid){

    	if (act_guid == null)
    		return "STATUS: ERROR; Actor guid is null";
    	
        log.debug("Inside DatabaseOperations: insertHeartbeat() - inserting heartbeats");

        Connection conn = null;
        String status = STATUS_SUCCESS;
        
        try{
            String clientIP = RegistryServlet.getClientIpAddress();
            //System.out.println("clientIP = " + clientIP);
            log.debug("DatabaseOperations: insertHeartbeat() -  clientIP = " + clientIP);

            if(clientIP == null){
                //System.out.println("Can't get IP address of client; Insert failed");
                log.error("DatabaseOperations: insertHeartbeat() - Can't get IP address of client; Insert failed");
                return "STATUS: ERROR; Can't get IP address of client";
            }

            String act_soapaxis2url = getSoapAxis2Url(act_guid);
            if(act_soapaxis2url == null){
                //System.out.println("Actor with guid: " + act_guid + " doesn't have a soapaxis2url");
                log.error("DatabaseOperations: inserHeartbeatt() - " + "Actor with guid: " + act_guid + " doesn't have a soapaxis2url; Insert failed");
                return "STATUS: ERROR; Actor does not have a soapaxis2 URL";
            }

            boolean insertEntry = checkIP(clientIP, act_soapaxis2url);

            if (!checkExistingGuid(act_guid)) {
            	log.error("DatabaseOperations: insertHeartbeat() - " + "Actor with guid: " + act_guid + " actor entry does not exist");
            	return "STATUS: ERROR; unknown actor " + act_guid;
            }
            
            //System.out.println("Trying to get a new instance");
            log.debug("Inside DatabaseOperations: insertHeartbeat() - Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            //System.out.println("Trying to get a database connection");
            log.debug("Inside DatabaseOperations: insertHeartbeat() - Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            //System.out.println ("Database connection established");
            log.debug("Inside DatabaseOperations: insertHeartbeat() - Database connection established");

            if(insertEntry){ // valid client trying to update an existing entry with rdfs

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String act_last_update = sdf.format(cal.getTime());

                PreparedStatement pStat = conn.prepareStatement("UPDATE Actors SET act_last_update= ? WHERE act_guid= ?");
                pStat.setString(1, act_last_update);
                pStat.setString(2, act_guid);
                pStat.execute();
            }

        }
        catch(Exception e){
            //System.err.println ("Error inserting heartbeats");
            log.error("Inside DatabaseOperations: insertHeartbeat() - Exception while inserting heartbeats");
            status = "STATUS: ERROR; Exception encountered while inserting";
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    //System.out.println ("Database connection terminated");
                    log.debug("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }
        return status;
    }

    // make sure no null keys or values are inserted
    private void nonNullMapPut(Map<String, String> m, String key, String val) {
    	if ((key == null) || (val == null))
    		return;
    	m.put(key, val);
    }
    
	/**
	 * Return information about actors as map indexed by actor name. If essential only set, don't
	 * return RDFs and descriptions
	 * @param actorType - one of 'actors', 'sms', 'brokers' or 'ams'
	 * @param validOnly - only admin-validated actors are included
	 * @param essentialOnly - provide only name, type, location and certificate
	 * @return
	 */
    public Map<String, Map<String, String>> queryMap(String actorType, boolean validOnly, boolean essentialOnly) {
    	
    	HashMap<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();

    	if (actorType == null) {
    		result.put("STATUS", new HashMap<String, String>() {
            	{
            		put("STATUS", "Unknown actor type");
            	}
    		});
    		return result;
    	}
    		
    	log.debug("Inside DatabaseOperations: query() - query for Actor of Type: " + actorType);
        Connection conn = null;

        try{
            //System.out.println("Trying to get a new instance");
            log.debug("Inside DatabaseOperations: query() - Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            //System.out.println("Trying to get a database connection");
            log.debug("Inside DatabaseOperations: query() - Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            //System.out.println ("Database connection established");
            log.debug("Inside DatabaseOperations: query() - Database connection established");

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            result.put("STATUS", new HashMap<String, String>() {
            	{
            		put("STATUS", "No actors match the query");
            	}
            });
            
            ResultSet srs = null;
            if(actorType.equalsIgnoreCase(QUERY_ACTORS)){
                srs = stmt.executeQuery("SELECT * FROM Actors");
            } else if (actorType.equalsIgnoreCase(QUERY_ACTORS_VERIFIED)) {
            	srs = stmt.executeQuery("SELECT * FROM Actors where act_verified='" + TRUE_STRING + "'");
            } else if(actorType.equalsIgnoreCase(QUERY_SMS)){
                srs = stmt.executeQuery("SELECT * FROM Actors where act_type=1");
            }
            else if(actorType.equalsIgnoreCase(QUERY_BROKERS)){
                srs = stmt.executeQuery("SELECT * FROM Actors where act_type=2");
            }
            else if(actorType.equalsIgnoreCase(QUERY_AMS)){
                srs = stmt.executeQuery("SELECT * FROM Actors where act_type=3");
            }
            else {
                result.put("STATUS", new HashMap<String, String>() {
                	{
                		put("STATUS", "Unknown actor type");
                	}
                });
            }

            while (srs.next()) {
            	HashMap<String, String> tmpMap = new HashMap<String, String>();
            	nonNullMapPut(tmpMap, ActorName, srs.getString("act_name"));
            	nonNullMapPut(tmpMap, ActorGuid, srs.getString("act_guid"));

            	String act_type = srs.getString("act_type");
                String actor_type = null;
                // These names match ConfigurationProcessor definitions in ORCA
                if(act_type.equalsIgnoreCase("1")){
                    actor_type = "sm";
                }
                if(act_type.equalsIgnoreCase("2")){
                    actor_type = "broker";
                }
                if(act_type.equalsIgnoreCase("3")){
                    actor_type = "site";
                }
            	nonNullMapPut(tmpMap, ActorType, actor_type);
            	
            	nonNullMapPut(tmpMap, ActorLocation, srs.getString("act_soapaxis2url"));

            	nonNullMapPut(tmpMap, ActorCert64, srs.getString("act_cert64"));
            	
            	if (!essentialOnly) {
            		nonNullMapPut(tmpMap, ActorFullRDF, srs.getString("act_full_rdf"));
            		nonNullMapPut(tmpMap, ActorAllocunits, srs.getString("act_allocatable_units"));
                	nonNullMapPut(tmpMap, ActorAbstractRDF, srs.getString("act_abstract_rdf"));
                	nonNullMapPut(tmpMap, ActorClazz, srs.getString("act_class"));
                	nonNullMapPut(tmpMap, ActorMapperclass, srs.getString("act_mapper_class"));
                	nonNullMapPut(tmpMap, ActorDesc, srs.getString("act_desc"));
                	nonNullMapPut(tmpMap, ActorPubkey, srs.getString("act_pubkey"));
            	}
            	// FIXME: hard code protocol for now
            	nonNullMapPut(tmpMap, ActorProtocol, SOAPAXIS2_PROTOCOL);
            	
                String act_last_update = srs.getString("act_last_update");
            	nonNullMapPut(tmpMap, ActorLastUpdate, act_last_update);
            	
                String act_production_deployment = srs.getString("act_production_deployment");
                nonNullMapPut(tmpMap, ActorProduction, act_production_deployment);
            	
                String act_verified = srs.getString("act_verified");
                nonNullMapPut(tmpMap, ActorVerified, act_verified);
                
            	// save the result 
                if (validOnly && isValidEntry(tmpMap))
                	result.put(srs.getString("act_guid"), tmpMap);
                else if (!validOnly)
                	result.put(srs.getString("act_guid"), tmpMap);
            }
        }
        catch(Exception e){
            //System.err.println ("Cannot query the database server");
            log.error("Inside DatabaseOperations: query() - Exception while querying the database server: " + e.toString());
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    //System.out.println ("Database connection terminated");
                    log.debug("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }
        
        if (result.size() > 1) 
            result.put("STATUS", new HashMap<String, String>() {
            	{
            		put("STATUS", STATUS_SUCCESS);
            	}
            });

        return result;
    }
    
    /**
     * Get data on one actor
     * @param act_guid
     * @param essentialOnly - provide only name, type, location and certificate
     * @return
     */
    public Map<String, String> queryMapForGuid(String act_guid, boolean essentialOnly) {

    	Map<String, String> tmpMap = new HashMap<String, String>();

    	if (act_guid == null) 
    		return tmpMap;
    	
        Connection conn = null;

        try{
            
            //System.out.println("Trying to get a new instance");
            log.debug("Inside DatabaseOperations: query() - Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            //System.out.println("Trying to get a database connection");
            log.debug("Inside DatabaseOperations: query() - Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            //System.out.println ("Database connection established");
            log.debug("Inside DatabaseOperations: query() - Database connection established");

            PreparedStatement pStat = conn.prepareStatement("SELECT * FROM Actors where act_guid= ?");
            pStat.setString(1, act_guid);
            
            ResultSet srs = pStat.executeQuery();

            while (srs.next()) {
            	nonNullMapPut(tmpMap, ActorName, srs.getString("act_name"));
            	nonNullMapPut(tmpMap, ActorGuid, srs.getString("act_guid"));

            	String act_type = srs.getString("act_type");
                String actor_type = null;
                // These names match ConfigurationProcessor definitions in ORCA
                if(act_type.equalsIgnoreCase("1")){
                    actor_type = "sm";
                }
                if(act_type.equalsIgnoreCase("2")){
                    actor_type = "broker";
                }
                if(act_type.equalsIgnoreCase("3")){
                    actor_type = "site";
                }
            	nonNullMapPut(tmpMap, ActorType, actor_type);
            	
            	nonNullMapPut(tmpMap, ActorLocation, srs.getString("act_soapaxis2url"));
            	nonNullMapPut(tmpMap, ActorCert64, srs.getString("act_cert64"));
            	
            	if (!essentialOnly) {
            		nonNullMapPut(tmpMap, ActorFullRDF, srs.getString("act_full_rdf"));
            		nonNullMapPut(tmpMap, ActorAllocunits, srs.getString("act_allocatable_units"));
                	nonNullMapPut(tmpMap, ActorAbstractRDF, srs.getString("act_abstract_rdf"));
                	nonNullMapPut(tmpMap, ActorClazz, srs.getString("act_class"));
                	nonNullMapPut(tmpMap, ActorMapperclass, srs.getString("act_mapper_class"));
                	nonNullMapPut(tmpMap, ActorDesc, srs.getString("act_desc"));
                	nonNullMapPut(tmpMap, ActorPubkey, srs.getString("act_pubkey"));
            	}
            	// FIXME: hard code protocol for now
            	nonNullMapPut(tmpMap, ActorProtocol, SOAPAXIS2_PROTOCOL);
            	
                String act_last_update = srs.getString("act_last_update");
            	nonNullMapPut(tmpMap, ActorLastUpdate, act_last_update);
            	
                String act_production_deployment = srs.getString("act_production_deployment");
                nonNullMapPut(tmpMap, ActorProduction, act_production_deployment);
            	
                String act_verified = srs.getString("act_verified");
                nonNullMapPut(tmpMap, ActorVerified, act_verified);
            }
        }
        catch(Exception e){
            //System.err.println ("Cannot query the database server");
            log.error("Inside DatabaseOperations: query() - Exception while querying the database server: " + e.toString());
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    //System.out.println ("Database connection terminated");
                    log.debug("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }
       
        return tmpMap;
    }
    
    private boolean checkExistingGuid(String input_act_guid){

    	if (input_act_guid == null)
    		return false;
    	
       log.debug("Inside DatabaseOperations: checkExistingGuid()");

       Connection conn = null;
       boolean guidExists = false;

       try{

            //System.out.println("Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            //System.out.println("Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            //System.out.println ("Database connection established");

            PreparedStatement pStat = conn.prepareStatement("SELECT act_name, act_guid FROM Actors where act_guid= ?");
            pStat.setString(1, input_act_guid);
            ResultSet srs = pStat.executeQuery();
            
            while (srs.next()) {
                String act_guid = srs.getString("act_guid");
                if(act_guid.equalsIgnoreCase(input_act_guid)){
                    //System.out.println("Actor with guid = " + input_act_guid + "  already exists");
                    log.debug("DatabaseOperations: checkExistingGuid() - Actor with guid = " + input_act_guid + "  already exists");
                    guidExists = true;
                }
            }
        }
        catch(Exception e){
            //System.err.println ("Cannot connect to database server");
            log.error("DatabaseOperations: checkExistingGuid() - Cannot connect to database server");
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    //System.out.println ("Database connection terminated");
                    log.debug("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }

        return (guidExists);

    }


    private String getSoapAxis2Url(String input_act_guid){

    	if (input_act_guid == null)
    		return null;
    	
        log.debug("Inside DatabaseOperations: getSoapAxis2Url()");

        String resSoapAxis2Url = null;
        Connection conn = null;

        try{

            //System.out.println("Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            //System.out.println("Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            //System.out.println ("Database connection established");

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet srs = null;
            srs = stmt.executeQuery("SELECT * FROM Actors");

            while (srs.next()) {
                String act_guid = srs.getString("act_guid");
                if(act_guid.equalsIgnoreCase(input_act_guid)){
                    resSoapAxis2Url = srs.getString("act_soapaxis2url");
                    //System.out.println("soapaxis2url from db = " + resSoapAxis2Url);
                    log.debug("DatabaseOperations: getSoapAxis2Url() - soapaxis2url from db = " + resSoapAxis2Url);
                }
            }

        }
        catch(Exception e){
            //System.err.println ("Cannot connect to database server");
            log.error("DatabaseOperations: getSoapAxis2Url() - Cannot connect to database server");
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    //System.out.println ("Database connection terminated");
                    log.debug("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }

        return resSoapAxis2Url;
        
    }

    private boolean checkIP(String clientIP, String act_soapaxis2url){

    	if ((clientIP == null) || (act_soapaxis2url == null))
    		return false;
    	
        log.debug("Inside DatabaseOperations: checkIP()");

        String[] splitSoapUrl = act_soapaxis2url.split("//");
        String noHttp = splitSoapUrl[1];
        String[] splitNoHttp = noHttp.split(":");
        String ipSoapUrl = splitNoHttp[0];

        //System.out.println("ip in input soapUrl = " + ipSoapUrl);

        String humanReadableIP = null;
        String numericIP = null;
        try {
            InetAddress address = InetAddress.getByName(ipSoapUrl);
            //System.out.println("humanreadable IP/numeric IP = " + address.toString());
            String[] splitResultGetByName = address.toString().split("/");
            humanReadableIP = splitResultGetByName[0];
            numericIP = splitResultGetByName[1];
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }

        boolean result = false;
        if(clientIP.equalsIgnoreCase(numericIP)){
            result = true;
        }
        else{
            if(ipSoapUrl.equalsIgnoreCase("localhost")){ // Special check: if the soapaxis url is localhost (implying test deployment) insert it into db
                result = true;
            }
            else {
                //System.out.println("Can't verify the identity of the client; client IP doesn't match with IP in SOAP-Axis URL of the Actor; It is also not a test deployment");
                log.debug("Can't verify the identity of the client; client IP doesn't match with IP in SOAP-Axis URL of the Actor; It is also not a test deployment");
                result = false;
            }
        }

        return result;


    }

    /**
     * Update a status of a particular entry ('True' means valid, anything else, invalid)
     * @param input_act_guid - actor guid
     * @param valid - True for Valid, False for Invalid
     */
    public void updateEntryValidStatus(String input_act_guid, boolean valid) {

    	if (input_act_guid == null)
    		return;
    	
        log.info("Setting status of actor " + input_act_guid + " to " + valid);
        Connection conn = null;
        String tableValue = null;
        
        if (valid)
        	tableValue = TRUE_STRING;
        else
        	tableValue = FALSE_STRING;
        
        try{
             //System.out.println("Trying to get a new instance");
             Class.forName ("com.mysql.jdbc.Driver").newInstance ();
             //System.out.println("Trying to get a database connection");
             conn = DriverManager.getConnection (url, userName, password);
             //System.out.println ("Database connection established");

             PreparedStatement pStat = conn.prepareStatement("UPDATE Actors SET act_verified= ? WHERE act_guid = ?");
             pStat.setString(1, tableValue);
             pStat.setString(2, input_act_guid);
             if (pStat.executeUpdate() != 1)
            	 log.error("Unable to update the state of actor " + input_act_guid);
             
         }
         catch(Exception e){
             //System.err.println ("Cannot connect to database server");
             log.error("DatabaseOperations: updateEntryValidStatus() - Cannot connect to database server");

         }
         finally{
             if (conn != null){
                 try{
                     conn.close ();
                     //System.out.println ("Database connection terminated");
                     log.debug("Database connection terminated");
                 }
                 catch (Exception e){ /* ignore close errors */
                 }
             }
         }
    }
   
    /**
     * Delete a row for this actor
     * @param input_act_guid - actor guid
     */
    public void deleteActor(String input_act_guid) {
    	if (input_act_guid == null)
    		return;
    	Connection conn = null;
    	
        try{
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            conn = DriverManager.getConnection (url, userName, password);

            PreparedStatement pStat = conn.prepareStatement("DELETE FROM Actors WHERE act_guid = ? LIMIT 1");
            pStat.setString(1, input_act_guid);
            if (pStat.executeUpdate() != 1)
           	 log.error("Unable to delete entry for actor " + input_act_guid);
            
        }
        catch(Exception e){
            log.error("DatabaseOperations: deleteActor() - Cannot connect to database server");

        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    log.debug("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }
    }
    
    /**
     * check the validity of an entry in a map
     * @param m
     * @return
     */
    protected boolean isValidEntry(Map<String, String> m) {
    	
    	if (m.get(ActorProduction) == null)
    		return false;
    	if (m.get(ActorLastUpdate) == null)
    		return false;
    	
    	String act_production_deployment = m.get(ActorProduction);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
        	Date lastUpdate = sdf.parse(m.get(ActorLastUpdate));
            Calendar cal = Calendar.getInstance();
            Date now = cal.getTime();

            long diff = now.getTime() - lastUpdate.getTime(); // diff is in milliseconds
            long diffInDays = diff / (1000L*60L*60L*24L); // difference in number of days
            long diffInSeconds = diff /(1000L); // difference in number of seconds
            long diffInMinutes = diff / (1000L*60L); // difference in number of minutes
            long diffInHours = diff / (1000L*60L*60L); // differenc in number of hours

            if((diffInMinutes <= 2) && act_production_deployment.equalsIgnoreCase(TRUE_STRING)){ 
            	// the entry is less than 2 minutes old and it is an entry with production deployment (!localhost)
            	return true;
            }	
        } catch (ParseException e) {

        } 
        return false;
    }
    
}
