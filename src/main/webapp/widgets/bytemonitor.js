
DisplayBuilderWebRuntime.prototype.widget_alarm_methods["byte_monitor"] =
    (widget, severity) =>  update_alarm_div(widget, severity, 4, false);

DisplayBuilderWebRuntime.prototype.widget_update_methods["byte_monitor"] = function(widget, data)
{
    let off = widget.data("off-color");
    let on = widget.data("on-color");
    let label_off = widget.data("label-off-color");
    let label_on = widget.data("label-on-color");
    let reverse = widget.data("reverse");
    let start = widget.data("start");
    if (start == undefined)
        start = 0;
    let value = data.value;
    
    // console.log("Reverse: " + reverse + ", start: " + start);

    let leds = widget.find("ellipse,rect");
    let labels = widget.find("text");
    for (let i=0; i<leds.length; ++i)
    {
        let led = leds.get(i);
        let label = labels.get(i);
        let mask = reverse
                 ? 1 << (start + i)
                 : 1 << (leds.length - 1 + start - i);
        // console.log("Mask: " + mask);
        if (value & mask)
        {
            led.setAttribute("fill", on);
            if (label !== undefined)
                label.setAttribute("fill", label_on);
        }
        else
        {
            led.setAttribute("fill", off);
            if (label !== undefined)
                label.setAttribute("fill", label_off);
        }
    }
}
