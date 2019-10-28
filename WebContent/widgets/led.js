function update_led_border(widget, severity)
{
    let square = widget.find("ellipse").get(0) === undefined;
    
    if (square)
        update_alarm_div(widget, severity, 4, false);
    else
        update_alarm_div(widget, severity, 3, true);
}

DisplayBuilderWebRuntime.prototype.widget_alarm_methods["led"] = update_led_border;
DisplayBuilderWebRuntime.prototype.widget_alarm_methods["multi_state_led"] = update_led_border;

DisplayBuilderWebRuntime.prototype.widget_update_methods["led"] = function(widget, data)
{
    if (is_bit_set(widget, data))
        set_svg_background_color(widget, widget.data("on-color"));
    else
        set_svg_background_color(widget, widget.data("off-color"));
}

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
