<?php
//echo "API::APP::PUT<BR>";
session_start();
require_once( "../../../luda_include_config.php"    );
require_once( "../../../luda_include_functions.php" );
require_once( "../../../luda_class_config.php"      );
require_once( "../../../luda_class_db.php"          );
require_once( "../../../luda_class_api.php"         );

// APP CompletedList Clear
require_once( "../../srv/cmd/api_srv_cmd_game_functions.php" );

{   
// API object.
$oAPI_SRV = new cLUDA_API( LUDA_CONSTANT_SERVER_TIPO_SRV );

// Data for STATUS: IDLE       
$status_code_idle       = $oAPI_SRV->Stato_GetCodiceByNome_01( LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_IDLE )['codice'];
// Data for STATUS: WAIT_APP
$status_code_wait_app   = $oAPI_SRV->Stato_GetCodiceByNome_01(LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_WAIT_APP)['codice'];
// Data for STATUS: GROUP_SENT
$status_code_group_sent = $oAPI_SRV->Stato_GetCodiceByNome_01(LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_GROUP_SENT)['codice'];
// Data for STATUS: GROUP_OVERRIDE
$status_code_group_override = $oAPI_SRV->Stato_GetCodiceByNome_01(LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_GROUP_OVERRIDE)['codice'];
// Data for STATUS: REACH_UDA
$status_code_reach_uda  = $oAPI_SRV->Stato_GetCodiceByNome_01(LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_REACH_UDA)['codice'];
// Data for STATUS: REACHING_UDA
$status_code_reaching_uda = $oAPI_SRV->Stato_GetCodiceByNome_01(LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_REACHING_UDA)['codice'];
// Data for STATUS: READY
$status_code_ready      = $oAPI_SRV->Stato_GetCodiceByNome_01(LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_READY)['codice'];
// Data for STATUS: STARTED
$status_code_started    = $oAPI_SRV->Stato_GetCodiceByNome_01(LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_STARTED)['codice'];
// Data for STATUS: PAUSE
$status_code_pause      = $oAPI_SRV->Stato_GetCodiceByNome_01(LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_PAUSE)['codice'];
// Data for STATUS: PAUSED
$status_code_paused     = $oAPI_SRV->Stato_GetCodiceByNome_01(LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_PAUSED)['codice'];
// Data for STATUS: RESUME
$status_code_resume     = $oAPI_SRV->Stato_GetCodiceByNome_01(LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_RESUME)['codice'];
// Data for STATUS: ABORT
$status_code_abort      = $oAPI_SRV->Stato_GetCodiceByNome_01(LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_ABORT)['codice'];
// Data for STATUS: RESTART
$status_code_restart    = $oAPI_SRV->Stato_GetCodiceByNome_01(LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_RESTART)['codice'];
// Data for STATUS: DATA_SENT
$status_code_data_sent  = $oAPI_SRV->Stato_GetCodiceByNome_01(LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_DATA_SENT)['codice'];
// Data for STATUS: FINALIZED
$status_code_finalized  = $oAPI_SRV->Stato_GetCodiceByNome_01(LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_FINALIZED)['codice'];
}

$idapp     = LUDA_API_Parameters_GetFromREST( $_GET, 'i'    );           
$status    = LUDA_API_Parameters_GetFromREST( $_GET, 'k'    );
$rest_data = LUDA_API_Parameters_GetFromREST( $_GET, 'data' );

// Objects.
$loDB     = new cLUDA_DB();
$oAPI_APP = new cLUDA_API( LUDA_CONSTANT_SERVER_TIPO_APP );
$oAPI_UDA = new cLUDA_API( LUDA_CONSTANT_SERVER_TIPO_UDA );

$uda_id_by_app = LUDA_API_Command_GetUDAbyApp( $idapp );

