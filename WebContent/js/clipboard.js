/* https://stackoverflow.com/questions/400212/how-do-i-copy-to-the-clipboard-in-javascript */
function copyTextToClipboard(text)
{
    var textArea = document.createElement("textarea");
    textArea.value = text;
    textArea.style.position="fixed";  //avoid scrolling to bottom
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();

    try
    {
        if (document.execCommand('copy'))
            console.log('Copy to clipboard: ' + text);
        else
            console.log('Cannot copy to clipboard: ' + text);
    }
    catch (err)
    {
        console.error('Failed to copy to clipboard ', err);
    }

    document.body.removeChild(textArea);
}
