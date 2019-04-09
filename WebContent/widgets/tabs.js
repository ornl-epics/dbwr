
function __select_tab(event)
{
    let tab = jQuery(event.target);
    let index = tab.data("index");
    // console.log("Selected tab " + index);
 
    // tab.parent == <ul>
    // Find only direct child tabs, not tabs embedded further down inside a tab body
    let all = jQuery(tab.parent().children(".Tab"));
    all.removeClass("ActiveTab");
    let sel = jQuery(all[index]);
    sel.addClass("ActiveTab");
    
    // tab.parent.parent == <div> that holds <ul> and tab body <div>s
    all = jQuery(tab.parent().parent().children(".TabBody"));
    all.removeClass("ActiveTab");
    sel = jQuery(all[index]);
    sel.addClass("ActiveTab");
}

DisplayBuilderWebRuntime.prototype.widget_init_methods['tabs'] = function(widget)
{
    widget.children("ul.Tabs").children("li.Tab").click(__select_tab);
}