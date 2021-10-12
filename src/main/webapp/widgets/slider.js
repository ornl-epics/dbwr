
DisplayBuilderWebRuntime.prototype.widget_init_methods["slider"] = function(widget)
{
    let slider = widget.children("input");
    slider.on("input", () => __submit_slider_value(widget, slider.val()));
}

DisplayBuilderWebRuntime.prototype.widget_update_methods["slider"] = function(widget, data)
{
    // console.log("Update slider");
    // console.log(widget);
    // console.log(data);

    // Determine range
    let range = get_min_max(widget, data);
    
    // Update slider
    let slider = widget.children("input");
    slider.attr("min", range[0]);
    slider.attr("max", range[1]);
    slider.val(data.value);

    // Update label
    let label = widget.children("label");
    label.text(format_pv_data_as_text(widget, data));
    
    // When read-only, inner input, label should use the disabled cursor
    showWriteAccess(widget, data.readonly);
    if (data.readonly)
        widget.children().css("cursor", "inherit");
    else
        widget.children().css("cursor", "auto");
}
