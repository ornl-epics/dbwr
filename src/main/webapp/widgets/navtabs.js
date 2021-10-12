
function _handleNavtabSelection(navtab, button)
{
    // console.log("Click: ")
    // console.log(button.data("file"));
    // console.log(button.data("macros"));
    
    if (button.hasClass("horizontal"))
    {
        // De-select all other buttons
        // Shrink non-selected buttons to keep from touching body
        button.parent()
              .find(".NavTabsButton")
              .removeClass("selected")
              .height( button.data("height")-5 );

        // Select this button
        button.addClass("selected");
        button.height( button.data("height") );
    }
    else
    {
        button.parent()
              .find(".NavTabsButton")
              .removeClass("selected")
              .width( button.data("width")-8 );
    
        button.addClass("selected");
        button.width( button.data("width") );
    }

    // Select this button
    button.addClass("selected");
    button.width( button.data("width") );
    
    let body = button.siblings(".NavTabsBody");
    
    // Stop widgets that are currently in body by
    // clearing all its PVs, which stops
    // updates to widget and associated rules.
    body.find(".Widget").each( (index, w) =>
    {
        let wdg = jQuery(w);
        dbwr.unsubscribe(wdg);
    });
    
    // Indicate what's loading to help debug issues if it never loads
    body.text("Loading " + button.text() + "...");
    
    // Load content
    let cache = window.location.search.indexOf("cache=false") > 0 ? "false" : "true";
    console.log("Load navtab with cache = " + cache);
    jQuery.get("screen",
            { display: button.data("file"), macros: JSON.stringify(button.data("macros")), cache: cache },
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
    // Instrument the navigation buttons
    widget.find(".NavTabsButton")
          .each( (index, button) => _initNavtabButton(widget, jQuery(button)) );
    
    // Trigger the 'selected' button
    widget.find(".NavTabsButton.selected").click();
}