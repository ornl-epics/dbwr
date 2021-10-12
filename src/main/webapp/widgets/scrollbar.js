
function __submit_slider_value(widget, val)
{
    // Write value to PV
    let pv = widget.data("pv")
    // console.log("Slider writes " + pv + " = " + val);
    dbwr.write(pv, val)
}

DisplayBuilderWebRuntime.prototype.widget_init_methods["scrollbar"] = function(widget)
{
    let slider = widget.children("input");
    slider.on("input", () => __submit_slider_value(widget, slider.val()));
}

DisplayBuilderWebRuntime.prototype.widget_update_methods["scrollbar"] = function(widget, data)
{
    // console.log("Update scrollbar");
    // console.log(widget);
    // console.log(data);

    // Determine range
    let range = get_min_max(widget, data);
    
    // Update slider
    let slider = widget.children("input");
    slider.attr("min", range[0]);
    slider.attr("max", range[1]);
    slider.val(data.value);
    
    // Show value in tool-tip
    let info = widget.data("pv") + " = " + format_pv_data_as_text(widget, data);
    widget.attr("title", info);
    
    showWriteAccess(widget, data.readonly);
    if (data.readonly)
        widget.children().css("cursor", "inherit");
    else
        widget.children().css("cursor", "auto");
}
