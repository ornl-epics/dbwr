
DisplayBuilderWebRuntime.prototype.widget_update_methods["progressbar"] = function(widget, data)
{
    if (widget.data("limits-from-pv"))
    {
        if (data.min !== undefined)
            widget.attr("min", data.min);
        if (data.max !== undefined)
            widget.attr("max", data.max);
    }
    widget.val(data.value);
}
