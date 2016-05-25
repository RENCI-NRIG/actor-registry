<%@ page import="java.io.*, java.net.*, java.util.*, orca.registry.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
<title>ORCA Controller Registry</title>

<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.9.0/build/fonts/fonts-min.css" /> 
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.9.0/build/paginator/assets/skins/sam/paginator.css" /> 
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.9.0/build/datatable/assets/skins/sam/datatable.css" /> 

<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.9.0/build/button/assets/skins/sam/button.css" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.9.0/build/container/assets/skins/sam/container.css" />


<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/connection/connection-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/element/element-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/button/button-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/dragdrop/dragdrop-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/container/container-min.js"></script>


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
.yui-skin-sam .yui-dt tr.defctrl,
.yui-skin-sam .yui-dt tr.defctrl td.yui-dt-asc,
.yui-skin-sam .yui-dt tr.defctrl td.yui-dt-desc,
.yui-skin-sam .yui-dt tr.defctrl td.yui-dt-asc,
.yui-skin-sam .yui-dt tr.defctrl td.yui-dt-desc {
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
<center>
<h2><font face="courier, bookman">ORCA Controller Registry</font></h2><br>

<script language="JavaScript">
    document.write('<b> This page last updated on ' + (new Date).toLocaleString() + '</b>');
    var registry = new Array();
</script>

</center>

<% 
	DatabaseOperations dbop = new DatabaseOperations();
	List< Map<String, String>> res = dbop.queryControllerList();

    for(Map<String, String> en: res) {
    	String fName = org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript(en.get(DatabaseOperations.CTRL_NAME));
    	String fUrl = org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript(en.get(DatabaseOperations.CTRL_URL));
    	String fDesc = org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript(en.get(DatabaseOperations.CTRL_DESCRIPTION));
 
%>
<script>
		registry.push({
		name: '<%= fName %>', 
		url:'<a href="<%= fUrl %>"><%= fUrl %></a>', 
		desc:'<%= fDesc %>',
		def:'<%= en.get(orca.registry.DatabaseOperations.CTRL_ENABLED) %>'});
</script>
<%
	}
%>		
 
<div id="paginated">
</div> 

<center>
<a href="secure/controllers-admin.jsp">Manage Controllers</a>
</center>

<script type="text/javascript"> 
var rowFormatter = function(elTr, oRecord) {
    if (oRecord.getData('def') == "True") {
        Dom.addClass(elTr, 'defctrl');
    } 
    return true;
}; 
 
YAHOO.util.Event.addListener(window, "load", function() {
    tableListener = function() {
        var myColumnDefs = [
        	{key:"name", label:"Controller Name", width: 100},
            {key:"url", label:"URL", sortable:true, resizeable:true, width:150},
            {key:"desc", label:"Description", resizeable:true, width:250},
        ];
 
        var myDataSource = new YAHOO.util.DataSource(registry);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["name", "url", "desc", "def"]
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

<p>
This service is also available as XMLRPC from <b>http://geni.renci.org:15080/registry/</b>.
For example in Python:
</p>
<pre>
import xmlrpclib
proxy=xmlrpclib.ServerProxy("http://geni.renci.org:15080/registry/")
proxy.registryService.getAllControllers()
</pre> 


</body>
</html>
