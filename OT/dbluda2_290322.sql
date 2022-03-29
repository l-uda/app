-- phpMyAdmin SQL Dump
-- version 5.1.0
-- https://www.phpmyadmin.net/
--
-- Versione del server: 5.0.92-50-log
-- Versione PHP: 8.0.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `dbluda2`
--

DELIMITER $$
--
-- Procedure
--
DROP PROCEDURE IF EXISTS `get_explorers_by_uda`$$
CREATE DEFINER=`dbluda2`@`%` PROCEDURE `get_explorers_by_uda` (IN `udaid` INT UNSIGNED)  SELECT idexplorer_in_app FROM luda_server_esploratori_per_gruppo WHERE idapp = (SELECT app_id_by_uda FROM luda_server_stati_uda WHERE iduda = udaid)$$

DROP PROCEDURE IF EXISTS `get_explorer_status_data`$$
CREATE DEFINER=`dbluda2`@`%` PROCEDURE `get_explorer_status_data` (IN `idapp` INT, IN `explorer` INT)  NO SQL
IF  explorer = -1 THEN 

    SELECT luda_server_esploratori_per_gruppo.stato_by_subgrp_app, luda_server_esploratori_per_gruppo.data_by_subgrp_app FROM luda_server_esploratori_per_gruppo WHERE luda_server_esploratori_per_gruppo.idapp = idapp LIMIT 1;
    
ELSE 

    SELECT luda_server_esploratori_per_gruppo.stato_by_subgrp_app, luda_server_esploratori_per_gruppo.data_by_subgrp_app FROM luda_server_esploratori_per_gruppo WHERE luda_server_esploratori_per_gruppo.idapp = idapp AND luda_server_esploratori_per_gruppo.explorer = explorer;
END IF$$

DROP PROCEDURE IF EXISTS `put_explorer_status_by_uda`$$
CREATE DEFINER=`dbluda2`@`%` PROCEDURE `put_explorer_status_by_uda` (IN `status` INT, IN `iduda` INT)  MODIFIES SQL DATA
UPDATE luda_server_esploratori_per_gruppo 
SET stato_by_subgrp_app = status 
WHERE idexplorer_in_app IN 
    (SELECT idexplorer_in_app 
     FROM 
        (
         SELECT idexplorer_in_app 
         FROM luda_server_esploratori_per_gruppo
        ) AS something 
         WHERE idapp = 
            (SELECT luda_server_stati_uda.app_id_by_uda 
             FROM luda_server_stati_uda 
             WHERE luda_server_stati_uda.iduda = iduda
            )
    )$$

DROP PROCEDURE IF EXISTS `set_materia_by_id`$$
CREATE DEFINER=`dbluda2`@`%` PROCEDURE `set_materia_by_id` (IN `idmateria` INT)  MODIFIES SQL DATA
BEGIN
	UPDATE luda_server_materie SET luda_server_materie.posizione = 0 WHERE luda_server_materie.posizione > 0;
    
	UPDATE luda_server_materie SET luda_server_materie.posizione = 1 WHERE luda_server_materie.idmateria = idmateria;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Struttura della tabella `luda_server_api_codici`
--

