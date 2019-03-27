DisplayBuilderWebRuntime.prototype.widget_update_methods["led"] = function(widget, data)
{
    let bit = widget.data("bit");
    let color;
    if (bit < 0)
    {
        if (data.value > 0)
            color = widget.data("on-color");
        else
            color = widget.data("off-color");
    }
    else
    {
        let mask = 1 << bit;
        if (data.value & mask)
            color = widget.data("on-color");
        else
            color = widget.data("off-color");
    }
    widget.find("ellipse,rect").attr("fill", color);
}