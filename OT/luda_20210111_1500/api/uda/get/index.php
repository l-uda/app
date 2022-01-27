<?php
//echo "API::UDA::GET<BR>";
session_start();
require_once( "../../../luda_include_config.php"    );
require_once( "../../../luda_include_functions.php" );
require_once( "../../../luda_class_db.php"          );
require_once( "../../../luda_class_api.php"         );

// Parameters.
$par_iI   = LUDA_API_Parameters_GetFromREST( $_GET, 'i'    );
$par_iK   = LUDA_API_Parameters_GetFromREST( $_GET, 'k'    );
$par_data = LUDA_API_Parameters_GetFromREST( $_GET, 'data' );

// Object.
$oAPI = new cLUDA_API( LUDA_CONSTANT_SERVER_TIPO_UDA );
$item = $oAPI->Get_03( $par_iI );

// Values.
$aJsonAPI = ARRAY();
$aJsonAPI[ 'type'         ] = LUDA_CONSTANT_SERVER_TIPO_UDA;
$aJsonAPI[ 'function'     ] = LUDA_CONSTANT_SERVER_FUNZIONE_GET;
$aJsonAPI[ 'i'            ] = $par_iI;
$aJsonAPI[ 'k'            ] = $par_iK;
$aJsonAPI[ 'status'       ] = $item[ 'stato_by_uda'  ];
$aJsonAPI[ 'data'         ] = $item[ 'data_by_uda'   ];
$aJsonAPI[ 'app_id'       ] = $item[ 'app_id_by_uda' ];
$aJsonAPI[ 'error'        ] = NULL ;
$aJsonAPI[ 'api'          ] = "API";
$aJsonAPI[ 'version'      ] = "3.3";
$aJsonAPI[ 'subversion'   ] = "20210111";
$aJsonAPI[ 'php_self'     ] = $_SERVER['PHP_SELF'];

// Return values.
$sJsonAPI = json_encode( $aJsonAPI );
echo $sJsonAPI;
?>