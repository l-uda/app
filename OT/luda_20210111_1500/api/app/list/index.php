<?php
session_start();
require_once( "../../../luda_include_config.php"    );
require_once( "../../../luda_include_functions.php" );
require_once( "../../../luda_class_db.php"          );
require_once( "../../../luda_class_api.php"         );

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
?>