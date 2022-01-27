<?php

// Aggiunge 
// un ID delle UDA gia' completate dalla APP in questione.
// alla lista degli ID delle UDA gia' completate dalla APP in questione.
 
function LUDA_API_APP_CMD_CompletedList_Add_02( $p_iIdApp, $p_iIdUdaCompletata )
    {
    echo "LUDA_API_APP_CMD_CompletedList_Add_02<br>";

    // Object.
    $l_sTipo = LUDA_CONSTANT_SERVER_TIPO_APP;
    $oAPI    = new cLUDA_API( $l_sTipo );

    // Query.  
    $query  = "";
    $query .= "UPDATE luda_server_stati_app ";
    $query .= "SET    completed_by_app = " .$p_iIdUdaCompletata. " ";
    $query .= "WHERE  idapp =  " . $p_iIdApp ;
    $query .= "";
    echo $query;

    $item = $oAPI->Query_Execute_01( $query );
    
    return $item  ;    

    }//LUDA_API_APP_CMD_CompletedList_Add_02
 



function LUDA_API_APP_CMD_CompletedList_Add_01( $p_iIdUdaCompletata )
    {
    
     echo "CL_ADD_WIP_NOP_EXIT";
      
echo "LUDA_API_SRV_CMD_Simulation_01<br>";

// Object.
$l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_APP;
$oAPI = new cLUDA_API( $l_sTipo );

// Query.
$query  = "";
$query .= "UPDATE luda_server_stati_app ";
$query .= "SET    completed_by_app = 5 - idapp + 1 ";
$query .= "WHERE  idapp > 0 ";
$query .= "";
echo $query;
//exit();
$item = $oAPI->Query_Execute_01( $query );

return;    

    }//LUDA_API_APP_CMD_CompletedList_Add_01



?>