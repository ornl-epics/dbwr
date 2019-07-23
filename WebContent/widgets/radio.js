
DisplayBuilderWebRuntime.prototype.widget_init_methods["radio"] = function(widget)
{
    // Check for items configured on widget, not fetched from PV
    let items = [];
    let i = 0;
    let item = widget.data("item-" + i);
    while (item !== undefined)
    {
        items.push(item);
        ++i;
        item = widget.data("item-" + i);
    }

    if (items.length > 0)
        widget.data("items", items);
}


DisplayBuilderWebRuntime.prototype.widget_update_methods["radio"] = function(widget, data)
{
    console.log("Update radio ");
    console.log(data);
    
    // Fetch items from PV?
    let items = widget.data("items");
    if (items === undefined)
    {
        items = data.labels;
        if (items === undefined)
        {
            console.log("Radio lacks items: " + widget);
            return;
        }
    }

    // Have buttons been created?
    widget.html("");
    let N = items.length;
    let width = parseFloat(widget.css("width"));
    for (let i=0; i<N; ++i)
    {
        let radio = jQuery("<input>").attr("type", "radio")
                                     .attr("name", widget.attr("id"));
        radio.click(event =>
        {
            let pv  = widget.data("pv");
            if (typeof(data.value) == "number")
            {
                console.log("TODO: Write PV " + pv + " = " + i);
                dbwr.write(pv, i);
            }
            else
            {
                console.log("TODO: Write PV " + pv + " = " + items[i]);
                dbwr.write(pv, items[i]);                            
            }
        });
        widget.append(jQuery("<label>").append(radio).append(" " + items[i] + " "));
    }
}
