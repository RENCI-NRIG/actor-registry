<%@ page import="java.io.*, java.net.*, java.util.*, orca.registry.*"  %>
<jsp:useBean id="data" class="orca.registry.ValidateFormData" scope="session"/>
<jsp:setProperty name="data" property="*"/>
<%

DatabaseOperations dbop = new DatabaseOperations();

if (data.getGuid() != null) {
	if (data.getAction().equals("validate")) {
		dbop.updateEntryValidStatus(data.getGuid(), true);
%>
<script language="javascript" type="text/javascript">
	window.location="../actors.jsp";
</script>
<%
	} else if (data.getAction().equals("invalidate")) {
		dbop.updateEntryValidStatus(data.getGuid(), false);
%>
<script language="javascript" type="text/javascript">
	window.location="../actors.jsp";
</script>
<%
	} else if (data.getAction().equals("delete-confirmed")) {
		dbop.deleteActor(data.getGuid());
%>
<script language="javascript" type="text/javascript">
	window.location="../actors.jsp";
</script>
<%
	} else if (data.getAction().equals("delete")) {
%>
<html>
<title>ORCA Actor Registry</title>
<h2>Confirm deleting Actor GUID: <%= data.getGuid() %></h2>
<form action="validate-action.jsp">
<input type="hidden" name="guid" value="<%= data.getGuid() %>" />
<input type="hidden" id="action" name="action" value="delete-confirmed" />
<input type="submit" name="Confirm" value="Confirm" />
<input type="button" name="Cancel" value="Cancel" onClick="window.location='../actors.jsp';"/>
</form>
<%
	} else {
%>
<script language="javascript" type="text/javascript">
	window.location="../actors.jsp";
</script>
<%
	}
}
%>
</html>