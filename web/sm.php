<?php 

$username="registry";
$password="registry";
$database="ActorRegistry";

mysql_connect(localhost,$username,$password) or die(mysql_error());
//echo "Connection to the database server was successful!<br/>";
@mysql_select_db($database) or die( "Unable to select database");
//echo "Selected the $database database<br/>";
$query="SELECT * FROM Actors where act_type=1";
$result=mysql_query($query);

$num=mysql_numrows($result);

mysql_close();

echo '<h2><font face="courier, bookman"><center>ORCA Actor Registry</center></font></h2><br>';

echo '<style type="text/css">';
echo 'table.sample {';
echo '	border-width: 1px;';
echo '	border-spacing: ;';
echo '	border-style: outset;';
echo '	border-color: gray;';
echo '	border-collapse: separate;';
echo '	background-color: white;';
echo '}';
echo 'table.sample th {';
echo '	border-width: 1px;';
echo '	padding: 1px;';
echo '	border-style: inset;';
echo '	border-color: gray;';
echo '	background-color: white;';
echo '	-moz-border-radius: ;';
echo '}';
echo 'table.sample td {';
echo '	border-width: 1px;';
echo '	padding: 1px;';
echo '	border-style: inset;';
echo '	border-color: gray;';
echo '	background-color: white;';
echo '	-moz-border-radius: ;';
echo '}';
echo '</style>';

echo '<style type="text/css">';
echo 'table.pretty {';
echo '	margin: 1em 1em 1em 2em;';
echo '	background: whitesmoke;';
#echo '	font-family: Verdana;';
echo '	font-family: courier, bookman;';
echo '  font-weight: normal;';
echo '  font-size: 15px;';
echo '	border-collapse: collapse;';
echo '}';
echo 'table.pretty th, table.pretty td {';
echo '	border: 1px silver solid;';
echo '	padding: 0.2em;';
echo '}';
echo 'table.pretty th {';
echo '	background: gainsboro;';
echo '	font-family: Verdana;';
echo '	text-align: center;';
echo '}';
echo 'table.pretty caption {';
echo '	margin-left: inherit;';
echo '	margin-right: inherit;';
echo '}';
echo '</style>';

#echo '<table border="1" width="100%" cellpadding="2" cellspacing="2">';
echo '<table class="pretty">';
echo '<tr>'; 
echo '<th align="center"><b>Actor Name</b></th>';
echo '<th align="center"><b><font color="red">Actor GUID</font></b></th>'; 
echo '<th align="center"><b>Actor Type</b></th>'; 
echo '<th align="center"><b>Actor Description</b></th>'; 
echo '<th align="center"><b>Actor SOAPAxis2 URL</b></th>'; 
echo '<th align="center"><b>Actor Class</b></th>'; 
echo '<th align="center"><b>Actor Policy</b></th>'; 
echo '<th align="center"><b>Actor Public Key</b></th>'; 
echo '<th align="center"><b>Actor Certificate</b></th>'; 
echo '<th align="center"><b>Abstract Site NDL</b></th>'; 
echo '<th align="center"><b>Full Site NDL</b></th>'; 
echo '</tr>';

$i=0;
while ($i < $num) {

	$act_id=mysql_result($result,$i,"act_id");
	$act_name=mysql_result($result,$i,"act_name");
	$act_guid=mysql_result($result,$i,"act_guid");
	$act_type=mysql_result($result,$i,"act_type");
	$act_desc=mysql_result($result,$i,"act_desc");
	$act_soapaxis2url=mysql_result($result,$i,"act_soapaxis2url");
	$act_class=mysql_result($result,$i,"act_class");
	$act_mapper_class=mysql_result($result,$i,"act_mapper_class");
	$act_pubkey=mysql_result($result,$i,"act_pubkey");
	$act_cert64=mysql_result($result,$i,"act_cert64");
	$act_abstract_rdf=mysql_result($result,$i,"act_abstract_rdf");
	$act_full_rdf=mysql_result($result,$i,"act_full_rdf");
	$act_allocatable_units=mysql_result($result,$i,"act_allocatable_units");

	// Write the public key to the file - <guid>.pubkey
	$filename=$act_guid.".pubkey";
	$file = fopen ($filename, "w"); 
	fwrite($file, $act_pubkey); 
	fclose ($file);

	// Write the certificate to the file - <guid>.cert64
	$certfilename=$act_guid.".cert64";
	$certfile = fopen ($certfilename, "w");
	fwrite($certfile, $act_cert64);
	fclose($certfile);

	// Write the abstract and full rdfs to file - <guid>.abstract.rdf and <guid>.full.rdf
	if ( $act_type == 3 ){
		$abstractrdfFilename=$act_guid.".abstract.rdf";
		$fullrdfFilename=$act_guid.".full.rdf";
		$abstractrdfFile = fopen ($abstractrdfFilename, "w");
		$fullrdfFile = fopen ($fullrdfFilename, "w");
		fwrite($abstractrdfFile, $act_abstract_rdf);
		fwrite($fullrdfFile, $act_full_rdf);
		fclose($abstractrdfFile);
		fclose($fullrdfFile);
	}

	$actor_type="foo";
	if ( $act_type == 1 ){
        	$actor_type="ORCA Service Manager (SM)";
	}
	elseif ( $act_type == 2 ){
        	$actor_type="ORCA Broker";
	}
	elseif ( $act_type == 3 ){
        	$actor_type="ORCA Site Authority / Aggregate Manager (AM)";
	}

	echo '<tr>'; 
	echo '<td align="center">'.$act_name.'</td>'; 
	echo '<td align="center">'.$act_guid.'</td>'; 
	echo '<td align="center">'.$actor_type.'</td>'; 
	echo '<td align="center">'.$act_desc.'</td>'; 
	if ( $act_soapaxis2url == "None"){
		echo '<td align="center">'.$act_soapaxis2url.'</td>'; 
	}
	else {
		echo '<td align="center">'.'<a href="'.$act_soapaxis2url.'">Link</a></td>'; 
	}
	echo '<td align="center">'.$act_class.'</td>'; 
	echo '<td align="center">'.$act_mapper_class.'</td>'; 
//	echo '<td align="center">'.$act_pubkey.'</td>'; 
	echo '<td align="center">'.'<a href="'.$filename.'">Download Public Key</a></td>'; 
	echo '<td align="center">'.'<a href="'.$certfilename.'">Download Actor Certificate</a></td>'; 
	if ( $act_type == 3 ){
		echo '<td align="center">'.'<a href="'.$abstractrdfFilename.'">Download Abstract Site NDL</a></td>'; 
		echo '<td align="center">'.'<a href="'.$fullrdfFilename.'">Download Full Site NDL</a></td>'; 
	}
	else {
		$notApplicable="N/A";
		echo '<td align="center">'.$notApplicable.'</td>'; 
		echo '<td align="center">'.$notApplicable.'</td>'; 
	}
	echo '</tr>'; 

	$i++;
}

?>
