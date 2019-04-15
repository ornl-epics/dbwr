
function __show_time(widget)
{
    let now = new Date();
    let text = "" + now.getSeconds();
    if (text.length < 2)
        text = "0" + text;
    text = now.getMinutes() + ":" + text;
    if (text.length < 5)
        text = "0" + text;
    text = now.getHours() + ":" + text;
    if (text.length < 8)
        text = "0" + text;
    
    if (widget.data("date"))
    {
        let date = now.getDate();
        if (date.length < 2)
            date = "0" + date;
        date = (now.getMonth()+1) + "-" + date;
        if (date.length < 5)
            date = "0" + date;
        date = now.getFullYear() + "-" + date;
        text = text + "<br>" + date;
    }
    widget.html(text);
}

DisplayBuilderWebRuntime.prototype.widget_init_methods['clock'] = function(widget)
{
    console.log("Should start clock updates");
    __show_time(widget);
    setInterval(() => __show_time(widget), 1000);
}
