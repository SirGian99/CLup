-- MariaDB dump 10.18  Distrib 10.5.8-MariaDB, for debian-linux-gnueabihf (armv7l)
--
-- Host: localhost    Database: clup_db
-- ------------------------------------------------------
-- Server version	10.5.8-MariaDB-3

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `streetName` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  `streetNumber` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  `city` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  `postalCode` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  `country` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,'Via Rubattino','12','Milano','20131','Italy'),(2,'Via Roma','1','Milano','20130','Italy'),(3,'Piazza Grande','1','Milano','20133','Italy'),(4,'Via Roma','5','Milano','20100','Italy'),(5,'Via Garibaldi','6','Milano','20100','Italy'),(6,'Via Andrea Genoino','21','Cava De\' Tirreni','84013','Italy'),(7,'Via Brombes','11','Torino','10121','Italy'),(8,'Piazza di Spagna','20','Roma','00100','Italy');
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attitude`
--

DROP TABLE IF EXISTS `attitude`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `attitude` (
  `id` bigint(20) NOT NULL,
  `appCustomer` varchar(128) CHARACTER SET utf8mb4 NOT NULL,
  `store` char(36) CHARACTER SET utf8mb4 NOT NULL,
  `averageVisitDuration` time DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `attitude_storeq_idx` (`store`),
  KEY `attitude_customer_idx` (`appCustomer`),
  CONSTRAINT `attitude_customer` FOREIGN KEY (`appCustomer`) REFERENCES `customer` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `attitude_store` FOREIGN KEY (`store`) REFERENCES `store` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attitude`
--

