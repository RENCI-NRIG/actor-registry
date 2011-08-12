<%@ page import="java.io.*, java.net.*, java.util.*, orca.registry.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
<title>ORCA Actor Registry</title>

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

/* Class for un-verified rows */
.yui-skin-sam .yui-dt tr.unver,
.yui-skin-sam .yui-dt tr.unver td.yui-dt-asc,
.yui-skin-sam .yui-dt tr.unver td.yui-dt-desc,
.yui-skin-sam .yui-dt tr.unver td.yui-dt-asc,
.yui-skin-sam .yui-dt tr.unver td.yui-dt-desc {
    background-color: #bbf;
    color: #fff;
}

/* Class for dead rows */
.yui-skin-sam .yui-dt tr.deadact,
.yui-skin-sam .yui-dt tr.deadact td.yui-dt-asc,
.yui-skin-sam .yui-dt tr.deadact td.yui-dt-desc,
.yui-skin-sam .yui-dt tr.deadact td.yui-dt-asc,
.yui-skin-sam .yui-dt tr.deadact td.yui-dt-desc {
    background-color: #a33;
    color: #fff;
}

/* class for localhost rows */
.yui-skin-sam .yui-dt tr.localhost,
.yui-skin-sam .yui-dt tr.localhost td.yui-dt-asc,
.yui-skin-sam .yui-dt tr.localhost td.yui-dt-desc,
.yui-skin-sam .yui-dt tr.localhost td.yui-dt-asc,
.yui-skin-sam .yui-dt tr.localhost td.yui-dt-desc {
    background-color: #FFFF99;
    color: #000;
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
<h2><font face="courier, bookman"><center>ORCA Actor Registry</center></font></h2><br>

<center>
<script language="JavaScript">
    document.write('<b> This page last updated on ' + (new Date).toLocaleString() + '</b>');
    var registry = new Array();
</script>

<table class="yui-dt">
<tr class="unver">
<td align="center">Actor is unverified; Other actors can't connect to this actor </td>
</tr>
<tr class="localhost">
<td align="center">Actor in test mode, using localhost; Other actors can't connect to this actor</td>
</tr>
<tr class="deadact">
<td align="center">Actor not live; Other actors can't connect to this actor</td>
</tr>
</table>
</center>

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
        	actor_type = "Service Manager";                
		}
		if(act_type.equalsIgnoreCase("broker")){
        	actor_type = "Broker";
        }
        if(act_type.equalsIgnoreCase("site")){
        	actor_type = "Site Authority";
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

		if (diffInHours <= 12) {
			String act_production_deployment = tmpMap.get(DatabaseOperations.ActorProduction);
			String manageText = "";
			if ("True".equals(act_verified)) 
				manageText = "Yes</br>";
			else
				manageText = "No</br>";
			manageText+="<form action=\"secure/validate.jsp\" method=\"POST\" name=\"" + act_guid + "\"> " +
			"<input type=\"hidden\" name=\"guid\" value=\"" + act_guid + "\" /> " +
			"<input type=\"hidden\" name=\"action\" value=\"manage\" /> " +
			"<input type=\"submit\" value=\"Manage\" /></form>";
			
			String andlLink = "N/A", fndlLink = "N/A";
			
            if(act_type.equalsIgnoreCase("site")){
                    if(act_abstract_rdf == null || act_full_rdf == null){
                            andlLink = "Not Available";
                            fndlLink = "Not Available";
                    }
                    else {
                            andlLink = "<a href=\"http://geni.renci.org:11080/registry?showFile=" + abstractRdfFileNameShort + "\">Click for Abstract Site NDL</a>";
                            fndlLink = "<td align=\"center\">" + "<a href=\"http://geni.renci.org:11080/registry?showFile=" + fullRdfFileNameShort + "\">Click for Full Site NDL</a></td>";
                    }
            }
%>
<script>
		registry.push({
		manage: '<%= manageText %>', 
		name:'<%= act_name %>', 
		guid:'<%= act_guid %>', 
		type:'<%= actor_type %>', 
		desc:'<%= act_desc %>', 
		url:'<%= act_soapaxis2url %>', 
		aclass:'<%= act_class %>', 
		apolicy:'<%= act_mapper_class %>',
		aprod: '<%= act_production_deployment %>',
		pubkey: '<a href="http://geni.renci.org:11080/registry?showString=<%= escaped_act_pubkey %>">Click for Public Key</a>',
		cert64: '<a href="http://geni.renci.org:11080/registry?showString=<%= escaped_act_cert64 %>">Click for Certificate</a>',
		andl: '<%= andlLink %>',
		fndl: '<%= fndlLink %>',
		verified: '<%= act_verified %>',
		amdiff: parseInt('<%= diffInMinutes %>')});
		
</script>
<%
		}
	}
%>		
 
<div id="paginated"></div> 
 
<script type="text/javascript"> 
// Define a custom row formatter function
var rowFormatter = function(elTr, oRecord) {
    if (oRecord.getData('amdiff') > 2) {
        Dom.addClass(elTr, 'deadact');
    } else if (oRecord.getData('aprod') == "False") {
		Dom.addClass(elTr, 'localhost'); 
		} else if (oRecord.getData('verified') == "False") {
			Dom.addClass(elTr, 'unver');
			}
    return true;
}; 
 
YAHOO.util.Event.addListener(window, "load", function() {
    tableListener = function() {
        var myColumnDefs = [
        	{key:"manage", label:"Actor Verified", width: 90},
            {key:"name", label:"Name", sortable:true, resizeable:true, width:100},
            {key:"guid", label:"GUID", sortable:true,resizeable:true, width:250},
            {key:"type", label:"Type", sortable:true, resizeable:true, width:100},
            {key:"desc", label:"Description", sortable:true, resizeable:true, width:150},
            {key:"url", label:"SOAP URL", sortable:true, resizeable:true, width:250},
 //           {key:"aclass", label:"Class", sortable:true, resizable:true, width:150},
 //           {key:"apolicy", label:"Policy", sortable:true, resizable:true, width:150},
            {key:"pubkey", label:"Public Key"},
            {key:"cert64", label:"Certificate"},
            {key:"andl", label:"Abstract NDL"},
            {key:"fndl", label:"Full NDL"}
        ];
 
        var myDataSource = new YAHOO.util.DataSource(registry);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["manage", "name", "guid", "type", "desc", "url", "aclass", "apolicy", "amdiff", "aprod", "pubkey", "cert64", "andl", "fndl", "verified"]
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
