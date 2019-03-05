
function resolve(filename)
{
    var tool = window.location.origin + window.location.pathname + "?display=";
    
    if (filename.startsWith("http"))
        return tool + escape(filename);
    
    // console.log("Resolve " + filename + " relative to " + window.location.href);

    // Get path to current display
    var display = dbwr.getDisplay();
    var end = display.lastIndexOf("/");
    var path = display.substring(0, end+1);

    var resolved = tool + escape(path + filename)
    return resolved;
}

function handleActionButtonClick(widget)
{
    // Open web page or display?
    var new_link = widget.attr("data-linked-file-0");
    if (new_link)
    {
        new_link = resolve(new_link);
        // console.log("Button opens " + new_link);
        
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
