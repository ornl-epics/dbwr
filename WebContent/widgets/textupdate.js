
function format_pv_data_as_text(widget, data)
{
    let text;
    if (data.text !== undefined)
    {
        if (data.value !== undefined  &&  widget.attr("data-format") == "decimal")
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
            if (widget.attr("data-format") == "exponential")
            {
                if (data.precision === undefined)
                    text = data.value.toExponential();
                else
                    text = data.value.toExponential(data.precision);
            }
            else if (widget.attr("data-format") == "hex")
            {
                text = (data.value | 0).toString(16);
                text = "0x" + text;
            }
            else if (widget.attr("data-format") == "binary")
            {
                text = (data.value | 0).toString(2);
                text = "0b" + text;
            }
            else
            {
                if (data.precision === undefined)
                    text = data.value.toString();
                else
                    text = data.value.toFixed(data.precision);
            }
        }
        if (data.units !== undefined  &&  widget.attr("data-show-units") != "false")
           text = text + " " + data.units;
    }
    else
        return "";
    
    return text;
}

DisplayBuilderWebRuntime.prototype.widget_update_methods["textupdate"] = function(widget, data)
{
    widget.html("<span>" + format_pv_data_as_text(widget, data) + "</span>");
};