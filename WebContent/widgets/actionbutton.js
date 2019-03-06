
function handleActionButtonClick(widget)
{
    // Open web page or display?
    var new_link = widget.attr("data-linked-file-0");
    if (new_link)
    {
        new_link = window.location.origin + window.location.pathname + "?display=" + escape(new_link);
        // alert("Button opens " + new_link);
        
        // Check for macros
        macros = widget.attr("data-linked-macros-0");
        if (macros)
            new_link += "&macros=" + encodeURIComponent(widget.attr("data-linked-macros-0"));
        // alert("Link with macros: " + new_link);
    }
    else
    {
        new_link = widget.attr("data-linked-url-0");
        
        // Give up if this is a write_pv or execute_command action
        if (! new_link)
            return;
    }
    window.location.href = new_link;
}

DataBrowserWebRuntime.prototype.widget_init_methods['action_button'] = function(widget)
{
    widget.click(() => handleActionButtonClick(widget));
}
