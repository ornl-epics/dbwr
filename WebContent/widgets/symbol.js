DisplayBuilderWebRuntime.prototype.widget_update_methods["symbol"] = function(widget, data)
{
    // Expect number, fall back to parsing number from string
    let number = data.value;
    if (number === undefined)
        number = parseInt(data.text);
    
    // For array, check index
    if (Array.isArray(number))
    {
        let index = widget.data("index");
        if (index === undefined)
            index = 0;
        
        number = number[index];
    }
    
    // Only use integer part
    number = Math.trunc(number);
    let symbol = widget.data("symbol-" + number);
    // console.log("Symbol update " + data.pv + " -> " + symbol);
    
    widget.attr("src", symbol);
}