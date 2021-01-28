
function __restore_text_entry(widget)
{
    let current = widget.data("editing");
    if (current == undefined)
        return false;
    
    widget.val(current);            
    widget.removeData("editing");            
    return true;
}

function __submit_text_entry(widget, val)
{
    // Write entered value to PV
    let pv = widget.data("pv")
    console.log("Text entry writes " + pv + " = " + val);
    // console.log("For now back to " + widget.val());
    dbwr.write(pv, val);
}

DisplayBuilderWebRuntime.prototype.widget_init_methods['textentry'] = function(widget)
{
    // On focus, save current value in 'editing',
    // which flags the widget as being edited and blocks updates
    widget.focusin(() =>
    {
        // console.log("Focus in...." + widget.val());
        widget.data("editing", widget.val());
    });

    // Handle 'Esc' and (Ctrl) 'Enter' key presses
    widget.keydown(event =>
    {
        // On escape, drop focus, which restores the original value resp. most recent update
        if (event.keyCode == 27)
        {
            if (widget.is("textarea"))
                __restore_text_entry(widget);
            widget.blur();
        }
            
        // Submit value to PV on enter
        else if (event.keyCode == 13)
        {
            // For input, submit on enter
            if (widget.is("input"))
            {
                // Get user's value, then blur() to drop focus and restore last known PV value
                let entered = widget.val();
                widget.blur();
                __submit_text_entry(widget, entered);
            }
            // For text area, require ctrl-enter.
            // Submit by simply dropping focus
            else if (event.ctrlKey)
                widget.blur();
        }
    })

    // When focus is lost, restore current value.
    // If new value was written ('Enter'), that
    // will soon trigger a value update
    widget.focusout(() =>
    {
        // console.log("Focus out..." + widget.val());
        
        let entered = widget.val();
        
        // Some text area users are incapable of pressing Ctrl,
        // so submit on exit.
        // They need to push Esc to exit widget w/o submitting.
        if (__restore_text_entry(widget)  &&  widget.is("textarea"))
            __submit_text_entry(widget, entered);
    });
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
    }
    else
    {
        // jQuery uses val() for both input.value and textarea.html
        widget.val(text);
        showWriteAccess(widget, data.readonly);
    }
}

