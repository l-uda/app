<?php
//echo "API::UDA::PUT<BR>";
session_start();
require_once( "../../../luda_include_config.php"    );
require_once( "../../../luda_include_functions.php" );
require_once( "../../../luda_class_db.php"          );
require_once( "../../../luda_class_api.php"         );

// Cancello il 'data'   nel DB della UDA.
// Setto    lo 'status' nel DB della UDA.
// Recupero l'App associata a questa UDA e le propago lo 'status' ed il 'data' della REST.

// Parameters.
$iduda    = LUDA_API_Parameters_GetFromREST( $_GET, 'i'    );           
$status   = LUDA_API_Parameters_GetFromREST( $_GET, 'k'    );
$par_data = LUDA_API_Parameters_GetFromREST( $_GET, 'data' );

// Object: API (UDA)
$oAPI_UDA = new cLUDA_API( LUDA_CONSTANT_SERVER_TIPO_UDA );

// API UDA PUT
$item = $oAPI_UDA->Put_03( $iduda, $status, "" );

$app_id_by_uda = LUDA_API_Command_GetUDAbyApp( $iduda );

// "ATTENZIONE!!! TODO!!! GESTIRE CASISTICA ERRORI!!!";
if( $app_id_by_uda == -1 )
{
$aJsonAPI = ARRAY();
$aJsonAPI[ 'status' ] = 22 ;
$aJsonAPI[ 'error'  ] = 22 ;
$sJsonAPI = json_encode( $aJsonAPI );
echo $sJsonAPI;
exit();        
}

$subgroups_registered = get_all_subgroups($app_id_by_uda)   // funzione che cerca nella nuova tabella
                                                            // tutti i sottogruppi presenti per quel gruppo
                                                            // ritorna array di indici di subgroups

$oAPI_APP = new cLUDA_API( LUDA_CONSTANT_SERVER_TIPO_APP );

if($status == "WAIT_DATA"){
    // parse "data" field (che è un json), get "allowed_users" che sarà un array vuoto o un array di indici
    $allowed_users = LUDA_APP_GET_REGISTERED_USERS($par_data)
    if($allowed_users == [])
        foreach $id in $subgroups_registered
            $oAPI_APP->Put_04( $app_id_by_uda, $id, $status, $par_data );
    else
        foreach $id in $allowed_users
            $oAPI_APP->Put_04( $app_id_by_uda, $id, $status, $par_data );
}

// $oAPI_APP->Put_03( $app_id_by_uda, $status, $par_data );

// API UDA GET
$item = $oAPI_UDA->Get_03( $iduda ); 

// Values.
$aJsonAPI = ARRAY();
$aJsonAPI[ 'type'         ] = LUDA_CONSTANT_SERVER_TIPO_UDA;
$aJsonAPI[ 'function'     ] = LUDA_CONSTANT_SERVER_FUNZIONE_PUT;
$aJsonAPI[ 'i'            ] = $iduda;
$aJsonAPI[ 'k'            ] = $status;
$aJsonAPI[ 'status'       ] = $item[ 'stato_by_uda'  ];
$aJsonAPI[ 'data'         ] = $item[ 'data_by_uda'   ];
$aJsonAPI[ 'app_id'       ] = $item[ 'app_id_by_uda' ];
$aJsonAPI[ 'error'        ] = NULL ;
$aJsonAPI[ 'api'          ] = "API";
$aJsonAPI[ 'version'      ] = "3.3";
$aJsonAPI[ 'subversion'   ] = "20210111";
$aJsonAPI[ 'php_self'     ] = $_SERVER['PHP_SELF'];

