// Support for PVs
//
// Uses epics2web web socket,
// and copies many ideas from epics2web.js

// Info for one PV
class PVInfo
{
    constructor(pv_name)
    {
        this.pv_name = pv_name;
        
        // PV Data type, 'DBR_DOUBLE'
        this.type = null;
        
        // Most recent value
        this.value = null;
        
        // Functions to invoke with this info when it changes
        this.callbacks = [];
    }    
}

class PVs
{    
    constructor()
    {
        this.socket = null;
        this.pv_infos = {}
    }
    
    // Open the one websocket which is used for all PVs
    open(on_connect)
    {
        if (this.socket == null  ||  this.socket.readyState === WebSocket.CLOSED)
        {
            let url = this.protocol + this.host + "/epics2web/monitor?clientName=WDB";
            console.log("Connecting to " + url);
            this.socket = new WebSocket(url);
            this.socket.onopen = event =>  on_connect();
            this.socket.onmessage = event =>
            {
                if (event.type !== 'message')
                {
                    console.error("Cannot handle socket message");
                    console.error(event);            
                }
                else
                {
                    let data = JSON.parse(event.data);            
                    // console.log("Received " + event.data);
                    if (data['type'] == 'info')
                    {
                        // console.log("PV INFO " + event.data);
                    }
                    else if (data['type'] == 'update')
                    {
                        let pv_name = data['pv'];
                        let value = data['value'];
                        // console.log("PV Update " + pv_name + " = " + value);
                        let info = this.pv_infos[pv_name];
                        if (info === undefined)
                            console.error("PV Update for unknown " + pv_name + " = " + value);
                        else
                        {
                            info.value = value;
                            let cb;
                            for (cb of info.callbacks)
                                cb(info);
                        }
                    }
                    else
                        console.error("Cannot handle socket message type " + event.data);
                }
            }
            this.socket.onerror = event =>
            {
                console.log("Socket error");
                console.log(event);
            };
            this.socket.onclose = event =>
            {
                console.log("Socket closed");
                console.log(event);
            };
        }
        else
        {
            console.error("Socket already open");
            return false;
        }
        return true;
    }

    subscribe(pv_name, callback)
    {
        let info = this.pv_infos[pv_name];
        let new_pv = info === undefined;
        if (new_pv)
        {
            info = new PVInfo(pv_name);
            this.pv_infos[pv_name] = info;
        }
        info.callbacks.push(callback);
        if (new_pv)
        {
            let pvs = [ pv_name ];
            let msg = { type: 'monitor', pvs: pvs };
            this.socket.send(JSON.stringify(msg));
            // console.log("Subscribed to " + pvs);
            // console.log(info);
            // console.log("Callbacks: " + info.callbacks.length);
        }
    }
}

// 'class' variables

// Local connection
//PVs.prototype.protocol = "ws://";
//PVs.prototype.host = "omaha2.ornl.gov:8080";

// Outside address needs wss
PVs.prototype.protocol = "wss://";
PVs.prototype.host = "status.sns.ornl.gov";

