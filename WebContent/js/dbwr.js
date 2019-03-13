
class Severity
{
}

Severity.NONE = "NONE";
Severity.MINOR = "MINOR";
Severity.MAJOR = "MAJOR";
Severity.INVALID = "INVALID";
Severity.UNDEFINED = "UNDEFINED";


// Info for one PV
class PVInfo
{
    constructor(pv_name)
    {
        this.pv_name = pv_name;
        
        // Most recent data
        this.data = { severity: Severity.UNDEFINED };
        
        // Functions to invoke with this info when it changes
        this.callbacks = [];
    }
}

class DisplayBuilderWebRuntime
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
                    // Place display's HTML into content
                    let content = jQuery("#content");
                    content.html(data);
                    // Update height to space that's actually required,
                    // so we can add further HTML for info etc.
                    // below.
                    content.height(content.prop("scrollHeight"));
                    
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
            this.info.html("");
        }
        else
        {
            // Update all widgets
            let pv_name;
            for (pv_name in this.pv_infos)
            {
                let info = this.pv_infos[pv_name];
                info.data.severity = Severity.UNDEFINED;
                let cb;
                for (cb of info.callbacks)
                    cb(info.data);
            }
            
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

        let pv_name = widget.attr("data-pv");
        if (pv_name)
        {
            // console.log("Subscribe for " + type + " widget with PV " + pv_name);
            this._subscribe(pv_name, data => this._handle_widget_pv_update(widget, type, data));
        }
    }

    /** Step 4: Subscribe to PV updates
     * 
     *  @param pv_name PV name
     *  callback callback Will be invoked with PV data
     */
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
    
    /** Step 5: Message from web socket
     * 
     *  Checks for 'update' messages and invokes the registered callbacks for the PV
     *  @param message Web socket message
     */
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

    /** Step 6: Callback for a PV update
     * 
     *  Updates the widget with data from PV
     *  @param widget Widget to update
     *  @param type Widget type
     *  @param data PV data
     */
    _handle_widget_pv_update(widget, type, data)
    {
        widget.removeClass("BorderMinor BorderMajor BorderInvalid BorderDisconnected");
        // By default, be alarm sensitive
        if (widget.attr("data-alarm-border") != "false")
        {
            if (data.severity == Severity.MINOR)
                widget.addClass("BorderMinor");
            else if (data.severity == Severity.MAJOR)
                widget.addClass("BorderMajor");
            else if (data.severity == Severity.INVALID)
                widget.addClass("BorderInvalid");
        }
        if (data.severity == Severity.UNDEFINED)
           widget.addClass("BorderDisconnected");
        
        // Widget's own update method handles the rest
        let method = this.widget_update_methods[type];
        if (method)
            method(widget, data)
    }
}

// Widgets can register init(widget) methods
// widget: jQuery object for the <div> or <svg> or ...
DisplayBuilderWebRuntime.prototype.widget_init_methods = {};

// Widgets with "data-pv" can register handle_update(widget, data) methods
// widget: jQuery object for the <div> or <svg> or ...
// data: Latest PV data
DisplayBuilderWebRuntime.prototype.widget_update_methods = {};


