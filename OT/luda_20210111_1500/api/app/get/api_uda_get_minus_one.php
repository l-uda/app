<?php

function LUDA_API_UDA_GET_Minus_one_01( )
    {
    return LUDA_API_GET_Minus_one_01();
    }//LUDA_API_UDA_GET_Minus_one_01

    
// Questa funzione e' stata DEPRECATED da quella piu' in alto (search and remove ove gia' utilizzata: 2021-03-04).    
function LUDA_API_GET_Minus_one_01( )
    {
    
    
// VERIFICA DELLO STATO (appena re-impostato !!!!
require_once( "../../../api/srv/cmd/api_srv_cmd_status.php" );
$item_srv = LUDA_API_SRV_CMD_Status_Get_02();

if( 1 ){}else
{
echo "<br>TRACE21<br>";
echo "<pre>";
print_r( $item );
echo "</pre>";
//exit();
}
    
    
$stato_by_app = $item_srv[ 'stato' ];
$uda_id_by_app  = -1;
$completed_by_app = "";

    $item = Array();
    
    $item[ 'stato_by_srv'     ] = $item_srv[ 'stato_by_srv' ] ;
    $item[ 'stato'            ] = $item_srv[ 'stato'        ] ;
    $item[ 'code'             ] = $item_srv[ 'code'         ] ;
    $item[ 'name'             ] = $item_srv[ 'name'         ] ;

    $item[ 'status'           ] = $stato_by_app     ; 
    $item[ 'stato_by_app'     ] = $stato_by_app     ;
    $item[ 'uda_id_by_app'  ] = $uda_id_by_app  ;
    $item[ 'completed_by_app' ] = $completed_by_app ;

    // Return value.
    return $item;

    }//LUDA_API_GET_Minus_one_01

?>