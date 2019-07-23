
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
    
    widget.data("itemcount", -1);
}


DisplayBuilderWebRuntime.prototype.widget_update_methods["radio"] = function(widget, data)
{
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

    // (Re)-create buttons?
    let N = items.length;
    if (widget.data("itemcount") != N)
    {
        widget.html("");
        let width = parseFloat(widget.css("width"));
        for (let i=0; i<N; ++i)
        {
            let radio = jQuery("<input>").attr("type", "radio")
                                         .attr("name", widget.attr("id"));
            radio.click(event =>
            {
                let pv  = widget.data("pv");
                if (typeof(data.value) == "number")
                    dbwr.write(pv, i);
                else
                    dbwr.write(pv, items[i]);                            
            });
            widget.append(jQuery("<label>").append(radio).append(" " + items[i] + " "));
        }
        
        widget.data("itemcount", N);
    }
}
