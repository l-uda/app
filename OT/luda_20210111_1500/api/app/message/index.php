<?php
session_start();
require_once( "../../../luda_include_config.php"    );
require_once( "../../../luda_include_functions.php" );
require_once( "../../../luda_class_db.php"          );
require_once( "../../../luda_class_api.php"         );


print_r( $_GET );
echo "<BR>";
$par_sName = LUDA_API_Parameters_GetFromREST( $_GET, 'name' );
$par_sText = LUDA_API_Parameters_GetFromREST( $_GET, 'text' );



//echo "LUDA_atss";

if( 0 )
{
$to      = "roberto.sagoleo@gmail.com";
}
else
{
$to      = "erica.volta.stella@gmail.com";
}
$subject = "Messaggio da LUDA@CESTO in Area Archeologia";
$headers = 'From: erica.volta.stella@gmail.com' . "\r\n" .
'Reply-To: erica.volta.stella@gmail.com' . "\r\n" .
'X-Mailer: PHP/' . phpversion();

$message  = "";
$message .= "Caro/Cara " . $par_sName;
$message .= "\n";
$message .= "\n";
$message .= " ecco il testo che hai scritto: ";
$message .= "\n";
$message .= "\n";
$message .= $par_sText;
$message .= "\n";
$message .= "\n";

// str_replace ( mixed $search , mixed $replace , mixed $subject [, int &$count ] ) : mixed
//$message .= "MSG64B::" . base64_decode( str_replace("msg64=","",$_SERVER['QUERY_STRING']) );

$rv = mail($to, $subject, $message, $headers);

$to = "sax.infomus@gmail.com";
$rv = mail($to, $subject, $message, $headers);

//echo '{"status":' .$rv . '}';

?>



<?php
exit();
?>



<?php
/*
// API::APP::GET
$l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_APP;
$l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_GET;
$oAPI = new cLUDA_API( $l_sTipo ); 
$l_aStati = $oAPI->Stati_Array_Get_01( strtoupper($l_sTipo), $l_sFunzione );
ksort( $l_aStati );
$g_aStatiGet = $l_aStati;

// API::APP::PUT
$l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_APP;
$l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_PUT; 
$oAPI = new cLUDA_API( $l_sTipo ); 
$l_aStati = $oAPI->Stati_Array_Get_01( strtoupper($l_sTipo), $l_sFunzione );
ksort( $l_aStati );
$g_aStatiPut = $l_aStati;

// Data.
$aJsonAPI = ARRAY();
$aJsonAPI[ 'api'          ] = "API";
$aJsonAPI[ 'version'      ] = "3.0";
$aJsonAPI[ 'type'         ] = $l_sTipo;
$aJsonAPI[ 'function'     ] = "LIST";
$aJsonAPI[ 'status'       ] = "OK";
$aJsonAPI[ 'error'        ] = NULL ;
$aJsonAPI[ 'list' ] = Array() ;
$aJsonAPI[ 'list' ][ LUDA_CONSTANT_SERVER_FUNZIONE_GET ] = $g_aStatiGet;
$aJsonAPI[ 'list' ][ LUDA_CONSTANT_SERVER_FUNZIONE_PUT ] = $g_aStatiPut;

// Return data.
$sJsonAPI = json_encode( $aJsonAPI );
echo $sJsonAPI;
*/
?>