
function __submit_slider_value(widget, val)
{
    // Write entered value to PV
    let pv = widget.data("pv")
    console.log("Slider writes " + pv + " = " + val);
    dbwr.write(pv, val)
}


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
    let minval = widget.attr("min");
    let maxval = widget.attr("max");
    if (widget.data("limits-from-pv"))
    {
        if (data.min !== undefined  &&  data.min != "NaN")
            minval = data.min;
        if (data.max !== undefined  &&  data.max != "NaN")
            maxval = data.max;
    }
    
    // Update slider
    let slider = widget.children("input");
    slider.attr("min", minval);
    slider.attr("max", maxval);
    slider.val(data.value);

    // Update label
    let label = widget.children("label");
    label.text(format_pv_data_as_text(widget, data));
}
