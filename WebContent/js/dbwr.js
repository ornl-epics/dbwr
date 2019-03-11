
let info = jQuery("#info");

class DataBrowserWebRuntime
{
    constructor()
    {
        this.display = "";
        // TODO Use PVWS()
        this.pvs = new PVs();
    }
    
    loadContent(display, macros)
    {
        this.display = display;
        info.html("Loading " + display + " with " + macros);
        console.log("Loading " + display + " with " + macros);
        
        jQuery.get("screen",
                { display: this.display, macros: macros },
                data =>
                {
                    jQuery("#content").html(data);
                    info.html("Connecting PVs");
                    this.pvs.open(this.initWidgets);
                });
    }

    initWidgets()
    {
        info.html("Initialize Widgets");
        jQuery(".Widget").each( (index, widget) => dbwr.initWidget(jQuery(widget)));
        info.html("");
    }

    initWidget(widget)
    {
        let type = widget.attr("data-type");
        let pv = widget.attr("data-pv");
        
        let method = this.widget_init_methods[type];
        if (method)
            method(widget);
            
        method = this.widget_subscribe_methods[type];
        if (method)
            this.pvs.subscribe(pv, pv_info => method(widget, pv_info));
    }
}

// Widgets can register init(widget) methods
// widget: jQuery object for the <div> or <svg> or ...
DataBrowserWebRuntime.prototype.widget_init_methods = {};

// Widgets with "data-pv" can register handle_update(widget, pv_info) methods
// widget: jQuery object for the <div> or <svg> or ...
// pv_info: Latest PV info
DataBrowserWebRuntime.prototype.widget_subscribe_methods = {};



let dbwr = new DataBrowserWebRuntime();

