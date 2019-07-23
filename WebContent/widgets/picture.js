
DisplayBuilderWebRuntime.prototype.widget_init_methods['picture'] = function(widget)
{
    let file = widget.data("file");

    let link = dbwr.display;
    let end = dbwr.display.lastIndexOf('/');
    link = link.substring(0, end) + "/" + file;
    // Better, but unclear how widely this is supported:
    // let link = new URL(file, dbwr.display).href;

    // Web client cannot access 'file://' on server.
    // Need to add a 'get_file' servlet, but that would by nature
    // allow access to _all_ files on server?!
    if (link.startsWith("file:"))
        console.warn("Cannot load picture from " + link + " because web browser cannot access files on server.");
    
    // console.log("Loading picture " + link);
    widget.attr("src", link);
}
