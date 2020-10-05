
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
    
    // Stop widgets that are currently in body
    body.find(".Widget").each( (index, w) =>
    {
        let wdg = jQuery(w);
        dbwr.unsubscribe(wdg);
        // TODO Rules??
    });
    
    // Indicate what's loading to help debug issues if it never loads
    body.text("Loading " + button.text() + "...");
    
    // Load content TODO Allow caching
    jQuery.get("screen",
            { display: button.data("file"), macros: JSON.stringify(button.data("macros")), cache: "false" },
            data =>
            {
                console.log("Loaded nav tab " + button.text());

                // Add the <div class="Screen"> ... </div>  <script>  Rules... </script>
                body.html(data);
                // Remove the 'Screen' since it's already nested within the top-level screen
                body.find(".Screen").removeClass("Screen");
                
                // Start widgets
                body.find(".Widget").each( (index, w) =>
                {
                    let wdg = jQuery(w);
                    // console.log("NavTabs starts " + wdg.attr("id") + " - " + wdg.data("type"));
                    dbwr._initWidget(wdg);
                });
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