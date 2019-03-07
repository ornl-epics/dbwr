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

Place `dbwr.war` in `$CATALINA_HOME/webapps`


Client URLs
-----------

Open the main page of the running instance for explanation
of URLs used to open displays.
Assuming Tomcat on `localhost:8080`, open

    http://localhost:8080/dbwr
    

Development Status
==================

Uses epics2web web socket to show Display Builder screens on the web.

Mostly Functional
-----------------

 * Macro support
 * Label
 * Rectangle
 * Ellipse
 * Arc
 * Polyline
 * Polygon
 * Text Update
 * Text Input
 * LED
 * Multi-State LED
 * Action Button to open display or web link
 * Group with group border
 * Embedded Displays


TODO
----

 * PV Web Socket:
      - Based on VType.PV (CA, PVA, MQTT, Local, Sim PVs)
      - ReactiveJ Throttling
      - Sends initial metadata, then updates. Client lib keeps complete data.
      - JSON, except some binary encoding for array values
 * Text formatting (precision, units, enum labels)
 * Update alarm-sensitive border based on PV
 * Combo (read-only)
 * Bool Button (read-only)
 * Limited Rule support: Color of rect/circle/label
 * XYPlot
 * Image
  
