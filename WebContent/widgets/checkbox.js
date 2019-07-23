
DisplayBuilderWebRuntime.prototype.widget_init_methods["checkbox"] = function(widget)
{
    widget.data("value", 0);

    let cb = widget.find("input");
    cb.click(event =>
    {
        let pv = widget.data("pv");
        console.log("Write checkbox to " + pv);
        
        // Toggle bit in current value (unless bit < 0)
        let val;
        let bit = widget.data("bit");
        if (bit < 0)
            val = cb.prop("checked") ? 1 : 0;
        else
            val = widget.data("value") ^ (1 << bit);
        dbwr.write(pv, val);
    });
}

DisplayBuilderWebRuntime.prototype.widget_update_methods["checkbox"] = function(widget, data)
{
    widget.data("value", data.value);
    let cb = widget.find("input");
    
    // Show current value
    if (is_bit_set(widget, data))
        cb.prop("checked", true);
    else
        cb.prop("checked", false);
    
    // Disable when read-only
    if (data.readonly)
        cb.css("cursor", "not-allowed");
    else
        cb.css("cursor", "auto");
    cb.prop('disabled', data.readonly);
}
