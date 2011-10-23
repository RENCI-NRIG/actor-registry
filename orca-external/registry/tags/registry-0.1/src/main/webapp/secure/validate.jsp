<%@ page import="java.io.*, java.net.*, java.util.*, java.security.cert.*, org.apache.ws.commons.util.*, java.security.*, java.lang.*, orca.registry.*"  %>
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

<script type="text/javascript">
function submitForm(actionValue) {

	document.getElementById('action').value=actionValue;
	document.actionform.submit();
}

function showWarning(html) {

	document.getElementById('warning').innerHTML=html;
}

</script>

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
			
			//CertificateFactory cf = CertificateFactory.getInstance("X.509");
 			//X509Certificate cert = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(Base64.decode(act_cert64)));
 			MessageDigest md = MessageDigest.getInstance("MD5");
 			//byte[] certHash = md.digest(cert.getEncoded());
 			byte[] certHash = md.digest(Base64.decode(act_cert64));
 			
 			String hashString = "";
 			for (int i=0; i<certHash.length; i++)
 				hashString += Integer.toHexString(certHash[i] & 0xFF) + ":";
		
%>
<h3>Actor Name: <%= act_name %> </h3>
<h3>Actor Guid: <%= act_guid %> </h3>
<h3>Actor Status:
<%
			if ("True".equals(tmpMap.get(DatabaseOperations.ActorVerified))) {
%>
<font style="color:green">Verified</font></h3>
<%
			} else {
%>
<font style="color:red">Not verified</font></h3>
<%
			}
%>
<h3>Actor Description: <%= act_desc %> </h3>
<h3>Actor Certificate MD5 Fingerprint: </h3>
<pre>
<%= hashString %>
</pre>
<h3>Actor Cert: </h3>
<pre>
<%= act_cert64 %>
</pre>

<form id="actionform" name="actionform" action="validate-action.jsp">
<input type="hidden" name="guid" value="<%= act_guid %>" />
<input type="hidden" id="action" name="action" value="manage" />
<table width="600px"><tr><td>
<%
			if ("True".equals(tmpMap.get(DatabaseOperations.ActorVerified))) {
%>
<input type="button" value="Invalidate" onClick="submitForm('invalidate')" onMouseOver="showWarning('<h2 style=color:red>You are about to INVALIDATE this actor!</h2>')" onMouseOut="showWarning('')" />
<%
			} else {
%>
<input type="button" value="Validate" onClick="submitForm('validate')" onMouseOver="showWarning('<h2 style=color:green>You are about to VALIDATE this actor!</h2>')"  onMouseOut="showWarning('')"/>
<%
			}
%>
</td><td>
<input type="button" name="Delete" value="Delete" onClick="submitForm('delete')" onMouseOver="showWarning('<h2 style=color:red>You are about to DELETE this actor from the database!</h2>')"  onMouseOut="showWarning('')"/>
</td><td>
<input type="button" name="Cancel" value="Cancel" onClick="window.location='../actors.jsp';"/>
</td></tr></table>
</form>
<%		
		}
%>
<div id="warning">
</div>
</body>
</html>