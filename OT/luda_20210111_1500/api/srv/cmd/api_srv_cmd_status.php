<?php
//session_start();
//require_once( "./luda_include_config.php"    );
//require_once( "./luda_include_functions.php" );
//require_once( "./luda_class_db.php"          );
//require_once( "./luda_class_api.php"         );

 

function LUDA_API_SRV_CMD_Status_Get_02( )
    {
    $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_SRV;
    $l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_PUT;
    $oAPI = new cLUDA_API( $l_sTipo );
    $numSrv = 1;
    $stato = $oAPI->Get_02( $numSrv );

    //echo "GAME::API::SRV::INIT";
    $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_COD;
    $l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_GNP;

    $oAPI = new cLUDA_API( $l_sTipo );

    //echo "Status... (for Code: " . $stato . ")";
    //echo "<BR>";

    $status_code = $stato;
    $status_item = $oAPI->Stato_GetNomeByCodice_01( $status_code );
    $status_name = $status_item[ 'nome' ];

    if( 1 ){}
    else
    {
    echo "<SPAN style='border:1px solid black' >";
    echo "Il SERVER si trova nello stato: ";
    echo " <B>" . $status_name . "</B>";
    echo " (codice <B>" . $status_code . "</B>)";
    echo "</SPAN>";
    echo "<BR>";
    echo "<BR>";
    }
    
    $item[ 'stato_by_srv' ] = $status_code;
    $item[ 'stato'        ] = $status_code;
    $item[ 'code'         ] = $status_code;
    $item[ 'name'         ] = $status_name;
 
    
    
if( 1 ){} else
{
echo "<PRE>";
print_r( $item );
echo "</PRE>";
echo "<br>EXIT49<br>";
exit();
}


    // Return value.
    return $item;
    
    }//LUDA_API_SRV_CMD_Status_Get_02
 
    



function LUDA_API_SRV_CMD_Status_01( )
    {
    $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_SRV;
    $l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_PUT;
    $oAPI = new cLUDA_API( $l_sTipo );
    $numSrv = 1;
    $stato = $oAPI->Get_02( $numSrv );

    //echo "GAME::API::SRV::INIT";
    $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_COD;
    $l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_GNP;

    $oAPI = new cLUDA_API( $l_sTipo );

    //echo "Status... (for Code: " . $stato . ")";
    //echo "<BR>";

    $status_code = $stato;
    $status_item = $oAPI->Stato_GetNomeByCodice_01( $status_code );
    $status_name = $status_item[ 'nome' ];

    echo "<SPAN style='border:1px solid black' >";
    echo "Il SERVER si trova nello stato: ";
    echo " <B>" . $status_name . "</B>";
    echo " (codice <B>" . $status_code . "</B>)";
    echo "</SPAN>";
    echo "<BR>";
    echo "<BR>";
    }//LUDA_API_SRV_CMD_Status_01
 
    