DROP TABLE IF EXISTS `luda_server_api_codici`;
CREATE TABLE `luda_server_api_codici` (
  `tipo` varchar(3) NOT NULL,
  `codice` int(11) NOT NULL,
  `funzione` varchar(3) NOT NULL,
  `num_parametri` int(11) NOT NULL COMMENT 'Numero di parametri di questa API',
  `num_ritorni` int(11) NOT NULL DEFAULT '-1' COMMENT 'Numero di valori di ritorno di questa API',
  `nome` varchar(16) NOT NULL COMMENT 'Nome (GET oppure PUT) di questa API.',
  `descrizione` varchar(256) NOT NULL,
  `note` text
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='Tabella dei Codici+Tipo (=PK) per le API';

--
-- Dump dei dati per la tabella `luda_server_api_codici`
--

INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 0, 'GNP', 0, -1, 'IDLE', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 1, 'GNP', 0, -1, 'WAIT_APP', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 2, 'GNP', 0, -1, 'GROUP_SENT', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 3, 'GNP', 0, -1, 'REACH_UDA', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 4, 'GNP', 0, -1, 'REACHING_UDA', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 5, 'GNP', 0, -1, 'READY', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 6, 'GNP', 0, -1, 'START', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 7, 'GNP', 0, -1, 'STARTED', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 8, 'GNP', 0, -1, 'PAUSE', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 9, 'GNP', 0, -1, 'PAUSED', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 10, 'GNP', 0, -1, 'RESUME', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 11, 'GNP', 0, -1, 'ABORT', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 12, 'GNP', 0, -1, 'ABORTED', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 13, 'GNP', 0, -1, 'RESTART', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 14, 'GNP', 0, -1, 'WAIT_DATA', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 15, 'GNP', 0, -1, 'DATA_SENT', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 16, 'GNP', 0, -1, 'COMPLETED', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 17, 'GNP', 0, -1, 'FINALIZE', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 18, 'GNP', 0, -1, 'FINALIZED', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 20, 'GNP', 0, -1, 'ERROR_UDA', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 21, 'GNP', 0, -1, 'ERROR_APP', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 22, 'GNP', 0, -1, 'ERROR_SERVER', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 0, 'GET', 1, 1, 'IDLE', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 1, 'GET', 1, 1, 'WAIT_APP', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 2, 'PUT', 2, 0, 'GROUP_SENT', '', 'parameters(id,status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 3, 'GET', 1, 2, 'REACH_UDA', '', 'returns(status,data)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 4, 'PUT', 2, 0, 'REACHING_UDA', '', 'parameters(id,status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 5, 'GET', 1, 1, 'READY', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 6, 'NOP', -1, -1, 'START', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 7, 'GET', 1, 1, 'STARTED', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 8, 'PUT', 2, 0, 'PAUSE', '', 'parameters(id,status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 9, 'GET', 1, 1, 'PAUSED', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 10, 'PUT', 2, 0, 'RESUME', '', 'parameters(id,status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 11, 'PUT', 2, 0, 'ABORT', '', 'parameters(id,status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 12, 'GET', 1, 1, 'ABORTED', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 13, 'PUT', 2, 0, 'RESTART', '', 'parameters(id,status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 14, 'GET', 1, 1, 'WAIT_DATA', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 15, 'PUT', 3, 0, 'DATA_SENT', '', 'parameters(id,status,data)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 16, 'GET', 1, 1, 'COMPLETED', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 17, 'NOP', -1, -1, 'FINALIZE', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 18, 'GET', 1, 1, 'FINALIZED', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 20, 'NOP', -1, -1, 'ERROR_UDA', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 21, 'NOP', -1, -1, 'ERROR_APP', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 22, 'NOP', -1, -1, 'ERROR_SERVER', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 0, 'GET', 1, 1, 'IDLE', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 1, 'GET', 1, 1, 'WAIT_APP', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 2, 'NOP', -1, -1, 'GROUP_SENT', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 3, 'NOP', -1, -1, 'REACH_UDA', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 4, 'NOP', -1, -1, 'REACHING_UDA', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 5, 'GET', 1, 1, 'READY', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 6, 'GET', 1, 1, 'START', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 7, 'PUT', 2, 0, 'STARTED', '', 'parameters(id,status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 8, 'GET', 1, 1, 'PAUSE', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 9, 'PUT', 2, 0, 'PAUSED', '', 'parameters(id,status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 10, 'GET', 1, 1, 'RESUME', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 11, 'GET', 1, 1, 'ABORT', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 12, 'PUT', 2, 0, 'ABORTED', '', 'parameters(id,status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 13, 'GET', 1, 1, 'RESTART', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 14, 'PUT', 2, 0, 'WAIT_DATA', '', 'parameters(id,status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 15, 'GET', 1, 2, 'DATA_SENT', '', 'returns(status,data)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 16, 'PUT', 2, 0, 'COMPLETED', '', 'parameters(id,status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 17, 'GET', 1, 1, 'FINALIZE', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 18, 'PUT', 2, 0, 'FINALIZED', '', 'parameters(id,status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 20, 'NOP', -1, -1, 'ERROR_UDA', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 21, 'NOP', -1, -1, 'ERROR_APP', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('UDA', 22, 'NOP', -1, -1, 'ERROR_SERVER', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 0, 'NOP', -1, -1, 'IDLE', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 1, 'NOP', -1, -1, 'WAIT_APP', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 2, 'NOP', -1, -1, 'GROUP_SENT', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 3, 'NOP', -1, -1, 'REACH_UDA', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 4, 'NOP', -1, -1, 'REACHING_UDA', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 5, 'NOP', -1, -1, 'READY', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 6, 'NOP', -1, -1, 'START', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 7, 'NOP', -1, -1, 'STARTED', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 8, 'NOP', -1, -1, 'PAUSE', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 9, 'NOP', -1, -1, 'PAUSED', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 10, 'NOP', -1, -1, 'RESUME', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 11, 'NOP', -1, -1, 'ABORT', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 12, 'NOP', -1, -1, 'ABORTED', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 13, 'NOP', -1, -1, 'RESTART', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 14, 'NOP', -1, -1, 'WAIT_DATA', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 15, 'NOP', -1, -1, 'DATA_SENT', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 16, 'NOP', -1, -1, 'COMPLETED', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 17, 'NOP', -1, -1, 'FINALIZE', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 18, 'NOP', -1, -1, 'FINALIZED', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 20, 'NOP', -1, -1, 'ERROR_UDA', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 21, 'NOP', -1, -1, 'ERROR_APP', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 22, 'NOP', -1, -1, 'ERROR_SERVER', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', -1, 'GET', 1, 1, 'INIT', '', 'returns(status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', 19, 'GNP', 0, -1, 'WAIT_SERVER', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('COD', -1, 'GNP', 0, -1, 'INIT', '', NULL);
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('SRV', 19, 'PUT', 2, 0, 'WAIT_SERVER', '', 'parameters(id,status)');
INSERT INTO `luda_server_api_codici` (`tipo`, `codice`, `funzione`, `num_parametri`, `num_ritorni`, `nome`, `descrizione`, `note`) VALUES('APP', 5, 'PUT', 2, 0, 'READY', '', 'parameters(id,status)');

-- --------------------------------------------------------

--
-- Struttura della tabella `luda_server_configs`
--

DROP TABLE IF EXISTS `luda_server_configs`;
CREATE TABLE `luda_server_configs` (
  `idconfig` int(11) NOT NULL,
  `name` varchar(64) NOT NULL,
  `value` varchar(256) NOT NULL,
  `description` text
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `luda_server_configs`
--

INSERT INTO `luda_server_configs` (`idconfig`, `name`, `value`, `description`) VALUES(1, 'LUDA_SERVER_MASTER_BUTTONS_LINK', 'BUTTON', 'ANCHOR oppure BUTTON');
INSERT INTO `luda_server_configs` (`idconfig`, `name`, `value`, `description`) VALUES(2, 'LUDA_SERVER_MASTER_GAME_STEPS_QTY', '5', 'Numero di \'steps\' (ovvero quanti Games ciascun gruppo deve eseguire) in una Sessione di gioco.');
INSERT INTO `luda_server_configs` (`idconfig`, `name`, `value`, `description`) VALUES(3, 'LUDA_SERVER_MASTER_GAME_STEP_CURR', '147', 'OBSOLETED - Numero \'ordinale\' del Game \'corrente\'.');

-- --------------------------------------------------------

--
-- Struttura della tabella `luda_server_esploratori_per_gruppo`
--

DROP TABLE IF EXISTS `luda_server_esploratori_per_gruppo`;
CREATE TABLE `luda_server_esploratori_per_gruppo` (
  `idexplorer_in_app` int(11) NOT NULL,
  `idapp` int(11) NOT NULL,
  `explorer` int(11) NOT NULL,
  `stato_by_subgrp_app` int(11) NOT NULL DEFAULT '0',
  `data_by_subgrp_app` varchar(1000) NOT NULL DEFAULT ''
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `luda_server_esploratori_per_gruppo`
--


--
-- Struttura della tabella `luda_server_indizi`
--

DROP TABLE IF EXISTS `luda_server_indizi`;
CREATE TABLE `luda_server_indizi` (
  `idindizio` int(11) NOT NULL,
  `simbolo` int(11) NOT NULL DEFAULT '-1' COMMENT 'Da 0 a 4',
  `gridx` int(11) NOT NULL DEFAULT '-1',
  `gridy` int(11) NOT NULL DEFAULT '-1',
  `rotazione` int(11) NOT NULL DEFAULT '-1' COMMENT '0 oppure 1',
  `mirror` int(11) NOT NULL DEFAULT '-1' COMMENT '0 oppure 1'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `luda_server_indizi`
--

INSERT INTO `luda_server_indizi` (`idindizio`, `simbolo`, `gridx`, `gridy`, `rotazione`, `mirror`) VALUES(1, 2, 3, 4, 1001, 1010);
INSERT INTO `luda_server_indizi` (`idindizio`, `simbolo`, `gridx`, `gridy`, `rotazione`, `mirror`) VALUES(2, 3, 4, 5, 2001, 2010);
INSERT INTO `luda_server_indizi` (`idindizio`, `simbolo`, `gridx`, `gridy`, `rotazione`, `mirror`) VALUES(3, 4, 5, 1, 3001, 3010);
INSERT INTO `luda_server_indizi` (`idindizio`, `simbolo`, `gridx`, `gridy`, `rotazione`, `mirror`) VALUES(4, 5, 1, 2, 4001, 4010);
INSERT INTO `luda_server_indizi` (`idindizio`, `simbolo`, `gridx`, `gridy`, `rotazione`, `mirror`) VALUES(5, 1, 2, 3, 5001, 5010);

-- --------------------------------------------------------

--
-- Struttura della tabella `luda_server_materie`
--

DROP TABLE IF EXISTS `luda_server_materie`;
CREATE TABLE `luda_server_materie` (
  `idmateria` int(11) NOT NULL,
  `posizione` int(11) NOT NULL DEFAULT '0' COMMENT '1 significa materia corrente',
  `nome` varchar(100) NOT NULL DEFAULT 'Materia X',
  `descrizione` varchar(100) NOT NULL DEFAULT 'Descrizione X',
  `has_subgroups` int(11) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `luda_server_materie`
--

INSERT INTO `luda_server_materie` (`idmateria`, `posizione`, `nome`, `descrizione`, `has_subgroups`) VALUES(1, 0, 'Matematica', 'La materia Matematica blah, blah, blah...', 0);
INSERT INTO `luda_server_materie` (`idmateria`, `posizione`, `nome`, `descrizione`, `has_subgroups`) VALUES(2, 0, 'Scienze', 'La materia Scienze blah, blah, blah...', 0);
INSERT INTO `luda_server_materie` (`idmateria`, `posizione`, `nome`, `descrizione`, `has_subgroups`) VALUES(3, 1, 'Storia', 'La materia Storia blah, blah, blah', 0);
INSERT INTO `luda_server_materie` (`idmateria`, `posizione`, `nome`, `descrizione`, `has_subgroups`) VALUES(4, 0, 'Inglese', 'La materia Inglese blah, blah, blah', 0);
INSERT INTO `luda_server_materie` (`idmateria`, `posizione`, `nome`, `descrizione`, `has_subgroups`) VALUES(5, 0, 'Italiano', 'La materia Italiano blah, blah, blah', 0);
INSERT INTO `luda_server_materie` (`idmateria`, `posizione`, `nome`, `descrizione`, `has_subgroups`) VALUES(6, 0, 'Storia', 'La materia Storia blah, blah, blah', 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `luda_server_stati_app`
--

DROP TABLE IF EXISTS `luda_server_stati_app`;
CREATE TABLE `luda_server_stati_app` (
  `idapp` int(11) NOT NULL,
  `codice` varchar(16) DEFAULT '2BDEF',
  `descrizione` varchar(64) NOT NULL,
  `uda_id_by_app` int(11) NOT NULL DEFAULT '-1',
  `note` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `luda_server_stati_app`
--

INSERT INTO `luda_server_stati_app` (`idapp`, `codice`, `descrizione`, `uda_id_by_app`, `note`) VALUES(1, 'AP1', 'Software APP N.1', 1, 'Gestione del Software APP N.1');
INSERT INTO `luda_server_stati_app` (`idapp`, `codice`, `descrizione`, `uda_id_by_app`, `note`) VALUES(2, 'AP2', 'Software APP N.2', -1, 'Gestione del Software APP N.2');
INSERT INTO `luda_server_stati_app` (`idapp`, `codice`, `descrizione`, `uda_id_by_app`, `note`) VALUES(3, 'AP3', 'Software APP N.3', -1, 'Gestione del Software APP N.3');
INSERT INTO `luda_server_stati_app` (`idapp`, `codice`, `descrizione`, `uda_id_by_app`, `note`) VALUES(4, 'AP4', 'Software APP N.4', -1, 'Gestione del Software APP N.4');
INSERT INTO `luda_server_stati_app` (`idapp`, `codice`, `descrizione`, `uda_id_by_app`, `note`) VALUES(5, 'AP5', 'Software APP N.5', -1, 'Gestione del Software APP N.5');

-- --------------------------------------------------------

--
-- Struttura della tabella `luda_server_stati_srv`
--

DROP TABLE IF EXISTS `luda_server_stati_srv`;
CREATE TABLE `luda_server_stati_srv` (
  `idsrv` int(11) NOT NULL,
  `codice` varchar(16) DEFAULT '2BDEF',
  `descrizione` varchar(64) NOT NULL,
  `stato_by_srv` int(11) DEFAULT '0',
  `turno_corrente` int(11) NOT NULL DEFAULT '-1',
  `note` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `luda_server_stati_srv`
--

INSERT INTO `luda_server_stati_srv` (`idsrv`, `codice`, `descrizione`, `stato_by_srv`, `turno_corrente`, `note`) VALUES(1, 'SRV1', 'Master SRV N.1', 1, 1, 'Gestione del Master SRV N.1');

-- --------------------------------------------------------

--
-- Struttura della tabella `luda_server_stati_uda`
--

DROP TABLE IF EXISTS `luda_server_stati_uda`;
CREATE TABLE `luda_server_stati_uda` (
  `iduda` int(11) NOT NULL,
  `codice` varchar(16) DEFAULT '2BDEF',
  `descrizione` varchar(64) NOT NULL,
  `stato_by_uda` int(11) NOT NULL DEFAULT '0',
  `app_id_by_uda` int(11) NOT NULL DEFAULT '-1',
  `indizi` varchar(100) NOT NULL,
  `data_by_uda` varchar(1000) NOT NULL,
  `note` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `luda_server_stati_uda`
--

INSERT INTO `luda_server_stati_uda` (`iduda`, `codice`, `descrizione`, `stato_by_uda`, `app_id_by_uda`, `indizi`, `data_by_uda`, `note`) VALUES(1, 'UD1', 'Settaggi UDA N.1', 0, 1, '0', '-1', 'UDA N.1 (Walter)');
INSERT INTO `luda_server_stati_uda` (`iduda`, `codice`, `descrizione`, `stato_by_uda`, `app_id_by_uda`, `indizi`, `data_by_uda`, `note`) VALUES(2, 'UD2', 'Settaggi UDA N.2', 0, -1, '0', '-1', 'Gestione della UDA N.2');
INSERT INTO `luda_server_stati_uda` (`iduda`, `codice`, `descrizione`, `stato_by_uda`, `app_id_by_uda`, `indizi`, `data_by_uda`, `note`) VALUES(3, 'UD3', 'Settaggi UDA N.3', 0, -1, '0', '-1', 'Gestione della UDA N.3');
INSERT INTO `luda_server_stati_uda` (`iduda`, `codice`, `descrizione`, `stato_by_uda`, `app_id_by_uda`, `indizi`, `data_by_uda`, `note`) VALUES(4, 'UD4', 'Settaggi UDA N.4', 0, -1, '0', '-1', 'Gestione della UDA N.4');
INSERT INTO `luda_server_stati_uda` (`iduda`, `codice`, `descrizione`, `stato_by_uda`, `app_id_by_uda`, `indizi`, `data_by_uda`, `note`) VALUES(5, 'UD5', 'Settaggi UDA N.5', 0, -1, '0', '-1', 'UDA N.5 (Simone)');

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `luda_server_api_codici`
--
ALTER TABLE `luda_server_api_codici`
  ADD PRIMARY KEY (`tipo`,`codice`,`funzione`),
  ADD UNIQUE KEY `tipo` (`tipo`,`codice`,`funzione`);

--
-- Indici per le tabelle `luda_server_configs`
--
ALTER TABLE `luda_server_configs`
  ADD PRIMARY KEY (`idconfig`);

--
-- Indici per le tabelle `luda_server_esploratori_per_gruppo`
--
ALTER TABLE `luda_server_esploratori_per_gruppo`
  ADD PRIMARY KEY (`idexplorer_in_app`);

--
-- Indici per le tabelle `luda_server_indizi`
--
ALTER TABLE `luda_server_indizi`
  ADD PRIMARY KEY (`idindizio`);

--
-- Indici per le tabelle `luda_server_materie`
--
ALTER TABLE `luda_server_materie`
  ADD PRIMARY KEY (`idmateria`);

--
-- Indici per le tabelle `luda_server_stati_app`
--
ALTER TABLE `luda_server_stati_app`
  ADD PRIMARY KEY (`idapp`);

--
-- Indici per le tabelle `luda_server_stati_srv`
--
ALTER TABLE `luda_server_stati_srv`
  ADD PRIMARY KEY (`idsrv`);

--
-- Indici per le tabelle `luda_server_stati_uda`
--
ALTER TABLE `luda_server_stati_uda`
  ADD PRIMARY KEY (`iduda`);

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `luda_server_configs`
--
ALTER TABLE `luda_server_configs`
  MODIFY `idconfig` int(11) NOT NULL auto_increment, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT per la tabella `luda_server_esploratori_per_gruppo`
--
ALTER TABLE `luda_server_esploratori_per_gruppo`
  MODIFY `idexplorer_in_app` int(11) NOT NULL auto_increment, AUTO_INCREMENT=1;

--
-- AUTO_INCREMENT per la tabella `luda_server_indizi`
--
ALTER TABLE `luda_server_indizi`
  MODIFY `idindizio` int(11) NOT NULL auto_increment, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT per la tabella `luda_server_materie`
--
ALTER TABLE `luda_server_materie`
  MODIFY `idmateria` int(11) NOT NULL auto_increment, AUTO_INCREMENT=9;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
