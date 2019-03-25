DisplayBuilderWebRuntime.prototype.widget_update_methods["multi_state_led"] = function(widget, data)
{
    let color = widget.attr("data-fallback-color");
    let index = 0;
    let value = widget.attr("data-state-value-" + index);
    while (value)
    {
        if (data.value == value)
        {
            color = widget.attr("data-state-color-" + index);
            break;
        }
        ++index;
        value = widget.attr("data-state-value-" + index);
    }
    widget.find("ellipse").attr("fill", color);
}