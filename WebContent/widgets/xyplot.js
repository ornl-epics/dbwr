DisplayBuilderWebRuntime.prototype.widget_update_methods["xyplot"] = function(widget, data)
{
    let id = widget.attr("id");
    // console.log("XYPlot update for " + id);
    // console.log("XYPlot update for " + data.pv);
    // console.log("XYPlot update for " + data.value);
    
    let i, trace = [];
    for (i=0; i<data.value.length; ++i)
        trace.push( [ i, data.value[i] ]);
    
    jQuery.plot("#" + id, [ trace ]);
   
}