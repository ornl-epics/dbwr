/** Constants for severity levels */
class Severity
{
}

Severity.NONE = "NONE";
Severity.MINOR = "MINOR";
Severity.MAJOR = "MAJOR";
Severity.INVALID = "INVALID";
Severity.UNDEFINED = "UNDEFINED";


/** Info for one PV */
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


/** Each page has one DisplayBuilderWebRuntime
 *  that handles the web socket,
 *  loads content,
 *  subscribes PVs,
 *  dispatches received PV updates to callbacks.
 */
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
    
    clearLog()
    {
        this.info.html("");
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
        this.log("Loading '" + display + "' with " + macros);
        
        jQuery.get("screen",
                { display: this.display, macros: macros },
                data =>
                {
                    // Place display's HTML into content
                    let content = jQuery("#content");
                    content.html(data);
                    
                    if (jQuery("#content>#error").length > 0)
                    {
                        this.log("Cannot load display");
                        return;
                    }
                    // Update height to space that's actually required,
                    // so we can add further HTML for info etc.
                    // below.
                    content.height(content.prop("scrollHeight"));
                    
                    let name = content.children().data("name");
                    if (name !== undefined)
                        document.title = name;
                    
                    this.log("Connecting PVs");
                    this.pvws.open();
                })
                .fail( (xhr, status, error) =>
                {
                    console.log("Error:");
                    console.log(xhr);
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
        let type = widget.data("type");

        // Call registered init method for widget type
        let method = this.widget_init_methods[type];
        if (method)
        {
            // console.log("Calling widget init method for " + type)
            method(widget);
        }

        // Handle 'data-pv'
        let pv_name = widget.data("pv");
        if (pv_name)
            this.subscribe(widget, type, pv_name);
        
        // Init rules of this widget
        let wid = widget.attr("id");
        let rules = this.widget_rules[wid];
        if (rules)
            for (let rule of rules)
            {
                // console.log("Init rule for " + wid);
                rule.init();
            }
    }
    
    /** Subscribe to a PV and register for value updates
     * 
     *  This is called automatically for widgets with a 'data-pv' attribute.
     *  When value udates are received from the PV,
     *  `widget_update_methods[type]` will be invoked,
     *  i.e. the widget should register such an update handler.
     *  
     *  @param widget jQuery widget object
     *  @param type Widget type, used to obtain the widget update method
     *  @param pv_name PV name to which to subscribe
     */
    subscribe(widget, type, pv_name)
    {
        // console.log("Subscribe for " + type + " widget to PV " + pv_name);
        // Until we get an update from the PV, consider widget disconnected
        widget.addClass("BorderDisconnected");
        this._subscribe(pv_name, data => this._handle_widget_pv_update(widget, type, data));
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
        else if (message.type == 'error')
        {
            this.log("Error: " + message.message);
        }
        else
        {
            this.log("Unknown message " + JSON.stringify(message));
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
        if (widget.data("alarm-border") != "false")
        {
            if (data.severity == Severity.MINOR)
                widget.addClass("BorderMinor");
            else if (data.severity == Severity.MAJOR)
                widget.addClass("BorderMajor");
            else if (data.severity == Severity.INVALID)
                widget.addClass("BorderInvalid");
        }
        // Always show disconnected state, even when not otherwise alarm sensitive
        if (data.severity == Severity.UNDEFINED)
           widget.addClass("BorderDisconnected");
        
        // Widget's own update method handles the rest
        let method = this.widget_update_methods[type];
        if (method)
            method(widget, data)
    }
    
    /** Write to PV
     *  @param pvs PV name
     *  @param value number or string
     */
    write(pv, value)
    {
        let info = this.pv_infos[pv];
        if (info === undefined)
        {
            this.log("Cannot write unknown PV " + pv);
            return;
        }
        this.clearLog();
        
        if (typeof(info.data.value) == "number")
        {
            value = parseFloat(value);
            console.log("Writing " + pv + " as number " + value);
        }
        
        this.pvws.write(pv, value);
    }
}

// Widget types can register init(widget) methods
// to initialize UI event handlers
// or to perform custom PV subscriptions:
//
// DisplayBuilderWebRuntime.prototype.widget_init_methods["xyz"] = function(widget)
// {
//     # Initialize an 'xyz' type widget
// }
//
// widget: jQuery object for the <div> or <svg> or ...
DisplayBuilderWebRuntime.prototype.widget_init_methods = {};


// Widget types can register handle_update(widget, data) methods.
//
// DisplayBuilderWebRuntime.prototype.widget_update_methods["xyz"] = function(widget, data)
// {
//     # Widget of type 'xyz' received new data for its PV
// }
//
// Widgets with "data-pv" are automatically subscribed to that PV
// and should register to handle PV value updates.
// Widgets might subscribe to additional PVs via the widget_init_methods.
//
// widget: jQuery object for the <div> or <svg> or ...
// data: Latest PV data
DisplayBuilderWebRuntime.prototype.widget_update_methods = {};

// Rules register here widget ID.
DisplayBuilderWebRuntime.prototype.widget_rules = {};



class WidgetRule
{
    /** @param wid Widget ID, "w123"
     *  @param property Widget property that the rule sets
     *  @oaram pvs Array of PVs
     */
    constructor(wid, property, pvs)
    {
        this.wid = wid;
        this.widget = jQuery("#" + wid);
        this.property = property;
        this.pvs = pvs;
        // value['NameOfPV'] is updated to latest numeric value
        this.value = {};
        
        // console.log("Register for " + wid + ":");
        // console.log(this);
        let rules = DisplayBuilderWebRuntime.prototype.widget_rules[wid];
        if (rules === undefined)
            DisplayBuilderWebRuntime.prototype.widget_rules[wid] = [ this ];
        else
            rules.push(this);
    }
    
    /** Subscribe to PVs */
    init()
    {
        // console.log("Starting rule for PVs " + this.pvs);
        for (let pv of this.pvs)
            dbwr._subscribe(pv, data => { this.value[pv] = data.value; this._trigger(pv); });
    }
    
    _trigger(pv)
    {
        let value = this.eval();
        console.log("Rule for " + this.wid + "." + this.property + " triggered by " + pv + ": " + value);
        this.update(this.widget, value);
    }
    
    eval()
    {
        // Override should use this.value[..] and rule expressions to determine value
        console.error("WidgetRule.eval() needs to be overridden");
        return undefined;
    }
    
    update(widget, value)
    {
        // Override should use value to update widget's property
        console.error("WidgetRule.update() needs to be overridden")
    }
}


// Methods used for WidgetRule.update
function set_x_pos(widget, value)
{
    widget.css("left", value + "px");        
}

function set_text_background_color(widget, color)
{
    widget.css("background-color", color);
}

function set_svg_background_color(widget, color)
{
    widget.find("ellipse,rect,path,polygon").attr("fill", color);
}

function set_visibility(widget, visible)
{
    widget.css("display", visible ? "block" : "none");        
}
// End of common WidgetRule.update methods
