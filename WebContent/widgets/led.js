function update_led_border(widget, severity)
{
    let square = widget.find("ellipse").get(0) === undefined;
    
    // Add a <div id="w123_border"> _before_ the widget
    let id = widget.attr("id");
    let bid = id + "_border";
    let border = jQuery("#" + bid);
    if (border.get(0) === undefined)
    {
        border = jQuery("<div>").attr("id", bid)
                                .addClass("Widget");
        widget.before(border);
    }
    
    let width = parseInt(widget.css("width"));
    let height = parseInt(widget.css("height"));
    let pad = 3;
    if (square)
        pad = 4;
    else
        border.css("border-radius", (width/2)+"px/" + (height/2) + "px");
    border.css("left",   (parseInt(widget.css("left")) -pad) + "px");
    border.css("top",    (parseInt(widget.css("top"))  -pad) + "px");
    border.css("width",  (width + 2*pad) + "px");
    border.css("height", (height+ 2*pad) + "px");

    // Always clear border, and always show disconnected state
    if (severity == Severity.NONE)
        border.css("border", "");
    else if (severity == Severity.UNDEFINED)
        border.css("border", "3px dotted #F0F");
    else if (widget.data("alarm-border") != "false")
    {   // Show remaining states unless specifically not alarm sensitive
        if (severity == Severity.MINOR)
            border.css("border", "3px solid #F80");
        else if (severity == Severity.MAJOR)
            border.css("border", "3px double #F00");
        else if (severity == Severity.INVALID)
            border.css("border", "3px dashed #F0F");
    }
}

// Both types of LED use the same round border
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
