/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orca.registry;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author anirban
 */
public class XmlrpcHandler {

    public static final String registryLogProperties="orca.registry.registry";
    Logger log;

    public XmlrpcHandler() {

        ClassLoader loader = this.getClass().getClassLoader();
        Properties p = PropertyLoader.loadProperties(registryLogProperties, loader);
        PropertyConfigurator.configure(p);
        log = Logger.getLogger(XmlrpcHandler.class);
        log.setLevel(Level.ALL);
        log.info("Starting logging for Registry XmlrpcHandler");

    }

    public String connect(){
        String result = "done connecting";
        //System.out.println("Inside connect");
        log.info("Inside XmlrpcHandler: connect()");
        DatabaseOperations dbop = new DatabaseOperations();
        dbop.connect();
        return result;
    }

    // insert version for inserting the actors and their properties
    public String insert(String act_name, String act_type, String act_guid, String act_desc, String act_soapaxis2url, String act_class, String act_mapper_class, String act_pubkey, String act_cert64){

        log.info("Inside XmlrpcHandler: insert() - insert actors and properties");
        //System.out.println("Inside insert");
        //System.out.println(act_name + " " + act_type + " " + act_guid + " " + act_desc);
        log.debug("Inserting: " + act_name + " " + act_type + " " + act_guid + " " + act_desc);

        DatabaseOperations dbop = new DatabaseOperations();
        dbop.insert(act_name, act_type, act_guid, act_desc, act_soapaxis2url, act_class, act_mapper_class, act_pubkey, act_cert64);
        String result = "done inserting";
        return result;

    }

    // insert version for inserting abstract rdf, full rdf and allocatable units for existing actors
    public String insert(String act_guid, String act_abstract_rdf, String act_full_rdf, String act_allocatable_units){

        log.info("Inside XmlrpcHandler: insert() - insert abstract rdf, full rdf, allocatable units");
        //System.out.println("Inside insert Ndl with allocatable units");
        //System.out.println("act_guid:" + act_guid + " act_abstract_rdf:" + act_abstract_rdf + " act_full_rdf:" + act_full_rdf + " act_allocatable_units:" + act_allocatable_units);
        log.debug("Inserting: " + "act_guid:" + act_guid + " act_abstract_rdf:" + act_abstract_rdf + " act_full_rdf:" + act_full_rdf + " act_allocatable_units:" + act_allocatable_units);

        DatabaseOperations dbop = new DatabaseOperations();
        dbop.insert(act_guid, act_abstract_rdf, act_full_rdf, act_allocatable_units);
        String result = "done inserting Ndl with allocatable units";
        return result;

    }

    // insert version for inserting abstract rdf, full rdf for existing actors
    public String insert(String act_guid, String act_abstract_rdf, String act_full_rdf){

        log.info("Inside XmlrpcHandler: insert() - insert abstract rdf, full rdf");
        //System.out.println("Inside insert Ndl");
        //System.out.println("act_guid:" + act_guid + " act_abstract_rdf:" + act_abstract_rdf + " act_full_rdf:" + act_full_rdf);
        log.debug("Inserting: " + "act_guid:" + act_guid + " act_abstract_rdf:" + act_abstract_rdf + " act_full_rdf:" + act_full_rdf);

        DatabaseOperations dbop = new DatabaseOperations();
        dbop.insert(act_guid, act_abstract_rdf, act_full_rdf);
        String result = "done inserting Ndl";
        return result;

    }

    // insert version to insert periodic heartbeats, which change the last update date for an actor
    public String insert(String act_guid){

        //System.out.println("Inside insert Heartbeats");
        //System.out.println("act_guid:" + act_guid);

        log.info("Inside XmlrpdHandler: insert() - insert heartbeats");
        log.debug("Inserting: heartbeats for act_guid: " + act_guid);
        DatabaseOperations dbop = new DatabaseOperations();
        dbop.insert(act_guid);

        String result = "done inserting heartbeat";
        return result;
        
    }

    public String getActors(){

        log.info("Inside XmlrpdHandler: getActors()");
        //System.out.println("Inside getActors()");
        String result = null;

        DatabaseOperations dbop = new DatabaseOperations();
        result = dbop.query("actors");

        return result;

    }

    public String getBrokers(){

        //System.out.println("Inside getBrokers()");
        log.info("Inside XmlrpdHandler: getBrokers()");
        String result = null;

        DatabaseOperations dbop = new DatabaseOperations();
        result = dbop.query("brokers");

        return result;

    }

    public String getSMs(){

        //System.out.println("Inside getSMs()");
        log.info("Inside XmlrpdHandler: getSMs()");
        String result = null;

        DatabaseOperations dbop = new DatabaseOperations();
        result = dbop.query("sm");

        return result;

    }

    public String getAMs(){

        //System.out.println("Inside getAMs()");
        log.info("Inside XmlrpdHandler: getAMs()");
        String result = null;

        DatabaseOperations dbop = new DatabaseOperations();
        result = dbop.query("am");

        return result;

    }

    public String getRegistryVersion() {
        /*
        String clientIP = RegistryServlet.getClientIpAddress();
        try {
            InetAddress address = InetAddress.getByName("www.renci.org");
            System.out.println(address.toString());
            
            InetAddress address1 = InetAddress.getByName("geni-test.renci.org");
            System.out.println(address1.toString());
            InetAddress address2 = InetAddress.getByName("geni-ben.renci.org");
            System.out.println(address2.toString());            
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        */
	return "ORCA Actor Registry version 1.0 ";
    }

    public String getRegistryVersion(String v){
        return "hello: " + v;
    }


}
