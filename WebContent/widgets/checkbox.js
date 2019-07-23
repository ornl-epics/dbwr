
DisplayBuilderWebRuntime.prototype.widget_init_methods["checkbox"] = function(widget)
{
    let cb = widget.find("input");
    cb.click(event =>
    {
        let pv = widget.data("pv");
        console.log("Write checkbox to " + pv);
        // TODO Update bit in value, don't clobber all bits
        let val = get_bit_value(widget, cb.prop("checked"));
        dbwr.write(pv, val);
    });
}

DisplayBuilderWebRuntime.prototype.widget_update_methods["checkbox"] = function(widget, data)
{
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
