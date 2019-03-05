DataBrowserWebRuntime.prototype.widget_subscribe_methods["textupdate"] = function(widget, pv_info)
{
	widget.html(parseFloat(pv_info.value).toPrecision(4));
};