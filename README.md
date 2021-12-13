# Generic Solr Importer

## Description

This tool contains a simple user interface (UI) and basic functionality for importing Solr-formatted XML files into
a Solr server. It can be used as a stand-alone tool to import already created XML files.

However, it is designed to function as a kind of a framework. The UI and the basic functionality can be reused by a 
specific project. This new project takes the role of a plugin. In it, you can implement your project-specific behavior,
like for example data file conversions or testing routines, and then add it to the basic functionality. In the Wiki of
this repository (https://github.com/subugoe/solr-importer/wiki), you can find explanations of the general,
technology-agnostic concepts. Here, we will be concentrating on the implementation details.

If you want to understand the source code, then take a look at the classes in 'docs' packages. A good starting point
is the documentation of the web module (https://github.com/subugoe/solr-importer/blob/master/web/src/main/java/docs/WebModuleDocumentation.java).

## System requirements

Linux, Java 8, Gradle 6.8

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

If you need to start the Web UI on another port, then execute:

``` java -jar -Dserver.port=9090 web/build/libs/web-0.0.1-SNAPSHOT.jar ```

## Usage as a framework

The main idea of this project is to encapsulate reusable code that can be used in several other projects. Your specific
project then can implement extensions which are integrated into the overall data import process.

Originally, the code of this project was part of a specific project. That specific project was tailored to convert some
very specific XML files to Solr XML files and then to import the results into Solr. Later on, there was a new project
that needed the same importing functionality, but the input data and consequently the conversion algorithms were
completely different. The idea was then to extract the commonly used functionality into a separate project and to create
an architecture that allows other projects to use it.

### Structure of the composite project

So, this project is a framework that can have extension plugins. In general, the structure of the whole thing looks like
this:

    parent project
     |- plugin module
     |- importer module (this project)
         |- core module
         |- web module

As you can see, this is a new composite project that contains several modules, and the importer project from this
repository becomes one of the modules. The parent project is needed to have the importer and the plugin under one roof 
as modules. This way, it is easy to manage the dependencies between them.

The project is managed by Gradle. In general, you must create a new Gradle project (the parent) with a module that will
contain the plugin extensions. This new project can be pushed into its own git repository. Then you clone the importer
(this project) into the parent's directory and define it and its modules (core and web) as the parent's modules.
In fact, it is not a problem for git to have one repository nested into another locally. You still can push and pull
separately. (In your own parent project, you need to make sure that this importer's directory is in the .gitignore
file.) This way, you can reuse the generic importer project in several specific parent projects.

### Dependencies

At compile time, the plugin depends on the importer, because it needs to extend and use its classes. More precisely,
the plugin must depend on the 'core' module. Refer to the Gradle documentation to find out how to define dependencies
between modules.

The compile time dependencies must look like this:

    web -> core (already defined)
    plugin -> core

Furthermore, there must be a runtime dependency:

    web -> plugin

This way, the compiled Web UI .jar file will have the plugin in its classpath. Of course, you cannot change the
configuration of the web module inside its own source code, since then it would depend on this one specific plugin.
What we want is for the importer to be generic. Instead, we 'force' this dependency from outside, namely from the
parent. In Gradle configuration this could look like this (in the parent's build file):

    project(':solr-importer:web') {
      dependencies {
	    runtime project(':my-plugin')
	  }
    }

Now you can build and start the whole thing locally, as if it was one project.

### Starting in Eclipse

During development, it is very convenient to start the Web UI from inside the IDE 'on-the-fly' without the need to
compile it on the command line. In Eclipse you can easily do this by executing the class 'WebApplication.java' in the
'web' module via the 'Run As' dialog. However, Eclipse does not understand the previously mentioned Gradle runtime
dependency on the plugin module. It will just execute the generic Web UI without the plugin. What you need to do is add
the dependency manually in the Eclipse configuration. In the 'Run Configurations' dialog, 'Classpath' tab, add your
plugin project to the 'User entries'.

Also, since the Web UI needs several environment variables, you must also set them in the 'Run Configurations' dialog.
Take a look at the file 'environment.sh.dist' for details.

### Implementing your own plugin

The plugin extension functionality is based on the dynamic discovery of classes by the Spring Dependency Injection (DI)
container. This project here contains the file 'context-default.xml' which is used if there is no plugin present. That
is, if it is built and executed as a stand-alone-tool. The XML file defines the names and the order of the 'importer
steps'. Those are simple Java classes that extend the class 'ImporterStep.java' in the 'api' package of the 'core'
module. The importer contains a couple of such steps, which are generic enough to be used in other projects.

In your own project, in the 'plugin' module, you can use this file as a template and create a file called 'context.xml'.
In it, you can use the already implemented steps, change their order, and add your own steps. Inspect the code of the
implemented steps to find out how to create new ones. As soon as your own steps are present in the new XML file, they
will be executed during the import process. The most important step is likely to be the conversion from some
text/XML/Excel/... format to the Solr XML format. Note that your own step can use all the environment variables,
most notably the input directory and output directory paths.
