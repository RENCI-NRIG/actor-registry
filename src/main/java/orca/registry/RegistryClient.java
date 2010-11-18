/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orca.registry;

import java.util.Vector;
import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.client.util.ClientFactory;

/**
 *
 * @author anirban
 */
public class RegistryClient {

    public static void main(String[] args) {

        String serverURL = "http://152.54.9.209:11080/registry/";

        try{
            String result;
            System.out.println("Starting xml-rpc client");

	    // create configuration
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(serverURL));
            config.setEnabledForExtensions(true);  
            config.setConnectionTimeout(60 * 1000);
            config.setReplyTimeout(60 * 1000);

            XmlRpcClient client = new XmlRpcClient();
        
            // use Commons HttpClient as transport
            client.setTransportFactory(
                new XmlRpcCommonsTransportFactory(client));
            // set configuration
            client.setConfig(config);

            Vector params = new Vector();


            params.addElement(new String("duke-vm-site-Updated"));
            params.addElement(new String("3"));
            params.addElement(new String("actorguidXY59"));
            params.addElement(new String("VM Site Authority at Duke"));
            params.addElement(new String("http://152.54.9.209:11080/orca/services/duke-vm-site"));
            params.addElement(new String("core.shirako.Blah"));
            params.addElement(new String("core.shirako.policy.blah"));
            params.addElement(new String("actorpubkey_1"));
            params.addElement(new String("actorcert64_1"));
            //params.addElement(new String("siteAbstractRdf"));
            //params.addElement(new String("siteAbstractRdf"));
            //params.addElement(new String("vm=10,vlan=100"));
            result = (String) client.execute("registryService.insert", params);
            System.out.println(result);

            params.clear();
            params.addElement(new String("actorguidXY59"));
            params.addElement(new String("Site abstract rdf NEW Duke"));
            params.addElement(new String("Site full rdf NEW Duke"));
            //params.addElement(new String("{vm=10 ; vlan=100}"));
            result = (String) client.execute("registryService.insert", params);
            System.out.println(result);
            
            params.clear();
            params.addElement(new String("unc-vm-site-Updated"));
            params.addElement(new String("3"));
            params.addElement(new String("actorguidAB123"));
            params.addElement(new String("VM Site Authority at UNC"));
            params.addElement(new String("http://152.54.9.209:11080/orca/services/unc-vm-site"));
            params.addElement(new String("core.shirako.Blah"));
            params.addElement(new String("core.shirako.policy.blah"));
            params.addElement(new String("actorpubkey_2"));
            params.addElement(new String("actorcert64_2"));
            //params.addElement(new String("siteAbstractRdf"));
            //params.addElement(new String("siteAbstractRdf"));
            //params.addElement(new String("vm=10,vlan=100"));
            result = (String) client.execute("registryService.insert", params);
            System.out.println(result);

            params.clear();
            params.addElement(new String("actorguidAB123"));
            params.addElement(new String("Site abstract rdf NEW UNC"));
            params.addElement(new String("Site full rdf NEW UNC"));
            //params.addElement(new String("{vm=10 ; vlan=100}"));
            result = (String) client.execute("registryService.insert", params);
            System.out.println(result);


            params.clear();
            params.addElement(new String("site"));
            params.addElement(new String("3"));
            params.addElement(new String("actorguidMN456"));
            params.addElement(new String("demo site authority"));
            params.addElement(new String("http://localhost:11080/orca/services/site"));
            params.addElement(new String("core.shirako.Blah"));
            params.addElement(new String("core.shirako.policy.blah"));
            params.addElement(new String("actorpubkey_3"));
            params.addElement(new String("actorcert64_3"));
            //params.addElement(new String("siteAbstractRdf"));
            //params.addElement(new String("siteAbstractRdf"));
            //params.addElement(new String("vm=10,vlan=100"));
            result = (String) client.execute("registryService.insert", params);
            System.out.println(result);

            params.clear();
            params.addElement(new String("actorguidMN456"));
            params.addElement(new String("Site abstract rdf NEW localhost"));
            params.addElement(new String("Site full rdf NEW localhost"));
            //params.addElement(new String("{vm=10 ; vlan=100}"));
            result = (String) client.execute("registryService.insert", params);
            System.out.println(result);

            params.clear();
            params.addElement(new String("unc-vm-site-Updated2"));
            params.addElement(new String("3"));
            params.addElement(new String("actorguidAB123"));
            params.addElement(new String("VM Site Authority at UNC"));
            params.addElement(new String("http://152.54.9.88:11080/orca/services/unc-vm-site"));
            params.addElement(new String("core.shirako.Blah"));
            params.addElement(new String("core.shirako.policy.blah"));
            params.addElement(new String("actorpubkey_2"));
            params.addElement(new String("actorcert64_2"));
            //params.addElement(new String("siteAbstractRdf"));
            //params.addElement(new String("siteAbstractRdf"));
            //params.addElement(new String("vm=10,vlan=100"));
            result = (String) client.execute("registryService.insert", params);
            System.out.println(result);


            params.clear();
            if(params == null) System.out.println("Null params for xml-rpc call");
            result = (String) client.execute("registryService.getActors", params);
            System.out.println(result);


/*
            params.clear();
            result = (String) client.execute("registryService.getBrokers", params);
            System.out.println(result);

            params.clear();
            result = (String) client.execute("registryService.getAMs", params);
            System.out.println(result);

            params.clear();
            result = (String) client.execute("registryService.getSMs", params);
            System.out.println(result);
*/
/*
	    params.clear();
	    result = (String)client.execute("registryService.getRegistryVersion", params);
	    System.out.println("Received version " + result);

            params.clear();
            params.addElement(new String("This is the other method signature"));
            result = (String)client.execute("registryService.getRegistryVersion", params);
	    System.out.println("Received version " + result);
*/

        } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error in XmlrpcClient");

        }


    }

}
