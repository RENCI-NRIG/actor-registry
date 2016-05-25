<%@ page import="java.io.*, java.net.*, java.util.*, orca.registry.*"  %>
<jsp:useBean id="data" class="orca.registry.ControllerFormData" scope="session"/>
<jsp:setProperty name="data" property="*"/>
<%

DatabaseOperations dbop = new DatabaseOperations();

//System.out.println("Dealing with request " + data.getAction());

if (data.getUrl() != null) {
	if (data.getAction().equals("delete")) {
		dbop.deleteController(org.apache.commons.lang3.StringEscapeUtils.unescapeEcmaScript(data.getUrl()));
%>
<script language="javascript" type="text/javascript">
	window.location="controllers-admin.jsp";
</script>
<%
	} else if (data.getAction().equals("enable")) {
		//System.out.println("Setting default image " + org.apache.commons.lang3.StringEscapeUtils.unescapeEcmaScript(data.getUrl()));
		dbop.toggleController(org.apache.commons.lang3.StringEscapeUtils.unescapeEcmaScript(data.getUrl()));
%>
<script language="javascript" type="text/javascript">
	window.location="controllers-admin.jsp";
</script>
<%
    } else if (data.getAction().equals("add")) {
    	System.out.println("Adding controller " + data.getName() + " " + data.getUrl());
        dbop.insertController(data.getName(), data.getUrl(), data.getDesc(), true);
%>
  <script language="javascript" type="text/javascript">
	window.location="controllers-admin.jsp";
</script>     
<%
	} else {
%>
<script language="javascript" type="text/javascript">
	window.location="controllers-admin.jsp";
</script>
<%
	}
}
%>
</html>