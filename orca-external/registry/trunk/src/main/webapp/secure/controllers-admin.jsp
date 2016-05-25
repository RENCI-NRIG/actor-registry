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

/* Class for default controller rows */
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
    	
    	String deleteText="<form action=\"controller-action.jsp\" method=\"GET\" url=\"" + fUrl + "\"> " +
			"<input type=\"hidden\" name=\"url\" value=\"" + en.get(DatabaseOperations.CTRL_URL) + "\" /> " +
			"<input type=\"hidden\" name=\"action\" value=\"delete\" /> " +
			"<input type=\"submit\" value=\"Delete\" /></form>";
		String enableText="<form action=\"controller-action.jsp\" method=\"GET\" url=\"" + fUrl + "\"> " +
			"<input type=\"hidden\" name=\"url\" value=\"" + en.get(DatabaseOperations.CTRL_URL) + "\" /> " +
			"<input type=\"hidden\" name=\"action\" value=\"enable\" /> " +
			"<input type=\"submit\" value=\"Enable\" /></form>";
%>
<script>
		registry.push({
		manage: '<%= deleteText %><br/><%= enableText %>',
		name: '<%= fName %>', 
		url:'<a href="<%= fUrl %>"><%= fUrl %></a>', 
		def:'<%= en.get(orca.registry.DatabaseOperations.CTRL_ENABLED) %>',
		desc:'<%= fDesc %>'});
</script>
<%
	}
%>		
 
<div id="paginated">
</div> 

<center>
<div>
<button id="show" align="center">Add new controller</button>
</div>
</center>

<div id="dialog1" class="yui-pe-content">
<div class="hd">Please enter your information</div>
<div class="bd">
<form method="GET" action="controller-action.jsp">
	<label for="name" style="display:block;width:100px;">Controller Name:</label><input type="textbox" name="name" />
	<div class="clear"></div>
	<label for="url" style="display:block;width:100px;">URL:</label><input type="textbox" name="url" />
	<div class="clear"></div>
	<label for="desc" style="display:block;width:100px;">Description</label><textarea name="desc" rows="4" cols="50"></textarea>
	
	<input type="hidden" name="action" value="add" />
</form>
</div>
</div>

 
 <script>
YAHOO.namespace("example.container");

YAHOO.util.Event.onDOMReady(function () {
	
	// Define various event handlers for Dialog
	var handleSubmit = function() {
		this.submit();
	};
	var handleCancel = function() {
		this.cancel();
	};
	var handleSuccess = function(o) {
		//document.getElementById("resp").innerHTML = "OK";
	};
	var handleFailure = function(o) {
		alert("Submission failed: " + o.status);
	};

    // Remove progressively enhanced content class, just before creating the module
    YAHOO.util.Dom.removeClass("dialog1", "yui-pe-content");

	// Instantiate the Dialog
	YAHOO.example.container.dialog1 = new YAHOO.widget.Dialog("dialog1", 
							{ width : "30em",
							  fixedcenter : true,
							  visible : false, 
							  constraintoviewport : true,
							  postmethod : "form",
							  buttons : [ { text:"Submit", handler:handleSubmit, isDefault:true },
								      { text:"Cancel", handler:handleCancel } ]
							});

	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.dialog1.validate = function() {
		var data = this.getData();
		if (data.firstname == "" || data.lastname == "") {
			alert("Please enter your first and last names.");
			return false;
		} else {
			return true;
		}
	};

	// Wire up the success and failure handlers
	YAHOO.example.container.dialog1.callback = { success: handleSuccess,
						     failure: handleFailure };
	
	// Render the Dialog
	YAHOO.example.container.dialog1.render();

	YAHOO.util.Event.addListener("show", "click", YAHOO.example.container.dialog1.show, YAHOO.example.container.dialog1, true);
	YAHOO.util.Event.addListener("hide", "click", YAHOO.example.container.dialog1.hide, YAHOO.example.container.dialog1, true);
});
</script>

 
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
        	{key:"manage", label:"Actions", width:90},
        	{key:"name", label:"Controller Name", width: 100},
            {key:"url", label:"URL", sortable:true, resizeable:true, width:150},
            {key:"desc", label:"Description", resizeable:true, width:250},
        ];
 
        var myDataSource = new YAHOO.util.DataSource(registry);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["manage","name", "url", "desc", "def"]
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
