<?php

// Ritorna la lista degli ID delle UDA completate dalla APP in questione.

function LUDA_API_APP_CMD_CompletedList_Get_01( $p_iAppId )
    {
    
    // Parameters.
    $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_APP;
    $l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_GET;

    // Object.
    $oAPI = new cLUDA_API( $l_sTipo );
    $item = $oAPI->Get_03( $p_iAppId );
    //var_dump( $item );    

    //$app_id               = $item->idapp;
    $app_status_code      = $item[ 'stato_by_app'     ];
    $app_status_group_id  = $item[ 'uda_id_by_app'  ];
    $app_status_completed = $item[ 'completed_by_app' ];
 
    $item = Array();
    $item[ 'stato_by_app'     ] = $app_status_code      ;
    $item[ 'uda_id_by_app'  ] = $app_status_group_id  ;
    $item[ 'completed_by_app' ] = $app_status_completed ;

    // Return value.
    return $item;

    }//LUDA_API_APP_CMD_CompletedList_Get_01

?>