
function set_poly_line_color(widget, color)
{
    // Set color of line, then also optional arrow heads
    widget.find("polyline").attr("stroke", color);
    widget.find("marker path").attr("fill", color);
}
