CREATE DATABASE  IF NOT EXISTS `globaltrade_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `globaltrade_db`;
-- MySQL dump 10.13  Distrib 8.0.43, for macos15 (arm64)
--
-- Host: 127.0.0.1    Database: globaltrade_db
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `audit_logs`
--

DROP TABLE IF EXISTS `audit_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_logs` (
  `id` varchar(255) NOT NULL,
  `action` varchar(50) NOT NULL,
  `action_timestamp` datetime(6) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `entity_id` varchar(255) NOT NULL,
  `entity_type` varchar(100) NOT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_audit_user` (`user_id`),
  KEY `idx_audit_entity` (`entity_type`,`entity_id`),
  KEY `idx_audit_timestamp` (`action_timestamp`),
  CONSTRAINT `FK_audit_logs_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_logs`
--

LOCK TABLES `audit_logs` WRITE;
/*!40000 ALTER TABLE `audit_logs` DISABLE KEYS */;
INSERT INTO `audit_logs` VALUES ('00f1bff6-a5fa-11f1-913b-58733aca4705','CANCEL','2026-09-01 17:11:00.139457','2026-09-01 17:11:00.146288','Order ORD-019 has been cancelled due to unpaid order within 24 hours','084ef6e2-a5f5-11f1-913b-58733aca4705','Order',NULL,'2026-09-01 17:11:00.146288',NULL),('00f77f54-a5fa-11f1-913b-58733aca4705','CANCEL','2026-09-01 17:11:00.181672','2026-09-01 17:11:00.182451','Order ORD-011 has been cancelled due to unpaid order within 24 hours','951f1fae-a468-11f1-913b-58733aca4705','Order',NULL,'2026-09-01 17:11:00.182451',NULL),('055aa298-a3ab-11f1-913b-58733aca4705','LOGIN','2026-08-29 18:40:34.974642','2026-08-29 18:40:34.975877','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-08-29 18:40:34.975877','15f67a8e-a043-11f1-913b-58733aca4705'),('0bbd00b0-a485-11f1-913b-58733aca4705','LOGIN','2026-08-30 20:41:15.973427','2026-08-30 20:41:15.973870','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-08-30 20:41:15.973870','629d3798-a14b-11f1-913b-58733aca4705'),('0c0ab712-a460-11f1-913b-58733aca4705','LOGIN','2026-08-30 16:16:25.103110','2026-08-30 16:16:25.104325','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-08-30 16:16:25.104325','629d3798-a14b-11f1-913b-58733aca4705'),('0c3257c2-a5a5-11f1-913b-58733aca4705','CREATE','2026-09-01 07:02:51.798194','2026-09-01 07:02:51.801162','Shipment SHI-016 created for order ORD-017 from warehouse GlobalTrade-Warehouse-A','0c2ff388-a5a5-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-01 07:02:51.801162',NULL),('0cc3f52a-a65d-11f1-913b-58733aca4705','SHIPMENT_OVERDUE','2026-09-02 05:00:00.151734','2026-09-02 05:00:00.153847','Shipment SHI-015 is overdue. Expected delivery: 2026-09-02T02:00:00.070271','1c00264a-a5a3-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-02 05:00:00.153847',NULL),('0cc7e50e-a65d-11f1-913b-58733aca4705','SHIPMENT_OVERDUE','2026-09-02 05:00:00.177527','2026-09-02 05:00:00.180040','Shipment SHI-013 is overdue. Expected delivery: 2026-09-02T02:00:00.070290','89681766-a59d-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-02 05:00:00.180040',NULL),('0cc98256-a65d-11f1-913b-58733aca4705','SHIPMENT_OVERDUE','2026-09-02 05:00:00.188151','2026-09-02 05:00:00.190644','Shipment SHI-014 is overdue. Expected delivery: 2026-09-02T02:00:00.070296','a8d55656-a5a0-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-02 05:00:00.190644',NULL),('0ccb8466-a65d-11f1-913b-58733aca4705','SHIPMENT_OVERDUE','2026-09-02 05:00:00.201477','2026-09-02 05:00:00.203807','Shipment SHI-012 is overdue. Expected delivery: 2026-09-02T02:00:00.070347','a97a14d4-a59b-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-02 05:00:00.203807',NULL),('10d1e308-a3fa-11f1-913b-58733aca4705','LOGIN','2026-08-30 04:06:24.453031','2026-08-30 04:06:24.454024','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-08-30 04:06:24.454024','15f67a8e-a043-11f1-913b-58733aca4705'),('1226f646-a3a3-11f1-913b-58733aca4705','LOGIN','2026-08-29 17:43:40.472950','2026-08-29 17:43:40.474032','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-08-29 17:43:40.474032','15f67a8e-a043-11f1-913b-58733aca4705'),('134ece82-a3f7-11f1-913b-58733aca4705','SHIPMENT_OVERDUE','2026-08-30 03:45:00.132182','2026-08-30 03:45:00.138719','Shipment SHI-003 is overdue. Expected delivery: 2026-08-30T02:45:00.064261','1f51d5d0-a136-11f1-913b-58733aca4705','Shipment',NULL,'2026-08-30 03:45:00.138719',NULL),('13536226-a3f7-11f1-913b-58733aca4705','SHIPMENT_OVERDUE','2026-08-30 03:45:00.166651','2026-08-30 03:45:00.168087','Shipment SHI-006 is overdue. Expected delivery: 2026-08-30T02:45:00.066448','c6784f30-a35f-11f1-913b-58733aca4705','Shipment',NULL,'2026-08-30 03:45:00.168087',NULL),('1354dac0-a3f7-11f1-913b-58733aca4705','SHIPMENT_OVERDUE','2026-08-30 03:45:00.175882','2026-08-30 03:45:00.178402','Shipment SHI-010 is overdue. Expected delivery: 2026-08-30T02:45:00.066924','df130f32-a36c-11f1-913b-58733aca4705','Shipment',NULL,'2026-08-30 03:45:00.178402',NULL),('13569c8e-a3f7-11f1-913b-58733aca4705','SHIPMENT_OVERDUE','2026-08-30 03:45:00.187401','2026-08-30 03:45:00.189532','Shipment SHI-008 is overdue. Expected delivery: 2026-08-30T02:45:00.067008','e663af28-a36a-11f1-913b-58733aca4705','Shipment',NULL,'2026-08-30 03:45:00.189532',NULL),('1357d19e-a3f7-11f1-913b-58733aca4705','SHIPMENT_OVERDUE','2026-08-30 03:45:00.195932','2026-08-30 03:45:00.197134','Shipment SHI-001 is overdue. Expected delivery: 2026-08-30T02:45:00.067058','e9ea49f6-9fbc-11f1-913b-58733aca4705','Shipment',NULL,'2026-08-30 03:45:00.197134',NULL),('18ad11f4-a488-11f1-913b-58733aca4705','LOGIN','2026-08-30 21:03:06.169180','2026-08-30 21:03:06.170223','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-08-30 21:03:06.170223','629d3798-a14b-11f1-913b-58733aca4705'),('19339be2-a45c-11f1-913b-58733aca4705','LOGIN','2026-08-30 15:48:09.194934','2026-08-30 15:48:09.195785','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-08-30 15:48:09.195785','15f67a8e-a043-11f1-913b-58733aca4705'),('195dceba-a3b4-11f1-913b-58733aca4705','SHIP','2026-08-29 19:45:34.020383','2026-08-29 19:45:34.021710','Executed shipShipment','e663af28-a36a-11f1-913b-58733aca4705','Shipment',NULL,'2026-08-29 19:45:34.021710','15f67a8e-a043-11f1-913b-58733aca4705'),('19817146-a41b-11f1-913b-58733aca4705','LOGIN','2026-08-30 08:02:52.417914','2026-08-30 08:02:52.418296','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-08-30 08:02:52.418296','629d3798-a14b-11f1-913b-58733aca4705'),('1aab34cc-a46f-11f1-913b-58733aca4705','LOGIN','2026-08-30 18:04:12.094068','2026-08-30 18:04:12.095186','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-08-30 18:04:12.095186','629d3798-a14b-11f1-913b-58733aca4705'),('1b3c09f4-a3b4-11f1-913b-58733aca4705','SHIP','2026-08-29 19:45:37.154650','2026-08-29 19:45:37.155967','Executed shipShipment','df130f32-a36c-11f1-913b-58733aca4705','Shipment',NULL,'2026-08-29 19:45:37.155967','15f67a8e-a043-11f1-913b-58733aca4705'),('1c012220-a5a3-11f1-913b-58733aca4705','CREATE','2026-09-01 06:48:59.327985','2026-09-01 06:48:59.328620','Shipment SHI-015 created for order ORD-016 from warehouse GlobalTrade-Warehouse-A','1c00264a-a5a3-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-01 06:48:59.328620',NULL),('1c857ea2-a3f6-11f1-913b-58733aca4705','LOGIN','2026-08-30 03:38:06.098247','2026-08-30 03:38:06.099171','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-08-30 03:38:06.099171','15f67a8e-a043-11f1-913b-58733aca4705'),('1d8f9be2-a5a5-11f1-913b-58733aca4705','LOGIN','2026-09-01 07:03:20.933114','2026-09-01 07:03:20.933479','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-09-01 07:03:20.933479','15f67a8e-a043-11f1-913b-58733aca4705'),('1fc1cf38-a489-11f1-913b-58733aca4705','LOGIN','2026-08-30 21:10:27.545914','2026-08-30 21:10:27.546767','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-08-30 21:10:27.546767','15f67a8e-a043-11f1-913b-58733aca4705'),('20d5e3e2-a38e-11f1-913b-58733aca4705','LOGIN','2026-08-29 15:13:45.676009','2026-08-29 15:13:45.677063','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-08-29 15:13:45.677063','15f67a8e-a043-11f1-913b-58733aca4705'),('26be1fba-a666-11f1-913b-58733aca4705','CREATE','2026-09-02 06:05:09.203991','2026-09-02 06:05:09.206736','Shipment SHI-020 created for order ORD-022 from warehouse GlobalTrade-Warehouse-B','26bd9e3c-a666-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-02 06:05:09.206736',NULL),('26c05226-a666-11f1-913b-58733aca4705','CREATE','2026-09-02 06:05:09.220570','2026-09-02 06:05:09.221064','Shipment SHI-021 created for order ORD-022 from warehouse GlobalTrade-Warehouse-A','26c02fa8-a666-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-02 06:05:09.221064',NULL),('2c5c4100-a45d-11f1-913b-58733aca4705','LOGIN','2026-08-30 15:55:50.834592','2026-08-30 15:55:50.835481','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-08-30 15:55:50.835481','15f67a8e-a043-11f1-913b-58733aca4705'),('2e6dff06-a66a-11f1-913b-58733aca4705','CUSTOM_DEADLINE','2026-09-02 06:34:00.084123','2026-09-02 06:34:00.092448','CUSTOMS WARNING: Shipment SHI-016 is missing documents: [COMMERCIAL_INVOICE, PACKING_LIST, CERTIFICATE_OF_ORIGIN, CUSTOMS_DECLARATION]','Custom','Custom',NULL,'2026-09-02 06:34:00.092448',NULL),('2e701b38-a66a-11f1-913b-58733aca4705','CUSTOM_DEADLINE','2026-09-02 06:34:00.103512','2026-09-02 06:34:00.104063','CUSTOMS WARNING: Shipment SHI-015 is missing documents: [COMMERCIAL_INVOICE, PACKING_LIST, CERTIFICATE_OF_ORIGIN, CUSTOMS_DECLARATION]','Custom','Custom',NULL,'2026-09-02 06:34:00.104063',NULL),('31e3f646-a47a-11f1-913b-58733aca4705','LOGIN','2026-08-30 19:23:35.517388','2026-08-30 19:23:35.518670','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-08-30 19:23:35.518670','629d3798-a14b-11f1-913b-58733aca4705'),('31e88540-a460-11f1-913b-58733aca4705','LOGIN','2026-08-30 16:17:28.632826','2026-08-30 16:17:28.633488','User logged in successfully','33e34df6-9f94-11f1-913b-58733aca4705','User',NULL,'2026-08-30 16:17:28.633488','33e34df6-9f94-11f1-913b-58733aca4705'),('35119a5e-a46f-11f1-913b-58733aca4705','LOGIN','2026-08-30 18:04:56.386374','2026-08-30 18:04:56.386888','User logged in successfully','7bc741cc-a273-11f1-913b-58733aca4705','User',NULL,'2026-08-30 18:04:56.386888','7bc741cc-a273-11f1-913b-58733aca4705'),('353be5d8-a59c-11f1-913b-58733aca4705','SHIP','2026-09-01 05:59:35.178809','2026-09-01 05:59:35.179721','Executed shipShipment','a97a14d4-a59b-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-01 05:59:35.179721','15f67a8e-a043-11f1-913b-58733aca4705'),('3e65544c-a460-11f1-913b-58733aca4705','LOGIN','2026-08-30 16:17:49.583741','2026-08-30 16:17:49.584112','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-08-30 16:17:49.584112','629d3798-a14b-11f1-913b-58733aca4705'),('3efb238c-a42e-11f1-913b-58733aca4705','LOGIN','2026-08-30 10:19:55.728834','2026-08-30 10:19:55.729417','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-08-30 10:19:55.729417','629d3798-a14b-11f1-913b-58733aca4705'),('4727f022-a59a-11f1-913b-58733aca4705','LOGIN','2026-09-01 05:45:46.253831','2026-09-01 05:45:46.254482','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-09-01 05:45:46.254482','15f67a8e-a043-11f1-913b-58733aca4705'),('4c42bc06-a458-11f1-913b-58733aca4705','LOGIN','2026-08-30 15:20:56.870998','2026-08-30 15:20:56.871733','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-08-30 15:20:56.871733','15f67a8e-a043-11f1-913b-58733aca4705'),('52bc41c8-a5a3-11f1-913b-58733aca4705','LOGIN','2026-09-01 06:50:31.151517','2026-09-01 06:50:31.151860','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-09-01 06:50:31.151860','15f67a8e-a043-11f1-913b-58733aca4705'),('5b0febc2-a468-11f1-913b-58733aca4705','LOGIN','2026-08-30 17:15:53.651517','2026-08-30 17:15:53.652154','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-08-30 17:15:53.652154','15f67a8e-a043-11f1-913b-58733aca4705'),('5ceb7bf8-a59b-11f1-913b-58733aca4705','LOGIN','2026-09-01 05:53:32.264273','2026-09-01 05:53:32.264830','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-09-01 05:53:32.264830','15f67a8e-a043-11f1-913b-58733aca4705'),('6265aabe-a36f-11f1-913b-58733aca4705','LOGIN','2026-08-29 11:33:41.271802','2026-08-29 11:33:41.272526','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-08-29 11:33:41.272526','15f67a8e-a043-11f1-913b-58733aca4705'),('6537856c-a36c-11f1-913b-58733aca4705','LOGIN','2026-08-29 11:12:17.512591','2026-08-29 11:12:17.513021','User logged in successfully','832bea7e-a36a-11f1-913b-58733aca4705','User',NULL,'2026-08-29 11:12:17.513021','832bea7e-a36a-11f1-913b-58733aca4705'),('70130b86-a59d-11f1-913b-58733aca4705','LOGIN','2026-09-01 06:08:23.393612','2026-09-01 06:08:23.394223','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-09-01 06:08:23.394223','629d3798-a14b-11f1-913b-58733aca4705'),('710e8704-a381-11f1-913b-58733aca4705','LOGIN','2026-08-29 13:42:56.807494','2026-08-29 13:42:56.808390','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-08-29 13:42:56.808390','15f67a8e-a043-11f1-913b-58733aca4705'),('7c0ae04e-a39f-11f1-913b-58733aca4705','CANCEL','2026-08-29 17:18:00.137188','2026-08-29 17:18:00.141847','Order ORD-007 has been cancelled due to unpaid order within 24 hours','c0e048cc-a14b-11f1-913b-58733aca4705','Order',NULL,'2026-08-29 17:18:00.141847',NULL),('7ddcfadc-a459-11f1-913b-58733aca4705','LOGIN','2026-08-30 15:29:29.586882','2026-08-30 15:29:29.587767','User logged in successfully','7bc741cc-a273-11f1-913b-58733aca4705','User',NULL,'2026-08-30 15:29:29.587767','7bc741cc-a273-11f1-913b-58733aca4705'),('82fbb966-a5a0-11f1-913b-58733aca4705','LOGIN','2026-09-01 06:30:23.607618','2026-09-01 06:30:23.608517','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-09-01 06:30:23.608517','629d3798-a14b-11f1-913b-58733aca4705'),('83653c8a-a5f3-11f1-913b-58733aca4705','CREATE','2026-09-01 16:24:32.525441','2026-09-01 16:24:32.529357','Executed createGRN','8352730c-a5f3-11f1-913b-58733aca4705','GoodsReceiveNote',NULL,'2026-09-01 16:24:32.529357','15f67a8e-a043-11f1-913b-58733aca4705'),('837ae14e-a381-11f1-913b-58733aca4705','SHIP','2026-08-29 13:43:27.716138','2026-08-29 13:43:27.717564','Executed shipShipment','df114260-a36c-11f1-913b-58733aca4705','Shipment',NULL,'2026-08-29 13:43:27.717564','15f67a8e-a043-11f1-913b-58733aca4705'),('8969b2ce-a59d-11f1-913b-58733aca4705','CREATE','2026-09-01 06:09:05.904169','2026-09-01 06:09:05.905236','Shipment SHI-013 created for order ORD-014 from warehouse GlobalTrade-Warehouse-A','89681766-a59d-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-01 06:09:05.905236',NULL),('8b6f6cc8-a59b-11f1-913b-58733aca4705','LOGIN','2026-09-01 05:54:50.304123','2026-09-01 05:54:50.304734','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-09-01 05:54:50.304734','629d3798-a14b-11f1-913b-58733aca4705'),('956d5364-a59d-11f1-913b-58733aca4705','LOGIN','2026-09-01 06:09:26.061090','2026-09-01 06:09:26.061580','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-09-01 06:09:26.061580','15f67a8e-a043-11f1-913b-58733aca4705'),('a1d6b160-a424-11f1-913b-58733aca4705','LOGIN','2026-08-30 09:11:06.617361','2026-08-30 09:11:06.617709','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-08-30 09:11:06.617709','629d3798-a14b-11f1-913b-58733aca4705'),('a8d610fa-a5a0-11f1-913b-58733aca4705','CREATE','2026-09-01 06:31:27.114238','2026-09-01 06:31:27.114926','Shipment SHI-014 created for order ORD-015 from warehouse GlobalTrade-Warehouse-A','a8d55656-a5a0-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-01 06:31:27.114926',NULL),('a977902e-a59b-11f1-913b-58733aca4705','CREATE','2026-09-01 05:55:40.688493','2026-09-01 05:55:40.689783','Shipment SHI-011 created for order ORD-013 from warehouse GlobalTrade-Warehouse-B','a975c8ac-a59b-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-01 05:55:40.689783',NULL),('a97a468e-a59b-11f1-913b-58733aca4705','CREATE','2026-09-01 05:55:40.706778','2026-09-01 05:55:40.707542','Shipment SHI-012 created for order ORD-013 from warehouse GlobalTrade-Warehouse-A','a97a14d4-a59b-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-01 05:55:40.707542',NULL),('ac350f18-a40f-11f1-913b-58733aca4705','LOGIN','2026-08-30 06:41:04.581150','2026-08-30 06:41:04.582064','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-08-30 06:41:04.582064','15f67a8e-a043-11f1-913b-58733aca4705'),('aca4d8f2-a59f-11f1-913b-58733aca4705','LOGIN','2026-09-01 06:24:24.006138','2026-09-01 06:24:24.006506','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-09-01 06:24:24.006506','15f67a8e-a043-11f1-913b-58733aca4705'),('afe6ad32-a3f7-11f1-913b-58733aca4705','LOGIN','2026-08-30 03:49:22.856951','2026-08-30 03:49:22.857812','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-08-30 03:49:22.857812','629d3798-a14b-11f1-913b-58733aca4705'),('b1497e94-a37e-11f1-913b-58733aca4705','SHIP','2026-08-29 13:23:16.077459','2026-08-29 13:23:16.078899','Executed shipShipment','c6741b04-a35f-11f1-913b-58733aca4705','Shipment',NULL,'2026-08-29 13:23:16.078899','15f67a8e-a043-11f1-913b-58733aca4705'),('b4145dac-a3be-11f1-913b-58733aca4705','LOGIN','2026-08-29 21:01:28.553640','2026-08-29 21:01:28.554486','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-08-29 21:01:28.554486','15f67a8e-a043-11f1-913b-58733aca4705'),('b677a3c4-a5a3-11f1-913b-58733aca4705','LOGIN','2026-09-01 06:53:18.473810','2026-09-01 06:53:18.474374','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-09-01 06:53:18.474374','629d3798-a14b-11f1-913b-58733aca4705'),('ba9254b0-a489-11f1-913b-58733aca4705','LOGIN','2026-08-30 21:14:47.280328','2026-08-30 21:14:47.282876','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-08-30 21:14:47.282876','15f67a8e-a043-11f1-913b-58733aca4705'),('c49c396a-a666-11f1-913b-58733aca4705','SHIP','2026-09-02 06:09:34.063175','2026-09-02 06:09:34.064623','Executed shipShipment','26bd9e3c-a666-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-02 06:09:34.064623','78f59e72-a664-11f1-913b-58733aca4705'),('c5eeb23a-a665-11f1-913b-58733aca4705','CREATE','2026-09-02 06:02:26.783997','2026-09-02 06:02:26.786089','Shipment SHI-019 created for order ORD-020 from warehouse GlobalTrade-Warehouse-B','c5ec4a18-a665-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-02 06:02:26.786089',NULL),('c7ca5670-a5c2-11f1-913b-58733aca4705','LOGIN','2026-09-01 10:35:41.933743','2026-09-01 10:35:41.936579','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-09-01 10:35:41.936579','629d3798-a14b-11f1-913b-58733aca4705'),('ca665e5a-a59b-11f1-913b-58733aca4705','LOGIN','2026-09-01 05:56:35.941327','2026-09-01 05:56:35.941769','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-09-01 05:56:35.941769','15f67a8e-a043-11f1-913b-58733aca4705'),('cb1769d6-a468-11f1-913b-58733aca4705','LOGIN','2026-08-30 17:19:01.605097','2026-08-30 17:19:01.606118','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-08-30 17:19:01.606118','629d3798-a14b-11f1-913b-58733aca4705'),('cb6c4a46-a5a3-11f1-913b-58733aca4705','LOGIN','2026-09-01 06:53:53.631773','2026-09-01 06:53:53.632116','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-09-01 06:53:53.632116','15f67a8e-a043-11f1-913b-58733aca4705'),('cec430da-a5a0-11f1-913b-58733aca4705','LOGIN','2026-09-01 06:32:30.750638','2026-09-01 06:32:30.751202','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-09-01 06:32:30.751202','15f67a8e-a043-11f1-913b-58733aca4705'),('d9c84012-a3ab-11f1-913b-58733aca4705','SHIP','2026-08-29 18:46:31.369721','2026-08-29 18:46:31.371267','Executed shipShipment','c6784f30-a35f-11f1-913b-58733aca4705','Shipment',NULL,'2026-08-29 18:46:31.371267','15f67a8e-a043-11f1-913b-58733aca4705'),('deea0094-a5a4-11f1-913b-58733aca4705','LOGIN','2026-09-01 07:01:35.828679','2026-09-01 07:01:35.829499','User logged in successfully','d7f317da-a5a4-11f1-913b-58733aca4705','User',NULL,'2026-09-01 07:01:35.829499','d7f317da-a5a4-11f1-913b-58733aca4705'),('e175a9b8-a599-11f1-913b-58733aca4705','LOGIN','2026-09-01 05:42:55.635287','2026-09-01 05:42:55.636242','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-09-01 05:42:55.636242','629d3798-a14b-11f1-913b-58733aca4705'),('e6e55f08-a669-11f1-913b-58733aca4705','CUSTOM_DEADLINE','2026-09-02 06:32:00.075297','2026-09-02 06:32:00.079231','CUSTOMS WARNING: Shipment SHI-016 is missing documents: [COMMERCIAL_INVOICE, PACKING_LIST, CERTIFICATE_OF_ORIGIN, CUSTOMS_DECLARATION]','Custom','Custom',NULL,'2026-09-02 06:32:00.079231',NULL),('f369b326-a65c-11f1-913b-58733aca4705','CREATE','2026-09-02 04:59:17.616821','2026-09-02 04:59:17.619558','Shipment SHI-017 created for order ORD-012 from warehouse GlobalTrade-Warehouse-B','f3568170-a65c-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-02 04:59:17.619558',NULL),('f3764f8c-a65c-11f1-913b-58733aca4705','CREATE','2026-09-02 04:59:17.700553','2026-09-02 04:59:17.702013','Shipment SHI-018 created for order ORD-012 from warehouse GlobalTrade-Warehouse-A','f375d8d6-a65c-11f1-913b-58733aca4705','Shipment',NULL,'2026-09-02 04:59:17.702013',NULL),('f45b301e-a45b-11f1-913b-58733aca4705','SHIP','2026-08-30 15:47:07.378042','2026-08-30 15:47:07.379477','Executed shipShipment','e6617cbc-a36a-11f1-913b-58733aca4705','Shipment',NULL,'2026-08-30 15:47:07.379477','7bc741cc-a273-11f1-913b-58733aca4705'),('f821cb0c-a5a2-11f1-913b-58733aca4705','LOGIN','2026-09-01 06:47:59.143947','2026-09-01 06:47:59.144657','User logged in successfully','629d3798-a14b-11f1-913b-58733aca4705','User',NULL,'2026-09-01 06:47:59.144657','629d3798-a14b-11f1-913b-58733aca4705'),('f87e0d1e-a484-11f1-913b-58733aca4705','LOGIN','2026-08-30 20:40:43.681981','2026-08-30 20:40:43.684674','User logged in successfully','15f67a8e-a043-11f1-913b-58733aca4705','User',NULL,'2026-08-30 20:40:43.684674','15f67a8e-a043-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `audit_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `companies`
--

DROP TABLE IF EXISTS `companies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `companies` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `hotline_1` varchar(20) DEFAULT NULL,
  `hotline_2` varchar(20) DEFAULT NULL,
  `name` varchar(150) NOT NULL,
  `registration_number` varchar(50) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `city` varchar(80) NOT NULL,
  `district` varchar(80) DEFAULT NULL,
  `address_line1` varchar(150) NOT NULL,
  `address_line2` varchar(150) DEFAULT NULL,
  `address_line3` varchar(150) DEFAULT NULL,
  `postal_code` varchar(20) NOT NULL,
  `state_province` varchar(80) DEFAULT NULL,
  `country_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `registration_number` (`registration_number`),
  UNIQUE KEY `idx_company_registration` (`registration_number`),
  KEY `idx_company_name` (`name`),
  KEY `FK_companies_country_id` (`country_id`),
  CONSTRAINT `FK_companies_country_id` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `companies`
--

LOCK TABLES `companies` WRITE;
/*!40000 ALTER TABLE `companies` DISABLE KEYS */;
INSERT INTO `companies` VALUES ('2a5cc41a-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@lankaglobal.test','+94110000001','+94770000001','Lanka Global Trade Solutions','GT-LK-0001','2026-08-23 19:53:09.000000','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705'),('2a5e1202-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@bharatlogistics.test','+91110000002','+91980000002','Bharat International Logistics','GT-IN-0002','2026-08-23 19:53:09.000000','Mumbai','Mumbai','25 Marine Drive','Office 12',NULL,'400001','Maharashtra','31da9830-9efd-11f1-913b-58733aca4705'),('2a5e58ca-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@lionfreight.test','+6560000003','+6590000003','Lion City Freight Network','GT-SG-0003','2026-08-23 19:53:09.000000','Singapore',NULL,'30 Harbour Front','Unit 08-10',NULL,'098585','Singapore','31daa3f2-9efd-11f1-913b-58733aca4705'),('2a5e5b54-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@malayatrade.test','+60300000004','+60120000004','Malaya Trade Corporation','GT-MY-0004','2026-08-23 19:53:09.000000','Kuala Lumpur',NULL,'50 Jalan Ampang','Unit 10',NULL,'50450','Kuala Lumpur','31daae38-9efd-11f1-913b-58733aca4705'),('2a5e5cb2-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@sakuraglobal.test','+81300000005','+81900000005','Sakura Global Commerce','GT-JP-0005','2026-08-23 19:53:09.000000','Tokyo','Chiyoda','5 Chiyoda Avenue','Building 3',NULL,'100-0001','Tokyo','31dac418-9efd-11f1-913b-58733aca4705'),('2a5e5e10-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@hanriverlogistics.test','+82200000006','+82100000006','Han River Logistics','GT-KR-0006','2026-08-23 19:53:09.000000','Seoul','Gangnam','20 Gangnam Street','Floor 8',NULL,'06130','Seoul','31dbbb98-9efd-11f1-913b-58733aca4705'),('2a5e5f50-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@dragongate.test','+86210000007','+86130000007','Dragon Gate Trading','GT-CN-0007','2026-08-23 19:53:09.000000','Shanghai','Pudong','88 Pudong Road','Tower A',NULL,'200120','Shanghai','31da849e-9efd-11f1-913b-58733aca4705'),('2a5e6086-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@pacificgateway.test','+61200000008','+61400000008','Pacific Gateway Logistics','GT-AU-0008','2026-08-23 19:53:09.000000','Sydney',NULL,'200 Harbour Street','Suite 15',NULL,'2000','New South Wales','31dacfbc-9efd-11f1-913b-58733aca4705'),('2a5e7044-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@gulfstar.test','+97140000009','+97150000009','Gulf Star Trading','GT-AE-0009','2026-08-23 19:53:09.000000','Dubai',NULL,'15 Sheikh Zayed Road','Office 20',NULL,'00000','Dubai','31dad9ee-9efd-11f1-913b-58733aca4705'),('2a5e71ca-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@arabiancargo.test','+96611000010','+96650000010','Arabian Cargo Partners','GT-SA-0010','2026-08-23 19:53:09.000000','Riyadh',NULL,'40 King Fahd Road','Building 5',NULL,'12211','Riyadh','31dae434-9efd-11f1-913b-58733aca4705'),('2a5e736e-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@eurolinkfreight.test','+4940000011','+49150000011','EuroLink Freight GmbH','GT-DE-0011','2026-08-23 19:53:09.000000','Hamburg',NULL,'90 Hafenstrasse','Office 4',NULL,'20095','Hamburg','31daee0c-9efd-11f1-913b-58733aca4705'),('2a5e74cc-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@rotterdamshipping.test','+3110000012','+3160000012','Rotterdam Global Shipping','GT-NL-0012','2026-08-23 19:53:09.000000','Rotterdam',NULL,'12 Port Avenue','Warehouse 2',NULL,'3011AA','South Holland','31daf7b2-9efd-11f1-913b-58733aca4705'),('2a5e760c-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@britanniatrade.test','+4420000013','+4470000013','Britannia Trade Services','GT-UK-0013','2026-08-23 19:53:09.000000','London',NULL,'75 Thames Road','Floor 6',NULL,'EC2N 4AY','England','31db0234-9efd-11f1-913b-58733aca4705'),('2a5e774c-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@pariscommerce.test','+3310000014','+3360000014','Paris International Commerce','GT-FR-0014','2026-08-23 19:53:09.000000','Paris',NULL,'18 Rue de Lyon','Suite 5',NULL,'75012','Île-de-France','31db0e96-9efd-11f1-913b-58733aca4705'),('2a5e78a0-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@alpinenetwork.test','+4144000015','+4179000015','Alpine Trade Network','GT-CH-0015','2026-08-23 19:53:09.000000','Zurich',NULL,'60 Bahnhofstrasse','Office 10',NULL,'8001','Zurich','31db5ed2-9efd-11f1-913b-58733aca4705'),('2a5e79e0-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@mapleleaflogistics.test','+1416000016','+1417000016','Maple Leaf Logistics','GT-CA-0016','2026-08-23 19:53:09.000000','Toronto',NULL,'100 King Street','Suite 200',NULL,'M5H 1J9','Ontario','31db700c-9efd-11f1-913b-58733aca4705'),('2a5e7b7a-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@atlantictrade.test','+1212000017','+1917000017','Atlantic Trade Corporation','GT-US-0017','2026-08-23 19:53:09.000000','New York',NULL,'250 Madison Avenue','Floor 10',NULL,'10016','New York','31db7db8-9efd-11f1-913b-58733aca4705'),('2a5e7cc4-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@southatlantic.test','+5511000018','+5511000018','South Atlantic Commerce','GT-BR-0018','2026-08-23 19:53:09.000000','São Paulo',NULL,'80 Paulista Avenue','Office 7',NULL,'01310-100','São Paulo','31db8a9c-9efd-11f1-913b-58733aca4705'),('2a5e7e04-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@capeglobal.test','+2721000019','+2782000019','Cape Global Freight','GT-ZA-0019','2026-08-23 19:53:09.000000','Cape Town',NULL,'45 Harbour Road','Unit 8',NULL,'8001','Western Cape','31db976c-9efd-11f1-913b-58733aca4705'),('2a5e7f3a-9efe-11f1-913b-58733aca4705','2026-08-23 19:53:09.000000','contact@eafricatrade.test','+2542000020','+2547000020','East Africa Trade Hub','GT-KE-0020','2026-08-23 19:53:09.000000','Nairobi',NULL,'10 Mombasa Road','Office 3',NULL,'00100','Nairobi','31dbaedc-9efd-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `companies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `country` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `name` varchar(150) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `country`
--

LOCK TABLES `country` WRITE;
/*!40000 ALTER TABLE `country` DISABLE KEYS */;
INSERT INTO `country` VALUES ('31d9a25e-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.545816','Sri Lanka','2026-08-23 19:46:12.545816'),('31da849e-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.551716','China','2026-08-23 19:46:12.551716'),('31da9830-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.552089','India','2026-08-23 19:46:12.552089'),('31daa3f2-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.552375','Singapore','2026-08-23 19:46:12.552375'),('31daae38-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.552642','Malaysia','2026-08-23 19:46:12.552642'),('31dac418-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.553202','Japan','2026-08-23 19:46:12.553202'),('31dacfbc-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.553503','Australia','2026-08-23 19:46:12.553503'),('31dad9ee-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.553780','United Arab Emirates','2026-08-23 19:46:12.553780'),('31dae434-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.554010','Saudi Arabia','2026-08-23 19:46:12.554010'),('31daee0c-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.554266','Germany','2026-08-23 19:46:12.554266'),('31daf7b2-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.554535','Netherlands','2026-08-23 19:46:12.554535'),('31db0234-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.554781','United Kingdom','2026-08-23 19:46:12.554781'),('31db0e96-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.555174','France','2026-08-23 19:46:12.555174'),('31db5ed2-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.557229','Switzerland','2026-08-23 19:46:12.557229'),('31db700c-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.557607','Canada','2026-08-23 19:46:12.557607'),('31db7db8-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.557975','United States','2026-08-23 19:46:12.557975'),('31db8a9c-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.558289','Brazil','2026-08-23 19:46:12.558289'),('31db976c-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.558615','South Africa','2026-08-23 19:46:12.558615'),('31dbaedc-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.559241','Kenya','2026-08-23 19:46:12.559241'),('31dbbb98-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.559530','South Korea','2026-08-23 19:46:12.559530');
/*!40000 ALTER TABLE `country` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `customer_number` varchar(30) NOT NULL,
  `customer_type` varchar(255) NOT NULL,
  `email` varchar(60) DEFAULT NULL,
  `first_name` varchar(45) NOT NULL,
  `kyc_verified` tinyint(1) DEFAULT '0',
  `last_name` varchar(45) NOT NULL,
  `mobile_1` varchar(20) DEFAULT NULL,
  `mobile_2` varchar(20) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `company_id` varchar(255) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `customer_number` (`customer_number`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `idx_customer_number` (`customer_number`),
  KEY `idx_customer_company_id` (`company_id`),
  KEY `idx_customer_email` (`email`),
  CONSTRAINT `fk_customer_company` FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`),
  CONSTRAINT `fk_customer_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES ('1c29e468-a665-11f1-913b-58733aca4705','2026-09-02 05:57:41.961305','CUS-005','LOCAL','customer@globaltrade.test','customer',0,'customer','+94762341234','+94762341231','ACTIVE','2026-09-02 05:57:41.961305','2a5cc41a-9efe-11f1-913b-58733aca4705','1c278d08-a665-11f1-913b-58733aca4705'),('33e79a0a-9f94-11f1-913b-58733aca4705','2026-08-24 13:47:09.999139','CUS-001','LOCAL','saman@gmail.com','saman',0,'Kumara','+94759278656',NULL,'ACTIVE','2026-08-24 13:47:09.999139','2a5cc41a-9efe-11f1-913b-58733aca4705','33e34df6-9f94-11f1-913b-58733aca4705'),('62a20f48-a14b-11f1-913b-58733aca4705','2026-08-26 18:10:57.460439','CUS-002','LOCAL','danuka@test.com','Danuka',0,'Perera','+94741256785','+941125002134','ACTIVE','2026-08-26 18:10:57.460439','2a5cc41a-9efe-11f1-913b-58733aca4705','629d3798-a14b-11f1-913b-58733aca4705'),('8331809c-a36a-11f1-913b-58733aca4705','2026-08-29 10:58:48.811853','CUS-003','LOCAL','anil@gmail.com','Anil',0,'Jayashantha','+94759278656','+94759278656','ACTIVE','2026-08-29 10:58:48.811853','2a5cc41a-9efe-11f1-913b-58733aca4705','832bea7e-a36a-11f1-913b-58733aca4705'),('d7f7eeae-a5a4-11f1-913b-58733aca4705','2026-09-01 07:01:24.176741','CUS-004','INTERNATIONAL','xi@gmail.com','Takahashi',0,'Ito','+81756321237','+81756321231','ACTIVE','2026-09-01 07:01:24.176741','2a5e5cb2-9efe-11f1-913b-58733aca4705','d7f317da-a5a4-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customs_documents`
--

DROP TABLE IF EXISTS `customs_documents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customs_documents` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `document_number` varchar(40) DEFAULT NULL,
  `document_type` varchar(20) NOT NULL,
  `file_name` varchar(255) NOT NULL,
  `file_path` varchar(500) NOT NULL,
  `issued_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `shipment_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `document_number` (`document_number`),
  KEY `idx_customs_document_shipment` (`shipment_id`),
  CONSTRAINT `FK_customs_documents_shipment_id` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customs_documents`
--

LOCK TABLES `customs_documents` WRITE;
/*!40000 ALTER TABLE `customs_documents` DISABLE KEYS */;
/*!40000 ALTER TABLE `customs_documents` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employees` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `department` varchar(50) NOT NULL,
  `email` varchar(150) NOT NULL,
  `employee_number` varchar(30) NOT NULL,
  `first_name` varchar(45) NOT NULL,
  `last_name` varchar(45) NOT NULL,
  `mobile_1` varchar(20) DEFAULT NULL,
  `mobile_2` varchar(20) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `city` varchar(80) NOT NULL,
  `district` varchar(80) DEFAULT NULL,
  `address_line1` varchar(150) NOT NULL,
  `address_line2` varchar(150) DEFAULT NULL,
  `address_line3` varchar(150) DEFAULT NULL,
  `postal_code` varchar(20) NOT NULL,
  `state_province` varchar(80) DEFAULT NULL,
  `country_id` varchar(255) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `employee_number` (`employee_number`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `idx_employee_number` (`employee_number`),
  KEY `idx_employee_email` (`email`),
  CONSTRAINT `fk_employee_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

LOCK TABLES `employees` WRITE;
/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
INSERT INTO `employees` VALUES ('15f8f6d8-a043-11f1-913b-58733aca4705','2026-08-25 10:39:01.708796','MANAGEMENT','admin@gmail.test','EMP-001','admin','admin','+94759278656','+94112345678','ACTIVE','2026-08-25 10:39:01.708796','Colombo','Colombo','45 Galle Road','Level 3',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','15f67a8e-a043-11f1-913b-58733aca4705'),('78f9c182-a664-11f1-913b-58733aca4705','2026-09-02 05:53:08.177295','MANAGEMENT','manager@globaltrade.test','EMP-003','Manager','Manager','+94759278089','+94759278081','ACTIVE','2026-09-02 05:53:08.177295','Colombo','Colombo','No 45','Colombo','New Street','11618','Western','31d9a25e-9efd-11f1-913b-58733aca4705','78f59e72-a664-11f1-913b-58733aca4705'),('7bccd812-a273-11f1-913b-58733aca4705','2026-08-28 05:30:30.715983','LOGISTICS','kane@test.com','EMP-002','Kane','Jonathen','+55123456789','+551987654321','ACTIVE','2026-08-28 05:30:30.715983','Teranos','North','No 12','New Wales Street','Teranos','8563','North Western','31db5ed2-9efd-11f1-913b-58733aca4705','7bc741cc-a273-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `goods_received_notes`
--

DROP TABLE IF EXISTS `goods_received_notes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_received_notes` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `grn_number` varchar(30) NOT NULL,
  `received_at` datetime(6) NOT NULL,
  `status` varchar(20) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `vendor_id` varchar(255) NOT NULL,
  `warehouse_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `grn_number` (`grn_number`),
  UNIQUE KEY `idx_grn_number` (`grn_number`),
  KEY `idx_grn_vendor` (`vendor_id`),
  KEY `idx_grn_warehouse` (`warehouse_id`),
  KEY `idx_grn_received_at` (`received_at`),
  CONSTRAINT `fk_grn_vendor` FOREIGN KEY (`vendor_id`) REFERENCES `vendors` (`id`),
  CONSTRAINT `fk_grn_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods_received_notes`
--

LOCK TABLES `goods_received_notes` WRITE;
/*!40000 ALTER TABLE `goods_received_notes` DISABLE KEYS */;
INSERT INTO `goods_received_notes` VALUES ('365d6b6c-9fb6-11f1-913b-58733aca4705','2026-08-24 17:50:37.015478','GRN-001','2026-08-24 17:50:37.005179','RECEIVED','2026-08-24 17:50:37.015478','78802742-9fb5-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('478c801e-a135-11f1-913b-58733aca4705','2026-08-26 15:32:43.093112','GRN-002','2026-08-26 15:32:43.090183','RECEIVED','2026-08-26 15:32:43.093112','143b8ec8-a133-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('8352730c-a5f3-11f1-913b-58733aca4705','2026-09-01 16:24:32.405510','GRN-004','2026-09-01 16:24:32.401204','RECEIVED','2026-09-01 16:24:32.405510','78802742-9fb5-11f1-913b-58733aca4705','e667cd76-a20f-11f1-913b-58733aca4705'),('a036545c-a210-11f1-913b-58733aca4705','2026-08-27 17:42:51.629451','GRN-003','2026-08-27 17:42:51.625491','RECEIVED','2026-08-27 17:42:51.629451','143b8ec8-a133-11f1-913b-58733aca4705','e667cd76-a20f-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `goods_received_notes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grn_items`
--

DROP TABLE IF EXISTS `grn_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grn_items` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `quantity` int NOT NULL,
  `unit_cost` decimal(15,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `grn_id` varchar(255) NOT NULL,
  `product_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_grn_item_grn` (`grn_id`),
  KEY `idx_grn_item_product` (`product_id`),
  CONSTRAINT `fk_grn_item_grn` FOREIGN KEY (`grn_id`) REFERENCES `goods_received_notes` (`id`),
  CONSTRAINT `fk_grn_item_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grn_items`
--

LOCK TABLES `grn_items` WRITE;
/*!40000 ALTER TABLE `grn_items` DISABLE KEYS */;
INSERT INTO `grn_items` VALUES ('366001e2-9fb6-11f1-913b-58733aca4705','2026-08-24 17:50:37.032409',100,35000.00,'2026-08-24 17:50:37.032409','365d6b6c-9fb6-11f1-913b-58733aca4705','7694de6a-9fb4-11f1-913b-58733aca4705'),('478f5cd0-a135-11f1-913b-58733aca4705','2026-08-26 15:32:43.112113',6000,300.00,'2026-08-26 15:32:43.112113','478c801e-a135-11f1-913b-58733aca4705','5cb0b4c0-a134-11f1-913b-58733aca4705'),('83551dc8-a5f3-11f1-913b-58733aca4705','2026-09-01 16:24:32.422700',50000,5.00,'2026-09-01 16:24:32.422700','8352730c-a5f3-11f1-913b-58733aca4705','343a0fa0-a5f3-11f1-913b-58733aca4705'),('a0384adc-a210-11f1-913b-58733aca4705','2026-08-27 17:42:51.641923',600,275.00,'2026-08-27 17:42:51.641923','a036545c-a210-11f1-913b-58733aca4705','5cb0b4c0-a134-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `grn_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `inventory_number` varchar(30) NOT NULL,
  `quantity` int NOT NULL,
  `reorder_level` int NOT NULL,
  `reserved_quantity` int NOT NULL,
  `selling_price` decimal(15,2) NOT NULL,
  `status` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `grn_item_id` varchar(255) NOT NULL,
  `product_id` varchar(255) NOT NULL,
  `warehouse_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `inventory_number` (`inventory_number`),
  KEY `idx_inventory_number` (`inventory_number`),
  KEY `idx_inventory_warehouse` (`warehouse_id`),
  KEY `idx_inventory_product` (`product_id`),
  KEY `idx_inventory_grn_item` (`grn_item_id`),
  CONSTRAINT `fk_inventory_grn_item` FOREIGN KEY (`grn_item_id`) REFERENCES `grn_items` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory`
--

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` VALUES ('366954ae-9fb6-11f1-913b-58733aca4705','2026-08-24 17:50:37.093523','INV-001',4890,10,55,700.00,'ACTIVE','2026-09-02 06:04:50.621189','366001e2-9fb6-11f1-913b-58733aca4705','7694de6a-9fb4-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('4793e39a-a135-11f1-913b-58733aca4705','2026-08-26 15:32:43.141070','INV-002',5952,10,226,500.00,'ACTIVE','2026-09-02 06:00:41.460894','478f5cd0-a135-11f1-913b-58733aca4705','5cb0b4c0-a134-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('835b6d7c-a5f3-11f1-913b-58733aca4705','2026-09-01 16:24:32.464078','INV-004',50000,10,354,10.00,'ACTIVE','2026-09-02 11:03:45.972723','83551dc8-a5f3-11f1-913b-58733aca4705','343a0fa0-a5f3-11f1-913b-58733aca4705','e667cd76-a20f-11f1-913b-58733aca4705'),('a03e9bbc-a210-11f1-913b-58733aca4705','2026-08-27 17:42:51.683471','INV-003',550,10,53,450.00,'ACTIVE','2026-09-02 06:09:34.042399','a0384adc-a210-11f1-913b-58733aca4705','5cb0b4c0-a134-11f1-913b-58733aca4705','e667cd76-a20f-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `number_sequences`
--

DROP TABLE IF EXISTS `number_sequences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `number_sequences` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `next_value` bigint NOT NULL,
  `prefix` varchar(20) NOT NULL,
  `sequence_key` varchar(50) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `sequence_key` (`sequence_key`),
  UNIQUE KEY `idx_number_sequence_key` (`sequence_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `number_sequences`
--

LOCK TABLES `number_sequences` WRITE;
/*!40000 ALTER TABLE `number_sequences` DISABLE KEYS */;
INSERT INTO `number_sequences` VALUES ('158d5c66-a043-11f1-913b-58733aca4705','2026-08-25 10:39:01.003858',4,'EMP','EMPLOYEE','2026-09-02 05:53:08.165653'),('25e3bfc6-9f60-11f1-913b-58733aca4705','2026-08-24 07:34:32.656101',4,'VEN','VENDOR','2026-08-26 15:16:57.973774'),('33c434de-9f94-11f1-913b-58733aca4705','2026-08-24 13:47:09.767540',6,'CUS','CUSTOMER','2026-09-02 05:57:41.956306'),('365b46b6-9fb6-11f1-913b-58733aca4705','2026-08-24 17:50:37.001698',5,'GRN','GRN','2026-09-01 16:24:32.434535'),('3668bb5c-9fb6-11f1-913b-58733aca4705','2026-08-24 17:50:37.089539',5,'INV','INVENTORY','2026-09-01 16:24:32.495752'),('56193030-9fb6-11f1-913b-58733aca4705','2026-08-24 17:51:30.255445',24,'ORD','ORDER','2026-09-02 11:03:45.950166'),('768efa4a-9fb4-11f1-913b-58733aca4705','2026-08-24 17:38:05.723860',4,'PR','PRODUCT','2026-09-01 16:22:19.721948'),('e9d695a0-9fbc-11f1-913b-58733aca4705','2026-08-24 18:38:35.102214',16,'PAY','PAYMENT','2026-09-02 06:05:09.182982'),('e9e9dab6-9fbc-11f1-913b-58733aca4705','2026-08-24 18:38:35.228362',22,'SHI','SHIPMENT','2026-09-02 06:05:09.232079');
/*!40000 ALTER TABLE `number_sequences` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(19,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `inventory_id` varchar(255) NOT NULL,
  `order_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_order_item_order` (`order_id`),
  KEY `idx_order_item_inventory` (`inventory_id`),
  CONSTRAINT `FK_order_items_inventory_id` FOREIGN KEY (`inventory_id`) REFERENCES `inventory` (`id`),
  CONSTRAINT `FK_order_items_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES ('084f1dca-a5f5-11f1-913b-58733aca4705','2026-09-01 16:35:25.016994',120,10.00,'2026-09-01 16:35:25.016994','835b6d7c-a5f3-11f1-913b-58733aca4705','084ef6e2-a5f5-11f1-913b-58733aca4705'),('0ce72384-a5a3-11f1-913b-58733aca4705','2026-09-01 06:48:33.992517',30,500.00,'2026-09-01 06:48:33.992517','4793e39a-a135-11f1-913b-58733aca4705','0ce62074-a5a3-11f1-913b-58733aca4705'),('0ce784dc-a5a3-11f1-913b-58733aca4705','2026-09-01 06:48:33.994917',12,700.00,'2026-09-01 06:48:33.994917','366954ae-9fb6-11f1-913b-58733aca4705','0ce62074-a5a3-11f1-913b-58733aca4705'),('136088da-a59a-11f1-913b-58733aca4705','2026-09-01 05:44:19.383921',10,500.00,'2026-09-01 05:44:19.383921','4793e39a-a135-11f1-913b-58733aca4705','135f5816-a59a-11f1-913b-58733aca4705'),('13610b02-a59a-11f1-913b-58733aca4705','2026-09-01 05:44:19.387227',20,450.00,'2026-09-01 05:44:19.387227','a03e9bbc-a210-11f1-913b-58733aca4705','135f5816-a59a-11f1-913b-58733aca4705'),('1baa9824-a666-11f1-913b-58733aca4705','2026-09-02 06:04:50.623690',15,700.00,'2026-09-02 06:04:50.623690','366954ae-9fb6-11f1-913b-58733aca4705','1baa86d6-a666-11f1-913b-58733aca4705'),('1baaa1fc-a666-11f1-913b-58733aca4705','2026-09-02 06:04:50.623933',10,450.00,'2026-09-02 06:04:50.623933','a03e9bbc-a210-11f1-913b-58733aca4705','1baa86d6-a666-11f1-913b-58733aca4705'),('389806ca-a665-11f1-913b-58733aca4705','2026-09-02 05:58:29.659300',20,10.00,'2026-09-02 05:58:29.659300','835b6d7c-a5f3-11f1-913b-58733aca4705','389743ac-a665-11f1-913b-58733aca4705'),('4696ea46-a5f4-11f1-913b-58733aca4705','2026-09-01 16:30:00.009771',15,10.00,'2026-09-01 16:30:00.009771','835b6d7c-a5f3-11f1-913b-58733aca4705','46958d36-a5f4-11f1-913b-58733aca4705'),('46da37fc-a021-11f1-913b-58733aca4705','2026-08-25 06:37:00.826983',2,70000.00,'2026-08-25 06:37:00.826983','366954ae-9fb6-11f1-913b-58733aca4705','46d8921c-a021-11f1-913b-58733aca4705'),('561f7d50-9fb6-11f1-913b-58733aca4705','2026-08-24 17:51:30.296674',3,70000.00,'2026-08-24 17:51:30.296674','366954ae-9fb6-11f1-913b-58733aca4705','561c18b8-9fb6-11f1-913b-58733aca4705'),('7985e5ee-a59d-11f1-913b-58733aca4705','2026-09-01 06:08:39.246450',10,500.00,'2026-09-01 06:08:39.246450','4793e39a-a135-11f1-913b-58733aca4705','79851556-a59d-11f1-913b-58733aca4705'),('853620ee-a14b-11f1-913b-58733aca4705','2026-08-26 18:11:55.473299',5,500.00,'2026-08-26 18:11:55.473299','4793e39a-a135-11f1-913b-58733aca4705','85360f14-a14b-11f1-913b-58733aca4705'),('853684d0-a14b-11f1-913b-58733aca4705','2026-08-26 18:11:55.475928',10,700.00,'2026-08-26 18:11:55.475928','366954ae-9fb6-11f1-913b-58733aca4705','85360f14-a14b-11f1-913b-58733aca4705'),('8726790c-a665-11f1-913b-58733aca4705','2026-09-02 06:00:41.455119',200,10.00,'2026-09-02 06:00:41.455119','835b6d7c-a5f3-11f1-913b-58733aca4705','8725f950-a665-11f1-913b-58733aca4705'),('8726c3ee-a665-11f1-913b-58733aca4705','2026-09-02 06:00:41.456998',150,500.00,'2026-09-02 06:00:41.456998','4793e39a-a135-11f1-913b-58733aca4705','8725f950-a665-11f1-913b-58733aca4705'),('94b83974-a021-11f1-913b-58733aca4705','2026-08-25 06:39:11.466419',2,700.00,'2026-08-25 06:39:11.466419','366954ae-9fb6-11f1-913b-58733aca4705','94b7cfd4-a021-11f1-913b-58733aca4705'),('95202c1e-a468-11f1-913b-58733aca4705','2026-08-30 17:17:31.066525',10,500.00,'2026-08-30 17:17:31.066525','4793e39a-a135-11f1-913b-58733aca4705','951f1fae-a468-11f1-913b-58733aca4705'),('9520e000-a468-11f1-913b-58733aca4705','2026-08-30 17:17:31.071130',18,450.00,'2026-08-30 17:17:31.071130','a03e9bbc-a210-11f1-913b-58733aca4705','951f1fae-a468-11f1-913b-58733aca4705'),('9691b96c-a59b-11f1-913b-58733aca4705','2026-09-01 05:55:08.984509',10,500.00,'2026-09-01 05:55:08.984509','4793e39a-a135-11f1-913b-58733aca4705','9690c0b6-a59b-11f1-913b-58733aca4705'),('96923b26-a59b-11f1-913b-58733aca4705','2026-09-01 05:55:08.987771',15,450.00,'2026-09-01 05:55:08.987771','a03e9bbc-a210-11f1-913b-58733aca4705','9690c0b6-a59b-11f1-913b-58733aca4705'),('9c3bad0a-a5a0-11f1-913b-58733aca4705','2026-09-01 06:31:05.970474',3,500.00,'2026-09-01 06:31:05.970474','4793e39a-a135-11f1-913b-58733aca4705','9c3b3e92-a5a0-11f1-913b-58733aca4705'),('9c3bf080-a5a0-11f1-913b-58733aca4705','2026-09-01 06:31:05.972131',8,700.00,'2026-09-01 06:31:05.972131','366954ae-9fb6-11f1-913b-58733aca4705','9c3b3e92-a5a0-11f1-913b-58733aca4705'),('ad317bf0-9fb8-11f1-913b-58733aca4705','2026-08-24 18:08:15.369759',100,70000.00,'2026-08-24 18:08:15.369759','366954ae-9fb6-11f1-913b-58733aca4705','ad3167c8-9fb8-11f1-913b-58733aca4705'),('af3c40d8-a35f-11f1-913b-58733aca4705','2026-08-29 09:41:18.238082',10,500.00,'2026-08-29 09:41:18.238082','4793e39a-a135-11f1-913b-58733aca4705','af3bb1b8-a35f-11f1-913b-58733aca4705'),('af3c9a2e-a35f-11f1-913b-58733aca4705','2026-08-29 09:41:18.240365',15,450.00,'2026-08-29 09:41:18.240365','a03e9bbc-a210-11f1-913b-58733aca4705','af3bb1b8-a35f-11f1-913b-58733aca4705'),('c0e069a6-a14b-11f1-913b-58733aca4705','2026-08-26 18:13:35.574871',3,500.00,'2026-08-26 18:13:35.574871','4793e39a-a135-11f1-913b-58733aca4705','c0e048cc-a14b-11f1-913b-58733aca4705'),('cd2d7f1e-a36c-11f1-913b-58733aca4705','2026-08-29 11:15:11.930370',3,500.00,'2026-08-29 11:15:11.930370','4793e39a-a135-11f1-913b-58733aca4705','cd2d0732-a36c-11f1-913b-58733aca4705'),('cd2ddc98-a36c-11f1-913b-58733aca4705','2026-08-29 11:15:11.932778',10,450.00,'2026-08-29 11:15:11.932778','a03e9bbc-a210-11f1-913b-58733aca4705','cd2d0732-a36c-11f1-913b-58733aca4705'),('d3364942-a36a-11f1-913b-58733aca4705','2026-08-29 11:01:03.061138',10,500.00,'2026-08-29 11:01:03.061138','4793e39a-a135-11f1-913b-58733aca4705','d335602c-a36a-11f1-913b-58733aca4705'),('d3372ccc-a36a-11f1-913b-58733aca4705','2026-08-29 11:01:03.066740',15,450.00,'2026-08-29 11:01:03.066740','a03e9bbc-a210-11f1-913b-58733aca4705','d335602c-a36a-11f1-913b-58733aca4705'),('db108f92-a135-11f1-913b-58733aca4705','2026-08-26 15:36:50.583234',10,500.00,'2026-08-26 15:36:50.583234','4793e39a-a135-11f1-913b-58733aca4705','db0f3f8e-a135-11f1-913b-58733aca4705'),('ddf69e04-a68f-11f1-913b-58733aca4705','2026-09-02 11:03:45.965877',14,10.00,'2026-09-02 11:03:45.965877','835b6d7c-a5f3-11f1-913b-58733aca4705','ddf600de-a68f-11f1-913b-58733aca4705'),('fc877834-a5a4-11f1-913b-58733aca4705','2026-09-01 07:02:25.515457',10,500.00,'2026-09-01 07:02:25.515457','4793e39a-a135-11f1-913b-58733aca4705','fc8696b2-a5a4-11f1-913b-58733aca4705'),('fc87f638-a5a4-11f1-913b-58733aca4705','2026-09-01 07:02:25.518595',20,700.00,'2026-09-01 07:02:25.518595','366954ae-9fb6-11f1-913b-58733aca4705','fc8696b2-a5a4-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `order_date` datetime(6) NOT NULL,
  `order_number` varchar(40) NOT NULL,
  `order_status` varchar(20) NOT NULL,
  `total_amount` decimal(19,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `shipping_city` varchar(255) DEFAULT NULL,
  `shipping_district` varchar(255) DEFAULT NULL,
  `shipping_address_line1` varchar(255) DEFAULT NULL,
  `shipping_address_line2` varchar(255) DEFAULT NULL,
  `shipping_address_line3` varchar(255) DEFAULT NULL,
  `shipping_postal_code` varchar(255) DEFAULT NULL,
  `shipping_state_province` varchar(255) DEFAULT NULL,
  `shipping_country_id` varchar(255) NOT NULL,
  `customer_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_number` (`order_number`),
  KEY `idx_order_number` (`order_number`),
  KEY `idx_order_customer` (`customer_id`),
  KEY `FK_orders_shipping_country_id` (`shipping_country_id`),
  CONSTRAINT `FK_orders_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
  CONSTRAINT `FK_orders_shipping_country_id` FOREIGN KEY (`shipping_country_id`) REFERENCES `country` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES ('084ef6e2-a5f5-11f1-913b-58733aca4705','2026-09-01 16:35:25.016107','2026-09-01 16:35:25.009590','ORD-019','CANCELLED',1200.00,'2026-09-01 17:11:00.201783','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','62a20f48-a14b-11f1-913b-58733aca4705'),('0ce62074-a5a3-11f1-913b-58733aca4705','2026-09-01 06:48:33.985860','2026-09-01 06:48:33.970686','ORD-016','CONFIRMED',23400.00,'2026-09-01 06:48:59.294033','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','62a20f48-a14b-11f1-913b-58733aca4705'),('135f5816-a59a-11f1-913b-58733aca4705','2026-09-01 05:44:19.376144','2026-09-01 05:44:19.345267','ORD-012','CONFIRMED',14000.00,'2026-09-01 05:44:55.963041','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','62a20f48-a14b-11f1-913b-58733aca4705'),('1baa86d6-a666-11f1-913b-58733aca4705','2026-09-02 06:04:50.623356','2026-09-02 06:04:50.617349','ORD-022','CONFIRMED',15000.00,'2026-09-02 06:05:09.190772','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','1c29e468-a665-11f1-913b-58733aca4705'),('389743ac-a665-11f1-913b-58733aca4705','2026-09-02 05:58:29.654361','2026-09-02 05:58:29.648250','ORD-020','CONFIRMED',200.00,'2026-09-02 06:02:26.738389','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','1c29e468-a665-11f1-913b-58733aca4705'),('46958d36-a5f4-11f1-913b-58733aca4705','2026-09-01 16:30:00.000881','2026-09-01 16:29:59.974117','ORD-018','CANCELLED',150.00,'2026-09-01 16:30:10.465689','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','62a20f48-a14b-11f1-913b-58733aca4705'),('46d8921c-a021-11f1-913b-58733aca4705','2026-08-25 06:37:00.816153','2026-08-25 06:37:00.802472','ORD-003','CANCELLED',140000.00,'2026-08-26 18:02:30.230661','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','33e79a0a-9f94-11f1-913b-58733aca4705'),('561c18b8-9fb6-11f1-913b-58733aca4705','2026-08-24 17:51:30.274663','2026-08-24 17:51:30.261423','ORD-001','CANCELLED',210000.00,'2026-08-24 18:07:35.890592','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','33e79a0a-9f94-11f1-913b-58733aca4705'),('79851556-a59d-11f1-913b-58733aca4705','2026-09-01 06:08:39.241153','2026-09-01 06:08:39.228475','ORD-014','CONFIRMED',5000.00,'2026-09-01 06:09:05.865183','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','62a20f48-a14b-11f1-913b-58733aca4705'),('85360f14-a14b-11f1-913b-58733aca4705','2026-08-26 18:11:55.472984','2026-08-26 18:11:55.463874','ORD-006','DELIVERED',9500.00,'2026-08-27 06:42:25.612683','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','62a20f48-a14b-11f1-913b-58733aca4705'),('8725f950-a665-11f1-913b-58733aca4705','2026-09-02 06:00:41.452001','2026-09-02 06:00:41.427424','ORD-021','PENDING',77000.00,'2026-09-02 06:00:41.452001','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','1c29e468-a665-11f1-913b-58733aca4705'),('94b7cfd4-a021-11f1-913b-58733aca4705','2026-08-25 06:39:11.463728','2026-08-25 06:39:11.456079','ORD-004','CANCELLED',1400.00,'2026-08-26 18:03:30.873487','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','33e79a0a-9f94-11f1-913b-58733aca4705'),('951f1fae-a468-11f1-913b-58733aca4705','2026-08-30 17:17:31.059744','2026-08-30 17:17:31.016433','ORD-011','CANCELLED',13100.00,'2026-09-01 17:11:00.194120','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','62a20f48-a14b-11f1-913b-58733aca4705'),('9690c0b6-a59b-11f1-913b-58733aca4705','2026-09-01 05:55:08.978172','2026-09-01 05:55:08.958731','ORD-013','CONFIRMED',11750.00,'2026-09-01 05:55:40.615289','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','62a20f48-a14b-11f1-913b-58733aca4705'),('9c3b3e92-a5a0-11f1-913b-58733aca4705','2026-09-01 06:31:05.967639','2026-09-01 06:31:05.949271','ORD-015','CONFIRMED',7100.00,'2026-09-01 06:31:27.094357','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','62a20f48-a14b-11f1-913b-58733aca4705'),('ad3167c8-9fb8-11f1-913b-58733aca4705','2026-08-24 18:08:15.369400','2026-08-24 18:08:15.358185','ORD-002','CANCELLED',7000000.00,'2026-08-26 16:41:55.341367','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','33e79a0a-9f94-11f1-913b-58733aca4705'),('af3bb1b8-a35f-11f1-913b-58733aca4705','2026-08-29 09:41:18.234336','2026-08-29 09:41:18.202270','ORD-008','CONFIRMED',11750.00,'2026-08-29 09:41:57.158073','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','62a20f48-a14b-11f1-913b-58733aca4705'),('c0e048cc-a14b-11f1-913b-58733aca4705','2026-08-26 18:13:35.574180','2026-08-26 18:13:35.561972','ORD-007','CANCELLED',1500.00,'2026-08-29 17:18:00.185543','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','62a20f48-a14b-11f1-913b-58733aca4705'),('cd2d0732-a36c-11f1-913b-58733aca4705','2026-08-29 11:15:11.927604','2026-08-29 11:15:11.913493','ORD-010','CONFIRMED',6000.00,'2026-08-29 11:15:41.930012','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','8331809c-a36a-11f1-913b-58733aca4705'),('d335602c-a36a-11f1-913b-58733aca4705','2026-08-29 11:01:03.054951','2026-08-29 11:01:03.027780','ORD-009','CONFIRMED',11750.00,'2026-08-29 11:01:35.196609','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','8331809c-a36a-11f1-913b-58733aca4705'),('db0f3f8e-a135-11f1-913b-58733aca4705','2026-08-26 15:36:50.574635','2026-08-26 15:36:50.549426','ORD-005','CONFIRMED',5000.00,'2026-08-26 15:38:45.030004','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','33e79a0a-9f94-11f1-913b-58733aca4705'),('ddf600de-a68f-11f1-913b-58733aca4705','2026-09-02 11:03:45.961990','2026-09-02 11:03:45.949515','ORD-023','PENDING',140.00,'2026-09-02 11:03:45.961990','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','1c29e468-a665-11f1-913b-58733aca4705'),('fc8696b2-a5a4-11f1-913b-58733aca4705','2026-09-01 07:02:25.509957','2026-09-01 07:02:25.477739','ORD-017','CONFIRMED',19000.00,'2026-09-01 07:02:51.727847','Tokyo','Chiyoda','5 Chiyoda Avenue','Building 3',NULL,'100-0001','Tokyo','31dac418-9efd-11f1-913b-58733aca4705','d7f7eeae-a5a4-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `payment_method` varchar(20) NOT NULL,
  `payment_number` varchar(30) NOT NULL,
  `status` varchar(20) NOT NULL,
  `total_amount` decimal(19,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `order_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `payment_number` (`payment_number`),
  UNIQUE KEY `order_id` (`order_id`),
  UNIQUE KEY `idx_payment_number` (`payment_number`),
  KEY `idx_payment_order` (`order_id`),
  CONSTRAINT `FK_payments_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES ('0c21ecd4-a5a5-11f1-913b-58733aca4705','2026-09-01 07:02:51.694069','2026-09-01 07:02:51.687983','ONLINE','PAY-013','PAID',19000.00,'2026-09-01 07:02:51.694069','fc8696b2-a5a4-11f1-913b-58733aca4705'),('1bf7d1ac-a5a3-11f1-913b-58733aca4705','2026-09-01 06:48:59.267749','2026-09-01 06:48:59.266585','ONLINE','PAY-012','PAID',23400.00,'2026-09-01 06:48:59.267749','0ce62074-a5a3-11f1-913b-58733aca4705'),('1f41c6cc-a136-11f1-913b-58733aca4705','2026-08-26 15:38:44.990924','2026-08-26 15:38:44.990149','ONLINE','PAY-003','PAID',5000.00,'2026-08-26 15:38:44.990924','db0f3f8e-a135-11f1-913b-58733aca4705'),('26ba5876-a666-11f1-913b-58733aca4705','2026-09-02 06:05:09.182127','2026-09-02 06:05:09.181002','ONLINE','PAY-015','PAID',15000.00,'2026-09-02 06:05:09.182127','1baa86d6-a666-11f1-913b-58733aca4705'),('2927d1d2-a59a-11f1-913b-58733aca4705','2026-09-01 05:44:55.922116','2026-09-01 05:44:55.920971','ONLINE','PAY-008','PAID',14000.00,'2026-09-01 05:44:55.922116','135f5816-a59a-11f1-913b-58733aca4705'),('5382da8a-a022-11f1-913b-58733aca4705','2026-08-25 06:44:31.562420','2026-07-25 06:44:31.558000','ONLINE','PAY-002','PAID',1400.00,'2026-08-25 06:44:31.562420','94b7cfd4-a021-11f1-913b-58733aca4705'),('896064a8-a59d-11f1-913b-58733aca4705','2026-09-01 06:09:05.844302','2026-09-01 06:09:05.843268','ONLINE','PAY-010','PAID',5000.00,'2026-09-01 06:09:05.844302','79851556-a59d-11f1-913b-58733aca4705'),('94b6b538-a14b-11f1-913b-58733aca4705','2026-08-26 18:12:21.481958','2026-01-26 18:12:21.480000','ONLINE','PAY-004','PAID',9500.00,'2026-08-26 18:12:21.481958','85360f14-a14b-11f1-913b-58733aca4705'),('a8d00c64-a5a0-11f1-913b-58733aca4705','2026-09-01 06:31:27.075761','2026-09-01 06:31:27.074408','ONLINE','PAY-011','PAID',7100.00,'2026-09-01 06:31:27.075761','9c3b3e92-a5a0-11f1-913b-58733aca4705'),('a9641b3e-a59b-11f1-913b-58733aca4705','2026-09-01 05:55:40.562356','2026-09-01 05:55:40.561187','ONLINE','PAY-009','PAID',11750.00,'2026-09-01 05:55:40.562356','9690c0b6-a59b-11f1-913b-58733aca4705'),('c5e3d702-a665-11f1-913b-58733aca4705','2026-09-02 06:02:26.715030','2026-09-02 06:02:26.713853','ONLINE','PAY-014','PAID',200.00,'2026-09-02 06:02:26.715030','389743ac-a665-11f1-913b-58733aca4705'),('c66aeff2-a35f-11f1-913b-58733aca4705','2026-08-29 09:41:57.131554','2026-04-29 09:41:57.130000','ONLINE','PAY-005','PAID',11750.00,'2026-08-29 09:41:57.131554','af3bb1b8-a35f-11f1-913b-58733aca4705'),('df0c6538-a36c-11f1-913b-58733aca4705','2026-08-29 11:15:41.912596','2026-03-01 11:15:41.911000','ONLINE','PAY-007','PAID',6000.00,'2026-08-29 11:15:41.912596','cd2d0732-a36c-11f1-913b-58733aca4705'),('e653e35e-a36a-11f1-913b-58733aca4705','2026-08-29 11:01:35.131758','2026-08-29 11:01:35.129851','ONLINE','PAY-006','PAID',11750.00,'2026-08-29 11:01:35.131758','d335602c-a36a-11f1-913b-58733aca4705'),('e9d9a09c-9fbc-11f1-913b-58733aca4705','2026-08-24 18:38:35.122211','2026-08-24 18:38:35.121186','CARD','PAY-001','PAID',7000000.00,'2026-08-24 18:38:35.122211','ad3167c8-9fb8-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `product_number` varchar(30) NOT NULL,
  `reorder_level` int NOT NULL,
  `title` varchar(150) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `product_number` (`product_number`),
  UNIQUE KEY `uk_product_title` (`title`),
  KEY `idx_product_title` (`title`),
  KEY `idx_product_number` (`product_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES ('343a0fa0-a5f3-11f1-913b-58733aca4705','2026-09-01 16:22:19.705590','Add two rows of the holes for the pins. So it can be connected with normal Dubond line','PR-003',100,'Arduino Compatible R3 ATmega328P','2026-09-01 16:22:19.705590'),('5cb0b4c0-a134-11f1-913b-58733aca4705','2026-08-26 15:26:09.066694','Glass Elite VisionGuard is made with ultra-strong tempered glass and has a blue light filter developed especially for people who spend a lot of time on their screens. And unlike other blue light solutions, Glass Elite VisionGuard won’t tamper with the colors on your screen.','PR-002',50,'Glass Elite Tempered Glass','2026-08-26 15:26:09.066694'),('7694de6a-9fb4-11f1-913b-58733aca4705','2026-08-24 17:38:05.759580','Nvidia GeForce RTX 5090 with 32GB of GDDR7 memory, delivering unmatched 4K and 8K gaming and AI performance','PR-001',300,'Nvidia GeForce RTX 5090','2026-08-24 17:38:05.759580');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refresh_tokens`
--

DROP TABLE IF EXISTS `refresh_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refresh_tokens` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `expires_at` datetime(6) DEFAULT NULL,
  `token_hash` varchar(64) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token_hash` (`token_hash`),
  UNIQUE KEY `user_id` (`user_id`),
  UNIQUE KEY `idx_refresh_token_hash` (`token_hash`),
  KEY `idx_refresh_token_expires` (`expires_at`),
  KEY `idx_refresh_token_user` (`user_id`),
  CONSTRAINT `fk_refresh_token_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refresh_tokens`
--

LOCK TABLES `refresh_tokens` WRITE;
/*!40000 ALTER TABLE `refresh_tokens` DISABLE KEYS */;
INSERT INTO `refresh_tokens` VALUES ('0e6b0186-a660-11f1-913b-58733aca4705','2026-09-02 05:21:31.417415','2026-10-02 05:21:31.414187','f66c97405d2d7360e7cb4c47550b78e2d582cbe7dfa5a2a22ccb4179d833d530','2026-09-02 05:21:31.417415','629d3798-a14b-11f1-913b-58733aca4705'),('27f4ed68-a685-11f1-913b-58733aca4705','2026-09-02 09:47:05.642939','2026-10-02 09:47:05.638480','b00596a8deb7db8c36482c510500978ffdafe4530bb773cd5c73a2f6c5bc4f2c','2026-09-02 09:47:05.642939','15f67a8e-a043-11f1-913b-58733aca4705'),('31e73564-a460-11f1-913b-58733aca4705','2026-08-30 16:17:28.625261','2026-09-29 16:17:28.624096','1a2e1f3b48e2457389c45d0d788354bd983a39e023553574d5435d7f39d04eee','2026-08-30 16:17:28.625261','33e34df6-9f94-11f1-913b-58733aca4705'),('3a444198-9f60-11f1-913b-58733aca4705','2026-08-24 07:35:06.842737','2026-09-23 07:35:06.841856','c30fd74d2bac3de2baed30a61bcbdf77a5bcb1fbff65369d6d38651f30a01a86','2026-08-24 07:35:06.842737','26b67772-9f60-11f1-913b-58733aca4705'),('6535a7ec-a36c-11f1-913b-58733aca4705','2026-08-29 11:12:17.500946','2026-09-28 11:12:17.499438','6dd9880c7396a8d85aa2d9e7ff03186f32e1c97652f9253e69abf825e6fe9fa4','2026-08-29 11:12:17.500946','832bea7e-a36a-11f1-913b-58733aca4705'),('733e0f8a-a5df-11f1-913b-58733aca4705','2026-09-01 14:00:55.493627','2026-10-01 14:00:55.492069','d942f655b364db2c0ce5246200663d3c2344f03ddee63dc3d236d53543117b8f','2026-09-01 14:00:55.493627','787b79c2-9fb5-11f1-913b-58733aca4705'),('cf80e352-a68f-11f1-913b-58733aca4705','2026-09-02 11:03:21.707418','2026-10-02 11:03:21.703128','0a83eb92b1d561c5effd019852f57ed2c7f7bfc9e730dc7cb96798324621d99c','2026-09-02 11:03:21.707418','1c278d08-a665-11f1-913b-58733aca4705'),('dee841c8-a5a4-11f1-913b-58733aca4705','2026-09-01 07:01:35.818191','2026-10-01 07:01:35.817245','74c9b41f5a96a03150624986a54ffc4444206456044d212e8dc61e8ba720ec70','2026-09-01 07:01:35.818191','d7f317da-a5a4-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `refresh_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `name` varchar(50) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES ('31d5ec72-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.524319','ADMIN','2026-08-23 19:46:12.524319'),('31d92cd4-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.542990','OPERATIONS_MANAGER','2026-08-23 19:46:12.542990'),('31d943b8-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.543371','LOGISTICS_OFFICER','2026-08-23 19:46:12.543371'),('31d95ac4-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.543982','WAREHOUSE_MANAGER','2026-08-23 19:46:12.543982'),('31d96690-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.544259','CUSTOMER','2026-08-23 19:46:12.544259'),('31d97072-9efd-11f1-913b-58733aca4705','2026-08-23 19:46:12.544497','VENDOR','2026-08-23 19:46:12.544497');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipment_items`
--

DROP TABLE IF EXISTS `shipment_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipment_items` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `status` varchar(30) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `order_item_id` varchar(255) NOT NULL,
  `shipment_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shipment_order_item` (`shipment_id`,`order_item_id`),
  KEY `idx_shipment_item_shipment` (`shipment_id`),
  KEY `idx_shipment_item_order_item` (`order_item_id`),
  CONSTRAINT `fk_shipment_item_order_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`id`),
  CONSTRAINT `fk_shipment_item_shipment` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipment_items`
--

LOCK TABLES `shipment_items` WRITE;
/*!40000 ALTER TABLE `shipment_items` DISABLE KEYS */;
INSERT INTO `shipment_items` VALUES ('0c314bac-a5a5-11f1-913b-58733aca4705','2026-09-01 07:02:51.794180','PENDING','2026-09-01 07:02:51.794180','fc877834-a5a4-11f1-913b-58733aca4705','0c2ff388-a5a5-11f1-913b-58733aca4705'),('0c31a5f2-a5a5-11f1-913b-58733aca4705','2026-09-01 07:02:51.796425','PENDING','2026-09-01 07:02:51.796425','fc87f638-a5a4-11f1-913b-58733aca4705','0c2ff388-a5a5-11f1-913b-58733aca4705'),('1c009922-a5a3-11f1-913b-58733aca4705','2026-09-01 06:48:59.325091','PENDING','2026-09-01 06:48:59.325091','0ce72384-a5a3-11f1-913b-58733aca4705','1c00264a-a5a3-11f1-913b-58733aca4705'),('1c00d36a-a5a3-11f1-913b-58733aca4705','2026-09-01 06:48:59.326554','PENDING','2026-09-01 06:48:59.326554','0ce784dc-a5a3-11f1-913b-58733aca4705','1c00264a-a5a3-11f1-913b-58733aca4705'),('1f52f83e-a136-11f1-913b-58733aca4705','2026-08-26 15:38:45.103910','SHIPPED','2026-08-27 05:59:17.808481','db108f92-a135-11f1-913b-58733aca4705','1f51d5d0-a136-11f1-913b-58733aca4705'),('26bdabc0-a666-11f1-913b-58733aca4705','2026-09-02 06:05:09.203635','DELIVERED','2026-09-02 06:13:06.053655','1baaa1fc-a666-11f1-913b-58733aca4705','26bd9e3c-a666-11f1-913b-58733aca4705'),('26c03bd8-a666-11f1-913b-58733aca4705','2026-09-02 06:05:09.220454','PENDING','2026-09-02 06:05:09.220454','1baa9824-a666-11f1-913b-58733aca4705','26c02fa8-a666-11f1-913b-58733aca4705'),('5391025e-a022-11f1-913b-58733aca4705','2026-08-25 06:44:31.654261','CANCELLED','2026-08-26 18:03:30.889891','94b83974-a021-11f1-913b-58733aca4705','53906420-a022-11f1-913b-58733aca4705'),('8968de30-a59d-11f1-913b-58733aca4705','2026-09-01 06:09:05.899857','PENDING','2026-09-01 06:09:05.899857','7985e5ee-a59d-11f1-913b-58733aca4705','89681766-a59d-11f1-913b-58733aca4705'),('94c36684-a14b-11f1-913b-58733aca4705','2026-08-26 18:12:21.564996','DELIVERED','2026-08-27 06:42:25.595378','853620ee-a14b-11f1-913b-58733aca4705','94c34262-a14b-11f1-913b-58733aca4705'),('94c37200-a14b-11f1-913b-58733aca4705','2026-08-26 18:12:21.565271','DELIVERED','2026-08-27 06:42:25.605952','853684d0-a14b-11f1-913b-58733aca4705','94c34262-a14b-11f1-913b-58733aca4705'),('a8d57352-a5a0-11f1-913b-58733aca4705','2026-09-01 06:31:27.110838','PENDING','2026-09-01 06:31:27.110838','9c3bad0a-a5a0-11f1-913b-58733aca4705','a8d55656-a5a0-11f1-913b-58733aca4705'),('a8d5bc18-a5a0-11f1-913b-58733aca4705','2026-09-01 06:31:27.112749','PENDING','2026-09-01 06:31:27.112749','9c3bf080-a5a0-11f1-913b-58733aca4705','a8d55656-a5a0-11f1-913b-58733aca4705'),('a9769f2a-a59b-11f1-913b-58733aca4705','2026-09-01 05:55:40.683592','PENDING','2026-09-01 05:55:40.683592','96923b26-a59b-11f1-913b-58733aca4705','a975c8ac-a59b-11f1-913b-58733aca4705'),('a97a26d6-a59b-11f1-913b-58733aca4705','2026-09-01 05:55:40.706590','SHIPPED','2026-09-01 05:59:35.170765','9691b96c-a59b-11f1-913b-58733aca4705','a97a14d4-a59b-11f1-913b-58733aca4705'),('c5ed69a2-a665-11f1-913b-58733aca4705','2026-09-02 06:02:26.777709','PENDING','2026-09-02 06:02:26.777709','389806ca-a665-11f1-913b-58733aca4705','c5ec4a18-a665-11f1-913b-58733aca4705'),('c675107c-a35f-11f1-913b-58733aca4705','2026-08-29 09:41:57.197951','SHIPPED','2026-08-29 13:23:16.046749','af3c9a2e-a35f-11f1-913b-58733aca4705','c6741b04-a35f-11f1-913b-58733aca4705'),('c6785c64-a35f-11f1-913b-58733aca4705','2026-08-29 09:41:57.219432','SHIPPED','2026-08-29 18:46:31.349787','af3c40d8-a35f-11f1-913b-58733aca4705','c6784f30-a35f-11f1-913b-58733aca4705'),('df11578c-a36c-11f1-913b-58733aca4705','2026-08-29 11:15:41.944800','SHIPPED','2026-08-29 13:43:27.705641','cd2ddc98-a36c-11f1-913b-58733aca4705','df114260-a36c-11f1-913b-58733aca4705'),('df131b9e-a36c-11f1-913b-58733aca4705','2026-08-29 11:15:41.956385','SHIPPED','2026-08-29 19:45:37.147510','cd2d7f1e-a36c-11f1-913b-58733aca4705','df130f32-a36c-11f1-913b-58733aca4705'),('e66192ec-a36a-11f1-913b-58733aca4705','2026-08-29 11:01:35.221224','DELIVERED','2026-08-30 15:59:56.823201','d3372ccc-a36a-11f1-913b-58733aca4705','e6617cbc-a36a-11f1-913b-58733aca4705'),('e663ba7c-a36a-11f1-913b-58733aca4705','2026-08-29 11:01:35.235346','SHIPPED','2026-08-29 19:45:33.998027','d3364942-a36a-11f1-913b-58733aca4705','e663af28-a36a-11f1-913b-58733aca4705'),('e9eb3eb0-9fbc-11f1-913b-58733aca4705','2026-08-24 18:38:35.237522','SHIPPED','2026-08-28 18:50:04.116735','ad317bf0-9fb8-11f1-913b-58733aca4705','e9ea49f6-9fbc-11f1-913b-58733aca4705'),('f3670cb6-a65c-11f1-913b-58733aca4705','2026-09-02 04:59:17.602027','PENDING','2026-09-02 04:59:17.602027','13610b02-a59a-11f1-913b-58733aca4705','f3568170-a65c-11f1-913b-58733aca4705'),('f3760f22-a65c-11f1-913b-58733aca4705','2026-09-02 04:59:17.700227','PENDING','2026-09-02 04:59:17.700227','136088da-a59a-11f1-913b-58733aca4705','f375d8d6-a65c-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `shipment_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipment_tracking`
--

DROP TABLE IF EXISTS `shipment_tracking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipment_tracking` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(300) DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `status` varchar(30) NOT NULL,
  `tracking_timestamp` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `shipment_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tracking_shipment` (`shipment_id`),
  KEY `idx_tracking_timestamp` (`tracking_timestamp`),
  CONSTRAINT `FK_shipment_tracking_shipment_id` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipment_tracking`
--

LOCK TABLES `shipment_tracking` WRITE;
/*!40000 ALTER TABLE `shipment_tracking` DISABLE KEYS */;
INSERT INTO `shipment_tracking` VALUES ('012a7dfa-a3ac-11f1-913b-58733aca4705','2026-08-29 18:47:37.447385','Shipments label creating','Warehouse','LABEL_CREATED','2026-08-29 18:47:37.441695','2026-08-29 18:47:37.447385','e663af28-a36a-11f1-913b-58733aca4705'),('04faeb6a-a45e-11f1-913b-58733aca4705','2026-08-30 16:01:54.265977','Shipments are currently at the port','port','ARRIVED_AT_PORT','2026-08-30 16:01:54.254059','2026-08-30 16:01:54.265977','e9ea49f6-9fbc-11f1-913b-58733aca4705'),('0671ce7a-a1b4-11f1-913b-58733aca4705','2026-08-27 06:39:59.951805','Arrived at Colombo PORT','Colombo Port','ARRIVED_AT_PORT','2026-08-27 06:39:59.944548','2026-08-27 06:39:59.951805','94c34262-a14b-11f1-913b-58733aca4705'),('11a6f362-a667-11f1-913b-58733aca4705','2026-09-02 06:11:43.320536','Shipments are in Transit','In Transir','IN_TRANSIT','2026-09-02 06:11:43.312164','2026-09-02 06:11:43.320536','26bd9e3c-a666-11f1-913b-58733aca4705'),('19552756-a3b4-11f1-913b-58733aca4705','2026-08-29 19:45:33.965732','Shipment picked up from warehouse: GlobalTrade-Warehouse-A','Warehouse: GlobalTrade-Warehouse-A','PICKED_UP','2026-08-29 19:45:33.960621','2026-08-29 19:45:33.965732','e663af28-a36a-11f1-913b-58733aca4705'),('1b397eaa-a3b4-11f1-913b-58733aca4705','2026-08-29 19:45:37.139327','Shipment picked up from warehouse: GlobalTrade-Warehouse-A','Warehouse: GlobalTrade-Warehouse-A','PICKED_UP','2026-08-29 19:45:37.137725','2026-08-29 19:45:37.139327','df130f32-a36c-11f1-913b-58733aca4705'),('1b79d9d4-a380-11f1-913b-58733aca4705','2026-08-29 13:33:23.731856','Shipment items are being proccessing','Warehouse','PROCESSING','2026-08-29 13:33:23.725013','2026-08-29 13:33:23.731856','df130f32-a36c-11f1-913b-58733aca4705'),('1f8f8b2e-a667-11f1-913b-58733aca4705','2026-09-02 06:12:06.655092','Shipments arrived at port','in port','ARRIVED_AT_PORT','2026-09-02 06:12:06.650241','2026-09-02 06:12:06.655092','26bd9e3c-a666-11f1-913b-58733aca4705'),('2087f4e2-a59c-11f1-913b-58733aca4705','2026-09-01 05:59:00.446961','Shipments are being processed','Warehouse','PROCESSING','2026-09-01 05:59:00.442022','2026-09-01 05:59:00.446961','a97a14d4-a59b-11f1-913b-58733aca4705'),('263bb306-a1b4-11f1-913b-58733aca4705','2026-08-27 06:40:53.285801','Customs Clearence','Colombo Custom','CUSTOMS_CLEARANCE','2026-08-27 06:40:53.277285','2026-08-27 06:40:53.285801','94c34262-a14b-11f1-913b-58733aca4705'),('2b23f7ae-a667-11f1-913b-58733aca4705','2026-09-02 06:12:26.082570','Custom clearence','Port','CUSTOMS_CLEARANCE','2026-09-02 06:12:26.078002','2026-09-02 06:12:26.082570','26bd9e3c-a666-11f1-913b-58733aca4705'),('2e26d5de-a2e3-11f1-913b-58733aca4705','2026-08-28 18:50:04.077201','Shipment picked up from warehouse: GlobalTrade-Warehouse-A','Warehouse: GlobalTrade-Warehouse-A','PICKED_UP','2026-08-28 18:50:04.075156','2026-08-28 18:50:04.077201','e9ea49f6-9fbc-11f1-913b-58733aca4705'),('3491e99a-a379-11f1-913b-58733aca4705','2026-08-29 12:43:59.356549','Label created','Warehouse','LABEL_CREATED','2026-08-29 12:43:59.349381','2026-08-29 12:43:59.356549','df114260-a36c-11f1-913b-58733aca4705'),('353928c0-a59c-11f1-913b-58733aca4705','2026-09-01 05:59:35.161962','Shipment picked up from warehouse: GlobalTrade-Warehouse-A','Warehouse: GlobalTrade-Warehouse-A','PICKED_UP','2026-09-01 05:59:35.160639','2026-09-01 05:59:35.161962','a97a14d4-a59b-11f1-913b-58733aca4705'),('39357700-a667-11f1-913b-58733aca4705','2026-09-02 06:12:49.686067','Shipments are out for delivery','Port','OUT_FOR_DELIVERY','2026-09-02 06:12:49.679112','2026-09-02 06:12:49.686067','26bd9e3c-a666-11f1-913b-58733aca4705'),('42f2e5b6-a667-11f1-913b-58733aca4705','2026-09-02 06:13:06.026616','Shipments are delivered','Warehouse','DELIVERED','2026-09-02 06:13:06.020312','2026-09-02 06:13:06.026616','26bd9e3c-a666-11f1-913b-58733aca4705'),('464431fc-a37e-11f1-913b-58733aca4705','2026-08-29 13:20:16.529741','The shipment items are being processing','Warehouse','PROCESSING','2026-08-29 13:20:16.516171','2026-08-29 13:20:16.529741','c6741b04-a35f-11f1-913b-58733aca4705'),('471400d8-a1b4-11f1-913b-58733aca4705','2026-08-27 06:41:48.388988','Packages are ready to be delivered','Colombo Custom','OUT_FOR_DELIVERY','2026-08-27 06:41:48.380779','2026-08-27 06:41:48.388988','94c34262-a14b-11f1-913b-58733aca4705'),('53ff1ea2-a378-11f1-913b-58733aca4705','2026-08-29 12:37:42.584152','Shipping labels generated','Warehouse','LABEL_CREATED','2026-08-29 12:37:42.574212','2026-08-29 12:37:42.584152','c6784f30-a35f-11f1-913b-58733aca4705'),('56c85a16-a1ae-11f1-913b-58733aca4705','2026-08-27 05:59:17.755386','Shipment picked up from warehouse: GlobalTrade-Warehouse-A','Warehouse: GlobalTrade-Warehouse-A','PICKED_UP','2026-08-27 05:59:17.752317','2026-08-27 05:59:17.755386','1f51d5d0-a136-11f1-913b-58733aca4705'),('5b63cc78-a2e3-11f1-913b-58733aca4705','2026-08-28 18:51:19.974933','Colombo In Transit','Colombo In Transit','IN_TRANSIT','2026-08-28 18:51:19.968443','2026-08-28 18:51:19.974933','e9ea49f6-9fbc-11f1-913b-58733aca4705'),('5d3d8eec-a1b4-11f1-913b-58733aca4705','2026-08-27 06:42:25.570987','Packages are successfully delivered','Colombo','DELIVERED','2026-08-27 06:42:25.565008','2026-08-27 06:42:25.570987','94c34262-a14b-11f1-913b-58733aca4705'),('8375d500-a381-11f1-913b-58733aca4705','2026-08-29 13:43:27.684718','Shipment picked up from warehouse: GlobalTrade-Warehouse-B','Warehouse: GlobalTrade-Warehouse-B','PICKED_UP','2026-08-29 13:43:27.682982','2026-08-29 13:43:27.684718','df114260-a36c-11f1-913b-58733aca4705'),('8399c3f6-a378-11f1-913b-58733aca4705','2026-08-29 12:39:02.450485','Label creating','Warehouse','LABEL_CREATED','2026-08-29 12:39:02.440582','2026-08-29 12:39:02.450485','df130f32-a36c-11f1-913b-58733aca4705'),('90987740-a379-11f1-913b-58733aca4705','2026-08-29 12:46:33.749312','Iteams are processing','Warehouse','PROCESSING','2026-08-29 12:46:33.744063','2026-08-29 12:46:33.749312','df114260-a36c-11f1-913b-58733aca4705'),('962590b0-a59c-11f1-913b-58733aca4705','2026-09-01 06:02:17.773556','Shipments are in Transit','Port','IN_TRANSIT','2026-09-01 06:02:17.768387','2026-09-01 06:02:17.773556','a97a14d4-a59b-11f1-913b-58733aca4705'),('9de2c7ec-a1ae-11f1-913b-58733aca4705','2026-08-27 06:01:17.047462','Shipment picked up from warehouse: GlobalTrade-Warehouse-A','Warehouse: GlobalTrade-Warehouse-A','PICKED_UP','2026-08-27 06:01:17.044494','2026-08-27 06:01:17.047462','94c34262-a14b-11f1-913b-58733aca4705'),('aca9297c-a45d-11f1-913b-58733aca4705','2026-08-30 15:59:26.090329','Shipments are currently at the port','at port','ARRIVED_AT_PORT','2026-08-30 15:59:26.082156','2026-08-30 15:59:26.090329','e6617cbc-a36a-11f1-913b-58733aca4705'),('ad15cedc-a666-11f1-913b-58733aca4705','2026-09-02 06:08:54.597190','Label creating','Warehouse','LABEL_CREATED','2026-09-02 06:08:54.588608','2026-09-02 06:08:54.597190','26bd9e3c-a666-11f1-913b-58733aca4705'),('ada3b0ea-a381-11f1-913b-58733aca4705','2026-08-29 13:44:38.450154','Shipments are in transit','In Transir','IN_TRANSIT','2026-08-29 13:44:38.445344','2026-08-29 13:44:38.450154','c6741b04-a35f-11f1-913b-58733aca4705'),('b13f90d2-a37e-11f1-913b-58733aca4705','2026-08-29 13:23:16.014000','Shipment picked up from warehouse: GlobalTrade-Warehouse-B','Warehouse: GlobalTrade-Warehouse-B','PICKED_UP','2026-08-29 13:23:16.012642','2026-08-29 13:23:16.014000','c6741b04-a35f-11f1-913b-58733aca4705'),('bc5dbcec-a666-11f1-913b-58733aca4705','2026-09-02 06:09:20.234006','Shipment items are being processed','Warehouse','PROCESSING','2026-09-02 06:09:20.229672','2026-09-02 06:09:20.234006','26bd9e3c-a666-11f1-913b-58733aca4705'),('bef5e3a4-a45d-11f1-913b-58733aca4705','2026-08-30 15:59:56.790851','Shipments are delivered','Custom','DELIVERED','2026-08-30 15:59:56.784202','2026-08-30 15:59:56.790851','e6617cbc-a36a-11f1-913b-58733aca4705'),('c4988ebe-a666-11f1-913b-58733aca4705','2026-09-02 06:09:34.040764','Shipment picked up from warehouse: GlobalTrade-Warehouse-B','Warehouse: GlobalTrade-Warehouse-B','PICKED_UP','2026-09-02 06:09:34.039404','2026-09-02 06:09:34.040764','26bd9e3c-a666-11f1-913b-58733aca4705'),('ccef62a4-a666-11f1-913b-58733aca4705','2026-09-02 06:09:48.032509','Label creating','Warehouse','LABEL_CREATED','2026-09-02 06:09:48.027829','2026-09-02 06:09:48.032509','26c02fa8-a666-11f1-913b-58733aca4705'),('d04f9106-a1b3-11f1-913b-58733aca4705','2026-08-27 06:38:29.130296','Packages are in transition','Colombo In Transit','IN_TRANSIT','2026-08-27 06:38:29.119694','2026-08-27 06:38:29.130296','94c34262-a14b-11f1-913b-58733aca4705'),('d479b24e-a3ab-11f1-913b-58733aca4705','2026-08-29 18:46:22.468937','Items are being processed','Warehouse','PROCESSING','2026-08-29 18:46:22.457236','2026-08-29 18:46:22.468937','c6784f30-a35f-11f1-913b-58733aca4705'),('d6bdbfaa-a662-11f1-913b-58733aca4705','2026-09-02 05:41:26.497518','Label creating','Warehouse','LABEL_CREATED','2026-09-02 05:41:26.487583','2026-09-02 05:41:26.497518','f375d8d6-a65c-11f1-913b-58733aca4705'),('d9c0c5e4-a3ab-11f1-913b-58733aca4705','2026-08-29 18:46:31.322458','Shipment picked up from warehouse: GlobalTrade-Warehouse-A','Warehouse: GlobalTrade-Warehouse-A','PICKED_UP','2026-08-29 18:46:31.320935','2026-08-29 18:46:31.322458','c6784f30-a35f-11f1-913b-58733aca4705'),('e063023a-a45b-11f1-913b-58733aca4705','2026-08-30 15:46:33.877399','Label generationg','Warehouse','LABEL_CREATED','2026-08-30 15:46:33.852432','2026-08-30 15:46:33.877399','e6617cbc-a36a-11f1-913b-58733aca4705'),('ed091c7c-a59b-11f1-913b-58733aca4705','2026-09-01 05:57:34.051746','Label creating','Warehouse','LABEL_CREATED','2026-09-01 05:57:34.046797','2026-09-01 05:57:34.051746','a97a14d4-a59b-11f1-913b-58733aca4705'),('f18855ca-a662-11f1-913b-58733aca4705','2026-09-02 05:42:11.448016','Labe creating','Warehouse','LABEL_CREATED','2026-09-02 05:42:11.438467','2026-09-02 05:42:11.448016','f3568170-a65c-11f1-913b-58733aca4705'),('f4519ba8-a45b-11f1-913b-58733aca4705','2026-08-30 15:47:07.316904','Shipment picked up from warehouse: GlobalTrade-Warehouse-B','Warehouse: GlobalTrade-Warehouse-B','PICKED_UP','2026-08-30 15:47:07.315128','2026-08-30 15:47:07.316904','e6617cbc-a36a-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `shipment_tracking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipments`
--

DROP TABLE IF EXISTS `shipments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipments` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `delivered_at` datetime(6) DEFAULT NULL,
  `estimated_delivery_date` datetime(6) DEFAULT NULL,
  `priority` int NOT NULL,
  `route_distance_km` decimal(10,2) DEFAULT NULL,
  `route_estimated_hours` decimal(10,2) DEFAULT NULL,
  `route_name` varchar(150) DEFAULT NULL,
  `route_risk_score` int DEFAULT NULL,
  `shipment_number` varchar(30) NOT NULL,
  `shipped_at` datetime(6) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `destination_city` varchar(255) DEFAULT NULL,
  `destination_district` varchar(255) DEFAULT NULL,
  `destination_address_line1` varchar(255) DEFAULT NULL,
  `destination_address_line2` varchar(255) DEFAULT NULL,
  `destination_address_line3` varchar(255) DEFAULT NULL,
  `destination_postal_code` varchar(255) DEFAULT NULL,
  `destination_state_province` varchar(255) DEFAULT NULL,
  `destination_country_id` varchar(255) NOT NULL,
  `origin_city` varchar(255) DEFAULT NULL,
  `origin_district` varchar(255) DEFAULT NULL,
  `origin_address_line1` varchar(255) DEFAULT NULL,
  `origin_address_line2` varchar(255) DEFAULT NULL,
  `origin_address_line3` varchar(255) DEFAULT NULL,
  `origin_postal_code` varchar(255) DEFAULT NULL,
  `origin_state_province` varchar(255) DEFAULT NULL,
  `origin_country_id` varchar(255) NOT NULL,
  `order_id` varchar(255) NOT NULL,
  `warehouse_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `shipment_number` (`shipment_number`),
  KEY `idx_shipment_number` (`shipment_number`),
  KEY `idx_shipment_order` (`order_id`),
  KEY `FK_shipments_destination_country_id` (`destination_country_id`),
  KEY `FK_shipments_origin_country_id` (`origin_country_id`),
  KEY `FK_shipments_warehouse_id` (`warehouse_id`),
  CONSTRAINT `FK_shipments_destination_country_id` FOREIGN KEY (`destination_country_id`) REFERENCES `country` (`id`),
  CONSTRAINT `FK_shipments_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FK_shipments_origin_country_id` FOREIGN KEY (`origin_country_id`) REFERENCES `country` (`id`),
  CONSTRAINT `FK_shipments_warehouse_id` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipments`
--

LOCK TABLES `shipments` WRITE;
/*!40000 ALTER TABLE `shipments` DISABLE KEYS */;
INSERT INTO `shipments` VALUES ('0c2ff388-a5a5-11f1-913b-58733aca4705','2026-09-01 07:02:51.785298',NULL,'2026-09-05 09:00:00.138503',0,3500.00,70.00,'Sri Lanka -> Japan',10,'SHI-016',NULL,'PENDING','2026-09-02 11:00:00.194183','Tokyo','Chiyoda','5 Chiyoda Avenue','Building 3',NULL,'100-0001','Tokyo','31dac418-9efd-11f1-913b-58733aca4705','Colombo','Colombo','No 45','New Lotus Road',NULL,'12345','Western','31d9a25e-9efd-11f1-913b-58733aca4705','fc8696b2-a5a4-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('1c00264a-a5a3-11f1-913b-58733aca4705','2026-09-01 06:48:59.322168',NULL,'2026-09-02 16:00:00.141310',0,250.00,5.00,'Sri Lanka -> Sri Lanka',35,'SHI-015',NULL,'PENDING','2026-09-02 11:00:00.194596','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Colombo','Colombo','No 45','New Lotus Road',NULL,'12345','Western','31d9a25e-9efd-11f1-913b-58733aca4705','0ce62074-a5a3-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('1f51d5d0-a136-11f1-913b-58733aca4705','2026-08-26 15:38:45.096222',NULL,'2026-09-02 16:00:00.142950',0,250.00,5.00,'Sri Lanka -> Sri Lanka',35,'SHI-003','2026-08-27 05:59:17.751713','SHIPPED','2026-09-02 11:00:00.195581','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Colombo','Colombo','No 45','New Lotus Road',NULL,'12345','Western','31d9a25e-9efd-11f1-913b-58733aca4705','db0f3f8e-a135-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('26bd9e3c-a666-11f1-913b-58733aca4705','2026-09-02 06:05:09.203337','2026-09-02 06:13:06.020312','2026-09-09 06:05:09.202909',0,NULL,NULL,NULL,NULL,'SHI-020','2026-09-02 06:09:34.038851','DELIVERED','2026-09-02 06:13:06.028033','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Jurong East','Central','No 66','New Jurong East',NULL,'12345','Johor','31daa3f2-9efd-11f1-913b-58733aca4705','1baa86d6-a666-11f1-913b-58733aca4705','e667cd76-a20f-11f1-913b-58733aca4705'),('26c02fa8-a666-11f1-913b-58733aca4705','2026-09-02 06:05:09.220170',NULL,'2026-09-02 16:00:00.143175',0,250.00,5.00,'Sri Lanka -> Sri Lanka',35,'SHI-021',NULL,'PROCESSING','2026-09-02 11:00:00.197370','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Colombo','Colombo','No 45','New Lotus Road',NULL,'12345','Western','31d9a25e-9efd-11f1-913b-58733aca4705','1baa86d6-a666-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('53906420-a022-11f1-913b-58733aca4705','2026-08-25 06:44:31.650183',NULL,'2026-09-01 06:44:31.649156',0,NULL,NULL,NULL,NULL,'SHI-002',NULL,'CANCELLED','2026-08-26 18:03:30.876176','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Colombo','Colombo','No 45','New Lotus Road',NULL,'12345','Western','31d9a25e-9efd-11f1-913b-58733aca4705','94b7cfd4-a021-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('89681766-a59d-11f1-913b-58733aca4705','2026-09-01 06:09:05.894636',NULL,'2026-09-02 16:00:00.143473',0,250.00,5.00,'Sri Lanka -> Sri Lanka',35,'SHI-013',NULL,'PENDING','2026-09-02 11:00:00.158532','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Colombo','Colombo','No 45','New Lotus Road',NULL,'12345','Western','31d9a25e-9efd-11f1-913b-58733aca4705','79851556-a59d-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('94c34262-a14b-11f1-913b-58733aca4705','2026-08-26 18:12:21.564087','2026-08-27 06:42:25.565008','2026-09-02 18:12:21.563716',0,NULL,NULL,NULL,NULL,'SHI-004','2026-08-27 06:01:17.043807','DELIVERED','2026-08-27 06:42:25.581579','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Colombo','Colombo','No 45','New Lotus Road',NULL,'12345','Western','31d9a25e-9efd-11f1-913b-58733aca4705','85360f14-a14b-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('a8d55656-a5a0-11f1-913b-58733aca4705','2026-09-01 06:31:27.110133',NULL,'2026-09-02 16:00:00.144926',0,250.00,5.00,'Sri Lanka -> Sri Lanka',35,'SHI-014',NULL,'PENDING','2026-09-02 11:00:00.177341','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Colombo','Colombo','No 45','New Lotus Road',NULL,'12345','Western','31d9a25e-9efd-11f1-913b-58733aca4705','9c3b3e92-a5a0-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('a975c8ac-a59b-11f1-913b-58733aca4705','2026-09-01 05:55:40.678033',NULL,'2026-09-05 09:00:00.145227',0,3500.00,70.00,'Singapore -> Sri Lanka',10,'SHI-011',NULL,'PENDING','2026-09-02 11:00:00.181314','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Jurong East','Central','No 66','New Jurong East',NULL,'12345','Johor','31daa3f2-9efd-11f1-913b-58733aca4705','9690c0b6-a59b-11f1-913b-58733aca4705','e667cd76-a20f-11f1-913b-58733aca4705'),('a97a14d4-a59b-11f1-913b-58733aca4705','2026-09-01 05:55:40.706238',NULL,'2026-09-02 16:00:00.145570',0,250.00,5.00,'Sri Lanka -> Sri Lanka',35,'SHI-012','2026-09-01 05:59:35.160310','IN_TRANSIT','2026-09-02 11:00:00.183915','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Colombo','Colombo','No 45','New Lotus Road',NULL,'12345','Western','31d9a25e-9efd-11f1-913b-58733aca4705','9690c0b6-a59b-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('c5ec4a18-a665-11f1-913b-58733aca4705','2026-09-02 06:02:26.770210',NULL,'2026-09-05 09:00:00.145610',0,3500.00,70.00,'Singapore -> Sri Lanka',10,'SHI-019',NULL,'PENDING','2026-09-02 11:00:00.184937','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Jurong East','Central','No 66','New Jurong East',NULL,'12345','Johor','31daa3f2-9efd-11f1-913b-58733aca4705','389743ac-a665-11f1-913b-58733aca4705','e667cd76-a20f-11f1-913b-58733aca4705'),('c6741b04-a35f-11f1-913b-58733aca4705','2026-08-29 09:41:57.191600',NULL,'2026-09-05 09:00:00.145624',0,3500.00,70.00,'Singapore -> Sri Lanka',10,'SHI-005','2026-08-29 13:23:16.011189','IN_TRANSIT','2026-09-02 11:00:00.185821','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Jurong East','Central','No 66','New Jurong East',NULL,'12345','Johor','31daa3f2-9efd-11f1-913b-58733aca4705','af3bb1b8-a35f-11f1-913b-58733aca4705','e667cd76-a20f-11f1-913b-58733aca4705'),('c6784f30-a35f-11f1-913b-58733aca4705','2026-08-29 09:41:57.219127',NULL,'2026-09-02 16:00:00.145634',0,250.00,5.00,'Sri Lanka -> Sri Lanka',35,'SHI-006','2026-08-29 18:46:31.320584','SHIPPED','2026-09-02 11:00:00.186169','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Colombo','Colombo','No 45','New Lotus Road',NULL,'12345','Western','31d9a25e-9efd-11f1-913b-58733aca4705','af3bb1b8-a35f-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('df114260-a36c-11f1-913b-58733aca4705','2026-08-29 11:15:41.944308',NULL,'2026-09-05 09:00:00.145645',0,3500.00,70.00,'Singapore -> Sri Lanka',10,'SHI-009','2026-08-29 13:43:27.681978','SHIPPED','2026-09-02 11:00:00.186889','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Jurong East','Central','No 66','New Jurong East',NULL,'12345','Johor','31daa3f2-9efd-11f1-913b-58733aca4705','cd2d0732-a36c-11f1-913b-58733aca4705','e667cd76-a20f-11f1-913b-58733aca4705'),('df130f32-a36c-11f1-913b-58733aca4705','2026-08-29 11:15:41.956111',NULL,'2026-09-02 16:00:00.145656',0,250.00,5.00,'Sri Lanka -> Sri Lanka',35,'SHI-010','2026-08-29 19:45:37.137237','SHIPPED','2026-09-02 11:00:00.187765','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Colombo','Colombo','No 45','New Lotus Road',NULL,'12345','Western','31d9a25e-9efd-11f1-913b-58733aca4705','cd2d0732-a36c-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('e6617cbc-a36a-11f1-913b-58733aca4705','2026-08-29 11:01:35.220671','2026-08-30 15:59:56.784202','2026-09-02 13:45:00.102619',0,3500.00,70.00,'Singapore -> Sri Lanka',10,'SHI-007','2026-08-30 15:47:07.314446','DELIVERED','2026-08-30 15:59:56.804187','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Jurong East','Central','No 66','New Jurong East',NULL,'12345','Johor','31daa3f2-9efd-11f1-913b-58733aca4705','d335602c-a36a-11f1-913b-58733aca4705','e667cd76-a20f-11f1-913b-58733aca4705'),('e663af28-a36a-11f1-913b-58733aca4705','2026-08-29 11:01:35.235049',NULL,'2026-09-02 16:00:00.145665',0,250.00,5.00,'Sri Lanka -> Sri Lanka',35,'SHI-008','2026-08-29 19:45:33.959567','SHIPPED','2026-09-02 11:00:00.188599','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Colombo','Colombo','No 45','New Lotus Road',NULL,'12345','Western','31d9a25e-9efd-11f1-913b-58733aca4705','d335602c-a36a-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('e9ea49f6-9fbc-11f1-913b-58733aca4705','2026-08-24 18:38:35.231219',NULL,'2026-09-02 16:00:00.145675',0,250.00,5.00,'Sri Lanka -> Sri Lanka',35,'SHI-001','2026-08-28 18:50:04.074759','IN_TRANSIT','2026-09-02 11:00:00.190379','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Colombo','Colombo','No 45','New Lotus Road',NULL,'12345','Western','31d9a25e-9efd-11f1-913b-58733aca4705','ad3167c8-9fb8-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705'),('f3568170-a65c-11f1-913b-58733aca4705','2026-09-02 04:59:17.493917',NULL,'2026-09-05 09:00:00.145721',0,3500.00,70.00,'Singapore -> Sri Lanka',10,'SHI-017',NULL,'PROCESSING','2026-09-02 11:00:00.191968','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Jurong East','Central','No 66','New Jurong East',NULL,'12345','Johor','31daa3f2-9efd-11f1-913b-58733aca4705','135f5816-a59a-11f1-913b-58733aca4705','e667cd76-a20f-11f1-913b-58733aca4705'),('f375d8d6-a65c-11f1-913b-58733aca4705','2026-09-02 04:59:17.699290',NULL,'2026-09-02 16:00:00.147505',0,250.00,5.00,'Sri Lanka -> Sri Lanka',35,'SHI-018',NULL,'PROCESSING','2026-09-02 11:00:00.193223','Colombo','Colombo','100 Galle Road','Level 4',NULL,'00300','Western','31d9a25e-9efd-11f1-913b-58733aca4705','Colombo','Colombo','No 45','New Lotus Road',NULL,'12345','Western','31d9a25e-9efd-11f1-913b-58733aca4705','135f5816-a59a-11f1-913b-58733aca4705','193a06e0-9fb5-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `shipments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` varchar(255) NOT NULL,
  `role_id` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FK_user_roles_role_id` (`role_id`),
  CONSTRAINT `FK_user_roles_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FK_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES ('15f67a8e-a043-11f1-913b-58733aca4705','31d5ec72-9efd-11f1-913b-58733aca4705'),('33e34df6-9f94-11f1-913b-58733aca4705','31d5ec72-9efd-11f1-913b-58733aca4705'),('78f59e72-a664-11f1-913b-58733aca4705','31d92cd4-9efd-11f1-913b-58733aca4705'),('7bc741cc-a273-11f1-913b-58733aca4705','31d92cd4-9efd-11f1-913b-58733aca4705'),('1c278d08-a665-11f1-913b-58733aca4705','31d96690-9efd-11f1-913b-58733aca4705'),('33e34df6-9f94-11f1-913b-58733aca4705','31d96690-9efd-11f1-913b-58733aca4705'),('629d3798-a14b-11f1-913b-58733aca4705','31d96690-9efd-11f1-913b-58733aca4705'),('832bea7e-a36a-11f1-913b-58733aca4705','31d96690-9efd-11f1-913b-58733aca4705'),('d7f317da-a5a4-11f1-913b-58733aca4705','31d96690-9efd-11f1-913b-58733aca4705'),('142a8efc-a133-11f1-913b-58733aca4705','31d97072-9efd-11f1-913b-58733aca4705'),('26b67772-9f60-11f1-913b-58733aca4705','31d97072-9efd-11f1-913b-58733aca4705'),('787b79c2-9fb5-11f1-913b-58733aca4705','31d97072-9efd-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `username` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('142a8efc-a133-11f1-913b-58733aca4705','2026-08-26 15:16:57.895773','john@gmail.com','$argon2id$v=19$m=65536,t=3,p=1$Eihh16cVfBUbsEDlXHFGsQ$+D2eAQb2Wl/jo93X8fitaAfYA9nycHt3uorXHxJjAkQ','ACTIVE','2026-08-26 15:16:57.895773','john'),('15f67a8e-a043-11f1-913b-58733aca4705','2026-08-25 10:39:01.692821','admin@gmail.test','$argon2id$v=19$m=65536,t=3,p=1$mrnsBXe5AcgdgvgrxKbSkg$VtF0z3jUOyN8YjA5PlFucQoyfP4dauTiAE3j93r9JEA','ACTIVE','2026-08-25 10:39:01.692821','admin'),('1c278d08-a665-11f1-913b-58733aca4705','2026-09-02 05:57:41.946627','customer@globaltrade.test','$argon2id$v=19$m=65536,t=3,p=1$0z2iwXj9CrVggN8BLggU2Q$lgCF6y2suVmiP72bwaLc3OnYhlVkKASAZTgU3USR8Kc','ACTIVE','2026-09-02 05:57:41.946627','customer'),('26b67772-9f60-11f1-913b-58733aca4705','2026-08-24 07:34:34.036914','kamal@gmail.com','$argon2id$v=19$m=65536,t=3,p=1$OZrJFwhQsu0eiuQGUZz4qg$Iv74k6AkM1WKTe44rXStPiIQr0PF4zUJMNKQlG94TL8','ACTIVE','2026-08-24 07:34:34.036914','kamal.test'),('33e34df6-9f94-11f1-913b-58733aca4705','2026-08-24 13:47:09.971268','saman@gmail.com','$argon2id$v=19$m=65536,t=3,p=1$wn4LqjSn/oAn/9N1yxCwqA$HQ+P9YoiPIJlOrGN5bg/ME0q+IKDbp4amMZQjfi8PVo','ACTIVE','2026-08-24 13:47:09.971268','saman'),('629d3798-a14b-11f1-913b-58733aca4705','2026-08-26 18:10:57.429012','danuka@test.com','$argon2id$v=19$m=65536,t=3,p=1$kZzT5n7RmgFrBdvs0EzGoQ$WOR0hJ5Ed+81cNboab3hbCYmF5s9C7nYhzd76Px0kyE','ACTIVE','2026-08-26 18:10:57.429012','danuka'),('787b79c2-9fb5-11f1-913b-58733aca4705','2026-08-24 17:45:18.445454','david.chen@gmail.test','$argon2id$v=19$m=65536,t=3,p=1$1lU5I4iUHJRkQykSr2YAlQ$yWJYaPRbloqPgrxLnWWE6GDyFpwUu37BRaXnSpFkuuY','ACTIVE','2026-08-24 17:45:18.445454','xu.chen'),('78f59e72-a664-11f1-913b-58733aca4705','2026-09-02 05:53:08.150759','manager@globaltrade.test','$argon2id$v=19$m=65536,t=3,p=1$aNs1zRZRyJt3QFkd6A/rSQ$aT/wcqaXOR9yqSzi/gs2uDUzc8agP9G9bBLR2GplA+c','ACTIVE','2026-09-02 05:53:08.150759','manager'),('7bc741cc-a273-11f1-913b-58733aca4705','2026-08-28 05:30:30.679501','kane@test.com','$argon2id$v=19$m=65536,t=3,p=1$mDjfIxtHOlv/05gVJF/CpA$BAT19kXDZIalDTAdsQMNtTsKRJgIe6LVmFqqq1w9bSM','ACTIVE','2026-08-28 05:30:30.679501','kane'),('832bea7e-a36a-11f1-913b-58733aca4705','2026-08-29 10:58:48.775493','anil@gmail.com','$argon2id$v=19$m=65536,t=3,p=1$Tj9F4kkKlJ5etqZk5t+GpA$1706MgbfX94GgIr8TAuvFi7hbAlHzm7C07XTeFXU2Dw','ACTIVE','2026-08-29 10:58:48.775493','anil'),('d7f317da-a5a4-11f1-913b-58733aca4705','2026-09-01 07:01:24.145689','xi@gmail.com','$argon2id$v=19$m=65536,t=3,p=1$dipnjcB/8Nh4nzZ1io8BnA$A0vf925OWveoS4l1xya7WDNMA0uQIW/znpsJbFGF+Es','ACTIVE','2026-09-01 07:01:24.145689','taka');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vendors`
--

DROP TABLE IF EXISTS `vendors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendors` (
  `id` varchar(255) NOT NULL,
  `contact_first_name` varchar(45) NOT NULL,
  `contact_last_name` varchar(45) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(150) DEFAULT NULL,
  `mobile_1` varchar(20) DEFAULT NULL,
  `mobile_2` varchar(20) DEFAULT NULL,
  `performance_score` int NOT NULL,
  `status` varchar(20) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `vendor_number` varchar(30) NOT NULL,
  `company_id` varchar(255) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `vendor_number` (`vendor_number`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `idx_vendor_number` (`vendor_number`),
  KEY `idx_vendor_company_id` (`company_id`),
  KEY `idx_vendor_email` (`email`),
  CONSTRAINT `fk_vendor_company` FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`),
  CONSTRAINT `fk_vendor_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vendors`
--

LOCK TABLES `vendors` WRITE;
/*!40000 ALTER TABLE `vendors` DISABLE KEYS */;
INSERT INTO `vendors` VALUES ('143b8ec8-a133-11f1-913b-58733aca4705','John','Dohn','2026-08-26 15:16:58.005051','john@gmail.com','+4413800000007','+44210000007',100,'ACTIVE','2026-08-28 12:20:00.127617','VEN-003','2a5e760c-9efe-11f1-913b-58733aca4705','142a8efc-a133-11f1-913b-58733aca4705'),('78802742-9fb5-11f1-913b-58733aca4705','Xu','Chen','2026-08-24 17:45:18.475947','david.chen@gmail.test','+8613800000007','+86210000007',100,'ACTIVE','2026-08-28 13:21:22.777665','VEN-002','2a5e5f50-9efe-11f1-913b-58733aca4705','787b79c2-9fb5-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `vendors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warehouses`
--

DROP TABLE IF EXISTS `warehouses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `warehouses` (
  `id` varchar(255) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime(6) NOT NULL,
  `name` varchar(150) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `city` varchar(80) NOT NULL,
  `district` varchar(80) DEFAULT NULL,
  `address_line1` varchar(150) NOT NULL,
  `address_line2` varchar(150) DEFAULT NULL,
  `address_line3` varchar(150) DEFAULT NULL,
  `postal_code` varchar(20) NOT NULL,
  `state_province` varchar(80) DEFAULT NULL,
  `country_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_warehouse_name` (`name`),
  KEY `FK_warehouses_country_id` (`country_id`),
  CONSTRAINT `FK_warehouses_country_id` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouses`
--

LOCK TABLES `warehouses` WRITE;
/*!40000 ALTER TABLE `warehouses` DISABLE KEYS */;
INSERT INTO `warehouses` VALUES ('193a06e0-9fb5-11f1-913b-58733aca4705',1,'2026-08-24 17:42:38.637238','GlobalTrade-Warehouse-A','2026-08-24 17:42:38.637238','Colombo','Colombo','No 45','New Lotus Road',NULL,'12345','Western','31d9a25e-9efd-11f1-913b-58733aca4705'),('e667cd76-a20f-11f1-913b-58733aca4705',1,'2026-08-27 17:37:39.897280','GlobalTrade-Warehouse-B','2026-08-27 17:37:39.897280','Jurong East','Central','No 66','New Jurong East',NULL,'12345','Johor','31daa3f2-9efd-11f1-913b-58733aca4705');
/*!40000 ALTER TABLE `warehouses` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-02 11:14:48
