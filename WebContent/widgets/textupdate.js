

/** Format number in engineering notation
 *  @param number Number
 *  @param precision Precision or undefined
 *  @returns Number with exponent that's a multiple of 3
 */
function format_engineering(number, precision)
{
    if (number == 0)
    {
        if (precision === undefined)
            return number.toString() + "E0";
        else
            return number.toFixed(precision) + "E0";
        return number.toFi
        
    }
    let neg = number < 0;
    number = Math.abs(number);
    let e = Math.round(Math.log10(number));
    
    e = Math.floor(e/3) * 3;
    
    let m = (e == 0) ? number : number * Math.pow(10, -e);
    
    let text;
    if (precision === undefined)
        text = m.toString();
    else
        text = m.toFixed(precision);
    
    text = text + "E" + e;
    if (neg)
        return "-" + text;
    
    return text;
}


/** Format data as text (e.g. number with precision, units)
 *  @param widget Widget that has 'format' etc.
 *  @param data PV data
 *  @returns Formatted text
 */
function format_pv_data_as_text(widget, data)
{
    // If precision is defined on widget, use it
    let precision = widget.data("precision");
    // Otherwise use precision from data
    if (precision === undefined  &&  data.precision !== undefined)
        precision = data.precision;
    
    let text;
    if (data.text !== undefined)
    {
        if (data.value !== undefined  &&  widget.data("format") == "decimal")
            text = data.value.toString();
        else
            text = data.text;
    }
    else if (data.value !== undefined)
    {
        if (data.value == "NaN")
            text = data.value
        else
        {
            if (widget.data("format") == "exponential")
            {
                if (precision === undefined)
                    text = data.value.toExponential().replace('e', 'E');
                else
                    text = data.value.toExponential(precision).replace('e', 'E');
            }
            else if (widget.data("format") == "engineering")
                text = format_engineering(data.value, precision);
            else if (widget.data("format") == "hex")
            {
                text = (data.value | 0).toString(16);
                text = "0x" + text;
            }
            else if (widget.data("format") == "binary")
            {
                text = (data.value | 0).toString(2);
                text = "0b" + text;
            }
            else
            {
                if (data.precision === undefined)
                    text = data.value.toString();
                else
                    if (typeof data.value.toFixed == 'function')
                        text = data.value.toFixed(precision);
                    else
                        text = data.value.toString();
            }
        }
        // show-units is by default undefined, or false to suppress units
        if (data.units !== undefined  &&  widget.data("show-units") === undefined)
           text = text + " " + data.units;
    }
    else
        return "";
    
    return text;
}

function update_text_widget(widget, data)
{
    // Use newlines to start new line via <br>
    let lines = format_pv_data_as_text(widget, data).split("\n");
    widget.html("<span>" + lines.join("<br>") + "</span>");
}

DisplayBuilderWebRuntime.prototype.widget_update_methods["textupdate"] = update_text_widget
