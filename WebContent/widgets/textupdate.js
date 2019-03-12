
function format_pv_data_as_text(data)
{
    if (data.text)
        return data.text;
    else
    {
        if (data.precision === undefined)
            return data.value.toString();
        else
            return data.value.toFixed(data.precision);
    }
}

DataBrowserWebRuntime.prototype.widget_subscribe_methods["textupdate"] = function(widget, data)
{
    widget.html(format_pv_data_as_text(data));
};