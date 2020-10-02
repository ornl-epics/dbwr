
function _handleNavtabSelection(navtab, button)
{
    console.log("Click: ")
    console.log(button.data("linked-file"));
    console.log(button.data("linked-macros"));
    
    // De-select all other buttons
    button.parent()
          .find(".NavTabsButton")
          .removeClass("selected");

    // Shrink non-selected buttons to keep from touching body
    button.parent()
          .find(".NavTabsButton")
          .width( button.data("width")-8 );

    // Select this button
    button.addClass("selected");
    button.width( button.data("width") );

}

function _initNavtabButton(navtab, button)
{
    button.click( () => _handleNavtabSelection(navtab, button) );
}

DisplayBuilderWebRuntime.prototype.widget_init_methods['navtabs'] = function(widget)
{
    widget.find(".NavTabsButton")
          .each( (index, button) => _initNavtabButton(widget, jQuery(button)) );
}