<?php
session_start();
require_once( "../../../luda_include_config.php"    );
require_once( "../../../luda_include_functions.php" );
require_once( "../../../luda_class_db.php"          );
require_once( "../../../luda_class_api.php"         );

//echo "API::SRV::GET";
//echo "<BR>";
$l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_SRV;
$l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_GET;

//print_r( $_GET );
//echo "<BR>";
$par_iI = LUDA_API_Parameters_GetFromREST( $_GET, 'i' );
$par_iK = LUDA_API_Parameters_GetFromREST( $_GET, 'k' );
 
$oAPI = new cLUDA_API( $l_sTipo );

if( $l_sFunzione == LUDA_CONSTANT_SERVER_FUNZIONE_PUT ) { $stato = $oAPI->Put_02( $par_iI, $par_iK ); $stato = $oAPI->Get_02( $par_iI );}
if( $l_sFunzione == LUDA_CONSTANT_SERVER_FUNZIONE_GET ) {                                             $stato = $oAPI->Get_02( $par_iI ); }
//echo "STATO = <B >[" .$stato. "]</B>";
//echo "<BR>";

$aJsonAPI = ARRAY();
$aJsonAPI[ 'api'          ] = "API";
$aJsonAPI[ 'version'      ] = "2.2";
$aJsonAPI[ 'type'         ] = $l_sTipo;
$aJsonAPI[ 'function'     ] = $l_sFunzione;
$aJsonAPI[ 'i'            ] = $par_iI;
$aJsonAPI[ 'k'            ] = $par_iK;
$aJsonAPI[ 'status'       ] = $stato;
$aJsonAPI[ 'error'        ] = NULL ;
$aJsonAPI[ 'description'  ] = "Questa funzione funziona!";
$aJsonAPI[ 'subversion'   ] = "20210111";
$aJsonAPI[ 'query_string' ] = $_SERVER['QUERY_STRING'];
$aJsonAPI[ 'request_uri'  ] = $_SERVER['REQUEST_URI'];
$aJsonAPI[ 'script_name'  ] = $_SERVER['SCRIPT_NAME'];
$aJsonAPI[ 'php_self'     ] = $_SERVER['PHP_SELF'];
$aJsonAPI[ 'notes'        ] = Array() ;
$aJsonAPI[ 'notes'        ][] = "notes";
$aJsonAPI[ 'notes'        ][] = "Lorem";
$aJsonAPI[ 'notes'        ][] = "Ipse";
$aJsonAPI[ 'notes'        ][] = "Dicitur";
//var_dump( $aJsonAPI );

$sJsonAPI = json_encode( $aJsonAPI );
echo $sJsonAPI;

?>