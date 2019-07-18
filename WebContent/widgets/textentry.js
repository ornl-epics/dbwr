
DisplayBuilderWebRuntime.prototype.widget_init_methods['textentry'] = function(widget)
{
    // On focus, save current value in 'editing',
    // which flags the widget as being edited and blocks updates
    widget.focusin(() =>
    {
        // console.log("Focus in...." + widget.val());
        widget.data("editing", widget.val());
        
    });

    // When loosing focus, restore current value.
    // If new value was written ('Enter'), that
    // will soon trigger a value update
    widget.focusout(() =>
    {
        // console.log("Focus out..." + widget.val());
        widget.val(widget.data("editing"))
        widget.removeData("editing");
    });
    
    // Handle 'Esc' and 'Enter' key presses
    widget.keydown(event =>
    {
        // On escape, loose focus, which restores the original value resp. most recent update
        if (event.keyCode == 27)
            widget.blur();
        else if (event.keyCode == 13)
        {
            // Get user's value, then blur() to loose focus and restore last known PV value
            let val = widget.val();
            widget.blur();

            // Write entered value to PV
            let pv = widget.data("pv")
            console.log("Text entry writes " + pv + " = " + val);
            // console.log("For now back to " + widget.val());
            dbwr.write(pv, val)
        }
    })
}


DisplayBuilderWebRuntime.prototype.widget_update_methods['textentry'] = function(widget, data)
{
    let text = format_pv_data_as_text(widget, data);

    // When editing, cache received value.
    // Don't update the currently displayed value since user is editing it.
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

