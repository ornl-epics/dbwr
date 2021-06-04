<%@page import="java.time.Instant"%>
<%@page trimDirectiveWhitespaces="true" %>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="dbwr.macros.MacroUtil"%>
<%@page import="dbwr.parser.HTMLUtil"%>
<%@page import="dbwr.parser.WidgetFactory"%>
<%@page import="dbwr.WebDisplayRepresentation"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<meta http-equiv="Cache-Control" content="no-store" />
<title>Display Builder Web Runtime</title>
<%
// Pseudo-unique value to prevent caching of the CSS and JS
final Instant now = Instant.now();
final String UNIQUE=Long.toString(now.getEpochSecond());

out.append("<!--  Generated " + now + " -->\n");
%>
<link rel="shortcut icon" href="favicon.png">
<link rel="stylesheet" type="text/css" href="css/normalize.css">
<link rel="stylesheet" type="text/css" href="css/widgets.css?V=<%=UNIQUE%>">
<%
for (String c : WidgetFactory.css)
	out.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"widgets/" + c + "?V=" + UNIQUE + "\">\n");
%>
<script type="text/javascript" src="../pvws/js/jquery-3.3.1.js"></script>
<script type="text/javascript" src="../pvws/js/base64.js"></script>
<script type="text/javascript" src="../pvws/js/pvws.js?V=<%=UNIQUE%>"></script> 
<script type="text/javascript" src="js/lib/jquery.event.drag.js"></script>
<script type="text/javascript" src="js/lib/jquery.mousewheel.js"></script>
<script type="text/javascript" src="js/flot/jquery.canvaswrapper.js"></script>
<script type="text/javascript" src="js/flot/jquery.colorhelpers.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.saturated.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.browser.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.drawSeries.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.errorbars.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.uiConstants.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.logaxis.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.symbol.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.flatdata.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.navigate.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.fillbetween.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.stack.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.touchNavigate.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.hover.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.touch.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.time.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.axislabels.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.selection.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.composeImages.js"></script>
<script type="text/javascript" src="js/flot/jquery.flot.legend.js"></script>
<script type="text/javascript" src="js/clipboard.js"></script> 
<script type="text/javascript" src="js/dbwr.js?V=<%=UNIQUE%>"></script> 
<%
for (String js : WidgetFactory.js)
	 out.append("<script type=\"text/javascript\" src=\"widgets/" + js + "?V=" + UNIQUE + "\"></script>\n");
%>
</head>

<body>

<div id="content"></div>

<div id="info_panel">
<span id="info">INFO</span>
<img id="status" alt="Status" title="Connect/disconnect" src="../pvws/img/disconnected.png">
</div>


<script type="text/javascript">
<%
// Display, default empty
String orig_display_name = request.getParameter("display");
if (orig_display_name == null)
    orig_display_name = "";

// Sanitize display name
// The display name is passed to Javascript like this:
//  dbwr.load_content('display_name'...
//
// Filter out text like "','{}','true');});" that would close
// a call like load_content(); and can then be followed by
// JavaScript like alert(..).
final String display_name = orig_display_name.replaceAll("[]<>(){};',\"]", "");
// System.out.println("   " + orig_display_name + "\n-> " + display_name);

// Cache, default "true"
String cache = request.getParameter("cache");
if (! ("true".equals(cache) || "false".equals(cache)))
    cache = "true";

// Macros are usually passed as "&macros=JSON map"
String macro_text = request.getParameter("macros");
if (macro_text == null)
{
    // For manually entered URLs, allow the more convenient
    // $(NAME)=value&$(OTHER)=other
    final Map<String, String> macros = new HashMap<String, String>();
    final Enumeration<String> params = request.getParameterNames();
    while (params.hasMoreElements())
    {
        String name = params.nextElement();
        if (name.startsWith("$("))
        {
            final String value = request.getParameter(name);
            name = name.substring(2, name.length()-1);
            macros.put(name, value);
        }
    }
    macro_text = MacroUtil.toJSON(macros);
}
// Use single quotes when passing macro_text on because the JSON contains double quotes.
// Don't allow single quote inside macro_text since that would 'end' the string prematurely
// and allow Javascript injection.
macro_text.replaceAll("['<>{},;/]", "");

if (WebDisplayRepresentation.pvws_url != null)
{
    out.println("// Web Socket URL");
    out.println("let wsurl = \"" + WebDisplayRepresentation.pvws_url + "/pv\";");
}
else
{
%>
// Determine PV Web Socket URL relative to this page
let wsurl = window.location.pathname;
wsurl = wsurl.substring(0, wsurl.indexOf("/dbwr"));
wsurl = window.location.host + wsurl + "/pvws/pv";
if (window.location.protocol == "https:")
    wsurl = "wss://" + wsurl;
else
    wsurl = "ws://" + wsurl;
<%
}
%>
let dbwr = new DisplayBuilderWebRuntime(wsurl);

jQuery("#status").click(() => dbwr.pvws.close() );

jQuery(() =>
{
	dbwr.load_content('<%=display_name%>', '<%=macro_text%>', '<%=cache%>');
});
</script>
</body>

</html>