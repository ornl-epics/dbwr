<%@page trimDirectiveWhitespaces="true" %>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="dbwr.macros.MacroUtil"%>
<%@page import="dbwr.parser.HTMLUtil"%>
<%@page import="dbwr.parser.WidgetFactory"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<title>Display Builder Web Runtime</title>
<link rel="stylesheet" type="text/css" href="css/normalize.css">
<link rel="stylesheet" type="text/css" href="css/widgets.css">
<%
for (String c : WidgetFactory.css)
	out.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"widgets/" + c + "\">\n");
%>
<script type="text/javascript" src="../pvws/js/jquery-3.3.1.js"></script>
<script type="text/javascript" src="../pvws/js/base64.js"></script>
<script type="text/javascript" src="../pvws/js/pvws.js"></script> 
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
<script type="text/javascript" src="js/dbwr.js"></script> 
<%
for (String js : WidgetFactory.js)
	 out.append("<script type=\"text/javascript\" src=\"widgets/" + js + "\"></script>\n");
%>
</head>

<body>

<div id="content"></div>

<div id="info_panel">
<span id="info">INFO</span>
<img id="status" alt="Status" src="../pvws/img/disconnected.png">
<input id="disconnect" type="button" value="Disconnect">
</div>


<script type="text/javascript">
<%
String display_name = request.getParameter("display");
if (display_name == null)
	display_name = "";
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
// Use single quotes when passing macro_text on because the JSON contains double quotes
%>

// Determine PV Web Socket URL relative to this page
let wsurl = window.location.pathname;
wsurl = wsurl.substring(0, wsurl.indexOf("/dbwr"))
wsurl = window.location.host + wsurl + "/pvws/pv"
if (window.location.protocol == "https:")
    wsurl = "wss://" + wsurl
else
    wsurl = "ws://" + wsurl

let dbwr = new DisplayBuilderWebRuntime(wsurl);

jQuery("#disconnect").click(() => dbwr.pvws.close() );

jQuery(() =>
{
	dbwr.load_content('<%=display_name%>', '<%=macro_text%>');
});
</script>
</body>

</html>