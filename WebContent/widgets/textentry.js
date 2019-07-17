
DisplayBuilderWebRuntime.prototype.widget_init_methods['textentry'] = function(widget)
{
    widget.focusin(() =>
    {
        console.log("Focus in...." + widget.val());
        widget.data("editing", widget.val());
        
    });

    widget.focusout(() =>
    {
        console.log("Focus out..." + widget.val());
        widget.val(widget.data("editing"))
        widget.removeData("editing");
    });
    
    widget.keydown(event =>
    {
        if (event.keyCode == 13)
        {
            let val = widget.val();
            console.log("ENTERED " + widget.data("pv") + " = " + val);
            // Loose focus, which restores the original value resp. most recent update
            widget.blur();
            console.log("For now back to " + widget.val());
            // TODO write val to PV
        }
    })
}


DisplayBuilderWebRuntime.prototype.widget_update_methods['textentry'] = function(widget, data)
{
    let text = format_pv_data_as_text(widget, data);

    if (widget.data("editing") !== undefined)
    {
        widget.data("editing", text);
        console.log("Editing, caching " + text);
        return;
    }
    
    widget.val(text);
    
    // See textupdate.js to center vertically?

    // TODO After testing the 'write' and 'return' vs. 'tab/escape' handling:
    // widget.prop('disabled', data.readonly);
    
    if (data.readonly)
        widget.css("cursor", "not-allowed");
    else
        widget.css("cursor", "auto");
}