LOCK TABLES `attitude` WRITE;
/*!40000 ALTER TABLE `attitude` DISABLE KEYS */;
/*!40000 ALTER TABLE `attitude` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `booking`
--

DROP TABLE IF EXISTS `booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `booking` (
  `uuid` char(36) CHARACTER SET utf8mb4 NOT NULL,
  `hfid` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  `store` char(36) CHARACTER SET utf8mb4 DEFAULT NULL,
  `appCustomer` varchar(128) CHARACTER SET utf8mb4 DEFAULT NULL,
  `numberOfPeople` int(11) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `dateTimeOfCreation` datetime DEFAULT NULL,
  `visitStartingTime` datetime DEFAULT NULL,
  `visitCompletionTime` datetime DEFAULT NULL,
  `desiredStartingTime` datetime DEFAULT NULL,
  `desiredDuration` time DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  KEY `bookingstore_idx` (`store`),
  KEY `booking_customer_idx` (`appCustomer`),
  CONSTRAINT `booking_customer` FOREIGN KEY (`appCustomer`) REFERENCES `customer` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `booking_store` FOREIGN KEY (`store`) REFERENCES `store` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking`
--

LOCK TABLES `booking` WRITE;
/*!40000 ALTER TABLE `booking` DISABLE KEYS */;
/*!40000 ALTER TABLE `booking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookingproduct`
--

DROP TABLE IF EXISTS `bookingproduct`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bookingproduct` (
  `booking` char(36) CHARACTER SET utf8mb4 NOT NULL,
  `productSection` bigint(20) NOT NULL,
  PRIMARY KEY (`booking`,`productSection`),
  KEY `prodsecbooking_idx` (`booking`),
  KEY `bookingproduct_productsection_idx` (`productSection`),
  CONSTRAINT `bookingproduct_booking` FOREIGN KEY (`booking`) REFERENCES `booking` (`uuid`),
  CONSTRAINT `bookingproduct_productsection` FOREIGN KEY (`productSection`) REFERENCES `productsection` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookingproduct`
--

LOCK TABLES `bookingproduct` WRITE;
/*!40000 ALTER TABLE `bookingproduct` DISABLE KEYS */;
/*!40000 ALTER TABLE `bookingproduct` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chain`
--

DROP TABLE IF EXISTS `chain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chain` (
  `name` varchar(100) CHARACTER SET utf8mb4 NOT NULL,
  `description` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
  `image` blob DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chain`
--

LOCK TABLES `chain` WRITE;
/*!40000 ALTER TABLE `chain` DISABLE KEYS */;
INSERT INTO `chain` VALUES ('Carretroix','Milano',NULL),('Errelunga','Milano',NULL);
/*!40000 ALTER TABLE `chain` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer` (
  `id` varchar(128) CHARACTER SET utf8mb4 NOT NULL,
  `isAppCustomer` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES ('0B4C1A70-4111-4687-B74E-21D8E89F6A8A',1),('0C204631-175A-4E8E-A96D-58C7063CC278',1),('11111A70-4111-4687-B74E-21D8E89F6A8B',1),('22221A70-4111-4687-B74E-21D8E89F6A8B',1),('27FBF161-F45D-45D8-BFBC-F886B94A59F0',1),('2DEE1BD8-8C0B-4A6D-A8AF-3EA09E15607C',1),('370296A8-15A5-4420-B02E-048CBB86E1FF',1),('3a12fd85-c6eb-41e8-903b-05e1c510e870',1),('4721548f-1853-45dd-839c-ccb81ea7cbfb',1),('5ACFE22F-6E4F-470E-8B83-F0066B6A4723',1),('80430c8c-e2ad-4102-abeb-56c014559734',1),('989BC7A6-5A7B-4986-B8CA-726F4A793350',1),('A066997A-B7EC-40DE-BA71-93A958EE51F7',1);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dayinterval`
--

DROP TABLE IF EXISTS `dayinterval`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dayinterval` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dayOfTheWeek` int(11) NOT NULL,
  `start` time NOT NULL,
  `end` time NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8 COMMENT='UNIQUE(day,start,end) \nCHECK day 1-7';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dayinterval`
--

LOCK TABLES `dayinterval` WRITE;
/*!40000 ALTER TABLE `dayinterval` DISABLE KEYS */;
INSERT INTO `dayinterval` VALUES (1,1,'00:00:01','23:59:59'),(2,2,'00:00:01','23:59:59'),(3,3,'00:00:01','23:59:59'),(4,4,'00:00:01','23:59:59'),(5,5,'00:00:01','23:59:59'),(6,6,'00:00:01','23:59:59'),(7,7,'00:00:01','23:59:59');
/*!40000 ALTER TABLE `dayinterval` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lineup`
--

DROP TABLE IF EXISTS `lineup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lineup` (
  `uuid` char(36) CHARACTER SET utf8mb4 NOT NULL,
  `hfid` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  `store` char(36) CHARACTER SET utf8mb4 DEFAULT NULL,
  `customer` varchar(128) CHARACTER SET utf8mb4 DEFAULT NULL,
  `estimatedTimeOfEntrance` datetime DEFAULT NULL,
  `numberOfPeople` int(10) unsigned DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `dateTimeOfCreation` datetime DEFAULT NULL,
  `visitStartingTime` datetime DEFAULT NULL,
  `visitCompletionTime` datetime DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  KEY `lineup_store_idx` (`store`),
  KEY `lineup_customer_idx` (`customer`),
  CONSTRAINT `lineup_customer` FOREIGN KEY (`customer`) REFERENCES `customer` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `lineup_store` FOREIGN KEY (`store`) REFERENCES `store` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lineup`
--

LOCK TABLES `lineup` WRITE;
/*!40000 ALTER TABLE `lineup` DISABLE KEYS */;
INSERT INTO `lineup` VALUES ('03a09c48-b6d6-45ca-aa07-6f5f2a1c97cf','L-H48','a8224c0b-6552-11eb-a3e0-dca632747890','80430c8c-e2ad-4102-abeb-56c014559734','2021-02-07 18:10:43',5,3,'2021-02-07 18:10:43','2021-02-07 18:39:27','2021-02-07 18:54:17'),('0947c673-6c81-4a0b-863a-331b85bd8b37','L-H853','a8224c0b-6552-11eb-a3e0-dca632747890','370296A8-15A5-4420-B02E-048CBB86E1FF','2021-02-07 17:49:38',1,1,'2021-02-07 17:49:38',NULL,NULL),('1dcd6dbf-d19f-40dc-9762-867d0826de60','L-H123','bbab1410-6481-11eb-a3e0-dca632747890','3a12fd85-c6eb-41e8-903b-05e1c510e870','2021-02-07 19:57:23',2,0,'2021-02-07 19:40:16',NULL,NULL),('6f1811bd-1973-44ec-ac3a-9ccff524200a','L-H545','bbab1410-6481-11eb-a3e0-dca632747890','4721548f-1853-45dd-839c-ccb81ea7cbfb','2021-02-07 19:47:23',3,0,'2021-02-07 19:37:23',NULL,NULL),('8ae5c64d-3756-4d2c-a6f6-28c4b3b1b57e','L-H815','44af9545-64ac-11eb-a3e0-dca632747890','80430c8c-e2ad-4102-abeb-56c014559734','2021-02-07 18:48:37',2,2,'2021-02-07 18:48:37','2021-02-07 19:30:13',NULL),('cfdbaf1c-6ac9-4fd2-a393-5ed45ee45cda','L-H872','44af9545-64ac-11eb-a3e0-dca632747890','989BC7A6-5A7B-4986-B8CA-726F4A793350','2021-02-07 10:51:47',1,1,'2021-02-07 10:51:47',NULL,NULL),('e7f83524-e344-47ed-9a37-0dff12faa49f','L-H617','44af9545-64ac-11eb-a3e0-dca632747890','27FBF161-F45D-45D8-BFBC-F886B94A59F0','2021-02-07 17:20:37',1,1,'2021-02-07 17:20:37',NULL,NULL),('f2adba07-4846-43fc-a121-01dcafa3fd19','L-H670','bbab1410-6481-11eb-a3e0-dca632747890','80430c8c-e2ad-4102-abeb-56c014559734','2021-02-07 19:30:57',3,2,'2021-02-07 19:30:57','2021-02-07 19:31:19',NULL);
/*!40000 ALTER TABLE `lineup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `manager`
--

DROP TABLE IF EXISTS `manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `manager` (
  `id` char(36) CHARACTER SET utf8mb4 NOT NULL,
  `username` varchar(45) CHARACTER SET utf8mb4 NOT NULL,
  `password` varchar(45) CHARACTER SET utf8mb4 NOT NULL,
  `name` varchar(45) CHARACTER SET utf8mb4 NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `manager`
--

LOCK TABLES `manager` WRITE;
/*!40000 ALTER TABLE `manager` DISABLE KEYS */;
INSERT INTO `manager` VALUES ('44b8347d-64ac-11eb-a3e0-dca632747890','christianted','pigreco','Chistian Tedelli'),('44b83a9b-64ac-11eb-a3e0-dca632747890','liatrapa','test','Lia'),('768e92e2-64e5-11eb-a3e0-dca632747890','tommyri','nonricordo','Tommy'),('c8c6f223-64e5-11eb-a3e0-dca632747890','davideca','password','Davide');
/*!40000 ALTER TABLE `manager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productsection`
--

DROP TABLE IF EXISTS `productsection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `productsection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `store` char(36) CHARACTER SET utf8mb4 DEFAULT NULL,
  `name` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  `currentOccupancy` float DEFAULT NULL,
  `maximumOccupancy` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `storeprod_idx` (`store`),
  CONSTRAINT `storeprod` FOREIGN KEY (`store`) REFERENCES `store` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productsection`
--

LOCK TABLES `productsection` WRITE;
/*!40000 ALTER TABLE `productsection` DISABLE KEYS */;
INSERT INTO `productsection` VALUES (1,'b9ab1420-6481-11eb-a3e0-dca632747890','Ethnic Food',0,20),(2,'b9ab1420-6481-11eb-a3e0-dca632747890','Baker',0,10),(3,'b9ab1420-6481-11eb-a3e0-dca632747890','Frozen food',0,30),(4,'44af9545-64ac-11eb-a3e0-dca632747890','Panettiere',0,20),(5,'44af7f16-64ac-11eb-a3e0-dca632747890','Panettiere',0,5),(6,'44af9545-64ac-11eb-a3e0-dca632747890','Tech',0,5),(7,'44af7f16-64ac-11eb-a3e0-dca632747890','Tech',0,15),(8,'44af7f16-64ac-11eb-a3e0-dca632747890','TV',0,20),(9,'a8224c0b-6552-11eb-a3e0-dca632747890','Macelleria',0,5),(10,'a8224c0b-6552-11eb-a3e0-dca632747890','Salumeria',0,5);
/*!40000 ALTER TABLE `productsection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store`
--

DROP TABLE IF EXISTS `store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `store` (
  `id` char(36) CHARACTER SET utf8mb4 NOT NULL,
  `chain` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 NOT NULL,
  `description` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
  `image` blob DEFAULT NULL,
  `address` bigint(20) DEFAULT NULL,
  `currentOccupancy` int(11) NOT NULL,
  `maximumOccupancy` int(11) NOT NULL,
  `averageVisitDuration` time NOT NULL DEFAULT '00:00:00',
  `safetyThreshold` float DEFAULT 0,
  `passepartoutuuid` char(36) CHARACTER SET utf8mb4 DEFAULT NULL,
  `passepartouthfid` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `passepartoutuuid_UNIQUE` (`passepartoutuuid`),
  UNIQUE KEY `address_UNIQUE` (`address`),
  KEY `chain_idx` (`chain`),
  KEY `storeaddress_idx` (`address`),
  CONSTRAINT `chain` FOREIGN KEY (`chain`) REFERENCES `chain` (`name`),
  CONSTRAINT `storeaddress` FOREIGN KEY (`address`) REFERENCES `address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store`
--

LOCK TABLES `store` WRITE;
/*!40000 ALTER TABLE `store` DISABLE KEYS */;
INSERT INTO `store` VALUES ('44af7f16-64ac-11eb-a3e0-dca632747890','Errelunga','Rubattino','Aperto nei giorni dispari',NULL,4,0,40,'01:15:15',0,'44af7fe8-64ac-11eb-a3e0-dca632747890',NULL),('44af9545-64ac-11eb-a3e0-dca632747890','Carretroix','Lambrate','Store aperto H24',NULL,1,2,25,'00:47:05',0,'44af95cf-64ac-11eb-a3e0-dca632747890',NULL),('a8224c0b-6552-11eb-a3e0-dca632747890','Errelunga','StoreDiTest','Store aperto H24',NULL,2,0,10,'00:15:00',0,'a8224cd8-6552-11eb-a3e0-dca632747890',NULL),('b9ab1420-6481-11eb-a3e0-dca632747890',NULL,'RST Groceries','Autonomous store (senza chain) - Aperto nei giorni pari',NULL,3,0,60,'00:11:20',0,'b9ab14ee-6481-11eb-a3e0-dca632747890',NULL),('bbab1410-6481-11eb-a3e0-dca632747890',NULL,'FilledStore','Store con coda',NULL,5,3,3,'00:10:00',0,'bbab1410-6481-11eb-a3e0-dca632747890',NULL);
/*!40000 ALTER TABLE `store` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `storemanager`
--

DROP TABLE IF EXISTS `storemanager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storemanager` (
  `store` char(36) CHARACTER SET utf8mb4 NOT NULL,
  `manager` char(36) CHARACTER SET utf8mb4 NOT NULL,
  PRIMARY KEY (`store`,`manager`),
  KEY `storemanagermanager_idx` (`manager`),
  CONSTRAINT `storemanagermanager` FOREIGN KEY (`manager`) REFERENCES `manager` (`id`),
  CONSTRAINT `storemanagerstore` FOREIGN KEY (`store`) REFERENCES `store` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storemanager`
--

LOCK TABLES `storemanager` WRITE;
/*!40000 ALTER TABLE `storemanager` DISABLE KEYS */;
INSERT INTO `storemanager` VALUES ('44af7f16-64ac-11eb-a3e0-dca632747890','44b8347d-64ac-11eb-a3e0-dca632747890'),('44af9545-64ac-11eb-a3e0-dca632747890','44b83a9b-64ac-11eb-a3e0-dca632747890'),('a8224c0b-6552-11eb-a3e0-dca632747890','768e92e2-64e5-11eb-a3e0-dca632747890'),('b9ab1420-6481-11eb-a3e0-dca632747890','c8c6f223-64e5-11eb-a3e0-dca632747890');
/*!40000 ALTER TABLE `storemanager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tassaddresses`
--

DROP TABLE IF EXISTS `tassaddresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tassaddresses` (
  `uri` varchar(45) NOT NULL,
  `store` char(36) DEFAULT NULL,
  PRIMARY KEY (`uri`),
  KEY `store_idx` (`store`),
  CONSTRAINT `tasstore` FOREIGN KEY (`store`) REFERENCES `store` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tassaddresses`
--

LOCK TABLES `tassaddresses` WRITE;
/*!40000 ALTER TABLE `tassaddresses` DISABLE KEYS */;
/*!40000 ALTER TABLE `tassaddresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workinghours`
--

DROP TABLE IF EXISTS `workinghours`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `workinghours` (
  `dayInterval` bigint(20) NOT NULL,
  `store` char(36) CHARACTER SET utf8mb4 NOT NULL,
  PRIMARY KEY (`dayInterval`,`store`),
  KEY `workingday_idx` (`dayInterval`),
  KEY `workinghoursstore_idx` (`store`),
  CONSTRAINT `wh_dayinterval` FOREIGN KEY (`dayInterval`) REFERENCES `dayinterval` (`id`),
  CONSTRAINT `wh_store` FOREIGN KEY (`store`) REFERENCES `store` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workinghours`
--

LOCK TABLES `workinghours` WRITE;
/*!40000 ALTER TABLE `workinghours` DISABLE KEYS */;
INSERT INTO `workinghours` VALUES (1,'44af7f16-64ac-11eb-a3e0-dca632747890'),(1,'44af9545-64ac-11eb-a3e0-dca632747890'),(1,'a8224c0b-6552-11eb-a3e0-dca632747890'),(1,'bbab1410-6481-11eb-a3e0-dca632747890'),(2,'44af9545-64ac-11eb-a3e0-dca632747890'),(2,'b9ab1420-6481-11eb-a3e0-dca632747890'),(2,'bbab1410-6481-11eb-a3e0-dca632747890'),(3,'44af7f16-64ac-11eb-a3e0-dca632747890'),(3,'44af9545-64ac-11eb-a3e0-dca632747890'),(3,'a8224c0b-6552-11eb-a3e0-dca632747890'),(3,'bbab1410-6481-11eb-a3e0-dca632747890'),(4,'44af9545-64ac-11eb-a3e0-dca632747890'),(4,'a8224c0b-6552-11eb-a3e0-dca632747890'),(4,'b9ab1420-6481-11eb-a3e0-dca632747890'),(4,'bbab1410-6481-11eb-a3e0-dca632747890'),(5,'44af7f16-64ac-11eb-a3e0-dca632747890'),(5,'44af9545-64ac-11eb-a3e0-dca632747890'),(5,'a8224c0b-6552-11eb-a3e0-dca632747890'),(5,'bbab1410-6481-11eb-a3e0-dca632747890'),(6,'44af9545-64ac-11eb-a3e0-dca632747890'),(6,'a8224c0b-6552-11eb-a3e0-dca632747890'),(6,'b9ab1420-6481-11eb-a3e0-dca632747890'),(6,'bbab1410-6481-11eb-a3e0-dca632747890'),(7,'44af9545-64ac-11eb-a3e0-dca632747890'),(7,'a8224c0b-6552-11eb-a3e0-dca632747890'),(7,'bbab1410-6481-11eb-a3e0-dca632747890');
/*!40000 ALTER TABLE `workinghours` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-02-07 19:44:51
