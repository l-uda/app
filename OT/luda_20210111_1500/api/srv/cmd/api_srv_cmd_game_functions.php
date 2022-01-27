<?php

///////////////////////////////////////////////////////////////////


// 
// Resetta la Randomization
// 
// Resetting the Gaming-Matrix (per una nuova Sessione di gioco)
// 
function LUDA_API_SRV_CMD_Game_Randomization_Reset_01( )
    {
    $l_sConfigValue = 0;
    
    // Config.
    echo "LUDA_API_SRV_CMD_GAME_RESET_NEW2222<br>";
    LUDA_API_SRV_CMD_Game_TurnoCorrente_Set_01( intval($l_sConfigValue) );   
  
    }//LUDA_API_SRV_CMD_Game_Randomization_Reset_01
///////////////////////////////////////////////////////////////////

  
//
// Imposta (su DB) la Randomization (allo 'step' n. 1).
// 
function LUDA_API_SRV_CMD_Game_Randomization_GotoNextStep_01( )
    {

    // Database.
    $loDB   = new cLUDA_DB();    

    // Config.
    $oConfig = new cLUDA_Config();
     
    $l_sConfigName = "LUDA_SERVER_MASTER_GAME_STEPS_QTY";

    $l_sConfigValue = $oConfig->Get_ByName_01( $l_sConfigName );
    $l_iStepsOfGamesQuantity = intval( $l_sConfigValue );
    //echo "LUDA_SERVER_MASTER_GAME_STEPS_QTY = [" .$l_iStepsOfGamesQuantity. "]<BR>\n";
 
    $l_sConfigName = "LUDA_SERVER_MASTER_GAME_STEP_CURR";

    $l_iPositionOfGroup = intval( LUDA_API_SRV_CMD_Game_TurnoCorrente_Get_01() );
    echo "SERVER_MASTER_GAME_STEP_CURR_NEW2 = [" .$l_iPositionOfGroup. "]<BR>\n";

    if( $l_iPositionOfGroup < $l_iStepsOfGamesQuantity )
        {
        $l_iPositionOfGroup++;
        }
    else
        {
        echo "ERROR!!! SERVER_MASTER_GAME_STEP_CURR :: RAGGIUNTO IL MASSIMO NUMERO DI GAMES (Non Incrementato lo step)!<BR>\n";
        }
    echo "SERVER_MASTER_GAME_STEP_CURR = [" .$l_iPositionOfGroup. "]<BR>\n";

    $l_sConfigValue = LUDA_API_SRV_CMD_Game_TurnoCorrente_Set_01( $l_iPositionOfGroup );
    $l_sConfigValue = LUDA_API_SRV_CMD_Game_TurnoCorrente_Get_01();
    $l_iPositionOfGroup = intval( $l_sConfigValue );
    echo "SERVER_MASTER_GAME_STEP_CURR_NEW222 = [" .$l_iPositionOfGroup. "]<BR>\n";

    echo "ASSIGNING_TURNO_TO_SERVER_NEWWW22<BR>";    
    LUDA_API_SRV_CMD_Game_TurnoCorrente_Set_01( $l_iPositionOfGroup );
    echo "<br>";

    {//20210513_strt
    echo "ASSIGNED_POSITION = [" . $l_iPositionOfGroup . "]<BR>";
    echo "<HR>";
    for( $uda_id = 1; $uda_id <= 5; $uda_id++ )
        {
        echo "NEXT_ASSIGN_COMMANDS per UDA N.[" . $uda_id . "]<BR>";


        // Colonne degli 'identificatori incrociati'.
        $pos_app   = LUDA_API_SRV_CMD_Game_Randomization_PositionOfApp_ByStep_Get_01( $uda_id , $l_iPositionOfGroup );
        $query_app = "UPDATE luda_server_stati_app SET uda_id_by_app = " . $uda_id  . " WHERE idapp = " .$pos_app. ";";
        $query_uda = "UPDATE luda_server_stati_uda SET app_id_by_uda = " . $pos_app . " WHERE iduda = " .$uda_id . ";";
        echo "QUERY_APP = [" .$query_app. "]<BR>";
        echo "QUERY_UDA = [" .$query_uda. "]<BR>";
        $result_app = $loDB->Query_01($query_app);
        $result_uda = $loDB->Query_01($query_uda);
        //echo "<br>";

$codice_temp_idle      = 0; // TODO Prendere da DB nel "solito" modo!    
$codice_temp_wait_app  = 1; // TODO Prendere da DB nel "solito" modo!
$codice_temp_reach_uda = 3; // TODO Prendere da DB nel "solito" modo! 

        // Colonne 'stati'
//$query_app = "UPDATE luda_server_stati_app SET stato_by_app = " . $codice_temp_wait_app  . " WHERE idapp = " .$pos_app. ";";  // 2021-21-23
$query_app = "UPDATE luda_server_stati_app SET stato_by_app = " . $codice_temp_reach_uda  . " , data_by_app = " . $uda_id  . " WHERE idapp = " .$pos_app. ";";   // 2021-21-23
$query_uda = "UPDATE luda_server_stati_uda SET stato_by_uda = " . $codice_temp_idle                                        . " WHERE iduda = " .$uda_id . ";";
        echo "QUERY_APP = [" .$query_app. "]<BR>";
        echo "QUERY_UDA = [" .$query_uda. "]<BR>";
        $result_app = $loDB->Query_01($query_app);
        $result_uda = $loDB->Query_01($query_uda);
    echo "<BR>";
    echo "<BR>";

        }//for_uda

    // LUDA_API_SRV_CMD_Game_Randomization_GotoNextStep_01();
    echo "<HR>";
    echo "<br>NEXT_STEP_OK<br>";
    }//20210513_stop    


    // Data for STATUS: WAIT_APP        
$l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_SRV;
$oAPI        = new cLUDA_API( $l_sTipo );
    $status_name_wait_app       = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_WAIT_APP;
    $status_item_wait_app       = $oAPI->Stato_GetCodiceByNome_01( $status_name_wait_app );
    $status_code_wait_app       = $status_item_wait_app[ 'codice' ];   

    // Il server va in wait_app
    $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_SRV;
    $l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_GNP;
    $oAPI        = new cLUDA_API( $l_sTipo );
    $l_iSrvNum   = 1;
    $stato       = $oAPI->Put_02( $l_iSrvNum, $status_code_wait_app ); 
    echo "    < messo il SERVER in *WAIT_APP* !!! >\n";

    return $l_iPositionOfGroup ;
    
    }//LUDA_API_SRV_CMD_Game_Randomization_GotoNextStep_01
