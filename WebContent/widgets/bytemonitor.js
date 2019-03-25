
DisplayBuilderWebRuntime.prototype.widget_update_methods["byte_monitor"] = function(widget, data)
{
    let on = widget.data("on-color");
    let off = widget.data("off-color");
    let reverse = widget.data("data") == "true";
    let value = data.value;

    let leds = widget.find("ellipse");
    for (let i=0; i<leds.length; ++i)
    {
        let led = leds.get(i);
        let mask = reverse
                 ? 1 << (leds.length - i)
                 : 1 << i;
        if (value & mask)
            led.setAttribute("fill", on);
        else
            led.setAttribute("fill", off);
    }
}
