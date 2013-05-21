<%@ page import="java.io.*, java.net.*, java.util.*, orca.registry.*"  %>
<jsp:useBean id="data" class="orca.registry.ImageFormData" scope="session"/>
<jsp:setProperty name="data" property="*"/>
<%

DatabaseOperations dbop = new DatabaseOperations();

System.out.println("Dealing with request " + data.getAction());

if (data.getHash() != null) {
	if (data.getAction().equals("delete")) {
		dbop.deleteImage(org.apache.commons.lang3.StringEscapeUtils.unescapeEcmaScript(data.getHash()));
%>
<script language="javascript" type="text/javascript">
	window.location="../images.jsp";
</script>
<%
	} else if (data.getAction().equals("setdefault")) {
		//System.out.println("Setting default image " + org.apache.commons.lang3.StringEscapeUtils.unescapeEcmaScript(data.getHash()));
		dbop.setDefaultImage(org.apache.commons.lang3.StringEscapeUtils.unescapeEcmaScript(data.getHash()));
%>
<script language="javascript" type="text/javascript">
	window.location="../images.jsp";
</script>
<%
    } else if (data.getAction().equals("add")) {
    	System.out.println("Adding image " + data.getName() + " " + data.getUrl());
        dbop.insertImage(data.getName(), data.getVer(), data.getNver(), data.getUrl(), data.getHash(), data.getOwner(), data.getDesc(), false);
%>
  <script language="javascript" type="text/javascript">
	window.location="../images.jsp";
</script>     
<%
	} else {
%>
<script language="javascript" type="text/javascript">
	window.location="../images.jsp";
</script>
<%
	}
}
%>
</html>