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
    var value = is_bit_set(widget, data);
    widget.data("value", value);
    if (value)
        set_svg_background_color(widget, widget.data("on-color"));
    else
        set_svg_background_color(widget, widget.data("off-color"));
}

//Called by color rules that update the on/off colors
function set_led_off_color(widget, color)
{
    // Update the color
    widget.data("off-color", color);
    // Re-draw right away because that's the color for the current state?
    if (! widget.data("value"))
        set_svg_background_color(widget, color);
}

function set_led_on_color(widget, color)
{
    widget.data("on-color", color);
    if (widget.data("value"))
        set_svg_background_color(widget, color);
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

