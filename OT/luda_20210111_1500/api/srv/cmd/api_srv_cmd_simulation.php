<?php
session_start();
require_once( "./luda_include_config.php"    );
require_once( "./luda_include_functions.php" );
require_once( "./luda_class_db.php"          );
require_once( "./luda_class_config.php"      );
require_once( "./luda_class_api.php"         );
//isset($_GET['status']) ? header( "Location: luda_status_index.php" ) : "";
?>    
<?php
/*
 * Dalla email di Alberto Inuggi del 2021-02-03_16:37 

rispondo solo a te

https://www.sagosoft.it/_API_/cpim/luda/www/luda_20210111_1500//api/app/get/?i=-1

i    =  - 1

torna ancora status null
deve tornare lo status dell'intera installazione. 

forse non lo abbiamo esplicitato completamente,
puo essere

IDLE                     server acceso, nessuna sessione in corso

WAIT_APP           almeno un gruppo non si è ancora registrato

READY                 tutte le app registrate, tutti i gruppi davanti alla propria uda, pronti per iniziare

STARTED             uda in corso

PAUSED               pausa globale

FINALIZED           gruppi davanti al portale (credo)

i = - 1   viene chiamato da ciascuna app quando vuole registrari e non ha ancora un id confermato

*/

//LUDA_API_SRV_CMD_Simulation_01();

function LUDA_API_SRV_CMD_Simulation_01( )
    {

    
    // Object.
    $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_SRV;
    $l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_GNP;
    $oAPI = new cLUDA_API( $l_sTipo );
    $g_aServerStatusSimulation_cont = 0;
    $g_aServerStatusSimulation_items = Array( );


    // SIMULAZIONE: IDLE
    $status_name = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_IDLE;
    $status_item = $oAPI->Stato_GetCodiceByNome_01( $status_name );
    //echo "<PRE>";
    //print_r( $status_item );
    //echo "</PRE>";
    //exit();
    for( $i=1; $i<=3; $i++ )
        { $g_aServerStatusSimulation_items[] = $status_item; }

    // SIMULAZIONE: WAIT_APP
    $status_name = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_WAIT_APP;
    $status_item = $oAPI->Stato_GetCodiceByNome_01( $status_name );
    //var_dump( $status_item );
    //exit();
    for( $i=1; $i<=3; $i++ )
        { $g_aServerStatusSimulation_items[] = $status_item; }

    // SIMULAZIONE: READY
    $status_name = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_READY;
    $status_item = $oAPI->Stato_GetCodiceByNome_01( $status_name );
    //exit();
    for( $i=1; $i<=3; $i++ )
        { $g_aServerStatusSimulation_items[] = $status_item; }

    // SIMULAZIONE: STARTED
    $status_name = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_STARTED;
    $status_item = $oAPI->Stato_GetCodiceByNome_01( $status_name );
    //exit();
    for( $i=1; $i<=3; $i++ )
        { $g_aServerStatusSimulation_items[] = $status_item; }

    // SIMULAZIONE: PAUSED
    $status_name = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_PAUSED;
    $status_item = $oAPI->Stato_GetCodiceByNome_01( $status_name );
    //exit();
    for( $i=1; $i<=3; $i++ )
        { $g_aServerStatusSimulation_items[] = $status_item; }

    // SIMULAZIONE: FINALIZED
    $status_name = LUDA_CONSTANT_COMMAND_API_COD_GNP_NAME_FINALIZED;
    $status_item = $oAPI->Stato_GetCodiceByNome_01( $status_name );
    //exit();
    for( $i=1; $i<=3; $i++ )
        { $g_aServerStatusSimulation_items[] = $status_item; }


if( 1 ) {} else
{
echo "<HR>";
echo "ARRAY_DI_SIMULAZIONE<BR>";
echo "<PRE>";
var_dump( $g_aServerStatusSimulation_items );         
echo "</PRE>";
echo "<HR>";
echo "STEP110";
exit(); 
}

$_SESSION['SRV_STAT_SIM_CONT'] = isset($_SESSION['SRV_STAT_SIM_CONT']) ? $_SESSION['SRV_STAT_SIM_CONT'] : 0;
//if(  )


echo "SESSION_CNT = [" . $_SESSION['SRV_STAT_SIM_CONT']. "]<BR>";

    {
    $status_code = $g_aServerStatusSimulation_items[ $_SESSION['SRV_STAT_SIM_CONT'] ]['codice'];
$status_item = $oAPI->Stato_GetNomeByCodice_01( $status_code );
$status_name = $status_item[ 'nome' ];

echo "status_code: " . $status_code . "<BR>";
echo "status_name: " . $status_name . "<BR>";
  
echo "<BR>";

//echo "STEP126";
// exit(); 



$l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_SRV;
$l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_PUT;
$oAPI = new cLUDA_API( $l_sTipo );
$numSrv = 1;
$stato = $oAPI->Put_02( $numSrv, $status_code );
    }
//else
    {
    //$_SESSION[ 'SRV_STAT_SIM_CONT' ] = 0;
    }
        
//echo "<PRE>";
    //var_dump( $g_aServerStatusSimulation_data );
//echo "</PRE>";

    /*
echo "<BR>";
echo "<BR>";
    echo "<A href='index.php' >Continua!</A>";
echo "<BR>";
echo "<BR>";
*/
    //exit();


//$g_aServerStatusSimulation_cont++;

$_SESSION[ 'SRV_STAT_SIM_CONT' ] ++;
if( $_SESSION['SRV_STAT_SIM_CONT'] >= sizeof($g_aServerStatusSimulation_items) ) { $_SESSION['SRV_STAT_SIM_CONT'] = 0; }

    }//LUDA_API_SRV_CMD_Simulation_01



?>