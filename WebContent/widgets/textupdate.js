DataBrowserWebRuntime.prototype.widget_subscribe_methods["textupdate"] = function(widget, pv_info)
{
    // TODO Format...
    if (isNaN(pv_info.value))
        widget.html(pv_info.value);
    else
        widget.html(parseFloat(pv_info.value).toPrecision(4));
};