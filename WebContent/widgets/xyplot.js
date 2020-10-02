// Information for one trace:
// PV names, most recent value for each PV, plot data
class XYTrace
{
    /** @param pvx Name of 'X' PV
     *  @param pvy Name of 'Y' PV
     *  @param label Trace label
     *  @param color Color of the trace
     *  @param linewidth Line width
     *  @param pointsize Use points instead of lines?
     *  @param bars Use bars instead of lines, points?
     */
    constructor(pvx, pvy, label, color, linewidth, pointsize, bars)
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
            label: label,
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
        if (value == "NaN")
            value = [];
        // console.log("XYPlot received data for " + pv + ": " + value.length + " samples");
        if (pv == this.pvy)
            this.y = value;
        else if (pv == this.pvx)
            this.x = value;
        else
            return;

        // Normalize data
        if (this.x === undefined)
            this.x = [];
        if (this.y === undefined)
            this.y = [];
        
        // Recompute plot data
        this.plotobj.data = [];
        if (this.x.length > 0  &&  this.y.length > 0)
        {
            let i, N = Math.min(this.x.length, this.y.length);
            // console.log("Plotting x[], y[]: " + N);
            for (i=0; i<N; ++i)
                this.plotobj.data.push( [ this.x[i], this.y[i] ] );        
        }
        else if (this.y.length > 0)
        {
            let i, N = this.y.length;
            // console.log("Plotting i, y[]: " + N);
            for (i=0; i<N; ++i)
                this.plotobj.data.push( [ i, this.y[i] ] );        
        }
        else
        {
            let i, N = this.x.length;
            // console.log("Plotting x[], i: " + N);
            for (i=0; i<N; ++i)
                this.plotobj.data.push( [ this.x[i], i ] );        
        }
    }
}    

DisplayBuilderWebRuntime.prototype.widget_init_methods["xyplot"] = widget =>
{
    let i=0, xpv = widget.data("pvx" + i), ypv = widget.data("pvy" + i);
    let traces = [];
    let options =
    {
        zoom:
        {
            interactive: true
        },
        pan:
        {
            interactive: true
        },
        legend:
        {
            show: false,
            position: "nw"
        }    
    };
    while (xpv  ||  ypv)
    {
        // console.log("Should connect to X/Y " + i + ": " + xpv + ", " + ypv);
        if (ypv)
            dbwr.subscribe(widget, "xyplot", ypv);
        if (xpv)
            dbwr.subscribe(widget, "xyplot", xpv);
        let name = widget.data("name" + i);
        if (name !== undefined)
            options.legend.show = true; 
        let color = widget.data("color" + i);
        let linewidth = widget.data("linewidth" + i);
        let pointsize = widget.data("pointsize" + i);
        let bars = widget.data("bars" + i);
        traces.push(new XYTrace(xpv, ypv, name, color, linewidth, pointsize, bars));
        
        ++i;
        xpv = widget.data("pvx" + i);
        ypv = widget.data("pvy" + i);
    }
    widget.data("traces", traces);
    widget.data("options", options);
};

DisplayBuilderWebRuntime.prototype.widget_update_methods["xyplot"] = function(widget, data)
{
    let id = widget.attr("id");
    // console.log("XYPlot " + id + " update: " + data.pv);
    // console.log("XYPlot update for " + data.value);
    
    
    let trace, traces = widget.data("traces"), options = widget.data("options"), plots = [];
    for (trace of traces)
    {
        trace.update(data.pv, data.value);
        plots.push( trace.plotobj );
    }
    
    jQuery.plot("#" + id, plots, options);
}
