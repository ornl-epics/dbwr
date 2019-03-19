class DBTrace
{
    /** @param pv Name of PV
     *  @param color Color of the trace
     *  @param linewidth Line width
     */
    constructor(pv, color, linewidth)
    {
        this.pv = pv;
        
        // Flot plot object with
        // data: [ [ x0, y0 ], [ x1, y1 ], ..., [ xn, yn ] ]
        this.plotobj = 
        {
            color: color,   
            clickable: true,
            hoverable: true,
            lines: { lineWidth: linewidth,steps: true },
            data: []
        };
    }
    
    /** @param pv Name of PV, might be X, Y or unknown PV
     *  @param time Time Stamp
     *  @param value Value of that PV
     */
    update(pv, time, value)
    {
        if (pv != this.pv)
            return;
        
        // Add to plot data
        
        this.plotobj.data.push( [ time, value ] );        
    }
}

DisplayBuilderWebRuntime.prototype.widget_init_methods["databrowser"] = widget =>
{
    let i=0, pv = widget.data("pv" + i);
    let traces = [];
    while (pv)
    {
        // console.log("Should connect to PV " + i + ": " + pv);
        dbwr.subscribe(widget, "databrowser", pv);
        
        let color = widget.data("color" + i);
        let linewidth = widget.data("linewidth" + i);
        traces.push(new DBTrace(pv, color, linewidth));
        
        ++i;
        pv = widget.data("pv" + i);
    }
    widget.data("traces", traces);
};

DisplayBuilderWebRuntime.prototype.widget_update_methods["databrowser"] = function(widget, data)
{
    let time = new Date().getTime();
    let id = widget.attr("id");
    // console.log("Data Browser " + id + " update: " + data.pv + " = "  + data.value);
    
    let trace, traces = widget.data("traces"), plots = [];
    for (trace of traces)
    {
        trace.update(data.pv, time, data.value);
        plots.push( trace.plotobj );
    }
    
    let options =
    {
        xaxis:
        {
            mode: "time",
            timeBase: "milliseconds",
            timezone: "browser"
        }
    }
    
    jQuery.plot("#" + id, plots, options);

}
