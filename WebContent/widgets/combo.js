DisplayBuilderWebRuntime.prototype.widget_update_methods["combo"] = function(widget, data)
{
    let text = widget.attr("data-item-" + data.value);
    if (text === undefined)
        text = data.text;
    if (text === undefined)
        text = data.value;
    widget.html("<span>" + text + "</span>");
};