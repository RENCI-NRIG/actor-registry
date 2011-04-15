<%@ page import="java.io.*, java.net.*, java.util.*, orca.registry.*"  %>
<jsp:useBean id="data" class="orca.registry.ValidateFormData" scope="session"/>
<jsp:setProperty name="data" property="*"/>
<html>
<title>ORCA Actor Registry</title>
<body>
<h2><font face="courier, bookman"><center>ORCA Actor Registry Actor Validation</center></font></h2><br>

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

<%
		String act_guid = data.getGuid();

		DatabaseOperations dbop = new DatabaseOperations();
		Map<String, String> tmpMap = dbop.queryMapForGuid(data.getGuid(), true); 

		if (tmpMap.size() == 0) {
%>
		<h2>Invalid guid <%= act_guid %> </h2>
<%
		} else {

			String act_name = tmpMap.get(DatabaseOperations.ActorName);
			if (act_name == null)
				act_name = "Not specified";
				
			String act_desc = tmpMap.get(DatabaseOperations.ActorDesc);
			if (act_desc == null)
				act_desc = "Not specified";

        	String act_pubkey = tmpMap.get(DatabaseOperations.ActorPubkey);
        	String escaped_act_pubkey = act_pubkey;

        	String act_cert64 = tmpMap.get(DatabaseOperations.ActorCert64);
			String escaped_act_cert64 = act_cert64;
		
%>
<h2>Actor Name: <%= act_name %> </h2>
<h2>Actor Guid: <%= act_guid %> </h2>
<h2>Actor Description: <%= act_desc %> </h2>
<h3>Actor Public Key: </h2>
<pre>
<%= act_pubkey %>
</pre>
<h3>Actor Cert: </h2>
<pre>
<%= act_cert64 %>
</pre>

<form action="validate-action.jsp">
<input type="hidden" name="guid" value="<%= act_guid %>" />
<%
	if ("validate".equals(data.getAction())) {
%>
<h2 style="color:green;">You are about to validate an actor!</h2>
<input type="submit" value="Validate" />
<input type="hidden" name="action" value="validate" />
<%
	} else {
%>
<h2 style="color:red;">Warning! You are about to invalidate an actor! </h2>
<input type="submit" value="Invalidate" />
<input type="hidden" name="action" value="invalidate" />
<%
	}
%>	
<input type="button" name="Cancel" value="Cancel" onclick="window.location='../actors.jsp' "/>
<input type="hidden" name="action" value="<%= data.getAction() %>" />
</form>
<%		
		}
%>
</body>
</html>