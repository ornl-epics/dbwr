DisplayBuilderWebRuntime.prototype.widget_update_methods["led"] = function(widget, data)
{
    var color;
    if (data.value > 0)
        color = widget.attr("data-on-color");
    else
        color = widget.attr("data-off-color");
    widget.find("ellipse").attr("fill", color);
}