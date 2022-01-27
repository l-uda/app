<?php
// API SRV MATERIE index.php ( 2021-12-23 )
/*
session_start();
require_once( "../../../luda_include_config.php"    );
require_once( "../../../luda_include_functions.php" );
require_once( "../../../luda_class_db.php"          );
require_once( "../../../luda_class_api.php"         );
*/ 
                                      

session_start();
require_once( "../../../luda_include_config.php"    );
require_once( "../../../luda_include_functions.php" );
require_once( "../../../luda_class_db.php"          );
require_once( "../../../luda_class_config.php"      );
require_once( "../../../luda_class_api.php"         );
require_once( "../../../luda_class_materie.php"      );


// Parameters.
//print_r( $_GET );
//echo "<BR>";
$par_iId        = LUDA_API_Parameters_GetFromREST( $_GET, 'id'        );
//$par_iPosizione = LUDA_API_Parameters_GetFromREST( $_GET, 'posizione' );

$oMaterie = new cLUDA_Materie();
$aMateria = $oMaterie->Get_FieldsArray_01( $par_iId ) ;
//var_dump( $aMateria );
//$materia_nome = $aMateria['nome'];


$par_get = isset( $_GET['get'] ) ? true : false ;

//exit();


// Object 'materia'
$oMateria = new cLUDA_Materie();
$aMaterie = Array();

// REST: "get"
if( $par_get == true )
    {
    for( $materia_id = 1; $materia_id <= 8; $materia_id++ )
        {//for_materia_strt
        $aArrayMateria = $oMateria->Get_FieldsArray_01( $materia_id );
        //var_dump( $aArrayMateria );
        //$aMaterie[] = $aArrayMateria; 
        if( $aArrayMateria['posizione'] == "1" )
            { 
            $aMaterie = Array();
            $aMaterie[ 'id'          ] = $aArrayMateria[ 'idmateria'] ;  
            $aMaterie[ 'nome'        ] = $aArrayMateria[ 'nome'] ;  
            $aMaterie[ 'posizione'        ] = $aArrayMateria[ 'posizione'] ;  
            $aMaterie[ 'descrizione' ] = $aArrayMateria[ 'descrizione'] ;  
            $sJsonAPI = json_encode( $aMaterie );
            echo $sJsonAPI;    
            exit();
            }//for_materia_stop
        // Return data.
        }
    // Return data.
    //$sJsonAPI = json_encode( $aMaterie );
    //echo $sJsonAPI;    
    exit();
    }




if( $par_iId <= 0 )
    {//if_materia_0_strt
    //echo "Parametro 'id' NON specificato tra i parametri GET!";    
    for( $materia_id = 1; $materia_id <= 8; $materia_id++ )
        {//for_materia_strt
        $aArrayMateria = $oMateria->Get_FieldsArray_01( $materia_id );
        //var_dump( $aArrayMateria );
        $aMaterie[] = $aArrayMateria;        
        }//for_materia_stop
        // Return data.
        $sJsonAPI = json_encode( $aMaterie );
        echo $sJsonAPI;    
        exit();
    }//if_materia_0_stop
    
    
    
// Materia indicata.
if( $par_iId <= 8 )
    {//if_materia_strt
    $aArrayMateria = $oMateria->Get_FieldsArray_01( $par_iId );
    //var_dump( $aArrayMateria );

    //if( ($par_iPosizione >= 1) && (($par_iPosizione <= 8)) )
        {
        //$data = $oMateria->Get_FieldFromArray( $aArrayMateria, $par_iPosizione );
        
        //$sJsonAPI = json_encode( $data );
        $sJsonAPI = json_encode( $aArrayMateria );
        //$sJsonAPI = json_encode( $aArrayMateria[$par_iPosizione] );
        
        echo $sJsonAPI;
        exit();
        }
    
    $sJsonAPI = json_encode( $aArrayMateria );
    echo $sJsonAPI;  
    exit();
    }//if_materia_stop
 
?>