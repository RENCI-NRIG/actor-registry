<%@ page import="java.io.*"  %>
<html>
<title>ORCA Actor Registry</title>
<body>
<h2><font face="courier, bookman"><center>ORCA Actor Registry</center></font></h2><br>

<style type="text/css">
table.pretty {
margin: 1em 1em 1em 2em;
background: whitesmoke;
font-family: courier, bookman;
font-weight: normal;
font-size: 15px;
border-collapse: collapse;
}
table.pretty th, table.pretty td {
border: 1px silver solid;
padding: 0.2em;
}
table.pretty th {
background: gainsboro;
font-family: Verdana;
text-align: center;
}
table.pretty caption {
margin-left: inherit;
margin-right: inherit;
}
</style>

<center>
<table class="pretty">
<tr BGCOLOR="#D2FFC4">
<td align="center">Actor in production mode; Other actors can connect to this actor</td>
</tr>
<tr BGCOLOR="#FFFF99">
<td align="center">Actor in test mode, using localhost; Other actors can't connect to this actor</td>
</tr>
<tr BGCOLOR="#FFB5B5">
<td align="center">Actor not live; Other actors can't connect to this actor</td>
</tr>
</table>
</center>


<table class="pretty">
<tr>
<th align="center"><b>Actor Name</b></th>
<th align="center"><b><font color="red">Actor GUID</font></b></th>
<th align="center"><b>Actor Type</b></th>
<th align="center"><b>Actor Description</b></th>
<th align="center"><b>Actor SOAPAxis2 URL</b></th>
<th align="center"><b>Actor Class</b></th>
<th align="center"><b>Actor Policy</b></th>
<th align="center"><b>Actor Public Key</b></th>
<th align="center"><b>Actor Certificate</b></th>
<th align="center"><b>Abstract Site NDL</b></th>
<th align="center"><b>Full Site NDL</b></th>
</tr>

<% 
//Load the MySql Driver. 
Class.forName("org.gjt.mm.mysql.Driver"); 

//Make the Connection to the database. 

//Replace IP, Name, Username, Password to match your information. 
java.sql.Connection connection=java.sql.DriverManager.getConnection("jdbc:mysql://localhost:3306/ActorRegistry","registry", "registry"); 

//Get SQL Statement. 
//Replace TableName to your table's name. 
java.sql.ResultSet srs=connection.createStatement().executeQuery("select * from Actors"); 

//Process results. 
//Make sure your table has at least 1 column. out.println("col count is "+rs.getMetaData().getColumnCount()); 

//Print the Results. 
//Just get the first column's name of the table 
//String colName=srs.getMetaData().getColumnLabel(1); 
//out.println("col label is "+colName); 

