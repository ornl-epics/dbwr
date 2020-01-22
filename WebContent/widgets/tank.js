DisplayBuilderWebRuntime.prototype.widget_update_methods["tank"] = function (widget, data)
{
    //console.log(widget, data);
    var tankbar = widget.find('.tankbar'),
        inity = parseInt(tankbar.attr('inity')),
        maxheight = parseInt(tankbar.attr('maxheight')),
        barsize = maxheight * data.value / data.max;
     widget.find('.tankbar')
           .attr({ 'height': barsize,
                   'y': inity + maxheight - barsize
                 });
 } 