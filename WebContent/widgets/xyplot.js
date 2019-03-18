// Information for one trace:
// PV names, most recent value for each PV, plot data
class XYTrace
{
    /** @param pvx Name of 'X' PV
     *  @param pvy Name of 'Y' PV
     *  @param color Color of the trace
     *  @param linewidth Line width
     *  @param pointsize Use points instead of lines?
     *  @param bars Use bars instead of lines, points?
     */
    constructor(pvx, pvy, color, linewidth, pointsize, bars)
    {
        this.pvx = pvx;
        this.pvy = pvy;
        
        // Most recent value for X, Y
        this.x = [];
        this.y = [];
        
        // Flot plot object with
        // data: [ [ x0, y0 ], [ x1, y1 ], ..., [ xn, yn ] ]
        this.plotobj = 
        {
            color: color,   
            clickable: true,
            hoverable: true,
            data: []
        };
        if (bars)
            this.plotobj.bars = { show: true };
        else if (pointsize)
            this.plotobj.points = { show: true, fill: true, radius: pointsize/2 };
        else
            this.plotobj.lines = { lineWidth: linewidth };
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
        this.plotobj.data = [];
        if (this.x.length > 0  &&  this.y.length > 0)
        {
            let i, N = Math.min(this.x.length, this.y.length);
            for (i=0; i<N; ++i)
                this.plotobj.data.push( [ this.x[i], this.y[i] ] );        
        }
        else if (this.y.length > 0)
        {
            let i, N = this.y.length;
            for (i=0; i<N; ++i)
                this.plotobj.data.push( [ i, this.y[i] ] );        
        }
        else
        {
            let i, N = this.x.length;
            for (i=0; i<N; ++i)
                this.plotobj.data.push( [ this.x[i], i ] );        
        }
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
        
        let color = widget.data("color" + i);
        let linewidth = widget.data("linewidth" + i);
        let pointsize = widget.data("pointsize" + i);
        let bars = widget.data("bars" + i);
        traces.push(new XYTrace(xpv, ypv, color, linewidth, pointsize, bars));
        
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
        plots.push( trace.plotobj );
    }
    
    jQuery.plot("#" + id, plots);
}
