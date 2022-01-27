<?php

// Aggiunge 
// un ID delle UDA gia' completate dalla APP in questione.
// alla lista degli ID delle UDA gia' completate dalla APP in questione.

function LUDA_API_APP_CMD_CompletedList_Add_01( $p_iIdUdaCompletata )
    {
    
     echo "CL_ADD_WIP_NOP_EXIT";
    exit();
return -147;    
    
    
$stato_by_app = -1234543210;
$data_by_app  = "WIP CHECK here il codice dello status dell'intera installazione...";
$completed_by_app = "1,2,3,4,5,6,7,8,9,147";
    $item = Array();
    $item[ 'status'           ] = $stato_by_app     ;
    $item[ 'stato_by_app'     ] = $stato_by_app     ;
    $item[ 'data_by_app'      ] = $data_by_app      ;
    $item[ 'completed_by_app' ] = $completed_by_app ;

    // Return value.
    return $item;

    }//LUDA_API_APP_CMD_CompletedList_Add_01



?>