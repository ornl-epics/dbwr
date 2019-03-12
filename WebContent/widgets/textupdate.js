
function format_pv_data_as_text(widget, data)
{
    let text;
    if (data.text)
        text = data.text;
    else
    {
        if (data.precision === undefined)
            text = data.value.toString();
        else
            text = data.value.toFixed(data.precision);
        
        if (data.units !== undefined  &&  widget.attr("data-show-units") != "false")
           text = text + " " + data.units;
    }
    
    return text;
}

DisplayBuilderWebRuntime.prototype.widget_update_methods["textupdate"] = function(widget, data)
{
    widget.html(format_pv_data_as_text(widget, data));
};