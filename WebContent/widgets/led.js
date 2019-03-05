DataBrowserWebRuntime.prototype.widget_subscribe_methods["led"] = function(widget, pv_info)
{
    var color;
    if (pv_info.value > 0)
        color = widget.attr("data-on-color");
    else
        color = widget.attr("data-off-color");
    widget.find("ellipse").attr("fill", color);
}