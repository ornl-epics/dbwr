
DisplayBuilderWebRuntime.prototype.widget_update_methods["checkbox"] = function(widget, data)
{
    let cb = widget.find("input");
    if (data.value)
        cb.prop("checked", true);
    else
        cb.prop("checked", false);
}
