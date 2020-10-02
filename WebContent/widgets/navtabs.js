
function _initNavtabButton(navtab, button)
{
    console.log("Nav button:");
    console.log(button.data("linked-file"));
    console.log(button.data("linked-macros"));
}

DisplayBuilderWebRuntime.prototype.widget_init_methods['navtabs'] = function(widget)
{
    widget.find(".NavTabsButton")
          .each( (index, button) => _initNavtabButton(widget, jQuery(button)) );
}