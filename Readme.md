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

First, install the [PV Web Socket](https://github.com/kasemir/pvws), which must be available
on the same Tomcat instance.

Set the following environment variables, for example in $CATALINA_HOME/bin/setenv.sh:

 * `DBWR1`, `DBWR2`, ...: URLs of displays to suggest on the start page.
 * `WHITELIST1`, `WHITELIST2`, ...: Regular expressions of allowed displays.

When no `WHITELIST1` entries are defined, `.*` will be used.

Place `dbwr.war` in `$CATALINA_HOME/webapps`


Client URLs
-----------

Open the main page of the running instance for explanation
of URLs used to open displays.
Assuming Tomcat on `localhost:8080`, open

    http://localhost:8080/dbwr
    


When you then open a display, you'll note that the resulting URL has the general format

    http://localhost:8080/dbwr/view.jsp?display=URL_OF_THE_DISPLAY.bob

Note that the display information is cached, so when you edit a display file
and would like to force an update to the web version right away,
circumventing the cache, add `cache=false` to the request:

    http://localhost:8080/dbwr/view.jsp?cache=false&display=URL_OF_THE_DISPLAY.bob


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
 * Text Input (can write)
 * Text formatting (precision, units, enum labels)
 * LED
 * Multi-State LED
 * Action Button to open display or web link
 * Action Button to write value to PV
 * Combo
 * Group with group border
 * Embedded Displays
 * Tabs
 * XYPlot
 * Image, runtime options to change scaling and color map
 * Macro support
 * Alarm-sensitive border based on PV
 * Limited Rule support: Color of rect/circle/label, visibility
 * Caching

In principle, the PV Web Socket supports both Channel Access and PV Access,
but so far all testing of the display builder web runtime has concentrated
on Channel Access.
 
Widget Implementation
=====================

Each widget needs to derive from `dbwr.widgets.Widget` and register in `widget.properties`.
The widget constructor parses the display file XML for the widget.

Static Widget
-------------

A static widget implements `Widget.fillHTML()` to create the static HTML content.

Dynamic Widget
--------------

A dynamic widget registers Javascript in a static initializer that calls `WidgetFactory.addJavaScript()`.
That Javascript can then register `init` or `update` methods via
`DisplayBuilderWebRuntime.prototype.widget_init_methods` and
`DisplayBuilderWebRuntime.prototype.widget_update_methods`.

PV Widgets
----------

Any widget with a `data-pv` attribute in its widget HTML will automatically
subscribe to that PV. It should register a java script method in
`DisplayBuilderWebRuntime.prototype.widget_update_methods` to handle the received PV updates.

PVs with multiple PVs can subscribe to additional PVs in their `widget_init_methods`.