///////////////////////////////////////////////////////////////////

    
    
    
    
    
    
    
//  
// Ritorna il posizionamento del Gruppo/App 'indicato' quando si trova allo Step 'indicato.
//    
function LUDA_API_SRV_CMD_Game_Randomization_PositionOfApp_ByStep_Get_01( $p_iAppId, $p_iStepPos )
    {

//    $l_iConfig
            
    $l_iMasterGameStepsQty = 5; // Prendere da DB!
    
    $l_iPositionOfGroup = ($p_iAppId + ($p_iStepPos -1) );
    if( $l_iPositionOfGroup > $l_iMasterGameStepsQty ) 
        { $l_iPositionOfGroup = $l_iPositionOfGroup - 5; }

    // Return value.
    return( $l_iPositionOfGroup );

    }//LUDA_API_SRV_CMD_Game_Randomization_PositionOfApp_ByStep_Get_01
///////////////////////////////////////////////////////////////////
    
     
//
// Ritorna il numero di "Step" (di Games) corrente (da DB) sul numero totale di Game-steps.
// 
function LUDA_API_SRV_CMD_Game_Randomization_StepCurrent_Get_01( )
    {

    // Config.
    $oConfig = new cLUDA_Config();
    $l_sConfigName = "LUDA_SERVER_MASTER_GAME_STEP__CURR";

    $l_iPositionOfGroup = intval( LUDA_API_SRV_CMD_Game_TurnoCorrente_Get_01() );
    //echo "SERVER_MASTER_GAME_STEP_CURR_NEW2 = [" .$l_iPositionOfGroup. "]<BR>\n";

    return $l_iPositionOfGroup ;
    
    }//LUDA_API_SRV_CMD_Game_Randomization_StepCurrent_Get_01
