<?php

// Azzera la lista degli ID delle UDA completate dalla APP in questione.


function LUDA_API_APP_CMD_CompletedList_Clear_01( )
    {    
    //echo "COMPLETED CLEARING...";

    // Object.
    $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_APP;
    $oAPI = new cLUDA_API( $l_sTipo );

    // Query.
    $query  = "";
    $query .= "UPDATE luda_server_stati_app ";
    $query .= "SET    completed_by_app = '' ";
    $query .= "WHERE  idapp > 0 ";
    $query .= "";
    //echo $query;
    //exit();

    $item = $oAPI->Query_Execute_01( $query );
    //var_dump( $item );
    //exit();    
    }//LUDA_API_APP_CMD_CompletedList_Clear_01

 

?>