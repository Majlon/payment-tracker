# payment-tracker

Simple demonstration program to keep record about payments.

### Key functions:

* Saving payments from console.
* Importing payments from text file.
* Automatic balance report every minute.
* On demand balance report
* On demand history report
* On demand report export to text file

### How to install:

1. Clone or download this repo.

2. Download and install apache Maven if necessary.

```
https://maven.apache.org/download.cgi
```

```
https://maven.apache.org/install.html
```
3. Run maven command inside root directory from command line.

```
mvn clean compile assembly:single
```
4. Run Jar file using java command from command line.
```
java -jar *filename.jar*
```

### Usage

Once in application, these commands are available: 

```
help - prints commands overview 
add - allows user to add new payment. expected format is Currency code then amount
get - allows user to get single payment by id
rem - allows user to remove single payment by id
rate - allows user to add exchange value to USD. Same code will overwrite previous rate
history - prints transaction history
balance - prints current balance on account
import - imports payments from specified text file
export - export payments to specified text file
quit - Terminates application
```
