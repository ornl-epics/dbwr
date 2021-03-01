<%@page trimDirectiveWhitespaces="true" %>
<%@page import="dbwr.WebDisplayRepresentation"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<title>Display Builder Web Runtime</title>
<link rel="shortcut icon" href="favicon.png">
<link rel="stylesheet" type="text/css" href="css/widgets.css">
<script type="text/javascript" src="../pvws/js/jquery-3.3.1.js"></script>
<script type="text/javascript" src="../pvws/js/tablesort.js"></script>
</head>

<body>

<h1>Display Builder Web Runtime</h1>

<h3>Example</h3>

<p>Enter a 'file:' or 'http:' URL for a *.bob or *.opi display:<p>

<form id="open_form">
    <table>
    <tr>
      <td>Display:</td>
      <td><input type="text" name="display" style="width: 95%;"></td>
      <td></td>
    </tr>
    <tr>
      <td></td>
      <td>     
        <select name="options" style="width: 95%;">
		<% for (String dsp : WebDisplayRepresentation.display_options)
		        out.println("<option>" + dsp + "</option>");
		%>
        </select>
      </td>
      <td></td>
    </tr>
    <tr>
      <td>Macros:</td>
      <td><input type="text" name="macros" style="width: 95%;"></td>
      <td><input type="button" value="Open"></td>
    </tr>
    </table>
</form>

<h4>Client URLs</h4>

<p>Serve file that's fetched via http: or https:</p>
<pre class="example_url">
view.jsp?display=https%3A//some_host/opi/file.opi
</pre>

<p>Display a file that's in the file system of the Tomcat host:</p>
<pre class="example_url">
view.jsp?display=file:/Path/to/Display+Builder/01_main.bob
</pre>

<p>Note limitations of <code>file:/..</code> URLs:
If the display contains links to other resources like images,
these will be turned URLs relative to the display URL,
i.e. also 'file:/' URLs.
The <span style="text-decoration: underline">client</span> will then try to resolve them,
not the server, which can cause two problems.
For one, the path will not resolve, because it was only
a valid file on the server, not the client.
Secondly, most web browsers now block
local file access for security reasons.

<p>To serve displays which include images, all the files should thus be provided
via http URLs, not file URLs.


<h4>Macros</h4>

<p>When manually entering a URL, you can use the syntax
<code>$(NAME)=Some Value&amp;$(OTHER)=Other Value</code> as in
<pre class="example_url">
view.jsp?display=https://some_host/opi/file.opi&amp;$(S)=06&amp;$(S1)=06
</pre>

<p>That simplified mechanism, however, is limited when you try to
pass values which contain '=' or '&amp;'.
</p>

<p>A more robust mechanism passes macros as a <code>macros=JSON map</code>,
for example <code>{"S"="06", "S1"="06"}</code>,
but note that the map needs to be URL encoded, for example using JavaScript
<code>encodeURIComponent('{"NAME"="Value"}')</code>, resulting in
</p>

<pre class="example_url">
view.jsp?display=https://some_host/opi/file.opi&amp;macros=%7B%22S%22%3A%2206%22%2C%22S1%22%3A%2206%22%7D
</pre>


<h4>Cache</h4>

<p>Page requests are cached, and this URL returns cache info:</p>
<pre class="example_url">
cache
</pre>

<p>During display development, caching can be disabled by including  <code>cache=false</code> in the request:</p>

<pre class="example_url">
view.jsp?cache=false&amp;display=file:/Path/to/Display+Builder/01_main.bob
</pre>


<input type="button" value="Cache Info" onclick="query_cache()">
<input type="button" value="Clear Cache" onclick="clear_cache()">

<p></p>

<div id="info"><%="JRE: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version") %></div>

<hr>

<div id="versions">
2021-03-01 Support 'led' and 'multi_state_led' labels.<br>
2021-02-04 Support 'byte monitor' labels.<br>
2021-01-29 Indicate read-only via same cursor as in CS-Studio.<br>
2021-01-28 Simple 'spinner'.<br>
2020-11-20 Suppress '{fileselector}'.<br>
2020-11-02 favicon.<br>
2020-10-28 Handle legacy Combo with items not-from-PV.<br>
2020-10-21 Defer initial update for known PV to next cycle (avoids update while widget still initializes).<br>
2020-10-05 Support 'navtabs' widgets.<br>
2020-10-02 Embedded display indicates name of failed/missing file.<br>
2020-10-01 Support 'cache=false'.<br>
2020-09-30 Support macros for 'Tabs'.<br>
2020-08-28 Combo support.<br>
2020-07-13 '$(DID)'.<br>
2020-06-11 'Symbol' widget for images. 'Text-Symbol'. Remove 'Disconnect' button. For testing, click on the connection indicator.<br>
2020-06-10 Disable DTD. Mark response as UTF-8 to support wider character sets.<br>
2020-06-09 Display whitelist.<br>
2020-01-17 Middle-click copies PV name to clipboard.<br>
2020-01-07 Use 'flex' display to align label, textupdate.<br>
2019-12-11 Coloring of buttons w/ custom background color.<br>
2019-12-10 Patch legacy 'longString' PV names.<br>
2019-10-28 Fix alarm border location for LED and ByteMonior. Check if action button 'enabled'.<br>
2019-09-30 More lenient rule 'expression' vs. 'value' lookup. Slider and scrollbar widgets.<br>
2019-09-19 Support simple 'out_exp' in visibility rules. Image plot supports rules on 'maximum'. XYPlot plots what's available when some PVs are NaN.<br>
2019-08-05 Several widgets support writing<br>
2019-07-04 Initial version presented at EPICS Meeting<br>
</div>

<script type="text/javascript" src="js/index.js"></script> 

</body>

</html>