// Return values.
$sJsonAPI = json_encode( $aJsonAPI );
echo $sJsonAPI;




   
/*
// API object.
$l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_SRV;
$l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_GNP;
$oAPI = new cLUDA_API( $l_sTipo );

// Data for STATUS: IDLE       
$status_name_idle = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_IDLE;
$status_item_idle = $oAPI->Stato_GetCodiceByNome_01( $status_name_idle );
$status_code_idle = $status_item_idle[ 'codice' ];   
      
// Data for STATUS: WAIT_APP        
$status_name_wait_app = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_WAIT_APP;
$status_item_wait_app = $oAPI->Stato_GetCodiceByNome_01( $status_name_wait_app );
$status_code_wait_app = $status_item_wait_app[ 'codice' ];   

// Data for STATUS: GROUP_SENT
$status_name_group_sent = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_GROUP_SENT;
$status_item_group_sent = $oAPI->Stato_GetCodiceByNome_01( $status_name_group_sent );
$status_code_group_sent = $status_item_group_sent[ 'codice' ];   
      
// Data for STATUS: REACH_UDA
$status_name_reach_uda = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_REACH_UDA;
$status_item_reach_uda = $oAPI->Stato_GetCodiceByNome_01( $status_name_reach_uda );
$status_code_reach_uda = $status_item_reach_uda[ 'codice' ];   
      
// Data for STATUS: READY        
$status_name_ready = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_READY;
$status_item_ready = $oAPI->Stato_GetCodiceByNome_01( $status_name_ready );
$status_code_ready = $status_item_ready[ 'codice' ];   
       
// Data for STATUS: STARTED        
$status_name_started = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_STARTED;
$status_item_started = $oAPI->Stato_GetCodiceByNome_01( $status_name_started );
$status_code_started = $status_item_started[ 'codice' ];   

// Data for STATUS: PAUSE        
$status_name_pause = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_PAUSE;
$status_item_pause = $oAPI->Stato_GetCodiceByNome_01( $status_name_pause );
$status_code_pause = $status_item_pause[ 'codice' ];   
 
// Data for STATUS: PAUSED        
$status_name_paused = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_PAUSED;
$status_item_paused = $oAPI->Stato_GetCodiceByNome_01( $status_name_paused );
$status_code_paused = $status_item_paused[ 'codice' ];   
 
// Data for STATUS: RESUME        
$status_name_resume = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_RESUME;
$status_item_resume = $oAPI->Stato_GetCodiceByNome_01( $status_name_resume );
$status_code_resume = $status_item_resume[ 'codice' ];   
 
// Data for STATUS: ABORT
$status_name_abort = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_ABORT;
$status_item_abort = $oAPI->Stato_GetCodiceByNome_01( $status_name_abort );
$status_code_abort = $status_item_abort[ 'codice' ];   
 
// Data for STATUS: RESTART
$status_name_restart = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_RESTART;
$status_item_restart = $oAPI->Stato_GetCodiceByNome_01( $status_name_restart );
$status_code_restart = $status_item_restart[ 'codice' ];   

// Data for STATUS: WAIT_DATA
$status_name_wait_data = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_WAIT_DATA;
$status_item_wait_data = $oAPI->Stato_GetCodiceByNome_01( $status_name_wait_data );
$status_code_wait_data = $status_item_wait_data[ 'codice' ];  

// Data for STATUS: DATA_SENT
$status_name_data_sent = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_DATA_SENT;
$status_item_data_sent = $oAPI->Stato_GetCodiceByNome_01( $status_name_data_sent );
$status_code_data_sent = $status_item_data_sent[ 'codice' ];  

// Data for STATUS: FINALIZED        
$status_name_finalized = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_FINALIZED;
$status_item_finalized = $oAPI->Stato_GetCodiceByNome_01( $status_name_finalized );
$status_code_finalized = $status_item_finalized[ 'codice' ];
*/



/*    
// Casistica dei vari Stati.
switch( $status )
    {//sw_strt
    case $status_code_idle :
        // Nota: Per adesso l'APP non riceve il comando 'idle'.
        break;
    case $status_code_started :
    case $status_code_wait_data :
        // Bisogna svuotare la colonna data_by_uda (Della UDA in questione)
        $query = "UPDATE luda_server_stati_uda SET data_by_uda = '' WHERE iduda = " . $iduda;
        //echo $query . "<br>";
        $oAPI_UDA->Query_Execute_02($query);
        //break;  //   ATTENZIONE!!! Il break e' stato commentato in modo da fare eseguire anche il case (paused) sottostante!       
    default:
        {
        // Bisogna passare $status all^APP di riferimento (che si trova nel DB). >
        // Ovvero:
        // $uda__app_id_by_uda = < l^ID della APP che gestisce in quel turno la UDA >
        // Object: API (APP)
        $l_sTipo  = LUDA_CONSTANT_SERVER_TIPO_APP;
        $oAPI_APP = new cLUDA_API( $l_sTipo );
        $oAPI_APP->Put_03( $uda_app_id_by_uda, $status, $par_data );
        }
        break;
    }//sw_stop
*/        

?>