//For each row, get the first column and print it out 
//while(srs.next()){ 
//Object o=srs.getObject(colName); 
//out.println(colName+" "+o); 
//} 

	//int flag = 0;
        //String output = "";
        while (srs.next()) {
                //flag = 1;
                String act_name = srs.getString("act_name");
                String act_guid = srs.getString("act_guid");
                String act_type = srs.getString("act_type");
                String act_desc = srs.getString("act_desc");
                String act_soapaxis2url = srs.getString("act_soapaxis2url");
                String act_class = srs.getString("act_class");
                String act_mapper_class = srs.getString("act_mapper_class");
                String act_pubkey = srs.getString("act_pubkey");
                String act_cert64 = srs.getString("act_cert64");

                String act_abstract_rdf = srs.getString("act_abstract_rdf");
                String act_full_rdf = srs.getString("act_full_rdf");
                String act_allocatable_units = srs.getString("act_allocatable_units");

		
		//String keyFileName = act_guid + ".pubkey";
		//try {   
    		//	PrintWriter pw = new PrintWriter(new FileOutputStream(keyFileName));
    		//	pw.println(act_pubkey);
    		//	pw.close();
		//} catch(IOException e) {
   		//	out.println(e.getMessage());
		//}

		String abstractRdfFileNameShort = act_guid + ".abstract.rdf";
		String fullRdfFileNameShort = act_guid + ".full.rdf";
		String abstractRdfFileName = "webapps/registry/WEB-INF/" + act_guid + ".abstract.rdf";
		String fullRdfFileName = "webapps/registry/WEB-INF/" + act_guid + ".full.rdf";
		if(act_type.equalsIgnoreCase("3")){
			try {   
    				PrintWriter pw1 = new PrintWriter(new FileOutputStream(abstractRdfFileName));
    				pw1.println(act_abstract_rdf);
    				pw1.close();
    				PrintWriter pw2 = new PrintWriter(new FileOutputStream(fullRdfFileName));
    				pw2.println(act_full_rdf);
    				pw2.close();
			} catch(IOException e) {
   				out.println(e.getMessage());
			}
		}

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
	
		//output += "ActorName = " + act_name + " , ActorGUID = " + act_guid + " , ActorType = " + actor_type + " , ActorSOAPAxis2URL = " + act_soapaxis2url + " , ActorClass = " + act_class + " , ActorPolicy = " + act_mapper_class + " , ActorPubkey = " + act_pubkey + " , ActorCert64 = " + act_cert64;

                String act_last_update = srs.getString("act_last_update");
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date lastUpdate = sdf.parse(act_last_update);
                java.util.Calendar cal = java.util.Calendar.getInstance();
                java.util.Date now = cal.getTime();

                long diff = now.getTime() - lastUpdate.getTime(); // diff is in milliseconds
                long diffInDays = diff / (1000L*60L*60L*24L); // difference in number of days
                long diffInSeconds = diff /(1000L); // difference in number of seconds
                long diffInMinutes = diff / (1000L*60L); // difference in number of minutes
                long diffInHours = diff / (1000L*60L*60L); // differenc in number of hours

                //System.out.println("The entry is " + diff + " milli secs / " + diffInDays + " days old");


                if(diffInHours <= 12){ // entry is less than 12 hours old; showing all actors who have registered in the last 12 hours

                    String act_production_deployment = srs.getString("act_production_deployment");

                    if(diffInMinutes <= 2){ // live actors
                        if(act_production_deployment.equalsIgnoreCase("False")){ // Mark this row in red as a test deployment
                            out.println("<tr BGCOLOR=\"#FFFF99\">");
                        }
                        else{ // production deployment
                            out.println("<tr BGCOLOR=\"#D2FFC4\">");
                        }
                    }
                    else { // Potentially dead actor
                        out.println("<tr BGCOLOR=\"#FFB5B5\">");
                    }

                    out.println("<td align=\"center\">" + act_name + "</td>");
                    out.println("<td align=\"center\">" + act_guid + "</td>");
                    out.println("<td align=\"center\">" + actor_type + "</td>");
                    out.println("<td align=\"center\">" + act_desc + "</td>");
                    if(act_soapaxis2url.equalsIgnoreCase("None")){
                            out.println("<td align=\"center\">" + act_soapaxis2url + "</td>");
                    }
                    else {
                            out.println("<td align=\"center\">" + "<a href=\"" + act_soapaxis2url + "\">Link</a></td>");
                    }
                    out.println("<td align=\"center\">" + act_class + "</td>");
                    out.println("<td align=\"center\">" + act_mapper_class + "</td>");
                    //out.println("<td align=\"center\">" + "<a href=\"" + keyFileName + "\">Download Public Key</a></td>");
                    out.println("<td align=\"center\">" + "<a href=\"http://geni-test.renci.org:11080/registry?showString=" + act_pubkey + "\">Click for Public Key</a></td>");
                    out.println("<td align=\"center\">" + "<a href=\"http://geni-test.renci.org:11080/registry?showString=" + act_cert64 + "\">Click for Actor Certificate</a></td>");

                    if(act_type.equalsIgnoreCase("3")){
                            if(act_abstract_rdf == null || act_full_rdf ==null){
                                    out.println("<td align=\"center\">" + "Not Available" + "</td>");
                                    out.println("<td align=\"center\">" + "Not Available" + "</td>");
                            }
                            else {
                                    out.println("<td align=\"center\">" + "<a href=\"http://geni-test.renci.org:11080/registry?showFile=" + abstractRdfFileNameShort + "\">Click for Abstract Site NDL</a></td>");
                                    out.println("<td align=\"center\">" + "<a href=\"http://geni-test.renci.org:11080/registry?showFile=" + fullRdfFileNameShort + "\">Click for Full Site NDL</a></td>");
                            }
                    }
                    else {
                            out.println("<td align=\"center\">" + "N/A" + "</td>");
                            out.println("<td align=\"center\">" + "N/A" + "</td>");
                    }

                    out.println("</tr>");

                    //out.println(output);
                }
	}
%>

</table>
</body>
</html>
