function update_led_border(widget, severity)
{
    if (widget.find("ellipse").get(0) === undefined)
    {   // Not a round LED, use outline
        apply_alarm_outline(widget, Severity.UNDEFINED);
        return;
    }
    
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
    border.css("left",   (parseInt(widget.css("left"))  -3) + "px");
    border.css("top",    (parseInt(widget.css("top"))   -3) + "px");
    border.css("width",  (width +6) + "px");
    border.css("height", (height+6) + "px");
    
    border.css("border-radius", (width/2)+"px/" + (height/2) + "px");
    
    // By default, be alarm sensitive
    if (widget.data("alarm-border") != "false")
    {
        if (severity == Severity.NONE)
            border.css("border", "");
        else if (severity == Severity.MINOR)
            border.css("border", "3px solid #F80");
        else if (severity == Severity.MAJOR)
            border.css("border", "3px double #F00");
        else if (severity == Severity.INVALID)
            border.css("border", "3px dashed #F0F");
    }
    // Always show disconnected state, even when not otherwise alarm sensitive
    if (severity == Severity.UNDEFINED)
        border.css("border", "3px dotted #F0F");
}


DisplayBuilderWebRuntime.prototype.widget_alarm_methods["led"] = update_led_border;


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
    
    set_svg_background_color(widget, color);
}