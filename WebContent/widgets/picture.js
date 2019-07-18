
DisplayBuilderWebRuntime.prototype.widget_init_methods['picture'] = function(widget)
{
    let file = widget.data("file");

    let link = dbwr.display + "/../" + file;
    // Better, but unclear how widely this is supported:
    // let link = new URL(file, dbwr.display).href;
    
    // console.log("Loading picture " + link);
    widget.attr("src", link);
}
