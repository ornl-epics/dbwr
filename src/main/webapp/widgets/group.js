DisplayBuilderWebRuntime.prototype.widget_init_methods["group"] = function(widget)
{
    // Locate group label
    let label = widget.find(".GroupLabel");
    if (label === undefined)
        return;
    // Set background color of label to next parent's color
    let p = widget.parent();
    while (p)
    {
        let c = p.css("background-color");
        if (c  &&  c != "rgba(0, 0, 0, 0)"  && c != "transparent")
        {
            label.css("background-color", c);
            return;
        }
        p = p.parent();
    }
}