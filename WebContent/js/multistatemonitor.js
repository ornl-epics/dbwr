
DisplayBuilderWebRuntime.prototype.widget_update_methods["multistatemonitor"] = function(widget, data) {
        //TODO Use color from opi
        if (data.value !== 0) {
                widget.find('image').attr('filter', 'url(#blackToBlue)');
        }
 }