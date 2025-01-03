CREATE DATABASE  IF NOT EXISTS `myphotoalbum` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `myphotoalbum`;
-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: myphotoalbum
-- ------------------------------------------------------
-- Server version	8.0.29

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
-- Table structure for table `album`
--

DROP TABLE IF EXISTS `album`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `album` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(45) NOT NULL,
  `creator` varchar(45) NOT NULL,
  `creationDate` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `album`
--

LOCK TABLES `album` WRITE;
/*!40000 ALTER TABLE `album` DISABLE KEYS */;
INSERT INTO `album` VALUES (1,'album1federica','federica','2020-10-10'),(2,'album1carlo','carlo','2020-08-10'),(3,'album1giovanni','giovanni','2019-08-10'),(4,'album1marco','marco','2022-07-18'),(5,'album1carmine','carmine','2022-07-18'),(6,'album2federica','federica','2022-07-20'),(7,'album3federica','federica','2022-07-20'),(8,'album4federica','federica','2022-07-25'),(9,'album5federica','federica','2022-07-29'),(10,'album2carlo','carlo','2022-07-29'),(11,'album3carlo','carlo','2022-07-29'),(12,'album2carmine','carmine','2022-07-30'),(13,'album2carlo','federica','2022-08-19'),(20,'album2giovanni','giovanni','2022-08-18');
/*!40000 ALTER TABLE `album` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `imageId` int NOT NULL,
  `creatorUsername` varchar(45) NOT NULL,
  `text` varchar(400) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,1,'federica','che bel natale'),(2,1,'federica','stupendo!'),(3,1,'federica','woww'),(52,13,'federica','bella macchina!'),(53,13,'federica','la preferivo gialla'),(54,11,'giovanni','bella macchina!');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `image`
--

DROP TABLE IF EXISTS `image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `image` (
  `id` int NOT NULL AUTO_INCREMENT,
  `idAlbum` int NOT NULL,
  `title` varchar(45) NOT NULL,
  `date` date NOT NULL,
  `descriptionText` varchar(100) NOT NULL,
  `path` varchar(200) NOT NULL,
  `creatorUsername` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `image`
--

LOCK TABLES `image` WRITE;
/*!40000 ALTER TABLE `image` DISABLE KEYS */;
INSERT INTO `image` VALUES (1,1,'natale1','2020-12-10','natale1','images/natale/natale1.jpg','federica'),(2,1,'natale2','2022-07-19','natale2','images/natale/natale2.jpg','federica'),(3,5,'natale3','2022-07-19','natale3','images/natale/natale3.jpg','carmine'),(4,2,'natura1','2022-07-19','natura1','images/natura/natura1.jpg','carlo'),(5,3,'natura2','2020-10-05','natura2','images/natura/natura2.jpg','giovanni'),(6,4,'natura3','2019-07-07','natura3','images/natura/natura3.jpg','marco'),(7,1,'natura4','2019-07-07','natura4','images/natura/natura4.jpg','federica'),(8,1,'famiglia1','2018-06-05','famiglia1','images/famiglia/famiglia1.jpg','federica'),(9,1,'famiglia3','2020-06-06','famiglia3','images/famiglia/famiglia3.jpg','federica'),(10,1,'auto1','2019-12-12','auto1','images/auto/auto1.jpg','federica'),(11,1,'auto2','2022-07-20','auto2','images/auto/auto2.jpg','federica'),(12,1,'natura2','2022-07-20','natura2','images/natura/natura2.jpg','federica'),(13,8,'auto4','2022-07-25','auto4','images/auto/auto4.jpg','federica'),(14,14,'famiglia6','2022-07-30','famiglia6','images/famiglia/famiglia6.jpg','federica'),(15,20,'natale2','2021-12-25','natale2','images/natale/natale2.jpg','giovanni'),(19,6,'auto2','2022-08-20','auto2','images/auto/auto2.jpg','federica'),(20,7,'auto2','2022-08-20','auto2','images/auto/auto2.jpg','federica'),(21,9,'aut02','2022-08-20','auto2','images/auto/auto2.jpg','federica'),(22,7,'natale1','2022-08-20','natale1','images/natale/natale1.jpg','federica');
/*!40000 ALTER TABLE `image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `username` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `albumsOrder` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('anna','anna@gmail.com','anna',NULL),('carlo','carlo@gmail.com','carlo',NULL),('carmine','carmine@gmail.com','carmine',NULL),('chiara','chiara@gmail.com','chiara',NULL),('federica','federica@gmail.com','ciao',NULL),('giovanni','giovanni@gmail.com','giovanni','3,20');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'myphotoalbum'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-08-24 15:03:03
