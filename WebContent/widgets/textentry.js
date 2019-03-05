DataBrowserWebRuntime.prototype.widget_subscribe_methods["textentry"] = function(widget, pv_info)
{
	widget.html(parseFloat(pv_info.value).toPrecision(4));
};