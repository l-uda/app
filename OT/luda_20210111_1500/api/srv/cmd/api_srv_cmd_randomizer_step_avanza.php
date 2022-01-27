<?php
session_start();

echo "Continua...: ";

echo "<A href='../../../luda_game_next.php' >luda_game_next.php</A>";

//echo "<BR>EXITED!!!<BR>";

exit();


$par_path = $_GET['path'];

require_once( $par_path . "luda_include_config.php"    );
require_once( $par_path . "luda_include_functions.php" );
require_once( $par_path . "luda_class_db.php"          );
require_once( $par_path . "luda_class_config.php"      );
require_once( $par_path . "luda_class_api.php"         );

// APP CompletedList Clear
require_once( $par_path . "./api/srv/cmd/api_srv_cmd_game_functions.php" );

LUDA_API_SRV_CMD_Game_Randomization_GotoNextStep_01();

echo "<HR>";
echo "<BR>";
echo "<BR>";
echo "<A href='" .$par_path. "index.php' >Continua!</A>";

exit();

?>