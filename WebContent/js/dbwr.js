/** Constants for severity levels */
class Severity
{
}

Severity.NONE = "NONE";
Severity.MINOR = "MINOR";
Severity.MAJOR = "MAJOR";
Severity.INVALID = "INVALID";
Severity.UNDEFINED = "UNDEFINED";

/** Apply alarm-based outline to widget
 * 
 *  Default unless widget registers with widget_alarm_methods[]
 */
function apply_alarm_outline(widget, severity)
{
    widget.removeClass("BorderMinor BorderMajor BorderInvalid BorderDisconnected");
    // By default, be alarm sensitive
    if (widget.data("alarm-border") != "false")
    {
        if (severity == Severity.MINOR)
            widget.addClass("BorderMinor");
        else if (severity == Severity.MAJOR)
            widget.addClass("BorderMajor");
        else if (severity == Severity.INVALID)
            widget.addClass("BorderInvalid");
    }
    // Always show disconnected state, even when not otherwise alarm sensitive
    if (severity == Severity.UNDEFINED)
       widget.addClass("BorderDisconnected");
}
        

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
                             connected => this._handle_connection(connected),
                             message   => this._handle_message(message));
        this.pv_infos = {}
    }
    
    /** @param message Message to log in 'info' span and console */
    log(message)
    {
        this.info.html(message);
        console.log(message);
    }
    
    clear_log()
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
    load_content(display, macros)
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
                    jQuery("html").html(xhr.responseText);
                });
    }

    /** Lifecycle step 2: PV web socket connected
     * 
     *  When connected, will initialize widgets
     *  
     *  @param connected True/false as PVWS connects/disconnects
     */
    _handle_connection(connected)
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
        if (type === undefined)
            return;

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
        let method = DisplayBuilderWebRuntime.prototype.widget_alarm_methods[type]
        if (method)
            method(widget, Severity.UNDEFINED);
        else
            apply_alarm_outline(widget, Severity.UNDEFINED);
        
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
    _handle_message(message)
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
        // Indicate alarm
        let method = DisplayBuilderWebRuntime.prototype.widget_alarm_methods[type]
        if (method)
            method(widget, data.severity);
        else
            apply_alarm_outline(widget, data.severity);
        
        // Widget's own update method handles the rest
        method = this.widget_update_methods[type];
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
        this.clear_log();
        
        if (typeof(info.data.value) == "number")
        {
            value = parseFloat(value);
            console.log("Writing " + pv + " as number " + value);
        }
        
        this.pvws.write(pv, value);
    }
    
    /** List info about all PVs */
    show_pvs()
    {
        Object.values(dbwr.pv_infos)
              .forEach( info  =>
        {
            console.log(info.data);
        });
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

// By default, widgets will get an alarm-sensitive CSS outline.
// To implement different alarm handling, register a method
//
// DisplayBuilderWebRuntime.prototype.widget_alarm_methods["xyz"] = function(widget, severity)
// {
//     if (severity == Severity.MINOR) ...
// }

DisplayBuilderWebRuntime.prototype.widget_alarm_methods = {};


// Rules register here widget ID.
DisplayBuilderWebRuntime.prototype.widget_rules = {};


/** Helper for handling updates based on a "bit"
 * 
 *  Checks for bits 0, 1, .. or any non-zero value for bit = -1
 *  @param widget WIdget with 'data-bit'
 *  @param data PV data
 *  @returns Is addressed bit set?
 */
function is_bit_set(widget, data)
{
    let bit = widget.data("bit");
    if (bit < 0)
        return data.value > 0;
    
    let mask = 1 << bit;
    return data.value & mask; 
}

/** @param data PV Data
 *  @returns Data as string, using label for enum
 */
function get_data_string(data)
{
    if (data.labels === undefined)
        return '' + data.value;
    
    if (data.value >= 0  &&  data.value < data.labels.length)
        return data.labels[data.value];
    
    return 'Invalid enum ' + data.value;
}

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
        // value['NameOfPV'] is updated to latest numeric or 'native' value
        this.value = {};
        // valueStr['NameOfPV'] is updated to latest string value
        this.valueStr = {};
        
        // Register rule so it will be initialized
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
            dbwr._subscribe(pv, data =>
            {
                this.value[pv] = data.value;
                this.valueStr[pv] = get_data_string(data);
                this._trigger(pv);
            });
    }
    
    _trigger(pv)
    {
        let value = this.eval();
        console.log("Rule for " + this.widget.data("type") + " " +
                    this.wid + " '" + this.property +
                    "' triggered by " + pv + ": " + value);
        this.update(this.widget, value);
    }
    
    eval()
    {
        // Override should use this.value[..] or valueStr[..] and rule expressions to determine value
        console.error("WidgetRule.eval() needs to be overridden");
        return undefined;
    }
    
    update(widget, value)
    {
        // Override should use value to update widget's property
        console.error("WidgetRule.update() needs to be overridden for " + this.wid + "." + this.property);
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



// Context menu support

let __active_menu = undefined;

/** Given a widget with 'id',
 *  create a 'id_context' div
 *  with the provided entries.
 *  
 *  Items may be plain text
 *  or jQuery items that handle click() etc.
 *  
 *  @param widget
 *  @param ...items
 */
function create_contextmenu(widget, items)
{
    let menu_id = widget.attr("id") + "_context";
    
    // In case menu already exists, remove
    jQuery("#" + menu_id).remove();
    
    let entries = jQuery("<ul>");
    
    // jQuery("<li>").html(menu_id).appendTo(entries);
    
    items.forEach(item => jQuery("<li>").html(item).appendTo(entries));
    
    let menu = jQuery("<div>").addClass("ContextMenu")
                              .attr("id", menu_id)
                              .append(entries);
    // Hide when pointer moves out of menu
    menu.mouseleave(() => hide_contextmenu(widget));
    widget.after(menu);
}

function hide_contextmenu(widget)
{
    if (__active_menu !== undefined)
    {
        __active_menu = undefined;
        let menu = jQuery("#" + widget.attr("id") + "_context");
        menu.hide();
    }
}

// Hide menu when pressing Escape
jQuery(window).keydown(event =>
{
    if (event.keyCode == 27)
        hide_contextmenu(__active_menu);
});

/** Event handler for click() or contextmenu()
 *  @param event
 */
function toggle_contextmenu(event)
{
    let widget = jQuery(event.target);
    let menu = jQuery("#" + widget.attr("id") + "_context");

    if (menu.is(":visible"))
        hide_contextmenu(widget);
    else
    {
        // CSS position: fixed, not relative to any container.
        // console.log("Page: " + event.pageX + "," + event.pageY);
        // console.log("Client: " + event.clientX + "," + event.clientY);
        // Would expect that pageX/Y are required, but clientX/Y are
        // in fact positioning the menu at the mouse cursor.
        // Move 10/10 into the menu to avoid immediate mouseleave() event.
        menu.css("left", (event.clientX-10) + "px");
        menu.css("top",  (event.clientY-10) + "px");
        menu.show();
        __active_menu = widget;
    }
    
    event.preventDefault();
}



function help()
{
    console.log("Display Builder Web Runtime");
    console.log("dbwr.show_pvs()   - List PVs");
}

