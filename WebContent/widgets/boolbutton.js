
DisplayBuilderWebRuntime.prototype.widget_update_methods["bool_button"] = function(widget, data)
{
    let on, off;
    // Get PV labels?
    if (widget.data("pv-labels")   &&
        data.labels !== undefined  &&
        data.labels.length >= 2)
    {
        off = data.labels[0];
        on = data.labels[1];
    }
    else
    {
        off = widget.data("off");
        on = widget.data("on");
    }
    
    let bit = widget.data("bit");
    let state;
    if (bit < 0)
        state = data.value > 0;
    else
    {
        let mask = 1 << bit;
        state = (data.value & mask) != 0;
    }

    let led = widget.find("ellipse");
    let label = widget.find("span");
    if (state)
    {
        if (led)
            led.attr("fill", widget.data("on-color"));
        label.html(" " + on);
    }
    else
    {
        if (led)
            led.attr("fill", widget.data("off-color"));
        label.html(" " + off);
    }
}
