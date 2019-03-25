DisplayBuilderWebRuntime.prototype.widget_update_methods["led"] = function(widget, data)
{
    let color;
    if (data.value > 0)
        color = widget.data("on-color");
    else
        color = widget.data("off-color");
    widget.children().attr("fill", color);
}