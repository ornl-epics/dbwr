Display Builder Web Runtime
===========================

Create displays in the [desktop version of the CS-Studio Display Builder](https://github.com/ControlSystemStudio/phoebus)
and use them in the control room.
This web runtime then provides convenient remote access.

 * Use any web browser with zero client-side installation, including smart phones
 * Supports most widgets and their key features

For more, see [DisplayBuilderWebRuntime.pdf](https://indico.cern.ch/event/766611/contributions/3438289/attachments/1855426/3047296/DisplayBuilderWebRuntime.pdf) 
presentation from [2019 EPICS Meeting at ITER](https://indico.cern.ch/event/766611/)

Version Info
------------

See bottom of `src/main/webapp/index.jsp`

Binary
------

.. is available as https://controlssoftware.sns.ornl.gov/css_phoebus/nightly/dbwr.war
but you may prefer to build it locally as described next.

Building
--------

To build with maven:

    mvn clean package

Project can also be imported into Eclipse JEE IDE
via File, Import, Maven, Existing Maven Projects.

**Docker**

Edit .env file with settings for git version and port number and docker/setenv.sh with your local site settings for Display/web socket settings. Then:

```
docker-compose build
```

Running under Tomcat
--------------------

First, install the [PV Web Socket](https://github.com/ornl-epics/pvws), which must be available
on the same Tomcat instance.

Set the following environment variables, for example in $CATALINA_HOME/bin/setenv.sh:

 * `DBWR1`, `DBWR2`, ...: URLs of displays to suggest on the start page.
 * `WHITELIST1`, `WHITELIST2`, ...: Regular expressions of allowed displays.

When no `WHITELIST1` entries are defined, `.*` will be used.

Place `dbwr.war` in `$CATALINA_HOME/webapps`

If you placed the `pvws.war` in the same Tomcat instance as the `dbwr.war`, there is nothing to configure.
By default, the display runtime will connect to the PV Web Socket under the same base URL.
When you access the displays via `http://some_host:8080/dbwr/...`, it will connect to PVs via `ws://some_host:8080/pvws`.

If you want to connect to the PV Web Socket on a different URL, for example on a different host,
you need to configure this via the following environment variables

 * `PVWS_HOST`: Hostname and port of PV Web Socket when not co-located with `dbwr.war`,
   for example `some.other.host.org:8081`
 * `PVWS_HTTP_PROTOCOL`: `http` or `https` based on what pvws uses (default is http)
 * `PVWS_WS_PROTOCOL`: Web socket protocol of PV Web Socket, either `ws` or `wss` (default is ws)

**Docker**

To run docker container (use -d option to run in detached mode):

```
docker-compose up
```

The status can be seen with docker ps. The status will be healthy if the dbwr index page loads
```
docker ps
```

Client URLs
-----------

Open the main page of the running instance for explanation
of URLs used to open displays.
Assuming Tomcat on `localhost:8080`, open

    http://localhost:8080/dbwr
    


When you then open a display, you'll find that the resulting URL has the general format

    http://localhost:8080/dbwr/view.jsp?display=URL_OF_THE_DISPLAY.bob


To access display files that you also use in the control room, you have two basic options.
You can make them available in the web server's file system, for example via a network file system mount
or by periodically fetching a copy of the current displays from a version control system. In that case,
use URLs like `...view.jsp?display=file:/path/to/display.bob`, where the file path refers to the
file system of the web server.
Alternatively, you may expose the display files themselves via a web server. For example, assume that
`http://my_control_system_host/displays/path/to/display.bob` serves a display file, then a URL like
`...view.jsp?display=http://my_control_system_host/displays/path/to/display.bob` will
instruct the display web runtime to fetch that display file and render it.

You might be concerned that the web runtime could be misused to probe the file
system of the tomcast host via for example `...view.jsp?display=file:/etc/password`.
To prevent this, refer to the `WHITELIST..` settings mentioned above and use them to limit
access to only the `file:/path/to/.*` or `http://my_control_system_host/displays/path/to/.*` paths
that you intent to expose.

While the web runtime can fundamentally read the same display files as the desktop version of the display builder,
note that it is a separate implementation that can't be 100% compatible.
While most widgets and their key features are supported, even including some rules, scripts are not,
and plots are also simplified.
In addition, displays created for desktop usage in the control room are often too big to be useful on a smaller device like a phone.
It might thus be necessary to optimize desktop displays,
for example to split one large desktop display into smaller displays meant for remote access.

The display information is cached, so when you edit a display file
and would like to force an update to the web version right away,
circumventing the cache, add `cache=false` to the request:

    http://localhost:8080/dbwr/view.jsp?cache=false&display=URL_OF_THE_DISPLAY.bob

Each time the display is fetched with `cache=false`, the entry in the cache
is replaced with a newly parsed display, replacing the cached version.
Following calls without `cache` or with `cache=true` will then again fetch
the cached display.


Development Status
==================

Maven layout is based on

    mvn archetype:generate -DgroupId=gov.ornl -DartifactId=dbwr -DarchetypeArtifactId=maven-archetype-webapp -DinteractiveMode=false

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
 * Byte monitor
 * Action Button to open display or web link
 * Action Button to write value to PV
 * Combo
 * Group with group border
 * Embedded Displays
 * Tabs
 * Navigation Tabs
 * Template/Instances
 * Basic XYPlot, Data Browser, Stripchart representation
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

For an example, see `LabelWidget.java` or `EllipseWidget.java`.

Dynamic Widget
--------------

A dynamic widget registers Javascript in a static initializer that calls `WidgetFactory.addJavaScript()`.
That Javascript can then register `init` or `update` methods via
`DisplayBuilderWebRuntime.prototype.widget_init_methods` and
`DisplayBuilderWebRuntime.prototype.widget_update_methods`.

'Dynamic' widgets are usually based on a single PV and use the `PVWidget` base class
to place the PV name into a `data-pv` attribute.
Any widget with a `data-pv` attribute in its widget HTML will automatically
subscribe to that PV. It should register a java script method in
`DisplayBuilderWebRuntime.prototype.widget_update_methods` to handle the received PV updates.

For an example, see `ProgressBarWidget.java`.

PVs with multiple PVs can subscribe to additional PVs in their `widget_init_methods`.

