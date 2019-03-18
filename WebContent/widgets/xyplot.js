// Information for one trace:
// PV names, most recent value for each PV, plot data
class XYTrace
{
    /** @param pvx Name of 'X' PV
     *  @param pvy Name of 'Y' PV
     */
    constructor(pvx, pvy)
    {
        this.pvx = pvx;
        this.pvy = pvy;
        
        // Most recent value for X, Y
        this.x = [];
        this.y = [];
        
        // Plot data: [ [ x0, y0 ], [ x1, y1 ], ..., [ xn, yn ] ]
        this.plot = [];
    }
    
    /** @param pv Name of PV, might be X, Y or unknown PV
     *  @param value Value of that PV
     */
    update(pv, value)
    {
        if (pv == this.pvy)
            this.y = value;
        else if (pv == this.pvx)
            this.x = value;
        else
            return;
        
        // Recompute plot data
        let i, N = Math.min(this.x.length, this.y.length);
        this.plot = [];
        for (i=0; i<N; ++i)
            this.plot.push( [ this.x[i], this.y[i] ] );        
    }
}    

DisplayBuilderWebRuntime.prototype.widget_init_methods["xyplot"] = widget =>
{
    let i=0, xpv = widget.data("pvx" + i), ypv = widget.data("pvy" + i);
    let traces = [];
    while (xpv  ||  ypv)
    {
        // console.log("Should connect to X/Y " + i + ": " + xpv + ", " + ypv);
        if (ypv)
            dbwr.subscribe(widget, "xyplot", ypv);
        if (xpv)
            dbwr.subscribe(widget, "xyplot", xpv);
        traces.push(new XYTrace(xpv, ypv));
        
        ++i;
        xpv = widget.data("pvx" + i);
        ypv = widget.data("pvy" + i);
    }
    widget.data("traces", traces);
};

DisplayBuilderWebRuntime.prototype.widget_update_methods["xyplot"] = function(widget, data)
{
    let id = widget.attr("id");
    // console.log("XYPlot " + id + " update: " + data.pv);
    // console.log("XYPlot update for " + data.value);
    
    let trace, traces = widget.data("traces"), plots = [];
    for (trace of traces)
    {
        trace.update(data.pv, data.value);
        plots.push( trace.plot );
    }
    
    jQuery.plot("#" + id, plots);
}
