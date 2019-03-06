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

Place `dbwr.war` in `tomcat/webapps`


Client URLs
-----------


These examples assume tomcat on `localhost:8080`.

Serve file that's local to the Tomcat file system:

    http://localhost:8080/dbwr/index.jsp?display=file:/Path/to/Display+Builder/01_main.bob


Serve file that's fetched via http, passing macros:

    http://localhost:8080/dbwr/index.jsp?display=https%3A//some_host/opi/file.opi&macros=%7B%22S%22%3A%2206%22%2C%22S1%22%3A%2206%22%7D

Macros are passed as a `macros=JSON map`, but note that the map needs to be URL encoded, for example using JavaScript `encodeURIComponent('{"NAME"="Value"}')`
The above example uses `{"S"="06", "S1"="06"}`.


Alternatively, when manually entering a URL, you can use the syntax `&$(NAME)=Value&$(OTHER)=Other+Value` as in

    http://localhost:8080/dbwr/index.jsp?display=https%3A//some_host/opi/file.opi&$(S)=06&$(S1)=06


Development Status
==================

Uses epics2web web socket to show Display Builder screens on the web.

Mostly Functional
-----------------

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
 * Basic Macro support


TODO
----

 * index.jsp that shows some of this info, form to enter file and macros
 * view.jsp to show the file
 * Embedded Displays
 * Pass all Macros on to related displays
 * PV Web Socket:
      - Based on VType.PV (CA, PVA, MQTT, Local, Sim PVs)
      - ReactiveJ Throttling
      - Sends initial metadata, then updates. Client lib keeps complete data.
      - JSON, except some binary encoding for array values
 * Text formatting (precision, units, enum labels)
 * Combo (read-only)
 * Bool Button (read-only)
 * Limited Rule support: Color of rect/circle/label
 * XYPlot
 * Image
  