function LUDA_API_APP_CMD_Status_01( )
    {
    $loDB = new cLUDA_DB();
    $l_sTableName = "luda_server_stati_app";
    $query = "SELECT * FROM " . $l_sTableName;
    $result = $loDB->Query_01($query);
    $num_records = $loDB->RecordCount_01();
    $columns_name = $loDB->ColumnsName_01();
    //var_dump( $columns_name );
    //exit();

    //echo "Tabella <b>'" .$l_sTableName. "'</b> &bull; Numero di records = <b>[" . $num_records . "]</b><br>";

    /* Select queries return a resultset */
    if ( ! is_null($result) ) 
        {
        //printf( "Il recordset contiene [%d] righe.\n", $num_records );
        echo "<TABLE border='1' >";

        // Header.
        $line  = "";
        $line .= "<TR class='cl_table_header_columns_name' >";
        foreach( $columns_name as $col_num => $col_name )
            {
            $line .= "<TD >" .$col_name. "</TD>";
            }
        $line .= "</TR>";
        //echo $line;

        // Data.
        while( $obj = $loDB->Fetch_01() )
            {//recordset_strt
            //var_dump( $obj );
            $line ="";
            $line.="<TR >";
            foreach( $columns_name as $col_num => $col_name )
                {
                $line .= "<TD >" . $obj->$col_name . "</TD>";
                }
            $line.="</TR>";
            //echo $line;
            $app_id   = $obj->idapp;
            $app_status_code      = $obj->stato_by_app     ;
$app_status_data      = $obj->uda_id_by_app  ;
$app_status_data      = $obj->data_by_app  ;
            $app_status_completed = $obj->completed_by_app ;

            $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_APP;
            $l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_GET;
            $oAPI = new cLUDA_API( $l_sTipo );
            $status_code = $app_status_code;
            $status_item = $oAPI->Stato_GetNomeByCodice_01( $app_status_code );
            $status_name = $status_item[ 'nome' ];
            $app_status_name = $status_name;

            
//require_once( "./app/cmd/api_app_cmd_completedlist_get.php" );
//$item = LUDA_API_GET_Minus_one_01();
//$item = LUDA_API_APP_CMD_CompletedList_Get_01();
//return $item;
            
$app_completed_list_str = "1,2,3,147";   
$app_completed_list_str = $app_status_completed;   
            
            echo "<TR >";
                echo "<TD colspan='100%' >";
                echo "L'APP con ID n.<B>" .$app_id. "</B>";
                echo "<UL>";
                echo "<LI>si trova nello stato: <b>" .$app_status_name. "</b> (codice <B>" .$app_status_code. "</B>)</LI>";
                echo "<LI>l'ultimo DATA_SENT risulta: [<B>" .$app_status_data. "</B>]</LI>";
                echo "<LI>ha completato le UDA n.: [<B>" .$app_completed_list_str. "</B>]</LI>";
                echo "</UL>";
                echo "</TD>";
            echo "</TR>";

            }//recordset_stop
        echo "</TABLE>";
        }//if_result

    echo "<BR>";

    }//LUDA_API_APP_CMD_Status_01

    

    
    
function LUDA_API_UDA_CMD_Status_01( )
    {
    $loDB = new cLUDA_DB();
    $l_sTableName = "luda_server_stati_uda";
    $query = "SELECT * FROM " . $l_sTableName;
    $result = $loDB->Query_01($query);
    $num_records = $loDB->RecordCount_01();
    $columns_name = $loDB->ColumnsName_01();
    //var_dump( $columns_name );
    //exit();

    //echo "Tabella <b>'" .$l_sTableName. "'</b> &bull; Numero di records = <b>[" . $num_records . "]</b><br>";

    /* Select queries return a resultset */
    if ( ! is_null($result) ) 
        {
        //printf( "Il recordset contiene [%d] righe.\n", $num_records );
        echo "<TABLE border='1' >";

        // Header.
        $line  = "";
        $line .= "<TR class='cl_table_header_columns_name' >";
        foreach( $columns_name as $col_num => $col_name )
            {
            $line .= "<TD >" .$col_name. "</TD>";
            }
        $line .= "</TR>";
        //echo $line;

        // Data.
        while( $obj = $loDB->Fetch_01() )
            {//recordset_strt
            //var_dump( $obj );
            $line ="";
            $line.="<TR >";
            foreach( $columns_name as $col_num => $col_name )
                {
                $line .= "<TD >" . $obj->$col_name . "</TD>";
                }
            $line.="</TR>";
            //echo $line;
            $uda_id              = $obj->iduda;
            $uda_status_code     = $obj->stato_by_uda;
            $uda_status_group_id = $obj->app_id_by_uda;

            $l_sTipo     = LUDA_CONSTANT_SERVER_TIPO_UDA;
            $l_sFunzione = LUDA_CONSTANT_SERVER_FUNZIONE_GET;
            $oAPI = new cLUDA_API( $l_sTipo );
            $status_code = $uda_status_code;
            $status_item = $oAPI->Stato_GetNomeByCodice_01( $uda_status_code );
            $status_name = $status_item[ 'nome' ];
            $uda_status_name = $status_name;

            echo "<TR >";
                echo "<TD colspan='100%' >";
                echo "L'UDA con ID n.<B>" .$uda_id. "</B>";
                echo "<UL>";
                echo "<LI>si trova nello stato <b>" .$uda_status_name. "</b> (codice <B>" .$uda_status_code. "</B>)</LI>"; 
// echo " e l'ultimo DATA_SENT risulta [<B>" .$app_status_data. "</B>]";
                echo "</UL>";
                echo "</TD>";
            echo "</TR>";

            }//recordset_stop
        echo "</TABLE>";
        }//if_result

    echo "<BR>";

    }//LUDA_API_UDA_CMD_Status_01
  
    
    
?>