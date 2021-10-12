
DisplayBuilderWebRuntime.prototype.widget_update_methods["spinner"] = function(widget, data)
{
    widget.val(data.value);
    showWriteAccess(widget, data.readonly);
}

DisplayBuilderWebRuntime.prototype.widget_init_methods['spinner'] = function(widget)
{
    widget.change(() =>
    {
        // Write entered value to PV
        let pv = widget.data("pv")
        console.log("Spinner writes " + pv + " = " + widget.val());
        dbwr.write(pv, widget.val());        
    });
}
