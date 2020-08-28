DisplayBuilderWebRuntime.prototype.widget_init_methods["combo"] = function(widget)
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


DisplayBuilderWebRuntime.prototype.widget_update_methods["combo"] = function(widget, data)
{
    // Fetch items from PV?
    let items = widget.data("items");
    if (items === undefined)
    {
        items = data.labels;
        if (items === undefined)
        {
            console.log("Combo lacks items: " + widget);
            return;
        }
    }
    
    selected=data.value
    // (Re)-create always to update selected item
    let N = items.length;

    // Clear existing HTML, to be replaced by combo
    widget.html("");

    let width = parseFloat(widget.css("width"));
    let height = parseFloat(widget.css("height"));
    
    let combo = jQuery("<select>").attr("type", "combo")
                                  .attr("name", widget.attr("id"));
    
    for (let i=0; i<N; ++i)
    {
        let option = jQuery("<option>");
        if(selected == i)
            option.attr("value",i).attr("selected","selected").text(items[i]);
        else
            option.attr("value",i).text(items[i]);
        combo.append(option)
    }
    combo.change(event =>
    {
        let pv  = widget.data("pv");
        let val=parseInt(event.currentTarget.value);
        dbwr.write(pv,  val);
    });
    combo.css("width",  width + "px")
         .css("height", height + "px");
    showWriteAccess(combo, data.readonly);
    widget.append(combo);
}
