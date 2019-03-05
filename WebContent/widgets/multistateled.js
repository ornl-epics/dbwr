DataBrowserWebRuntime.prototype.widget_subscribe_methods["multi_state_led"] = function(widget, pv_info)
{
    var color = widget.attr("data-fallback-color");
    var index = 0;
    var value = widget.attr("data-state-value-" + index);
    while (value)
    {
        if (pv_info.value == value)
        {
            color = widget.attr("data-state-color-" + index);
            break;
        }
        ++index;
        value = widget.attr("data-state-value-" + index);
    }
    widget.find("ellipse").attr("fill", color);
}