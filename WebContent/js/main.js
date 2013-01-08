function editPost(form) {
	
	var edit = prompt("Type the edition:");
	form.elements["edition"].value=edit;
	form.submit();

}

function image(form) {
	
	var path = prompt("Enter the path of the image:");
	form.elements["newThing"].value=path;
	form.submit();

}


function facebook(form) {
	
	var postF = prompt("Type the post:");
	form.elements["newThing"].value=postF;
	form.submit();

}

function deletePost(form)
{
	form.elements["edition"].value="*DELETE*";
	form.submit();

}

function editReply(form, num)
{

	var edit = prompt("Type the edition:");
	form.elements["edition"].value="*EDITR*";
	form.elements["editionR"].value=edit;
	form.elements["editionID"].value=num;
	form.submit();

}

function deleteReply(form, num)
{

	form.elements["edition"].value="*DELETER*";
	form.elements["editionID"].value=num;
	form.submit();

}


function message(form)
{
	var title = prompt("Type the title:");
	var subject = prompt("Type the subject:");
	var content = prompt("Type the content:");
	var user = prompt("Type the username of the destination:");
	
	
	form.elements["messageTitle"].value=title;
	form.elements["messageSubject"].value=subject;
	form.elements["messageContent"].value=content;
	form.elements["dest"].value=user;
	
	form.submit();

}

var websocket;

window.onload = function() { // execute once the page loads
    initialize();

}

function initialize() { 
    connect('ws://'+window.location.host+'/_SD2/messageAndOnline');
}

function connect(host) { // connect to the host websocket servlet
    if ('WebSocket' in window)
        websocket = new WebSocket(host);
    else if ('MozWebSocket' in window)
        websocket = new MozWebSocket(host);
    else {
       alert("Get a real browser which supports WebSocket.");
        return;
    }

    websocket.onopen    = onOpen; // set the event listeners below
    websocket.onclose   = onClose;
    websocket.onmessage = onMessage;
    websocket.onerror   = onError;
}

function onOpen(event) {


}

function onClose(event) {


}

function onMessage(message) { // print the received message

	if(message.data.slice(-18, -1) == "just disconnected")
	{
	var ind = message.data.search("just");
	deleteFromHistory(message.data.slice(0, ind));
	}
else if(message.data.slice(-17, -1) == "is now connected")
	{
	var ind = message.data.search("is now");
	writeToHistory(message.data.slice(0, ind));
	}
	
else 
	alert(message.data);

}

function onError(event) {
    alert("WebSocket error (" + event.data + ").");

}



function deleteFromHistory(text)
{
	var online = document.getElementById('online');
	var i;
	for(i=0; i<online.childNodes.length; i++)
		{
		if(online.childNodes[i].innerHTML == text)
			{
			online.removeChild(online.childNodes[i]);
			}
		}
	
}

function writeToHistory(text) {
    var online = document.getElementById('online');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.innerHTML = text;
    online.appendChild(p);

    online.scrollTop = online.scrollHeight;
}




