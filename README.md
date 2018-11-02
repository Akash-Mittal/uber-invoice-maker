## uber-invoice-maker
UBER Invoice Maker (For Education Purpose Only).

[![GitHub issues](https://img.shields.io/github/issues/Akash-Mittal/uber-invoice-maker.svg)](https://github.com/Akash-Mittal/uber-invoice-maker/issues)
[![GitHub forks](https://img.shields.io/github/forks/Akash-Mittal/uber-invoice-maker.svg)](https://github.com/Akash-Mittal/uber-invoice-maker/network)



* Create uber like invoice by uploading a CSV file.
* CSV contains raw data like`From Address`, ` To Address `, `Fare` driver and other details.
* Uses batch processuing to generate the invoices.

### Technologies

* Java 8
* MySQL and H2  
* Maven
* SLF4J
* Spring and Spring Boot
* Thymeleaf
* Lombok

### Steps

* Add details in a CSV given format (Please download it from 
https://github.com/Akash-Mittal/uber-invoice-maker/blob/master/src/test/resources/sample.csv).
* Upload.
* Click on the links to download the file(By default files will be created with driver name).
* filename header in CSV is disabled - as it has some security complications.
* Maximum File size is 128KB.

### Sample Invoice.

* ![aiport-hotel](https://user-images.githubusercontent.com/2044872/43359019-0f37c036-92b9-11e8-828b-e29882e09551.png)

### APP URL

https://uberinvoicemaker.herokuapp.com/


File Upload Has Been Referenced from Springs gs-uploading-files project.

##### Tutorial 

 https://spring.io/guides/gs/uploading-files/
 https://github.com/spring-guides/gs-uploading-files.git
 
 
### To Remove the Compilation from IDE you need to install LAMBOK.

  https://projectlombok.org/
