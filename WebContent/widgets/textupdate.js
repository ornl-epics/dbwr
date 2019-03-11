DataBrowserWebRuntime.prototype.widget_subscribe_methods["textupdate"] = function(widget, pv_info)
{
    if (pv_info.text)
        widget.html(pv_info.text);
    else
    {
        // TODO Format...
        console.log(pv_info);
        widget.html(pv_info.value);
    }
};