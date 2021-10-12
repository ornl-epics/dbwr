function update_led_border(widget, severity)
{
    let square = widget.find("ellipse").get(0) === undefined;
    
    if (square)
        update_alarm_div(widget, severity, 4, false);
    else
        update_alarm_div(widget, severity, 3, true);
}

DisplayBuilderWebRuntime.prototype.widget_alarm_methods["led"] = update_led_border;
DisplayBuilderWebRuntime.prototype.widget_alarm_methods["multi_state_led"] = update_led_border;


// Threshold for considering a color 'bright', suggesting black for text
let __BRIGHT_THRESHOLD = 410*255;

// Turn a '#rrggbb' type color into a brightness value
function __getColorBrightness(color)
{
    var r = parseInt(color.substr(1,2),16);
    var g = parseInt(color.substr(3,2),16);
    var b = parseInt(color.substr(5,2),16);
    return r * 299 + g * 587 + b * 114;
}

function __getContrastingColor(color)
{
    let brightness = __getColorBrightness(color);
    let text_color;
    
    if (brightness > __BRIGHT_THRESHOLD)
        text_color = "#000000";
    else
        text_color = "#FFFFFF";
    return text_color;
}

DisplayBuilderWebRuntime.prototype.widget_update_methods["led"] = function(widget, data)
{
    var value = is_bit_set(widget, data);
    widget.data("value", value);
    
    var label;
    var color;
    if (value)
    {
        color = widget.data("on-color");
        label = widget.data("on-label");
    }
    else
    {
        color = widget.data("off-color");
        label = widget.data("off-label");
    }
    if (label == undefined)
        label = "";
    
    set_svg_background_color(widget, color);
    let text = widget.find("text");
    text.html(label);    
    text.css("fill", __getContrastingColor(color));
}

//Called by color rules that update the on/off colors
function set_led_off_color(widget, color)
{
    // Update the color
    widget.data("off-color", color);
    // Re-draw right away because that's the color for the current state?
    if (! widget.data("value"))
        set_svg_background_color(widget, color);
}

function set_led_on_color(widget, color)
{
    widget.data("on-color", color);
    if (widget.data("value"))
        set_svg_background_color(widget, color);
}


DisplayBuilderWebRuntime.prototype.widget_update_methods["multi_state_led"] = function(widget, data)
{
    let color = widget.data("fallback-color");
    let label = widget.data("fallback_label");
    let index = 0;
    let value = widget.data("state-value-" + index);
    while (value !== undefined)
    {
        if (data.value == value)
        {
            color = widget.data("state-color-" + index);
            label = widget.data("state-label-" + index);
            break;
        }
        ++index;
        value = widget.data("state-value-" + index);
    }
    if (label == undefined)
       label = "Err";

    widget.find("ellipse,rect").attr("fill", color);

    let text = widget.find("text");
    text.html(label);    
    text.css("fill", __getContrastingColor(color));
}

