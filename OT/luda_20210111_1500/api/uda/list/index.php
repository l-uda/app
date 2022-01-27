<?php
session_start();
require_once( "../../../luda_include_config.php"    );
require_once( "../../../luda_include_functions.php" );
require_once( "../../../luda_class_db.php"          );
require_once( "../../../luda_class_api.php"         );

// Parameters.
//print_r( $_GET );
//echo "<BR>";
$par_sType   = LUDA_API_Parameters_GetFromREST( $_GET, 'type'   );
$par_iCodice = LUDA_API_Parameters_GetFromREST( $_GET, 'codice' );
$par_sNome   = LUDA_API_Parameters_GetFromREST( $_GET, 'nome'   );

// API::UDA::GET
$l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_UDA;
$l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_GET;
$oAPI = new cLUDA_API( $l_sTipo );
$l_aStati = $oAPI->Stati_Array_Get_01( strtoupper($l_sTipo), $l_sFunzione );
ksort( $l_aStati );
$g_aStatiGet = $l_aStati;

// API::UDA::PUT
$l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_UDA;
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
if( $par_sType == LUDA_CONSTANT_SERVER_FUNZIONE_GET ) { $aJsonAPI[ 'list' ][ LUDA_CONSTANT_SERVER_FUNZIONE_GET ] = $g_aStatiGet; }
if( $par_sType == LUDA_CONSTANT_SERVER_FUNZIONE_PUT ) { $aJsonAPI[ 'list' ][ LUDA_CONSTANT_SERVER_FUNZIONE_PUT ] = $g_aStatiPut; }

// All list (no filtered).
//if( $par_sType != "" )
if( 1 )
    {
    $sJsonAPI = json_encode( $aJsonAPI );
    }
    
// Only one by 'codice'.
if( $par_iCodice != "" ) 
    {
    $sJsonAPI = json_encode( $aJsonAPI['list'][$par_sType][$par_iCodice] );
    };

// Only one by 'nome'.
if( $par_sNome != "" ) 
    {// if_strt
    $sJsonAPI = json_encode( $aJsonAPI['list'][$par_sType][-1] );
    foreach( $aJsonAPI['list'][$par_sType] as $codice => $item )
        {
        // Cerca l'indice tramite il contenuto (associativo) del 'nome'.
        //var_dump( $item );
        $l_iCodice = $item[ 'codice' ];      
        $l_sNome   = $item[ 'nome'   ];      
        //var_dump( $l_iCodice );
        if( $l_sNome == $par_sNome )
            {
            //$sJsonAPI = json_encode( $aJsonAPI['list'][$par_sType][$l_iCodice] );
            $sJsonAPI = json_encode( $item );
            }
        }; 
    }//if_stop

// Return data.
echo $sJsonAPI;
?>