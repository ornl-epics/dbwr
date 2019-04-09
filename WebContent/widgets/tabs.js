
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
    
    // Now that tab is visible, adjust the body size of embedded tabs
    let widget = tab.parent().parent();
    widget.find("div[data-type='tabs']").each( (index, widget) => __adjust_tab_body(jQuery(widget)));
}

// Given a tabs widget, adjust the size of each TabBody
// to use the overall widget height minus the space used by the header.
// Since header size depends on text/font in tab headers,
// this can only be done after the header has been rendered.
function __adjust_tab_body(widget)
{
    let tabs = widget.children("ul.Tabs");
    let header = tabs.height();
    if (header <= 0)
    {
        // console.log("Cannot adjust tab height at this time");
        return;
    }
    // console.log("Height of Tabs widget: " + widget.height() +
    //             ", height of tab header: " + tabs.height());
    let body_hei = widget.height() - tabs.height();
    widget.children("div.TabBody").height(body_hei);
}

DisplayBuilderWebRuntime.prototype.widget_init_methods['tabs'] = function(widget)
{
    let tabs = widget.children("ul.Tabs");
    tabs.children("li.Tab").click(__select_tab);
    __adjust_tab_body(widget);
}