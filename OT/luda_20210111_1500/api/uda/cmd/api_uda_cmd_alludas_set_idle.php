<?php

// 
// Setta tutte le UDA to IDLE.
//
 


function LUDA_API_UDA_CMD_AllUdas_SetTo_Idle_02( )
    {    
    echo "<br>LUDA_API_UDA_CMD_AllUdas_SetTo_Idle_02::I<br>\n";

    // Object.
    $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_APP;
    $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_UDA;
    $oAPI = new cLUDA_API( $l_sTipo );

    
//  IDLE.        
$status_name_idle = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_IDLE;
$status_item_idle = $oAPI->Stato_GetCodiceByNome_01( $status_name_idle );
$status_code_idle = $status_item_idle[ 'codice' ];
//$status_code_idle = $status_code;


$status_code_idle_MOD = -1;
    
    // Query.
    $query  = "";
//    $query .= "UPDATE luda_server_stati_app ";
    $query .= "UPDATE luda_server_stati_uda ";
    $query .= "SET    stato_by_uda = '" .$status_code_idle. "' ";
    $query .= "WHERE  iduda > 0 ";
    $query .= "";
    echo $query;
    echo "<BR>";
    //exit();

    $item = $oAPI->Query_Execute_01( $query );
    //var_dump( $item );
    //exit();    
    
    
    
/*    
{//20210513_strt
    // Query.
    $query  = "";
//    $query .= "UPDATE luda_server_stati_app ";
    $query .= "UPDATE luda_server_stati_uda ";
    $query .= "SET    app_id_by_uda = '" .$status_code_idle_MOD. "' ";
    $query .= "WHERE  iduda > 0 ";
    $query .= "";
    echo $query;
    echo "<BR>";
    //exit();
    $item = $oAPI->Query_Execute_01( $query );
}//20210513_stop
 */   
    

/*
{//20210518_strt
    // Query.
    $query  = "";
//    $query .= "UPDATE luda_server_stati_app ";
    $query .= "UPDATE luda_server_stati_uda ";
    $query .= "SET    data_by_uda = '" ."-1". "' ";
    $query .= "WHERE  iduda > 0 ";
    $query .= "";
    echo $query;
    echo "<BR>";
    //exit();
    $item = $oAPI->Query_Execute_01( $query );
}//20210518_stop
*/   


    
    }//LUDA_API_UDA_CMD_AllUdas_SetTo_Idle_02





function LUDA_API_UDA_CMD_AllUdas_SetTo_Idle_And_More_01( )
    {    
    echo "<br>COMPLETED_CLEARING...<br>\n";

    // Object.
    $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_APP;
    $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_UDA;
    $oAPI = new cLUDA_API( $l_sTipo );

    
//  IDLE.        
$status_name_idle = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_IDLE;
$status_item_idle = $oAPI->Stato_GetCodiceByNome_01( $status_name_idle );
$status_code_idle = $status_item_idle[ 'codice' ];
//$status_code_idle = $status_code;


$status_code_idle_MOD = -1;
    
    // Query.
    $query  = "";
//    $query .= "UPDATE luda_server_stati_app ";
    $query .= "UPDATE luda_server_stati_uda ";
    $query .= "SET    stato_by_uda = '" .$status_code_idle. "' ";
    $query .= "WHERE  iduda > 0 ";
    $query .= "";
    echo $query;
    echo "<BR>";
    //exit();

    $item = $oAPI->Query_Execute_01( $query );
    //var_dump( $item );
    //exit();    
    
    
    
    
{//20210513_strt
    // Query.
    $query  = "";
//    $query .= "UPDATE luda_server_stati_app ";
    $query .= "UPDATE luda_server_stati_uda ";
    $query .= "SET    app_id_by_uda = '" .$status_code_idle_MOD. "' ";
    $query .= "WHERE  iduda > 0 ";
    $query .= "";
    echo $query;
    echo "<BR>";
    //exit();
    $item = $oAPI->Query_Execute_01( $query );
}//20210513_stop
    
    


{//20210518_strt
    // Query.
    $query  = "";
//    $query .= "UPDATE luda_server_stati_app ";
    $query .= "UPDATE luda_server_stati_uda ";
    $query .= "SET    data_by_uda = '" ."-1". "' ";
    $query .= "WHERE  iduda > 0 ";
    $query .= "";
    echo $query;
    echo "<BR>";
    //exit();
    $item = $oAPI->Query_Execute_01( $query );
}//20210518_stop
    
    

    
    }//LUDA_API_UDA_CMD_AllUdas_SetTo_Idle_And_More_01



?>