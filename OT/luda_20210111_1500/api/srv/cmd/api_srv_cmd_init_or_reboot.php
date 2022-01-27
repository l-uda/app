<?php


echo "MOVED_EXIT_!!!";
exit();

session_start();
require_once( "./luda_include_config.php"    );
require_once( "./luda_include_functions.php" );
require_once( "./luda_class_db.php"          );
require_once( "./luda_class_config.php"      );
require_once( "./luda_class_api.php"         );


// APP CompletedList Clear
require_once( "./api/app/cmd/api_app_cmd_completedlist_clear.php" );

// APP SetAll to IDLE
require_once( "./api/app/cmd/api_app_cmd_allapps_set_idle.php" );

// APP SetAll to INIT
require_once( "./api/app/cmd/api_app_cmd_allapps_set_init.php" );

// UDA SetAll to IDLE
require_once( "./api/uda/cmd/api_uda_cmd_alludas_set_idle.php" );
//exit();

// GAME randomization matrix functions
require_once( "./api/srv/cmd/api_srv_cmd_game_functions.php" );
//exit();



// INIT or REBOOT: vedi "init_or_reboot" (dato che il codice e' -per-adesso- identico)

// Parameters. 
//$par_iStatus = LUDA_API_Parameters_GetFromREST( $_GET, 'status' );
$par_iStatus = $par_GET_status;

//echo "GAME::API::SRV::INIT";
$l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_COD;
$l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_GNP;

$oAPI = new cLUDA_API( $l_sTipo );

echo "Initing... (for code: " . $par_iStatus . ")";
echo "<BR>";
 
$status_code = $par_iStatus;
$status_item = $oAPI->Stato_GetNomeByCodice_01( $status_code );
$status_name = $status_item[ 'nome' ];

echo "status_code: " . $status_code . "<BR>";
echo "status_name: " . $status_name . "<BR>";
  
echo "<BR>";
 
$l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_SRV;
$l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_PUT;
$oAPI = new cLUDA_API( $l_sTipo );
$numSrv = 1;
$stato = $oAPI->Put_02( $numSrv, $status_code );


//  IDLE.        
$status_name_idle = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_IDLE;
$status_item_idle = $oAPI->Stato_GetCodiceByNome_01( $status_name_idle );
$status_code_idle = $status_item_idle[ 'codice' ];
//$status_code_idle = $status_code;

//  WAIT_APP.
$status_name_wait_app = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_WAIT_APP;        
$status_item_wait_app = $oAPI->Stato_GetCodiceByNome_01( $status_name_wait_app );
$status_code_wait_app = $status_item_wait_app[ 'codice' ];
//$status_code_wait_app = $status_code;

// Commands.
switch( $status_code )
    {//sw_strt
    case $status_code_idle :
        // COMMAND: [[[ REBOOT ]]]
        echo "STATO___IDLE";
        echo "<BR>";

        echo "<HR>";
        echo "Resetting the 'completion' of all the UDAs for each APP...";
        echo "<BR>";
        echo "<BR>";
        //LUDA_API_SRV_CMD_Simulation_01();
        LUDA_API_APP_CMD_CompletedList_Clear_01();

        echo "<HR>";
        echo "Setting the Status to IDLE for each APP...";
        echo "<BR>";
//LUDA_API_APP_CMD_AllApps_SetTo_Idle_01();
  LUDA_API_APP_CMD_AllApps_SetTo_Init_01();

        echo "<HR>";
        echo "Setting the Status to IDLE for each UDA...";
        echo "<BR>";
        LUDA_API_UDA_CMD_AllUdas_SetTo_Idle_And_More_01();

        // Resetta la Randomization. 
        echo "<HR>";
        echo "Resetting the Gaming-Matrix (per una nuova Sessione di gioco)...";
        echo "<BR>";
        echo "<BR>";
        LUDA_API_SRV_CMD_Game_Randomization_Reset_01(); 
        //echo "EXITED95";
        //exit();

        break;
    case $status_code_wait_app :
        // COMMAND: [INIT]
        echo "STATO___WAIT_APP<br>";
        
// Inzializza la Randomization (allo 'step' n. 1).        
LUDA_API_SRV_CMD_Game_Randomization_Reset_01(); 
LUDA_API_SRV_CMD_Game_Randomization_GotoNextStep_01();        
     
        break;
    
}//sw_stop
 
echo "<HR><A href='index.php' >Continua!</A>";
?>