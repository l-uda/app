<?php
session_start();
require_once( "../../../luda_include_config.php"    );
require_once( "../../../luda_include_functions.php" );
require_once( "../../../luda_include_functions_apps.php" );
require_once( "../../../luda_include_functions_udas.php" );
require_once( "../../../luda_class_db.php"          );
require_once( "../../../luda_class_config.php"      );
require_once( "../../../luda_class_api.php"         );
//isset($_GET['status']) ? header( "Location: luda_status_index.php" ) : "";
?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="it" xmlns="http://www.w3.org/1999/xhtml">
<head>
<?php LUDA_HTML_HEAD_Metas_Print   (); ?>
<?php 
//LUDA_HTML_HEAD_Styles_Print  (); 
//$wwwpath = LUDA_WWW_Site_Path_Get_01();

$wwwpath = "../../../";

$url = $wwwpath . "css/bootstrap.min.css";
echo "<link rel='stylesheet' type='text/css' href='" .$url. "' >\n";
$url = $wwwpath . "css/luda_style.css";
echo "<link rel='stylesheet' type='text/css' href='" .$url. "' >\n";
?>
    
<?php LUDA_HTML_HEAD_Scripts_Init  (); 

?>
<?php 
// LUDA_HTML_HEAD_Scripts_Print (); 
//$wwwpath = LUDA_WWW_Site_Path_Get_01();
$wwwpath =  "../../../";

$url = $wwwpath . "js/jquery-3.4.1.min.js";    
echo "<script type='text/javascript' src='" .$url. "'></script>";

$url = $wwwpath . "js/popper.min.js";    
echo "<script type='text/javascript' src='" .$url. "'></script>";

$url = $wwwpath . "js/bootstrap.min.js";    
echo "<script type='text/javascript' src='" .$url. "'></script>";

$url = $wwwpath . "js/luda_server.js";    
echo "<script type='text/javascript' src='" .$url. "'></script>";

?>
<script type="text/javascript" > function Body_Loaded() { if(0)Body_Loaded_Index(); } </script>
<script>
</script>
</head>
    
    
<body style=' text-align:center; '>
  

<header>
<?php
// Mostra il NavBar Menu SE in debug
if( $prm_d == "TRUE" )
    {
    $g_bDebug = true;
    LUDA_HTML_HEADER_Navbar_Print();
    }
?>
</header>

<?php $wwwpath = LUDA_WWW_Site_Path_Get_01(); ?>
<CENTER >
<main role="main" class='container' >
<CENTER >
<div class='row' style=' margin:1px; padding:30px; '>
<CENTER >
<div class=" col-12 " style=" background-color:#80FF80; border-radius: 50px 20px; display:inline-block; qqqheight:80%; margin:1px; padding:30px; " >
    
    <DIV class='row' class=" col-12 " >
    <DIV class=' ' class=" col-12 " >
    <FORM method='get' action='../message/' target='_blank'>
        Come ti chiami?
        <br>
        <input id='name' name='name' type='text' style=' display:block; width:400px; QQQheight:300px; 'value='Beethoven' >
        <br>
        <br>
        <br>
        Ti e' piaciuto giocare con noi?
        <br>
        <textarea id='text' name='text' class=' col-12' style=' display:block; width:400px; height:300px; '
>Moltissimo!
Grazie ed alla prossima Avventura!
Topolino e Minnie!</textarea>
            <br>
                <input type='submit' value='Invia il tuo messaggio...' >
    </FORM>
    </DIV>
    </DIV>
    
</div>
</CENTER>
</div>
</CENTER>
</main>
</CENTER>
    
</body>
</html>