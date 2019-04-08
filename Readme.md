Display Builder Web Runtime
===========================

Building
--------

    export ANT_HOME=/path/to/apache-ant
    export CATALINA_HOME=/path/to/apache-tomcat
    export JAVA_HOME=/path/to/jdk8
    export PATH=$ANT_HOME/bin:$JAVA_HOME/bin:$PATH
    
    ant clean war

Running under Tomcat
--------------------

First, install the PV Web Socket, which must be available
on the same Tomcat instance.

Set the following environment variables, for example in $CATALINA_HOME/bin/setenv.sh:

 * `DBWR1`, `DBWR2`, ...: URLs of displays to suggest on the start page.

Place `dbwr.war` in `$CATALINA_HOME/webapps`


Client URLs
-----------

Open the main page of the running instance for explanation
of URLs used to open displays.
Assuming Tomcat on `localhost:8080`, open

    http://localhost:8080/dbwr
    

Development Status
==================

The following widget types and features have been implemented with basic functionality:

 * Label
 * Rectangle
 * Ellipse
 * Arc
 * Polyline
 * Polygon
 * Text Update
 * Text Input
 * Text formatting (precision, units, enum labels)
 * LED
 * Multi-State LED
 * Action Button to open display or web link
 * Combo
 * Group with group border
 * Embedded Displays
 * XYPlot
 * Image
 * Macro support
 * Alarm-sensitive border based on PV
 * Limited Rule support: Color of rect/circle/label, visibility
 * Caching
