
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
    

    // (Re)-create buttons always to update selected value
    let N = items.length;
    
    widget.html("");

    let horizontal = widget.data("horizontal");
    if (horizontal === undefined)
        horizontal = true;
    let width = parseFloat(widget.css("width"));
    let height = parseFloat(widget.css("height"));
    
    selected=data.value
    
    for (let i=0; i<N; ++i)
    {
        let radio = jQuery("<input>")
        
        if(selected == i)
          radio.attr("type", "radio").attr("checked", "true").attr("name", widget.attr("id"));
        else
          radio.attr("type", "radio").attr("name", widget.attr("id"));
          
        radio.click(event =>
        {
            // Only support clicking left button to toggle
            // (middle button copies PV name and doesn't toggle)
            if (event.which != 1)
                return false;

            let pv  = widget.data("pv");
            if (typeof(data.value) == "number")
                dbwr.write(pv, i);
            else
                dbwr.write(pv, items[i]);                            
        });
        let label = jQuery("<label>");
        label.append(radio).append(" " + items[i]);
        label.css("position", "absolute");
        
        if (horizontal)
            label.css("left", i*width/N + "px")
                 .css("height", height + "px")
                 .css("line-height", height + "px");
        else
            label.css("top", i*height/N + "px");
        widget.append(label);
    }
    
    widget.data("itemcount", N);
    
    
    let buttons = widget.find("input");
    showWriteAccess(buttons, data.readonly);
}
