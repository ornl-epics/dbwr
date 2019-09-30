
DisplayBuilderWebRuntime.prototype.widget_update_methods["progressbar"] = function(widget, data)
{
    let range = get_min_max(widget, data);
    widget.attr("min", range[0]);
    widget.attr("max", range[1]);
    widget.val(data.value);
}
