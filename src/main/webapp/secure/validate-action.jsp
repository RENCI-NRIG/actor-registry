<%@ page import="java.io.*, java.net.*, java.util.*, orca.registry.*"  %>
<jsp:useBean id="data" class="orca.registry.ValidateFormData" scope="session"/>
<jsp:setProperty name="data" property="*"/>
<%

DatabaseOperations dbop = new DatabaseOperations();

if (data.getGuid() != null) {
	if (data.getAction().equals("validate"))
		dbop.updateEntryValidStatus(data.getGuid(), true);
	else if (data.getAction().equals("invalidate"))
		dbop.updateEntryValidStatus(data.getGuid(), false);
}
%>
<script language="javascript" type="text/javascript">
	window.location="../actors.jsp";
</script>