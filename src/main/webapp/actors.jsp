<%@ page import="java.io.*, java.net.*, java.util.*, orca.registry.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
<title>ORCA Actor Registry</title>

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

input.redbutton
{
   font-weight:bold;
   color:#000000;
   background-color:#FF0000;
}

input.greenbutton
{
   font-weight:bold;
   color:#000000;
   background-color:#339900;
}

</style>
<body>
<h2><font face="courier, bookman"><center>ORCA Actor Registry</center></font></h2><br>

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
<th align="center" width="70"><b>Admin<br/>Verified</b></th>
<th align="center" width="100"><b>Actor Name</b></th>
<th align="center" width="150"><b><font color="red">Actor GUID</font></b></th>
<th align="center" width="150"><b>Actor Type</b></th>
<th align="center" width="150"><b>Actor Description</b></th>
<th align="center" width="100"><b>Actor SOAPAxis2 URL</b></th>
<th align="center" width="150"><b>Actor Class</b></th>
<th align="center" width="150"><b>Actor Policy</b></th>
<th align="center" width="100"><b>Actor Public Key</b></th>
<th align="center" width="100"><b>Actor Certificate</b></th>
<th align="center" width="100"><b>Abstract Site NDL</b></th>
<th align="center" width="100"><b>Full Site NDL</b></th>
</tr>

<% 
	DatabaseOperations dbop = new DatabaseOperations();
	Map<String, Map<String, String>> res = dbop.queryMap(DatabaseOperations.QUERY_ACTORS, false, false);

    for(Map.Entry<String, Map<String, String>> en: res.entrySet()) {
        Map<String, String> tmpMap = en.getValue();
        
        String act_name = tmpMap.get(DatabaseOperations.ActorName);
        String act_guid = en.getKey();
        String act_type = tmpMap.get(DatabaseOperations.ActorType);
        String act_desc = tmpMap.get(DatabaseOperations.ActorDesc);
        String act_soapaxis2url = tmpMap.get(DatabaseOperations.ActorLocation);
        String act_class = tmpMap.get(DatabaseOperations.ActorClazz);
        String act_mapper_class = tmpMap.get(DatabaseOperations.ActorMapperclass);
        String act_pubkey = tmpMap.get(DatabaseOperations.ActorPubkey);
        String act_verified = tmpMap.get(DatabaseOperations.ActorVerified);

		if (act_pubkey == null)
			continue;
		String escaped_act_pubkey = URLEncoder.encode(act_pubkey);

        String act_cert64 = tmpMap.get(DatabaseOperations.ActorCert64);
			
		String escaped_act_cert64 = URLEncoder.encode(act_cert64);

        String act_abstract_rdf = tmpMap.get(DatabaseOperations.ActorAbstractRDF);
        String act_full_rdf = tmpMap.get(DatabaseOperations.ActorFullRDF);
        String act_allocatable_units = tmpMap.get(DatabaseOperations.ActorAllocunits);


		String abstractRdfFileNameShort = act_guid + ".abstract.rdf";
		String fullRdfFileNameShort = act_guid + ".full.rdf";
		String abstractRdfFileName = "webapps/registry/WEB-INF/" + act_guid + ".abstract.rdf";
		String fullRdfFileName = "webapps/registry/WEB-INF/" + act_guid + ".full.rdf";
		if(act_type.equalsIgnoreCase("site")){
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
        if(act_type.equalsIgnoreCase("sm")){
        	actor_type = "ORCA Service Manager (SM)";                
		}
		if(act_type.equalsIgnoreCase("broker")){
        	actor_type = "ORCA Broker";
        }
        if(act_type.equalsIgnoreCase("site")){
        	actor_type = "ORCA Site Authority / Aggregate Manager (AM)";
		}
	
		//output += "ActorName = " + act_name + " , ActorGUID = " + act_guid + " , ActorType = " + actor_type + " , ActorSOAPAxis2URL = " + act_soapaxis2url + " , ActorClass = " + act_class + " , ActorPolicy = " + act_mapper_class + " , ActorPubkey = " + act_pubkey + " , ActorCert64 = " + act_cert64;

        String act_last_update = tmpMap.get(DatabaseOperations.ActorLastUpdate);
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


        if (diffInHours <= 12){ // entry is less than 12 hours old; showing all actors who have registered in the last 12 hours

            String act_production_deployment = tmpMap.get(DatabaseOperations.ActorProduction);

            if (diffInMinutes <= 2){ // live actors
                if(act_production_deployment.equalsIgnoreCase("False")) { 
                	// Mark this row in red as a test deployment
                	%>
                    <tr BGCOLOR="#FFFF99">
					<td align="center">
					<%
					if ("True".equals(act_verified)) {
					%>
					Yes<br/>
					<%
					} else {
					%>
					No<br/>
					<%
					}
                }
            	else { // production deployment
            		%>
	                <tr BGCOLOR="#D2FFC4">
					<td align="center">
					<%
					if ("True".equals(act_verified)) {
					%>
					Yes<br/>
					<%
					} else {
					%>
					No<br/>
					<%
					}
                }
            }
            else { // Potentially dead actor
            	%>
                <tr BGCOLOR="#FFB5B5">
				<td align="center">
				<%
				if ("True".equals(act_verified)) {
				%>
				Yes<br/>
				<%
				} else {
				%>
				No<br/>
				<%
				}
            }
%>
			<form action="secure/validate.jsp" method="POST" name="<%= act_guid %>">
			<input type="hidden" name="guid" value="<%= act_guid %>" />
			<input type="hidden" name="action" value="manage" />
			<input type="submit" value="Manage" />
			</form>
			</td>
            <td align="center"><%= act_name %></td>
            <td align="center"><%= act_guid %></td>
            <td align="center"><%= actor_type %></td>
            <td align="center"><%= act_desc %></td>
<%
            if(act_soapaxis2url.equalsIgnoreCase("None")){
                    out.println("<td align=\"center\">" + act_soapaxis2url + "</td>");
            }
            else {
                    out.println("<td align=\"center\">" + "<a href=\"" + act_soapaxis2url + "\">Link</a></td>");
            }
            out.println("<td align=\"center\" width=\"150\" style=\"WORD-BREAK:BREAK-ALL;\">" + act_class + "</td>");
            out.println("<td align=\"center\" width=\"150\" style=\"WORD-BREAK:BREAK-ALL;\">" + act_mapper_class + "</td>");
            //out.println("<td align=\"center\">" + "<a href=\"" + keyFileName + "\">Download Public Key</a></td>");
            out.println("<td align=\"center\">" + "<a href=\"http://geni.renci.org:11080/registry?showString=" + escaped_act_pubkey + "\">Click for Public Key</a></td>");
            out.println("<td align=\"center\">" + "<a href=\"http://geni.renci.org:11080/registry?showString=" + escaped_act_cert64 + "\">Click for Actor Certificate</a></td>");

            if(act_type.equalsIgnoreCase("site")){
                    if(act_abstract_rdf == null || act_full_rdf ==null){
                            out.println("<td align=\"center\">" + "Not Available" + "</td>");
                            out.println("<td align=\"center\">" + "Not Available" + "</td>");
                    }
                    else {
                            out.println("<td align=\"center\">" + "<a href=\"http://geni.renci.org:11080/registry?showFile=" + abstractRdfFileNameShort + "\">Click for Abstract Site NDL</a></td>");
                            out.println("<td align=\"center\">" + "<a href=\"http://geni.renci.org:11080/registry?showFile=" + fullRdfFileNameShort + "\">Click for Full Site NDL</a></td>");
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
