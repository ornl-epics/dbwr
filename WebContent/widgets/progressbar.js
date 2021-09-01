
// Example for a comparably simple dynamic widget.
// This file is registered in ProgressBarWidget.java.
// 
// Add a method that will be called whenever a "progressbar" widget
// receives a new value
DisplayBuilderWebRuntime.prototype.widget_update_methods["progressbar"] = function(widget, data)
{
    // widget: The HTML element created in ProgressBarWidget.java
    // data: Most recent PV data
    let range = get_min_max(widget, data);
    widget.attr("min", range[0]);
    widget.attr("max", range[1]);
    widget.val(data.value);
}
