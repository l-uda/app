<?php

// 
// Setta tutte le APP to IDLE.
//

function LUDA_API_APP_CMD_AllApps_SetTo_Idle_01( )
    {    
    //echo "COMPLETED CLEARING...";

    // Object.
    $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_APP;
    $oAPI = new cLUDA_API( $l_sTipo );

    
//  IDLE.        
$status_name_idle = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_IDLE;
$status_item_idle = $oAPI->Stato_GetCodiceByNome_01( $status_name_idle );
$status_code_idle = $status_item_idle[ 'codice' ];
//$status_code_idle = $status_code;

    
    // Query.
    $query  = "";
    $query .= "UPDATE luda_server_stati_app ";
    $query .= "SET    stato_by_app = '" .$status_code_idle. "' ";
    $query .= "WHERE  idapp > 0 ";
    $query .= "";
 echo $query;
    echo "<BR>";
    //echo "EXIT28";
    //exit();

    $item = $oAPI->Query_Execute_01( $query );
    //var_dump( $item );
    //exit();    
    }//LUDA_API_APP_CMD_AllApps_SetTo_Idle_01

?>