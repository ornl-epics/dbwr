DisplayBuilderWebRuntime.prototype.widget_update_methods["tank"] = function (widget, data)
{
    let tankbar = widget.find('.TankBar');
    let top = tankbar.data('top');
    let full = tankbar.data('height');

    let min, max;
    [ min, max] = get_min_max(widget, data);
    // console.log("value: " + data.value + ", range: " + min + " .. " + max);

    // y: top edge of fill
    // height: amount of fill
    if (min < max)
    {
        // Fill from bottom
        let barsize = (data.value - min) * full / (max - min);
        tankbar.attr('height', barsize)
               .attr('y', top + full - barsize);
    }
    else if (max < min)
    {
        // Fill from top
        let barsize = (data.value - max) * full / (min - max);
        tankbar.attr('height', barsize)
               .attr('y', top);
    }
 } 