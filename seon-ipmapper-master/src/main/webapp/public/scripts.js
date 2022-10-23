function httpGetAsync(theUrl, callback)
{
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
            callback(xmlHttp.responseText);
    }
    xmlHttp.open("GET", theUrl, true); // true for asynchronous
    xmlHttp.send(null);
}

function fillUpTable(data, tableName, fn){
    let r = new Array(), j = -1;
    for (let key=0, size=data.length; key<size; key++){
        r[++j] ='<tr><td onclick=\'' + fn + '("' + data[key].ip + '")' + '\'>';
        r[++j] = data[key].ip;
        r[++j] = '</td><td>'
        r[++j] = data[key].vpncount > 0?("VPN ("+Math.round(100*data[key].vpncount/data[key].ivdat.length)+"%)"):"";
        r[++j] = '</td><td>'
        r[++j] = data[key].cellcount >0?("[Cellular]"):"" +  data[key].wificount >0?("[WiFi]"):"";
        r[++j] = '</td></tr>';
    }
    $(tableName).html(r.join(''));
}

function fillUpTable2(data, tableName){
    let r = new Array(), j = -1;
    let a="<tr><th>Latitude</th><th>Longitude</th><th>Accuracy</th>" +
        "<th>VPN</th><th>WiFi</th><th>Cellular</th></tr>"
    for (let key=0, size=data.length; key<size; key++){
        r[++j] ='<tr><td>';
        r[++j] = data[key].lat;
        r[++j] = "</td><td>"
        r[++j] = data[key].lon;
        r[++j] = "</td><td>"
        r[++j] = data[key].accu + "m";
        r[++j] = "</td><td>"
        r[++j] = data[key].vpn?"\u2714":"";
        r[++j] = "</td><td>"
        r[++j] = data[key].wifi?"\u2714":"";
        r[++j] = "</td><td>"
        r[++j] = data[key].cellular?"\u2714":"";
        r[++j] = '</td></tr>';
    }
    $(tableName).html(a+r.join(''));
}


function myFunction(ip) {
    // Get the checkbox

    //var checkBox = document.getElementById("myCheck");
    var id="ip"+ip.toString();
    var checkBox = document.getElementById(id)
    // Get the output text
    //var text = document.getElementById("text");

    // If the checkbox is checked, display the output text
    if (checkBox.checked == true){
        array_temp[ip].show = true;
        oncheck();
    } else {
        array_temp[ip].show = false;
        oncheck();
    }
}
function set_zoom(prec){
    if (prec<250){
        return 17;
    }else if(prec<280){
        return 16;
    }else if(prec<800){
        return 15;
    }else if(prec<1600){
        return 14;
    }else if (prec<3400){
        return 13;
    }else if(prec<6800){
        return 12;
    }else if(prec<13800){
        return 11;
    }else if(27000>prec){
        return 10;
    }else if(prec<50000){
        return 9;
    }else if (prec<100000){
        return 8;
    }else{
        return 7;
    }
}

function meters_between_coords(lat1,lat2,lon1,lon2){
    let R = 6371e3; // metres
    let φ1 = lat1 * Math.PI/180; // φ, λ in radians
    let φ2 = lat2 * Math.PI/180;
    let Δφ = (lat2-lat1) * Math.PI/180;
    let Δλ = (lon2-lon1) * Math.PI/180;

    let a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
        Math.cos(φ1) * Math.cos(φ2) *
        Math.sin(Δλ/2) * Math.sin(Δλ/2);
    let c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

    return( R * c) // in metres
}

function matches(obj, list) {
    var i;
    for (i = 0; i < list.length; i++) {
        if (list[i].ip === obj) {
            return true;
        }
    }

    return false;
}
function find(obj, list) {
    let i;
    for (i = 0; i < list.length; i++) {
        if (list[i].ip === obj) {
            return list[i];
        }
    }

    return false;
}

class Location_Vis{
    constructor(coordinate, prec) {
        this.coordinate = coordinate;
        this.prec = prec;
        this.show = true;
    }
}

let user1=new Location_Vis([47.499749536055745, 19.05870280215767],300);
let user2=new Location_Vis([47.49, 19.05],100);
let user3=new Location_Vis([47.484, 19.07],0);
let array_temp= [user1,user2,user3];





function oncheck(array_temp){
    map.remove();
    let coordinate_avg=[0.0,0.0];
    let prec_max=0;
    let zoom_min=0;
    let show_much=0;
    for (let i=0;i<array_temp.length;i++){
        if (array_temp[i].show == true){
            show_much+=1;
            coordinate_avg[0]+=array_temp[i].coordinate[0];
            coordinate_avg[1]+=array_temp[i].coordinate[1];
            if (prec_max<array_temp[i].prec){
                prec_max=array_temp[i].prec;
            }
        }
    }
    if(show_much !== 0){
        coordinate_avg[0]=coordinate_avg[0]/show_much;
        coordinate_avg[1]=coordinate_avg[1]/show_much;
    }
    let mainzoom=0;

    if(show_much>0){
        coordinates_min=[200.0,200.0];
        coordinates_max=[-200.0,-200.0];
        for (let i=0;i<array_temp.length;i++){
            if (array_temp[i].show){
                if (array_temp[i].coordinate[0]>coordinates_max[0]){
                    coordinates_max[0]=array_temp[i].coordinate[0];
                }
                if(array_temp[i].coordinate[0]<coordinates_min[0]){
                    coordinates_min[0]=array_temp[i].coordinate[0];
                }
                if (array_temp[i].coordinate[1]>coordinates_max[1]){
                    coordinates_max[1]=array_temp[i].coordinate[1];
                }
                if(array_temp[i].coordinate[1]<coordinates_min[1]){
                    coordinates_min[1]=array_temp[i].coordinate[1];
                }
            }
        }
        mainzoom=meters_between_coords(coordinates_min[0],coordinates_max[0],coordinates_min[1],coordinates_max[1]);
        zoom_min=set_zoom(mainzoom/2+prec_max);
    }
    let mapOptions = {
        center:coordinate_avg,
        zoom:zoom_min,
        preferCanvas:true
    }

    map = new L.map('map',mapOptions,);

    let layer = new L.TileLayer("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png");
    map.addLayer(layer);


    for(let i=0;i<array_temp.length;i++){
        if (array_temp[i].show){
            if (array_temp[i].prec<15){
                new L.Marker(array_temp[i].coordinate).addTo(map);
            }
            else {
                new L.circle(array_temp[i].coordinate, array_temp[i].prec).addTo(map);
            }
        }
    }


}

