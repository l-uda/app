<?php

// 
// Setta tutte le APP to INIT.
//
 
function LUDA_API_APP_CMD_AllApps_SetTo_Init_01( )
    {
    //echo "COMPLETED CLEARING...";

    // Object.
    $l_sTipo = LUDA_CONSTANT_SERVER_TIPO_APP;
    $oAPI    = new cLUDA_API( $l_sTipo );

    
//  IDLE. 
echo "<PRE>";
$status_name_init = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_INIT;
//var_dump( $status_name_init );
$status_item_init = $oAPI->Stato_GetCodiceByNome_01( $status_name_init );
//var_dump( $status_item_init );
$status_code_init = $status_item_init[ 'codice' ];
//$status_code_init = $status_code;
//echo "EXITED_58";
//exit();
    
    // Query.
    $query  = "";
    $query .= "UPDATE luda_server_stati_app ";
    $query .= "SET    stato_by_app = '" .$status_code_init. "' ";
    $query .= "WHERE  idapp > 0 ";
    $query .= "";
 echo $query;
    echo "<BR>";
    //echo "EXIT_36";
echo "</PRE>";
    //exit();
    $item = $oAPI->Query_Execute_01( $query );


    

    // Query.
    $query  = "";
    $query .= "UPDATE luda_server_stati_app ";
    $query .= "SET    completed_by_app = '" .$status_code_init. "' ";
    $query .= "WHERE  idapp > 0 ";
    $query .= "";
 echo $query;
    echo "<BR>";
    //echo "EXIT_36";
echo "</PRE>";
    //exit();
    $item = $oAPI->Query_Execute_01( $query );

    
    


{//20210513_strt
 // Query.
    $query  = "";
    $query .= "UPDATE luda_server_stati_app ";
    $query .= "SET    uda_id_by_app = '" .$status_code_init. "' ";
    $query .= "WHERE  idapp > 0 ";
    $query .= "";
 echo $query;
    echo "<BR>";
    //echo "EXIT_36";
echo "</PRE>";
    //exit();
    $item = $oAPI->Query_Execute_01( $query );
}//20210513_stop


 

{//20210518_strt
 // Query.
    $query  = "";
    $query .= "UPDATE luda_server_stati_app ";
    $query .= "SET    data_by_app = '" ."-1". "' ";
    $query .= "WHERE  idapp > 0 ";
    $query .= "";
 echo $query;
    echo "<BR>";
    //echo "EXIT_36";
echo "</PRE>";
    //exit();
    $item = $oAPI->Query_Execute_01( $query );
}//20210518_stop


 



    //var_dump( $item );
    //exit();    
    }//LUDA_API_APP_CMD_AllApps_SetTo_Init_01

?>