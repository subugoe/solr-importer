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
    |- plugin module
    |- importer module (this project)
       |- core module
       |- web module

The parent project is needed to have the importer and the plugin under one roof as modules. This way, it is easy to manage the dependencies between them. At compile time, the plugin depends on the importer, because it needs to extend and use its classes. More precisely, the plugin must depend on the 'core' module. This dependency (and in fact the whole project management) is managed by Gradle.

Long story short, you must create a new Gradle project (the parent) with a module that will contain the plugin extensions. Then you clone the importer (this project) into the parent's directory and define it and its modules (core and web) as the parent's modules. (In fact, it is not a problem for git to have one repository nested into another locally. You still can push and pull separately.) The compile time dependencies must look like this:

    web -> core (already defined)
    plugin -> core

Furthermore, there must be a runtime dependency:

    web -> plugin

This way, the compiled web UI .jar file will have the plugin in its classpath. Of course, you cannot change the configuration of the web module inside its own source code, since then it would depend on this one specific plugin. What we want is for the importer to be generic. Instead, we 'force' this dependency from outside, namely from the parent. In Gradle configuration this could look like this:

    project(':solr-importer:web') {
      dependencies {
	    runtime project(':my-plugin')
	  }
    }

Now you can build the whole thing locally, as if it was one project. 
