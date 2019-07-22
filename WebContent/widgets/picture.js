
DisplayBuilderWebRuntime.prototype.widget_init_methods['picture'] = function(widget)
{
    let file = widget.data("file");

    let link = dbwr.display;
    let end = dbwr.display.lastIndexOf('/');
    link = link.substring(0, end) + "/" + file;
    // Better, but unclear how widely this is supported:
    // let link = new URL(file, dbwr.display).href;

    if (link.startsWith("file:"))
        console.warn("Cannot load picture from " + link + " because web browser cannot access files on server.");
    
    // console.log("Loading picture " + link);
    widget.attr("src", link);
}