///////////////////////////////////////////////////////////////////
 
    
function LUDA_API_SRV_CMD_Game_Randomization_StepQuantity_Get_01( )
    {
    
    // Config.
    $oConfig = new cLUDA_Config();
    $l_sConfigName = "LUDA_SERVER_MASTER_GAME_STEPS_QTY";

    $l_sConfigValue = $oConfig->Get_ByName_01( $l_sConfigName );
    $l_iStepsOfGamesQuantity = intval( $l_sConfigValue );
    //echo "LUDA_SERVER_MASTER_GAME_STEPS_QTY = [" .$l_iStepsOfGamesQuantity. "]<BR>\n";

    return $l_iStepsOfGamesQuantity ;

    }//LUDA_API_SRV_CMD_Game_Randomization_StepQuantity_Get_01  
///////////////////////////////////////////////////////////////////

 
function LUDA_API_SRV_CMD_Game_TurnoCorrente_Get_01( )    
    {//20210513_strt
            
    // Database.
    $loDB   = new cLUDA_DB();    

    //echo "GETTING_TURNO_FROM_SERVER...<BR>";    
    $query_srv = "SELECT turno_corrente FROM luda_server_stati_srv WHERE idsrv = " ."1". ";";
    //echo "QUERY_SRV = [" .$query_srv. "]<BR>";
    $result_srv = $loDB->Query_01($query_srv);
    $num_records = $loDB->RecordCount_01();
    $obj = $loDB->Fetch_01();
    $turno_corrente = $obj->turno_corrente;
    //echo "TURNO_CORRENTE_GET = [" . $turno_corrente . "]";
    //echo "<br>";
    
    // Return turno-corrente.
    return $turno_corrente;
    
    }//20210513_stop
///////////////////////////////////////////////////////////////////  
    
    
function LUDA_API_SRV_CMD_Game_TurnoCorrente_Set_01( $p_iPositionOfGroup )    
    {//20210513_strt
            
    // Database.
    $loDB   = new cLUDA_DB();    

    echo "ASSIGNING_TURNO_TO_SERVER: N.[" .$p_iPositionOfGroup. "]<BR>";    
    $query_srv = "UPDATE luda_server_stati_srv SET turno_corrente = " . $p_iPositionOfGroup . " WHERE idsrv = " ."1". ";";
    echo "QUERY_SRV = [" .$query_srv. "]<BR>";
    $result_srv = $loDB->Query_01($query_srv);
    echo "<br>";
    }//20210513_stop
///////////////////////////////////////////////////////////////////  
 
function LUDA_API_SRV_CMD_Game_Server_Stato_Get_01( )    
    {//20210513_strt
            
    // Database.
    $loDB   = new cLUDA_DB();    

    //echo "GETTING_TURNO_FROM_SERVER...<BR>";    
    $query_srv = "SELECT stato_by_srv FROM luda_server_stati_srv WHERE idsrv = " ."1". ";";
    //echo "QUERY_SRV = [" .$query_srv. "]<BR>";
    $result_srv = $loDB->Query_01($query_srv);
    $num_records = $loDB->RecordCount_01();
    $obj = $loDB->Fetch_01();
    $stato_server = $obj->stato_by_srv;
    //echo "STATO_CORRENTE_GET = [" . $stato_server . "]";
    //echo "<br>";
    
    // Return turno-corrente.
    return $stato_server;
    
    }//LUDA_API_SRV_CMD_Game_Server_Stato_Get_01
///////////////////////////////////////////////////////////////////  
      
///////////////////////////////////////////////////////////////////  
//
///////////////////////////////////////////////////////////////////
?>