

// Info for one PV
class PVInfo
{
    constructor(pv_name)
    {
        this.pv_name = pv_name;
        
        // Most recent data
        this.data = null;
        
        // Functions to invoke with this info when it changes
        this.callbacks = [];
    }    
}

class DataBrowserWebRuntime
{
    /** Construct data browser web runtime for a PV Web Socket
     *  @param pvws_url PV Web Socket URL
     */
    constructor(pvws_url)
    {
        this.display = "";
        this.info = jQuery("#info");
        this.pvws = new PVWS(pvws_url,
                             connected => this._handleConnection(connected),
                             message   => this._handleMessage(message));
        this.pv_infos = {}
    }
    
    /** @param message Message to log in 'info' span and console */
    log(message)
    {
        this.info.html(message);
        console.log(message);
    }
    
    /** Lifecycle step 1: Load display
     * 
     *  Will then connect to PVs
     *  
     *  @param display URL of display to load
     *  @param macros Macros (JSON)
     */
    loadContent(display, macros)
    {
        this.display = display;
        this.log("Loading " + display + " with " + macros);
        
        jQuery.get("screen",
                { display: this.display, macros: macros },
                data =>
                {
                    jQuery("#content").html(data);
                    this.log("Connecting PVs");
                    this.pvws.open();
                });
    }

    /** Lifecycle step 2: PV web socket connected
     * 
     *  When connected, will initialize widgets
     *  
     *  @param connected True/false as PVWS connects/disconnects
     */
    _handleConnection(connected)
    {
        jQuery("#status").attr("src", connected ? "../pvws/img/connected.png" : "../pvws/img/disconnected.png");
        if (connected)
        {
            this.log("Initialize Widgets");
            jQuery(".Widget").each( (index, widget) => dbwr._initWidget(jQuery(widget)));
        }
        else
        {
            // Need to re-subscribe when we reconnect
            this.pv_infos = {}
            this.log("Disconnected");
        }
    }

    /** Lifecycle step 3: Start widget
     * 
     *  @param widget Widget that's initialized so it can subscribe to PVs
     */
    _initWidget(widget)
    {
        let type = widget.attr("data-type");
        
        let method = this.widget_init_methods[type];
        if (method)
        {
            // console.log("Calling widget init method for " + type)
            method(widget);
        }
            
        method = this.widget_subscribe_methods[type];
        if (method)
        {
            let pv_name = widget.attr("data-pv");
            // console.log("Calling widget subscribe method for " + type + " with " + pv_name);
            this._subscribe(pv_name, pv_info => method(widget, pv_info));
        }
    }
    
    _subscribe(pv_name, callback)
    {
        let info = this.pv_infos[pv_name];
        let new_pv = info === undefined;
        if (new_pv)
            this.pv_infos[pv_name] = info = new PVInfo(pv_name);
        info.callbacks.push(callback);
        if (new_pv)
        {
            this.pvws.subscribe(pv_name);
            // console.log("Subscribed to " + pv_name);
            // console.log(info);
            // console.log("Callbacks: " + info.callbacks.length);
        }
        else
        {
            // TODO Invoke callback with the known data
        }
    }
    
    _handleMessage(message)
    {
        if (message.type == 'update')
        {
            // console.log(message);
            let pv_name = message['pv'];
            
            let info = this.pv_infos[pv_name];
            if (info === undefined)
                console.error("PV Update for unknown " + pv_name + ": " + JSON.stringify(message));
            else
            {
                info.data = message;
                let cb;
                for (cb of info.callbacks)
                    cb(message);
            }
        }
        else
        {
            console.log("Received unknown PV message");
            console.log(message);
        }
    }
}

// Widgets can register init(widget) methods
// widget: jQuery object for the <div> or <svg> or ...
DataBrowserWebRuntime.prototype.widget_init_methods = {};

// Widgets with "data-pv" can register handle_update(widget, pv_info) methods
// widget: jQuery object for the <div> or <svg> or ...
// pv_info: Latest PV info
DataBrowserWebRuntime.prototype.widget_subscribe_methods = {};


