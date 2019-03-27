DisplayBuilderWebRuntime.prototype.widget_update_methods["multi_state_led"] = function(widget, data)
{
    let color = widget.data("fallback-color");
    let index = 0;
    let value = widget.data("state-value-" + index);
    while (value !== undefined)
    {
        if (data.value == value)
        {
            color = widget.data("state-color-" + index);
            break;
        }
        ++index;
        value = widget.data("state-value-" + index);
    }
    widget.find("ellipse,rect").attr("fill", color);
}