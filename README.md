## Spring and Angular Application - Coupon System - Server Side
<hr>
<p>This repository is for the server side of a demo application. 
<br>The purpose of this demo is to practice the Test Driven Development methodology, using Unit and Integration tests, and apply the technologies mentioned below</p>
<p>The <a href="">Client Side Repository</a> of the Application</p>of the application.</p>

<hr>
<p><i>Note: Project AWS live link will be added during Step 2.</i></p>

### Getting Started
<p>To run the projet and tests you will need:<br>
  In MySQL,  Create a user with DDL privliges(name and password can be customized in the application.properties in /main or the application-mysql-test-connection.properties in /test files). This is to create the schema for the first time and to run the tests.<br> The default username and password are <b>springuser</b> for both.</p>
  <p>Should set up the <a href="">Client Side</a> of the Application.</p>

### Used Technologies
<p><b>Database: </b>MySQL</p>
<p><b>Testing: </b>JUnit 5, Mockito, Hamcrest</p>
<p><b>Spring: </b>Spring Boot, Spring Data JPA, Spring MVC</p>

### Project Major Steps
<ol>
<li>Create Server-Side application connected to MySQL</li>
<li>Create Angular Single Page Application connected to the Server-Side Application</li>
<li>Add Spring Security</li>
<li>Add Angular security</li>
</ol>

### Project Description
<p>The applicaion will be an interactive Coupon System. Including Companies who can create coupons, Customers who can purchase and use them, and the Coupons themselves.<br>
There will be user roles ( such as an administrator), authentication, security and other features in future updates.
