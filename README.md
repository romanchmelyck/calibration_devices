
# Calibration devices system 

Centralized system for calibration of measurement devices. Enterprise open-source project intended to optimize process of verification calibration devices. The main goal of the project is to create centralized system of measurement devices for calibration and verification processes in Ukraine.

### Installation dependencies

The following dependencies are necessary:

 - Java 8
 - Bower
 - Maven 3
 - MySQL

### Installing frontend dependencies

Run the following command on the root folder of the repository:

    bower install

### Building and starting the server

Before running the app, create MySQL database called `measurement_devices` with charset encoding UTF-8. 
Then run the following command on the root folder of the repository:

    mvn clean install tomcat7:run-war

 Once hibernate creates tables  use `database_script.sql` to fill data.

After the server starts, the application is accessible at the following URL:

    http://localhost:8080/
    
To get an admin page login with the following credentials:

    username: admin
    password: password

Check `database_script.sql` for additional information about other users such as provider, calibrator etc.

### Some overview

> TODO


### REST API

> TODO
=======
# calibration_devices
>>>>>>> refs/remotes/Roman_remote/master
