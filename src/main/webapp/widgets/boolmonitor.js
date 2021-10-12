
// Davy Dequidt
DisplayBuilderWebRuntime.prototype.widget_update_methods["boolmonitor"] = function(widget, data) {
        // functionn called for eahc pv subscribed to the widget including pv for scripts or rules.

        if(widget.data('pv') == data.pv) {
                // Apply only for the widget pv

                //TODO Use color from opi
                if (data.value) {
                        widget.find('image').attr('filter', 'url(#blackToBlue)');
                }
                if(data.value) {
                        //console.log(widget.attr('data-img-on'));
                        widget.find('image').attr('xlink:href', widget.attr('data-img-on'));
                } else {
                        widget.find('image').attr('xlink:href', widget.attr('data-img-off'));
                }
        }

  }