/** @param widget Action button widget
 *  @param index  Index 0, 1, .. of action to invoke
 */
function __handleAction(widget, index)
{
    let pv  = widget.data("pv-" + index);
    let val = widget.data("value-" + index);
    if (pv !== undefined  &&  val !== undefined)
    {
        console.log("Action button writes " + pv + " = " + val);
        dbwr.write(pv, val);
        return;
    }
    
    // Open web page or display?
    let new_link = widget.data("linked-file-" + index);
    if (new_link)
    {
        new_link = window.location.origin + window.location.pathname + "?display=" + escape(new_link);
        // alert("Button opens " + new_link);
        
        // Check for macros
        macros = widget.data("linked-macros-" + index);
        if (macros)
            new_link += "&macros=" + encodeURIComponent(widget.data("linked-macros-" + index));
        // alert("Link with macros: " + new_link);
    }
    else
        new_link = widget.data("linked-url-" + index);
    
    if (new_link)
        window.location.href = new_link;
    else
    {
        console.log("ActionButton with unknown action");
        console.log(widget.data());
        return;
    }
}


DisplayBuilderWebRuntime.prototype.widget_init_methods['action_button'] = function(widget)
{
    if (widget.data("enabled") === false)
    {   // If specifically not enabled...
        widget.css("cursor", "not-allowed");
        return;
    }
    
    // Subscribe to all data-pv-#
    // Check all data items for "pv-..." since
    // there might be a data-pv-1 but no data-pv-0
    // in case the first action opens a display,
    // the next writes to a PV.
    Object.keys(widget.data())
          .filter(key => key.startsWith("pv-"))
          .map(key => widget.data(key))
          .forEach(pv_name =>
          {
              // console.log("Action button 'write' PV " + pv_name);
              dbwr.subscribe(widget, 'action_button', pv_name);
              
              // Mark as disconnected
              widget.addClass("BorderDisconnected");
              // On first PV update, border is cleared.
              // Do _not_ show other alarms
              widget.data("alarm-border", "false");
          });

    // Just one action?
    if (widget.data("linked-label-1") === undefined)
        widget.click(() => __handleAction(widget, 0));
    else
    {
        // Build menu to list all options
        let actions = [];
        let index = 0;
        let label = widget.data("linked-label-" + index);
        while (label !== undefined)
        {
            let current_index = index;
            let item = jQuery("<a>").attr("href", "#").text(label);
            item.click(() =>
            {
                hide_contextmenu(widget);
                __handleAction(widget, current_index);
            });
            
            actions.push(item);
            ++index;
            label = widget.data("linked-label-" + index);
        }
        create_contextmenu(widget, actions);
        widget.click(toggle_contextmenu);
    }
}


DisplayBuilderWebRuntime.prototype.widget_update_methods['action_button'] = function(widget, data)
{
    // _handle_widget_pv_update() clears disconnected alarm border
    showWriteAccess(widget, data.readonly);
}

function set_action_button_background_color(widget, color)
{
    widget.css("background-image", "linear-gradient(to bottom right, " + color + ", #DDD)");
}

