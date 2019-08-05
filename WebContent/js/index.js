
function format_date(date)
{
    return date.toLocaleString("en-US", { hour12: false });
}

function query_cache()
{
    let info = jQuery("#info");
    info.html("Fetching cache info...");
    
    jQuery.ajax(
    {
        url: "cache",
        method: "GET",
        dataType: "json",
        success: data =>
        {
            if (! data  ||  !data.displays  || data.displays.length <= 0)
            {
                info.html("Cache is empty");
                return;
            }
            // Build table: Header
            info.html("");
            let table = jQuery("<table>").css("table-layout", "fixed")
                                         .css("word-wrap", "break-word");
            table.append($("<tr>").append($("<th>").css("width", "4ex").text("Run"))
                                  .append($("<th>").css("width", "40%").text("Display"))
                                  .append($("<th>").css("width", "15%").text("Macros"))
                                  .append($("<th>").css("width", "10%").text("Created"))
                                  .append($("<th>").css("width", "10%").text("Last Access"))
                                  .append($("<th>").css("width", "10%").text("Size"))
                                  .append($("<th>").css("width",  "5%").text("Calls"))
                                  .append($("<th>").css("width", "10%").text("Time")) );
            // .. Rows
            let view = window.location.origin + window.location.pathname + "view.jsp?display=";
            let size = 0, calls = 0, ms = 0;
            for (let display of data.displays)
            {
                size += display.size;
                calls += display.calls;
                ms += display.ms;
                let view_url = view + encodeURIComponent(display.display) + "&macros=" + encodeURIComponent(JSON.stringify(display.macros));
                let run_link = "<a href=\"" + view_url  + "\"><img src=\"runtime.png\"></a>";
                let link = "<a href=\"" + display.display + "\">" + display.display + "</a>";
                table.append($("<tr>").append($("<td>").html(run_link))
                                      .append($("<td>").html(link))
                                      .append($("<td>").text(JSON.stringify(display.macros)))
                                      .append($("<td>").text(format_date(new Date(display.created))))
                                      .append($("<td>").text(format_date(new Date(display.stamp))))
                                      .append($("<td>").css("text-align", "right").text( (display.size / 1024.0).toFixed(1) + " kB" ))
                                      .append($("<td>").css("text-align", "right").text(display.calls))
                                      .append($("<td>").css("text-align", "right").text( (display.ms / 1000.0).toFixed(3) + " s")) );
            }
            table.append($("<tr>").append($("<td>"))
                                  .append($("<td>"))
                                              .append($("<td>"))
                                  .append($("<td>"))
                                                  .append($("<td>").html("<b>Total:</b>"))
                                                  .append($("<td>").css("text-align", "right").text( (size / 1024.0).toFixed(1) + " kB" ))
                                                  .append($("<td>").css("text-align", "right").text(calls))
                                                  .append($("<td>").css("text-align", "right").text( (ms / 1000.0).toFixed(3) + " s")) );
            makeTableSortable(table, true);
            info.append(table);
            // Scroll to 'bottom' to show table
            window.scrollTo(0, 100000);
        },
        error: (xhr, status, error) => info.html("No Info: " + status),        
    });
}

function clear_cache()
{
    let info = jQuery("#info");
    info.html("Clearing cache ..");
    
    jQuery.ajax(
    {
        url: "cache",
        method: "DELETE",
        success: data =>
        {
            // Clear
            info.html("Cache cleared");
            query_cache();
        },
        error: (xhr, status, error) => info.html("Cache Clear Error: " + status),        
    });
}


jQuery(() =>
{
    // Add actual http://.. location to the example URLs
    let root = window.location.origin + window.location.pathname;
    jQuery(".example_url").each( (index, example) =>
    {
        let ex = jQuery(example);
        let url = ex.html();
        ex.html(root + url);
    });
    
    // Populate input with the first option
    jQuery("#open_form input[name=display]").val( jQuery("#open_form select option")[0].value );

    // Update input when another option is selected
    let options = jQuery("#open_form select");
    options.change(() => jQuery("#open_form input[name=display]").val(options.val()));
    
    jQuery("#open_form input[name=macros]").val("$(S)=Value One&$(N)=1");
    jQuery("#open_form input[type=button]").click(() =>
    {
        let display = jQuery("#open_form input[name=display]").val();
        let macros = jQuery("#open_form input[name=macros]").val();
        
        let new_link = root + "view.jsp?display=" + display;
        
        if (macros)
        {   // Parse $(NAME)=VALUE&$(NAME)=VALUE into NAME, VALUE
                let map = {}, i = 0, items = macros.split("&");
                while (i < items.length)
                {
                    let name_value = items[i].split('=');
                    let name = name_value[0];
                    // Remove $( .. ) from NAME
                    name = name.replace("$(", "");
                    if (name.endsWith(")"))
                        name = name.substr(0, name.length-1)
                    map[name] = name_value[1];
                    i += 1;
                }
                macros = JSON.stringify(map)
                new_link = new_link + "&macros=" + encodeURI(macros);
        }
        
        // if (confirm(new_link))
        window.location.href = new_link;
    });
});
