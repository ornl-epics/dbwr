<%@page trimDirectiveWhitespaces="true" %>
<%@page import="dbwr.WebDisplayRepresentation"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<title>Display Builder Web Runtime</title>
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

<p>Display a file that's in the file system of the Tomcat host:</p>
<pre class="example_url">
view.jsp?display=file:/Path/to/Display+Builder/01_main.bob
</pre>

<p>Serve file that's fetched via http: or https:</p>
<pre class="example_url">
view.jsp?display=https%3A//some_host/opi/file.opi
</pre>


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

<input type="button" value="Cache Info" onclick="query_cache()">
<input type="button" value="Clear Cache" onclick="clear_cache()">

<p></p>

<div id="info"><%="JRE: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version") %></div>

<hr>

<div id="versions">
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
