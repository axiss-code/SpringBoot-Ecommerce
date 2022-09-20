# E-commerce app developed with SpringBoot Framework (MVC pattern)

## https://vimeo.com/751420375   -> See it in action
**Used in this project**  

> IDE & DB:
* Spring Tools Suite 4 
* JDK 17  
* H2 (Embedded DB)

> Dependencies:
* Spring Web
* Spring JPA
* Thymeleaf
* H2-DB
* Spring Boot DevTools
* Spring Boot Starter Security

Installation
------------

- Import project in Spring Tools Suite 4, compile and run as SpringBootApplication.
    - In src/main/resources/h2db folder is located a DB file with sample data, for easy testing the app.
    - http://localhost:8080 and start using it.
    - Login as ADMIN (email:a@a.com - pass:123) to create/delete products, view existing orders and users.
    - Login as USER (email:b@b.com - pass:123) to finalize an order, and check buyed orders.

- An ADMIN user must be created for the Backend View. If DB file is deleted:
    - Create a standard user (http://localhost:8080/usuario/registro) and manually change the ROLE from USER to ADMIN in the DB.
    - http://localhost:8080/h2-console to access to the web-based DB administrator embedded with H2-DB (pre-filled data is ok: just click connect button)
    
- In the application.properties file can be changed the DB by a different one like MySql
    - Remember to include proper dependencies in pom.xml, and create an ADMIN user in the new DB.
