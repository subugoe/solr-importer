# Generic Solr Importer
## Description

This tool contains a simple user interface (UI) and basic functionality for importing Solr-formatted XML files into a Solr server. It can be used as a stand-alone tool to import already created XML files.

However, it is designed to function as a kind of framework. The UI and the basic functionality can be reused by a specific project. This new project takes the role of a plugin. In it, you can implement your project-specific behavior, like for example data file conversions or testing routines, and then add it to the basic functionality.

## System requirements

Linux, Java 8, Gradle 4.2

## Installation (as a stand-alone tool)

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

Now, you can start the tool (Web UI):

``` java -jar web/build/libs/web-0.0.1-SNAPSHOT.jar ```

The Web UI is now accessible on localhost:8080

## Usage as a framework

The main idea of this project is to encapsulate reusable code that can be used in several other projects. Your specific project then can implement extensions which are integrated into the overall data import process. In the Wiki of this repository, you can find explanations of the general technology-agnostic concepts. Here, we concentrate on the implementation details.

So, this project is a framework that can have extension plugins. In general, the structure of the whole thing looks like this:


    parent project
    |- this project
    |- plugin project

