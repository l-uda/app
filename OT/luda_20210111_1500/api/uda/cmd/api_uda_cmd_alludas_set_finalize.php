<?php

// 
// Setta tutte le UDA to FINALIZE.
//

function LUDA_API_UDA_CMD_AllUdas_SetTo_Finalize_01( )
    {
    //echo "LUDA_API_UDA_CMD_AllUdas_SetTo_Finalize_01 :: I <BR>\n";

    // Object.
    $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_APP;
    $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_UDA;
    $oAPI = new cLUDA_API( $l_sTipo );

    // FINALIZE.        
    $status_name_finalize = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_FINALIZE;
    $status_item_finalize = $oAPI->Stato_GetCodiceByNome_01( $status_name_finalize );
    $status_code_finalize = $status_item_finalize[ 'codice' ];
    
    // Query.
    $query  = "";
    $query .= "UPDATE luda_server_stati_uda ";
    $query .= "SET    stato_by_uda = '" .$status_code_finalize. "' ";
    $query .= "WHERE  iduda > 0 ";
    $query .= "";
    //echo $query;
    //echo "<BR>";
    //exit();

    $item = $oAPI->Query_Execute_01( $query );
    //var_dump( $item );
    //exit();    
    }//LUDA_API_UDA_CMD_AllUdas_SetTo_Finalize_01

?>