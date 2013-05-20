<%@ page import="java.io.*, java.net.*, java.util.*, orca.registry.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
<title>ORCA Image Registry</title>

<meta http-equiv="refresh" content="120">

<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.9.0/build/fonts/fonts-min.css" /> 
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.9.0/build/paginator/assets/skins/sam/paginator.css" /> 
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.9.0/build/datatable/assets/skins/sam/datatable.css" /> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/yahoo-dom-event/yahoo-dom-event.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/connection/connection-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/json/json-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/element/element-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/paginator/paginator-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/datasource/datasource-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/event-delegate/event-delegate-min.js"></script> 
<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/datatable/datatable-min.js"></script> 
 
 <style type="text/css"> 
/* custom styles for this example */
.yui-skin-sam .yui-dt-liner { white-space:wrap; } 

/* Class for default image rows */
.yui-skin-sam .yui-dt tr.defimage,
.yui-skin-sam .yui-dt tr.defimage td.yui-dt-asc,
.yui-skin-sam .yui-dt tr.defimage td.yui-dt-desc,
.yui-skin-sam .yui-dt tr.defimage td.yui-dt-asc,
.yui-skin-sam .yui-dt tr.defimage td.yui-dt-desc {
    background-color: #55CC33;
    color: #fff;
}

/* for pagination */
#paginated {
    text-align: center;
}
#paginated table {
    margin-left:auto; margin-right:auto;
}
#paginated, #paginated .yui-dt-loading {
    text-align: center; background-color: transparent;
}
</style> 
 
<body class="yui-skin-sam">
<h2><font face="courier, bookman"><center>ORCA Image Registry</center></font></h2><br>

<center>
<script language="JavaScript">
    document.write('<b> This page last updated on ' + (new Date).toLocaleString() + '</b>');
    var registry = new Array();
</script>
</center>

<% 
	DatabaseOperations dbop = new DatabaseOperations();
	List< Map<String, String>> res = dbop.queryImageList();

    for(Map<String, String> en: res) {
    	String fName = org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript(en.get(DatabaseOperations.IMAGE_NAME));
    	String fUrl = org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript(en.get(DatabaseOperations.IMAGE_URL));
    	String fHash = org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript(en.get(DatabaseOperations.IMAGE_HASH));
    	String fVer = org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript(en.get(DatabaseOperations.IMAGE_VERSION));
    	String fNver = org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript(en.get(DatabaseOperations.IMAGE_NEUCA_VERSION));
    	String fOwner = org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript(en.get(DatabaseOperations.IMAGE_OWNER));
    	String fDesc = org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript(en.get(DatabaseOperations.IMAGE_DESCRIPTION));
    	
    	String deleteText="<form action=\"secure/image-action.jsp\" method=\"GET\" hash=\"" + fHash + "\"> " +
			"<input type=\"hidden\" name=\"hash\" value=\"" + en.get(DatabaseOperations.IMAGE_HASH) + "\" /> " +
			"<input type=\"hidden\" name=\"action\" value=\"delete\" /> " +
			"<input type=\"submit\" value=\"Delete\" /></form>";
		String setDefaultText="<form action=\"secure/image-action.jsp\" method=\"GET\" hash=\"" + fHash + "\"> " +
			"<input type=\"hidden\" name=\"hash\" value=\"" + en.get(DatabaseOperations.IMAGE_HASH) + "\" /> " +
			"<input type=\"hidden\" name=\"action\" value=\"setdefault\" /> " +
			"<input type=\"submit\" value=\"Default\" /></form>";
%>
<script>
		registry.push({
		manage: '<%= deleteText %><br/><%= setDefaultText %>',
		name: '<%= fName %>', 
		url:'<%= fUrl %>', 
		hash:'<%= fHash %>', 
		ver:'<%= fVer %>', 
		nver:'<%= fNver %>', 
		owner:'<%= fOwner %>', 
		date:'<%= en.get(orca.registry.DatabaseOperations.IMAGE_DATE) %>', 
		def:'<%= en.get(orca.registry.DatabaseOperations.IMAGE_DEFAULT) %>',
		desc:'<%= fDesc %>'});
</script>
<%
	}
%>		
 
<div id="paginated"></div> 
 
<script type="text/javascript"> 
var rowFormatter = function(elTr, oRecord) {
    if (oRecord.getData('def') == "True") {
        Dom.addClass(elTr, 'defimage');
    } 
    return true;
}; 
 
YAHOO.util.Event.addListener(window, "load", function() {
    tableListener = function() {
        var myColumnDefs = [
        	{key:"manage", label:"Actions", width:90},
        	{key:"name", label:"Image Name", width: 100},
            {key:"url", label:"URL", sortable:true, resizeable:true, width:150},
            {key:"hash", label:"Hash", sortable:true, resizeable:true, width:150},
            {key:"desc", label:"Description", resizeable:true, width:250},
            {key:"ver", label:"Version", sortable:true, resizeable:true, width:50},
            {key:"nver", label:"Neuca", sortable:true, resizeable:true, width:50},
            {key:"owner", label:"Owner", sortable:true, resizeable:true, width:150},
            {key:"date", label:"Date added", sortable:true, resizeable:true, width:150},
        ];
 
        var myDataSource = new YAHOO.util.DataSource(registry);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["manage","name", "ver", "nver", "url", "hash", "owner", "date", "desc", "def"]
        };
 
        var myDataTable = new YAHOO.widget.DataTable("paginated",
                myColumnDefs, myDataSource, 
                {formatRow: rowFormatter, 
                draggableColumns:true, 
                sortedBy: {key:"name", dir:"asc"},
                paginator: new YAHOO.widget.Paginator({
                    rowsPerPage: 15})
                });
                
        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
    }();
});
</script> 

</body>
</html>
