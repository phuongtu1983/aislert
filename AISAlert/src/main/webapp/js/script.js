var map;
var markers = [];

$(document).ready(function () {
    success();
});

function success()
{
    var googleLatLng = new google.maps.LatLng(10.663402010340304, 106.79608941078186);
    var mapOtn = {
        zoom: 16,
        center: googleLatLng,
        mapTypeId: google.maps.MapTypeId.SATELLITE
    }

    var Pmap = document.getElementById("map");

    map = new google.maps.Map(Pmap, mapOtn);

    var triangleCoords = [
        {lat: 10.668079801211517, lng: 106.79417967796326},
        {lat: 10.662055945753107, lng: 106.80123388767242},
        {lat: 10.65901234329248, lng: 106.79836392402649},
        {lat: 10.664987054794873, lng: 106.7909449338913},
        {lat: 10.668079801211517, lng: 106.79417967796326}
    ];
    drawPolygon(triangleCoords, 'yellow');

    triangleCoords = [
        {lat: 10.666268527714273, lng: 106.79630532860756},
        {lat: 10.663871198480797, lng: 106.79908946156502},
        {lat: 10.660950622337852, lng: 106.7959512770176},
        {lat: 10.663276805517713, lng: 106.79308265447617},
        {lat: 10.666268527714273, lng: 106.79630532860756}
    ];
    drawPolygon(triangleCoords, 'red');
/*
    // yellow
    drawCircle(10.66260333459467, 106.79513587939925, 520);
    drawCircle(10.662226400982233, 106.79475031443121, 520);
    drawCircle(10.663322933796216, 106.79588287214222, 520);
    drawCircle(10.663760491420474, 106.79634085958105, 520);
    drawCircle(10.664184868961435, 106.79679543725299, 520);
    
    //red
    drawCircle(10.66260333459467, 106.79513587939925, 200);
    drawCircle(10.662226400982233, 106.79475031443121, 200);
    drawCircle(10.663322933796216, 106.79588287214222, 200);
    drawCircle(10.662978949781976, 106.79552010416523, 200);
    drawCircle(10.663760491420474, 106.79634085958105, 200);
    drawCircle(10.663546984470017, 106.79615588901048, 200);
    drawCircle(10.663977952040657, 106.7965698537846, 200);
    drawCircle(10.664393792645135, 106.79700437380211, 200);
    drawCircle(10.664600020294783, 106.79722331065, 200);
    drawCircle(10.664184868961435, 106.79679543725299, 200);
*/
    window.setInterval(getAISAjax, 5000);
}

function getAISAjax() {
    $.ajax({
        url: "AISMapJsonServlet",
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        mimeType: 'application/json',

        success: function (json) {
            clearMarkers();
            markers = [];
            var data = json.aisList;
            $( "#alertSpan" ).css("background-color", json.alert);
            $.each(data, function (index, boat) {
                var location = new google.maps.LatLng(parseFloat(boat.latitude), parseFloat(boat.longtitude));
                addMarker(location, boat.name);
            });
        },
        error: function (data, status, er) {
        }
    });
}

function addMarker(googleLatLng, title) {
    var markerOptn = {
        position: googleLatLng,
        map: map,
        title: title
    };

    var marker = new google.maps.Marker(markerOptn);
    markers.push(marker);
}

function clearMarkers() {
    setMapOnAll(null);
}

function setMapOnAll(map) {
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(map);
    }
}

function drawPolygon(triangleCoords, color) {
    // Construct the polygon.
    new google.maps.Polygon({
        paths: triangleCoords,
        map: map,
        strokeColor: color,
        strokeOpacity: 0.8,
        strokeWeight: 2,
        fillOpacity: 0.1
    });
}

function drawCircle(lat, lon, radius) {
    new google.maps.Circle({
        strokeColor: 'blue',
        strokeOpacity: 0.8,
        strokeWeight: 2,
        fillOpacity: 0.1,
        map: map,
        center: {lat: lat, lng: lon},
        radius: radius
    });

}
