<?php
/*
session_start();
require_once( "../../../luda_include_config.php"    );
require_once( "../../../luda_include_functions.php" );
require_once( "../../../luda_class_db.php"          );
require_once( "../../../luda_class_api.php"         );
*/


session_start();
require_once( "../../../luda_include_config.php"    );
require_once( "../../../luda_include_functions.php" );
require_once( "../../../luda_class_db.php"          );
require_once( "../../../luda_class_config.php"      );
require_once( "../../../luda_class_api.php"         );
require_once( "../../../luda_class_indizi.php"      );


// Parameters.
//print_r( $_GET );
//echo "<BR>";
$par_iId        = LUDA_API_Parameters_GetFromREST( $_GET, 'id'        );
$par_iPosizione = LUDA_API_Parameters_GetFromREST( $_GET, 'posizione' );


// Object 'indizio'
$oIndizio = new cLUDA_Indizi();
$aIndizi = Array();
if( $par_iId <= 0 )
    {//if_indizio_0_strt
    //echo "Parametro 'id' NON specificato tra i parametri GET!";    
    for( $indizio_id = 1; $indizio_id <= 5; $indizio_id++ )
        {//for_indizio_strt
        $aArrayIndizio = $oIndizio->Get_FieldsArray_01( $indizio_id );
        //var_dump( $aArrayIndizio );
        $aIndizi[] = $aArrayIndizio;        
        }//for_indizio_stop
        // Return data.
        $sJsonAPI = json_encode( $aIndizi );
        echo $sJsonAPI;    
        exit();
    }//if_indizio_0_stop
    
    
    
// Indizio indicato.
if( $par_iId <= 5 )
    {//if_indizio_strt
    $aArrayIndizio = $oIndizio->Get_FieldsArray_01( $par_iId );
    //var_dump( $aArrayIndizio );

    if( ($par_iPosizione >= 1) && (($par_iPosizione <= 5)) )
        {
        $data = $oIndizio->Get_FieldFromArray( $aArrayIndizio, $par_iPosizione );
        
        $sJsonAPI = json_encode( $data );
        //$sJsonAPI = json_encode( $aArrayIndizio[$par_iPosizione] );
        
        echo $sJsonAPI;
        exit();
        }
    
    $sJsonAPI = json_encode( $aArrayIndizio );
    echo $sJsonAPI;  
    exit();
    }//if_indizio_stop




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
 * 
*/
?>