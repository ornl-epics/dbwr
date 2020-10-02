
let __navbody = undefined;
let __navdata = undefined;

function _handleNavtabSelection(navtab, button)
{
    // console.log("Click: ")
    // console.log(button.data("file"));
    // console.log(button.data("macros"));
    
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
    
    let body = button.siblings(".NavTabsBody");
    
    // TODO Stop widgets that are currently in body

    
    // Indicate what's loading to help debug issues if it never loads
    body.text("Loading " + button.text() + "...");
    
    // Load content
    jQuery.get("screen",
            { display: button.data("file"), macros: JSON.stringify(button.data("macros")) },
            data =>
            {
                __navbody = body;
                __navdata = data;

                // Add the <div class="Screen"> ... </div>  <script>  Rules... </script>
                body.html(data);
                // Remove the 'Screen' since it's already nested within the top-level screen
                body.find(".Screen").removeClass("Screen");
                
                // TODO Start widgets
            })
            .fail( (xhr, status, error) =>
            {
                console.log("NavTabs Error:");
                console.log(xhr);
                body.html(xhr.responseText);
            });
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