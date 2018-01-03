# Generic Solr Importer
## Description

This tool contains a simple user interface (UI) and basic functionality for importing Solr-formatted XML files into a Solr server. It can be used as a stand-alone tool to import already created XML files.

However, it is designed to function as a kind of framework. The UI and the basic functionality can be reused by a specific project. This new project takes the role of a plugin. In it, you can implement your project-specific behavior, like for example data file conversions or testing routines, and then add it to the basic functionality.

## System requirements

Linux, Java 8, Gradle 4.2

## Installation 

The cloned project must be built using Gradle by executing in the project directory:

```gradle build```

or just 

```gradle```

The tool is configured via environment variables. First, you should make a copy of the file containing default values:

``` cp environment.sh.dist environment.sh ```

Now adapt all the values for your needs:

``` vi environment.sh ```

Then, make the variables available in the current shell:

``` source environment.sh ```

Now, you can start the built tool (Web UI):

``` java -jar web/build/libs/web-0.0.1-SNAPSHOT.jar ```

The Web UI is now accessible on localhost:8080
