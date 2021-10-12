// Get widget's data-symbol-# based on number from PV data
function __getSymbol(widget, data)
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
    return widget.data("symbol-" + number);
}

DisplayBuilderWebRuntime.prototype.widget_update_methods["symbol"] = function(widget, data)
{
    let symbol = __getSymbol(widget, data);
    // console.log("Symbol update " + data.pv + " -> " + symbol);
    widget.attr("src", symbol);
}

DisplayBuilderWebRuntime.prototype.widget_update_methods["text-symbol"] = function(widget, data)
{
    let symbol = __getSymbol(widget, data);
    // console.log("Text Symbol update " + data.pv + " -> " + symbol);
    widget.html(symbol);
}