DataBrowserWebRuntime.prototype.widget_subscribe_methods["textentry"] = function(widget, data)
{
    widget.html(format_pv_data_as_text(data));
};