// "ATTENZIONE!!! TODO!!! GESTIRE CASISTICA ERRORI!!!";
if( $uda_id_by_app == -1 )
{
$aJsonAPI = ARRAY();
$aJsonAPI[ 'status' ] = 22 ;
$aJsonAPI[ 'error'  ] = 22 ;
$sJsonAPI = json_encode( $aJsonAPI );
echo $sJsonAPI;
exit();        
}

// Stato del server.
$server_status = LUDA_API_SRV_CMD_Game_Server_Stato_Get_01();
//echo "Il Server e' nello stato con Codice: <B>[" .$server_status. "]</B><BR>";

// Casistica dei vari Stati.
switch( $status )
    {//sw_strt

    case $status_code_group_override:
        if( $server_status == $status_code_wait_app ){
            $status = $status_code_reach_uda
            $item   = $oAPI_APP->Put_03($idapp, $status, $subgroup);
        }
    case $status_code_group_sent:
        if( $server_status == $status_code_wait_app ){
            // calculate subgroup and pass back to App   27/01/22
            $subgroup   = LUDA_API_Command_SetAPPSubGroup($idapp, $rest_data);
            if($subgroup != -1){
                $status = $status_code_reach_uda
                $item   = $oAPI_APP->Put_03($idapp, $status, $subgroup);
            }
            else{
                $aJsonAPI = ARRAY();
                $aJsonAPI[ 'status' ] = 22 ;
                $aJsonAPI[ 'error'  ] = 103 ;
                $sJsonAPI = json_encode( $aJsonAPI );
                echo $sJsonAPI;
                exit();
            }
        }
        else {
            // pesca dal DB lo stato corrente della APP
            $item = $oAPI_APP->Get_03( $idapp );
            $item = $oAPI_APP->Put_03( $idapp, $item['status'], $item['data'] );
        }
        break;

    case $status_code_data_sent :
        // Il data e' un intero (stringato) e deve essere passato.
        $item = $oAPI_UDA->Put_03( $uda_id_by_app, $status, $rest_data );

        // 2021-10-05     
        $item = $oAPI_APP->Get_03( $idapp );
        $item = $oAPI_APP->Put_03( $idapp, $status, $item['data'] ); 
        break;

    case $status_code_pause   :
    case $status_code_resume  :
    case $status_code_abort   :
    case $status_code_restart :
        // Deve mandare lo stesso comando alla UDA di competenza.
        $item = $oAPI_UDA->Put_03( $uda_id_by_app, $status, "" );

        // 2021-10-05     
        $item = $oAPI_APP->Get_03( $idapp );
        $item = $oAPI_APP->Put_03( $idapp, $status, $item['data'] ); 
        break;
   
     default:
        $item = $oAPI_APP->Put_03( $idapp, $status, "" ); 
        break;  
    }//sw_stop

$item = $oAPI_APP->Get_03( $idapp );

// Values.
$aJsonAPI = ARRAY();
$aJsonAPI[ 'type'         ] = LUDA_CONSTANT_SERVER_TIPO_APP;
$aJsonAPI[ 'function'     ] = LUDA_CONSTANT_SERVER_FUNZIONE_PUT;
$aJsonAPI[ 'i'            ] = $idapp;
$aJsonAPI[ 'k'            ] = $status;
$aJsonAPI[ 'status'       ] = $item[ 'stato_by_app'  ];
$aJsonAPI[ 'data'         ] = $item[ 'data_by_app'   ];
$aJsonAPI[ 'uda_id'       ] = $item[ 'uda_id_by_app' ];
$aJsonAPI[ 'error'        ] = NULL ;
$aJsonAPI[ 'api'          ] = "API";
$aJsonAPI[ 'version'      ] = "2.3";
$aJsonAPI[ 'subversion'   ] = "20220127";
$aJsonAPI[ 'php_self'     ] = $_SERVER['PHP_SELF'];

// Return values.
$sJsonAPI = json_encode( $aJsonAPI );
echo $sJsonAPI;
?>