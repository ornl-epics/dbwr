
// Flot plot options
let _db_plot_options =
{
    xaxis:
    {
        mode: "time",
        timeBase: "milliseconds",
        timezone: "browser"
    },
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
        show: true,
    }    
};





// Information for one trace:
// PV name, plot data
//
// The last sample, plotobj.data[N], is used for scrolling.
// It carries the same value as plotobj.data[N-1] with a time stamp of 'now'.
// Calls to scroll() update its time stamp.
// When a new value is received from the PV, that scroll sample is removed,
// the actual data is added, and another scroll sample is added back in.
// At the very start, 'null' is added as a scroll sample:
// Flot handles null by creating a gap in the line.
// In update(), there is now a scroll sample that can be removed/replaced,
// no need to handle 'no samples' differently from 'have some samples'.
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
            label: pv,
            color: color,   
            clickable: true,
            hoverable: true,
            lines: { lineWidth: linewidth,steps: true },
            data: [ null ]
        };
    }

    /** @param now Time Stamp */
    scroll(now)
    {
        let last = this.plotobj.data.pop();
        if (last == null)
            this.plotobj.data.push(null);
        else
            this.plotobj.data.push( [ now, last[1] ]);
    }
    
    /** @param pv Name of PV, might be this one or unknown PV
     *  @param time Time Stamp
     *  @param value Value of that PV
     */
    update(pv, time, value)
    {
        if (pv != this.pv)
            return;
        
        // Add to plot data
        this.plotobj.data.pop();
        this.plotobj.data.push( [ time, value ] );        
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

    widget.bind("plotpan", function (event, plot) {
        var axes = plot.getAxes();
        $(".message").html("Panning to x: "  + axes.xaxis.min.toFixed(2)
        + " &ndash; " + axes.xaxis.max.toFixed(2)
        + " and y: " + axes.yaxis.min.toFixed(2)
        + " &ndash; " + axes.yaxis.max.toFixed(2));
    });

    widget.bind("plotzoom", function (event, plot) {
            var axes = plot.getAxes();
            $(".message").html("Zooming to x: "  + axes.xaxis.min.toFixed(2)
            + " &ndash; " + axes.xaxis.max.toFixed(2)
            + " and y: " + axes.yaxis.min.toFixed(2)
            + " &ndash; " + axes.yaxis.max.toFixed(2));
    });
    
    __scroll(widget, traces);
};

DisplayBuilderWebRuntime.prototype.widget_update_methods["databrowser"] = function(widget, data)
{
    // console.log("Data Browser " + widget.attr("id") + " update: " + data.pv + " = "  + data.value);

    let time = new Date().getTime();
    let trace, traces = widget.data("traces");
    for (trace of traces)
        trace.update(data.pv, time, data.value);
    __replot(widget, traces);
}

function __scroll(widget, traces)
{
    let time = new Date().getTime();
    let trace;
    for (trace of traces)
        trace.scroll(time);
    __replot(widget, traces);
    // Scroll every 3 seconds
    setTimeout(() => __scroll(widget, traces), 3000);
}

function __replot(widget, traces)
{
    let trace, plots = [];
    for (trace of traces)
        plots.push( trace.plotobj );
    jQuery.plot(widget, plots, _db_plot_options);    
}
