// Uses 'flot' library (jQuery.plot) to
// display X/Y waveform data.

// Information for one trace:
// PV names, most recent value for each PV, plotobj as needed by plot
class XYTrace
{
    /** @param pvx Name of 'X' PV
     *  @param pvy Name of 'Y' PV
     *  @param yaxis Y axis index 1, 2, ...
     *  @param label Trace label
     *  @param color Color of the trace
     *  @param linewidth Line width
     *  @param pointsize Use points instead of lines?
     *  @param bars Use bars instead of lines, points?
     */
    constructor(pvx, pvy, yaxis, label, color, linewidth, pointsize, bars)
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
            data: [],
			yaxis: yaxis
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
        },
		xaxis:
		{
			mode: null,
			axisLabel: 'X Axis',
			show: true
		},
		yaxes:
		[
			{ show: true,  axisLabel: 'Y Axis',  mode: null },
			{ show: false, axisLabel: 'Y2 Axis', mode: null, position: "right" }
		]
    };

	let x_axis_title = widget.data("x_axis_title");
	// console.log("xaxis title: " + x_axis_title)
	if (x_axis_title !== undefined)
		options.xaxis.axisLabel = x_axis_title;

	let x_axis_visible = widget.data("x_axis_visible");
	// console.log("xaxis visible: " + x_axis_visible)
	options.xaxis.show = widget.data("x_axis_visible");

	let y_axis_0_title = widget.data("y_axis_0_title");
	// console.log("xaxis title: " + y_axis_0_title)
	if (y_axis_0_title !== undefined)
		options.yaxes[0].axisLabel = y_axis_0_title
	let y_axis_0_mode = widget.data("y_axis_0_mode");
	// console.log("xaxis title: " + y_axis_0_mode)
	if (y_axis_0_mode !== undefined)
		options.yaxes[0].mode = y_axis_0_mode

	let y_axis_1_title = widget.data("y_axis_1_title");
	// console.log("xaxis title: " + y_axis_1_title)
	if (y_axis_1_title !== undefined)
		options.yaxes[1].axisLabel = y_axis_1_title
	let y_axis_1_mode = widget.data("y_axis_1_mode");
	// console.log("xaxis title: " + y_axis_1_mode)
	if (y_axis_1_mode !== undefined)
		options.yaxes[1].mode = y_axis_1_mode

	let y_axis_1_on_right = widget.data("y_axis_1_on_right");
	// console.log("yaxis location: " + y_axis_1_on_right)
	if (y_axis_1_on_right !== undefined)
		options.yaxes[1].position = y_axis_1_on_right

	let y_axis_1_visible = widget.data("y_axis_1_visible");
	// console.log("xaxis visible: " + y_axis_1_visible)
	options.yaxes[1].show = widget.data("y_axis_1_visible");

    let i=0, xpv = widget.data("pvx" + i), ypv = widget.data("pvy" + i);
    let traces = [];
    console.log("XYPlot " + widget.attr('id') + " PVs");
    while (xpv  ||  ypv)
    {
        console.log("X/Y " + i + " = '" + xpv + "', '" + ypv + "'");
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
		// .bob files the axis index starts from 0 while flot axis index starts from 1
		let axis = widget.data("y_axis"+i) + 1
        traces.push(new XYTrace(xpv, ypv, axis, name, color, linewidth, pointsize, bars));

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

    let trace, plots = [];
    for (trace of widget.data("traces"))
    {
        trace.update(data.pv, data.value);
        plots.push( trace.plotobj );
    }

    jQuery.plot("#" + id, plots, widget.data("options"));
}
