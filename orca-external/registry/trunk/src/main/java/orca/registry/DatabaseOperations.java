/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orca.registry;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author anirban
 */
public class DatabaseOperations {

    private String userName = "registry";
    private String password = "registry";
    private String url = "jdbc:mysql://localhost:3306/ActorRegistry";

    public DatabaseOperations() {

    }


    public void connect(){

       Connection conn = null;

       try{
            
            System.out.println("Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            System.out.println("Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            System.out.println ("Database connection established");
        }
        catch(Exception e){
            System.err.println ("Cannot connect to database server");
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    System.out.println ("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }

    }

    public void testQuery(String query){

        Connection conn = null;

        try{
            
            System.out.println("Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            System.out.println("Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            System.out.println ("Database connection established");

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet srs = stmt.executeQuery("SELECT * FROM TestActors");

            while (srs.next()) {
                String act_name = srs.getString("act_name");
                String act_guid = srs.getString("act_guid");
                System.out.println("Actor Name: " + act_name + " | Actor GUID: " + act_guid );
            }

        }
        catch(Exception e){
            System.err.println ("Cannot query the database server");
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    System.out.println ("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }

    }

    // insert version for inserting the actors and their properties
    public void insert(String act_name, String act_type, String act_guid, String act_desc, String act_soapaxis2url, String act_class, String act_mapper_class, String act_pubkey, String act_cert64){


        Connection conn = null;

        try{
            
            
            // Query the Actors table to find out if act_guid already present
            // If act_guid already present, check if the ip address of the client 
            // matches the IP address returned by InetAddress.getByName(act_soapaxis2url - the extracted portion of soapaxis2url)
            // If it matches, execute an 'Update' command for that row, OR, delete that row and insert this new row
            // Set new timestamp for that row
            
            String clientIP = RegistryServlet.getClientIpAddress();
            System.out.println("clientIP = " + clientIP);

            if(clientIP == null){
                System.out.println("Can't get IP address of client; Insert failed");
                return;
            }

            String[] splitSoapUrl = act_soapaxis2url.split("//");
            String noHttp = splitSoapUrl[1];
            String[] splitNoHttp = noHttp.split(":");
            String ipSoapUrl = splitNoHttp[0];

            System.out.println("ipSoapUrl = " + ipSoapUrl);

            String humanReadableIP = null;
            String numericIP = null;
            try {
                InetAddress address = InetAddress.getByName(ipSoapUrl);
                System.out.println("humanreadable IP/numeric IP = " + address.toString());
                String[] splitResultGetByName = address.toString().split("/");
                humanReadableIP = splitResultGetByName[0];
                numericIP = splitResultGetByName[1];
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }

            boolean insertEntry = false;
            String act_production_deployment = "False";
            if(clientIP.equalsIgnoreCase(numericIP)){
                insertEntry = true;
                act_production_deployment = "True";
            }
            else{
                if(ipSoapUrl.equalsIgnoreCase("localhost")){ // Special check: if the soapaxis url is localhost (implying test deployment) set production deployment as false
                    insertEntry = true;
                    act_production_deployment = "False";
                }
                else {
                    System.out.println("Can't verify the identity of the client; client IP doesn't match with IP in SOAP-Axis URL of the Actor; It is also not a test deployment. INSERT Failed !!!");
                    return;
                }
            }

            boolean actorExists = checkExistingGuid(act_guid);

            System.out.println("Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            System.out.println("Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            System.out.println ("Database connection established");

            Statement stmt = conn.createStatement();

            if(insertEntry){ // valid client trying to insert new entry or trying to update an existing entry

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String act_last_update = sdf.format(cal.getTime());

                int res;
                if(!actorExists){ // New actor
                    res = stmt.executeUpdate("INSERT into `Actors` ( `act_name` , `act_guid` , `act_type`, `act_desc`, `act_soapaxis2url`, `act_class`, `act_mapper_class`, `act_pubkey`, `act_cert64`, `act_production_deployment`, `act_last_update`) values " +
                                             "('" + act_name + "', '" + act_guid + "', '" + act_type + "' , '" + act_desc + "', '" + act_soapaxis2url + "', '" + act_class + "', '" + act_mapper_class + "', '" + act_pubkey + "', '" + act_cert64 + "', '" + act_production_deployment + "', '" + act_last_update +  "')");
                }
                else{ // Existing actor
                    // Delete the current row and add the new row
                    res = stmt.executeUpdate("DELETE from `Actors` where `act_guid` = '" + act_guid + "'");
                    res = stmt.executeUpdate("INSERT into `Actors` ( `act_name` , `act_guid` , `act_type`, `act_desc`, `act_soapaxis2url`, `act_class`, `act_mapper_class`, `act_pubkey`, `act_cert64`, `act_production_deployment`, `act_last_update`) values " +
                                             "('" + act_name + "', '" + act_guid + "', '" + act_type + "' , '" + act_desc + "', '" + act_soapaxis2url + "', '" + act_class + "', '" + act_mapper_class + "', '" + act_pubkey + "', '" + act_cert64 + "', '" + act_production_deployment + "', '" + act_last_update +  "')");
                }

            }
        }
        catch(Exception e){
            System.err.println ("Error inserting into Actors table");
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    System.out.println ("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }

    }

    // insert version for inserting abstract rdf, full rdf and allocatable units for existing actors
    public void insert(String act_guid, String act_abstract_rdf, String act_full_rdf, String act_allocatable_units){

        Connection conn = null;

        try{

            String clientIP = RegistryServlet.getClientIpAddress();
            System.out.println("clientIP = " + clientIP);

            if(clientIP == null){
                System.out.println("Can't get IP address of client; Insert failed");
                return;
            }

            String act_soapaxis2url = getSoapAxis2Url(act_guid);

            /*
            String[] splitSoapUrl = act_soapaxis2url.split("//");
            String noHttp = splitSoapUrl[1];
            String[] splitNoHttp = noHttp.split(":");
            String ipSoapUrl = splitNoHttp[0];

            System.out.println("ip in input soapUrl = " + ipSoapUrl);

            String humanReadableIP = null;
            String numericIP = null;
            try {
                InetAddress address = InetAddress.getByName(ipSoapUrl);
                System.out.println("humanreadable IP/numeric IP = " + address.toString());
                String[] splitResultGetByName = address.toString().split("/");
                humanReadableIP = splitResultGetByName[0];
                numericIP = splitResultGetByName[1];
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }

            boolean insertEntry = false;
            if(clientIP.equalsIgnoreCase(numericIP)){
                insertEntry = true;
            }
            else{
                if(ipSoapUrl.equalsIgnoreCase("localhost")){ // Special check: if the soapaxis url is localhost (implying test deployment) insert it into db
                    insertEntry = true;
                }
                else {
                    System.out.println("Can't verify the identity of the client; client IP doesn't match with IP in SOAP-Axis URL of the Actor; It is also not a test deployment");
                    return;
                }
            }
            */

            boolean insertEntry = checkIP(clientIP, act_soapaxis2url);

            System.out.println("Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            System.out.println("Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            System.out.println ("Database connection established");

            Statement stmt = conn.createStatement();

            if(insertEntry){ // valid client trying to update an existing entry with rdfs

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String act_last_update = sdf.format(cal.getTime());

                int res = stmt.executeUpdate("UPDATE `Actors` set act_abstract_rdf='" + act_abstract_rdf + "' where act_guid='" + act_guid +"'");
                res = stmt.executeUpdate("UPDATE `Actors` set act_full_rdf='" + act_full_rdf + "' where act_guid='" + act_guid +"'");
                res = stmt.executeUpdate("UPDATE `Actors` set act_allocatable_units='" + act_allocatable_units + "' where act_guid='" + act_guid +"'");
                res = stmt.executeUpdate("UPDATE `Actors` set act_last_update='" + act_last_update + "' where act_guid='" + act_guid +"'");
            }
            
        }
        catch(Exception e){
            System.err.println ("Error inserting Ndl into Actors table");
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    System.out.println ("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }

    }

    // insert version for inserting abstract rdf, full rdf for existing actors
    public void insert(String act_guid, String act_abstract_rdf, String act_full_rdf){

        Connection conn = null;

        try{
            String clientIP = RegistryServlet.getClientIpAddress();
            System.out.println("clientIP = " + clientIP);

            if(clientIP == null){
                System.out.println("Can't get IP address of client; Insert failed");
                return;
            }

            String act_soapaxis2url = getSoapAxis2Url(act_guid);
            if(act_soapaxis2url == null){
                System.out.println("Actor with guid: " + act_guid + " doesn't have a soapaxis2url");
                return;
            }

            boolean insertEntry = checkIP(clientIP, act_soapaxis2url);

            System.out.println("Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            System.out.println("Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            System.out.println ("Database connection established");

            Statement stmt = conn.createStatement();

            if(insertEntry){ // valid client trying to update an existing entry with rdfs

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String act_last_update = sdf.format(cal.getTime());

                int res = stmt.executeUpdate("UPDATE `Actors` set act_abstract_rdf='" + act_abstract_rdf + "' where act_guid='" + act_guid +"'");
                res = stmt.executeUpdate("UPDATE `Actors` set act_full_rdf='" + act_full_rdf + "' where act_guid='" + act_guid +"'");
                res = stmt.executeUpdate("UPDATE `Actors` set act_last_update='" + act_last_update + "' where act_guid='" + act_guid +"'");
                
            }

        }
        catch(Exception e){
            System.err.println ("Error inserting Ndl into Actors table");
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    System.out.println ("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }

    }


    // insert version for heartbeats; The method name is confusing; the semantic is to insert the most recent last update date for the actor
    public void insert(String act_guid){

        Connection conn = null;

        try{
            String clientIP = RegistryServlet.getClientIpAddress();
            //System.out.println("clientIP = " + clientIP);

            if(clientIP == null){
                System.out.println("Can't get IP address of client; Insert failed");
                return;
            }

            String act_soapaxis2url = getSoapAxis2Url(act_guid);
            if(act_soapaxis2url == null){
                System.out.println("Actor with guid: " + act_guid + " doesn't have a soapaxis2url");
                return;
            }

            boolean insertEntry = checkIP(clientIP, act_soapaxis2url);

            //System.out.println("Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            //System.out.println("Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            //System.out.println ("Database connection established");

            Statement stmt = conn.createStatement();

            if(insertEntry){ // valid client trying to update an existing entry with rdfs

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String act_last_update = sdf.format(cal.getTime());

                int res = stmt.executeUpdate("UPDATE `Actors` set act_last_update='" + act_last_update + "' where act_guid='" + act_guid +"'");

            }

        }
        catch(Exception e){
            System.err.println ("Error inserting heartbeats");
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    System.out.println ("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }

    }


    public String query(String actorType){

        String result = null;
        Connection conn = null;

        try{
            
            System.out.println("Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            System.out.println("Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            System.out.println ("Database connection established");

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet srs = null;
            if(actorType.equalsIgnoreCase("actors")){
                srs = stmt.executeQuery("SELECT * FROM Actors");
            }
            else if(actorType.equalsIgnoreCase("sm")){
                srs = stmt.executeQuery("SELECT * FROM Actors where act_type=1");
            }
            else if(actorType.equalsIgnoreCase("brokers")){
                srs = stmt.executeQuery("SELECT * FROM Actors where act_type=2");
            }
            else if(actorType.equalsIgnoreCase("am")){
                srs = stmt.executeQuery("SELECT * FROM Actors where act_type=3");
            }
            else{
                result = "Unknown actor Type";
            }

            int flag = 0;
            String output = "";
            while (srs.next()) {
                String act_name = srs.getString("act_name");
                String act_guid = srs.getString("act_guid");
                String act_type = srs.getString("act_type");
                String act_soapaxis2url = srs.getString("act_soapaxis2url");
                String act_class = srs.getString("act_class");
                String act_mapper_class = srs.getString("act_mapper_class");
                String act_pubkey = srs.getString("act_pubkey");
                String act_cert64 = srs.getString("act_cert64");
		
                String act_abstract_rdf = srs.getString("act_abstract_rdf");
                String act_full_rdf = srs.getString("act_full_rdf");
                String act_allocatable_units = srs.getString("act_allocatable_units");

                String actor_type = null;
                if(act_type.equalsIgnoreCase("1")){
                    actor_type = "ORCA Service Manager (SM)";
                }
                if(act_type.equalsIgnoreCase("2")){
                    actor_type = "ORCA Broker";
                }
                if(act_type.equalsIgnoreCase("3")){
                    actor_type = "ORCA Site Authority / Aggregate Manager (AM)";
                }

                String act_last_update = srs.getString("act_last_update");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date lastUpdate = sdf.parse(act_last_update);
                Calendar cal = Calendar.getInstance();
                Date now = cal.getTime();

                long diff = now.getTime() - lastUpdate.getTime(); // diff is in milliseconds
                long diffInDays = diff / (1000L*60L*60L*24L); // difference in number of days
                long diffInSeconds = diff /(1000L); // difference in number of seconds
                long diffInMinutes = diff / (1000L*60L); // difference in number of minutes
                long diffInHours = diff / (1000L*60L*60L); // differenc in number of hours
                System.out.println("The entry is " + diff + " milli secs / " + diffInMinutes + " minutes old");

                String act_production_deployment = srs.getString("act_production_deployment");

                if((diffInMinutes <= 2) && act_production_deployment.equalsIgnoreCase("True")){ // the entry is less than 2 minutes old and it is an entry with production deployment (!localhost)

                    flag = 1; // result will have at least one entry

                    output += "ActorName = " + act_name + " , ActorGUID = " + act_guid + " , ActorType = " + actor_type + " , ActorSOAPAxis2URL = " + act_soapaxis2url + " , ActorClass = " + act_class + " , ActorPolicy = " + act_mapper_class + " , ActorPubkey = " + act_pubkey + " , ActorCert64 = " + act_cert64;

                    if(act_type.equalsIgnoreCase("3")){
                        if(act_abstract_rdf != null && act_full_rdf != null && act_allocatable_units != null){
                            output += " , ActorSiteAbstractRDF = " + act_abstract_rdf + " , ActorSiteFullRDF = " + act_full_rdf + " , ActorAllocatableUnits = " + act_allocatable_units;
                        }
                        else if(act_abstract_rdf != null && act_full_rdf != null){
                            output += " , ActorSiteAbstractRDF = " + act_abstract_rdf + " , ActorSiteFullRDF = " + act_full_rdf;
                        }
                        else{
                            output += " , ActorSiteAbstractRDF = " + "UNKNOWN" + " , ActorAllocatableUnits = " + "UNKNOWN";
                        }
                    }

                    output += " \n" ;

                    System.out.println("Actor Name: " + act_name + " | Actor GUID: " + act_guid );
                }


            }
            if(flag == 0){
                result = "Query did not match any actor in the registry";
            }
            else if(flag == 1){
                result = output;
            }

        }
        catch(Exception e){
            System.err.println ("Cannot query the database server");
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    System.out.println ("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }

        return result;

    }

    private boolean checkExistingGuid(String input_act_guid){

       Connection conn = null;
       boolean guidExists = false;

       try{

            System.out.println("Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            System.out.println("Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            System.out.println ("Database connection established");

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet srs = null;
            srs = stmt.executeQuery("SELECT * FROM Actors");

            if(srs == null){
                System.out.println("Actors table is empty");
                return (false);
            }

            while (srs.next()) {
                String act_guid = srs.getString("act_guid");
                if(act_guid.equalsIgnoreCase(input_act_guid)){
                    System.out.println("Actor with guid = " + input_act_guid + "  already exists");
                    guidExists = true;
                }
            }


        }
        catch(Exception e){
            System.err.println ("Cannot connect to database server");
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    System.out.println ("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }

        return (guidExists);

    }


    private String getSoapAxis2Url(String input_act_guid){

        String resSoapAxis2Url = null;
        Connection conn = null;

        try{

            System.out.println("Trying to get a new instance");
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            System.out.println("Trying to get a database connection");
            conn = DriverManager.getConnection (url, userName, password);
            System.out.println ("Database connection established");

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet srs = null;
            srs = stmt.executeQuery("SELECT * FROM Actors");

            if(srs == null){
                System.out.println("Actors table is empty");
                return null;
            }

            while (srs.next()) {
                String act_guid = srs.getString("act_guid");
                if(act_guid.equalsIgnoreCase(input_act_guid)){
                    resSoapAxis2Url = srs.getString("act_soapaxis2url");
                    System.out.println("soapaxis2url from db = " + resSoapAxis2Url);
                }
            }

        }
        catch(Exception e){
            System.err.println ("Cannot connect to database server");
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                    System.out.println ("Database connection terminated");
                }
                catch (Exception e){ /* ignore close errors */
                }
            }
        }

        return resSoapAxis2Url;
        
    }

    private boolean checkIP(String clientIP, String act_soapaxis2url){

        String[] splitSoapUrl = act_soapaxis2url.split("//");
        String noHttp = splitSoapUrl[1];
        String[] splitNoHttp = noHttp.split(":");
        String ipSoapUrl = splitNoHttp[0];

        System.out.println("ip in input soapUrl = " + ipSoapUrl);

        String humanReadableIP = null;
        String numericIP = null;
        try {
            InetAddress address = InetAddress.getByName(ipSoapUrl);
            System.out.println("humanreadable IP/numeric IP = " + address.toString());
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
                System.out.println("Can't verify the identity of the client; client IP doesn't match with IP in SOAP-Axis URL of the Actor; It is also not a test deployment");
                result = false;
            }
        }

        return result;


    }




}
