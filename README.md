# iban-generator-utility
A utility for generating test IBANs.

## Synopsis
The utility provides functionality to generate valid IBANs for different countries. Valid in this context means syntactically valid according to the [ISO standard](https://en.wikipedia.org/wiki/International_Bank_Account_Number), but not necessarily existing bank accounts.
For the time being it is provided support for Germany, Austria and the Netherlands, but it is easily extensible by providing a custom implementation of the [CountryFormatProvider interface](https://github.com/mcjojos/iban-generator-utility/blob/master/src/main/java/com/jojos/challenge/iban/country/CountryFormatProvider.java) and calling the API function of [IbanApi](src/main/java/com/jojos/challenge/iban/api/IbanApi.java).
The IBAN generator tool guarantees uniqueness of the generated IBANs within a specific instance of the tool and it is thread safe.
This tool can be used as a library by other services.
Moreover, automated tests are executed in parallel and concurrently use this library for generating test data.

## Requirements
The IBAN generator is written in Java 8 as a Maven project. Therefore you will need Java 8 and maven to build it.

## How do I use it?
- You can import the project as a maven dependency like that
```
        <dependency>
            <groupId>com.jojos.challenge</groupId>
            <artifactId>iban-generator-utility</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```

- Alternative you can clone/download the project and build the application with
```
mvn package
or
mvn package -Dmaven.test.skip=true
if you need to skip the tests since they might take some time
```
You will need the -jar-with-dependencies.jar under the target/ folder of your current directory.

Import into your project the file
*target/iban-generator-utility-1.0-SNAPSHOT-jar-with-dependencies.jar*

###### Typical usage

Once you have resolved the dependencies with one way or another, you can start using the tool.
A typical usage of the tool from a client's point of view is
```Java
// Generate a random valid IBAN for The Netherlands.
Iban iban = IbanApi.generateFor(CountryISO.NL);
// Print the IBAN in a human readable format using the underlying formatter's #asHumanReadableString()
System.out.println(iban.asHumanReadableString());
// print the IBAN in a compact format using the underlying formatter's #asHumanReadableString()
System.out.println(iban.asString());
```

The countries currently supported are Austria (country code: AT), Germany (DE) and Netherlands (NL)
You can use use your custom implementations of

- [CountryFormatProvider](https://github.com/mcjojos/iban-generator-utility/blob/master/src/main/java/com/jojos/challenge/iban/country/CountryFormatProvider.java)
for supporting more countries and
- [IbanFormatter](https://github.com/mcjojos/iban-generator-utility/blob/master/src/main/java/com/jojos/challenge/iban/format/IbanFormatter.java) for supporting your own IBAN print format overriding methods #asHumanReadableString(Iban) and #asString(Iban)

and make a different function call of the [IbanApi#generateFor(CountryISO, IbanFormatter, CountryFormatProvider)](https://github.com/mcjojos/iban-generator-utility/blob/master/src/main/java/com/jojos/challenge/iban/api/IbanApi.java)

**ENJOY!**
