package tryakash;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.providers.*;
import de.fhpotsdam.unfolding.providers.Google.*;

import java.util.List;
import java.util.Map;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.geo.Location;

import java.util.HashMap;
import java.util.Iterator;

import de.fhpotsdam.unfolding.marker.Marker;

/**
 * Visualizes life expectancy in different countries.
 * 
 * It loads the country shapes from a GeoJSON file via a data reader, and loads
 * the population density values from another CSV file (provided by the World
 * Bank). The data value is encoded to transparency via a simplistic linear
 * mapping.
 */
public class RFIDMapTry1 extends PApplet {

    UnfoldingMap map;
    HashMap<String, GPSCoordinates> lifeExpMap;
    List<Feature> countries;
    List<Marker> countryMarkers;

    public void setup() {
        size(800, 600, OPENGL);
        map = new UnfoldingMap(this, 50, 50, 700, 500, new Google.GoogleMapProvider());
        MapUtils.createDefaultEventDispatcher(this, map);

        // Load lifeExpectancy data
        lifeExpMap = loadLifeExpectancyFromCSV("Riverside.csv");
        println("Loaded " + lifeExpMap.size() + " data entries");
        Location loc; 
        ImageMarker marker;
        String key;
        Iterator<Map.Entry<String, GPSCoordinates>> i = lifeExpMap.entrySet().iterator(); 
        while(i.hasNext()){
            key = i.next().getKey();
            System.out.println(key+", value: "+lifeExpMap.get(key).x+" , "+lifeExpMap.get(key).y);
            
           loc = new Location(lifeExpMap.get(key).x, lifeExpMap.get(key).y);
        	
            marker = new ImageMarker(loc, loadImage("ui/marker.png"));
            map.addMarkers(marker);
            int zoomLevel = 15;
    	    map.zoomAndPanTo(zoomLevel, new Location(30.641602 , -96.4739));
    	    // water body location 30.635620, -96.463557
    	  
        	
        }
       // System.out.println("Asset ID:"+lifeExpMap.get+" GPS_Coordinates:"+tmpObj.x+","+tmpObj.y);
       

        // Load country polygons and adds them as markers
       // countries = GeoJSONReader.loadData(this, "countries_copy.geo2.json");
       // countryMarkers = MapUtils.createSimpleMarkers(countries);
       // map.addMarkers(countryMarkers);

        // Country markers are shaded according to life expectancy (only once)
       // shadeCountries();
    }

    public void draw() {
        // Draw map tiles and country markers
        map.draw();
    }

    // Helper method to color each country based on life expectancy
    // Red-orange indicates low (near 40)
    // Blue indicates high (near 100)
    private void shadeCountries() {
        for (Marker marker : countryMarkers) {
            // Find data for country of the current marker
            String countryId = marker.getId();
            if (lifeExpMap.containsKey(countryId)) {
                //float lifeExp = lifeExpMap.get(countryId);
                // Encode value as brightness (values range: 40-90)
               // int colorLevel = (int) map(lifeExp, 40, 90, 10, 255);
              //  marker.setColor(color(255 - colorLevel, 100, colorLevel));
            } else {
                marker.setColor(color(150, 150, 150));
            }
        }
    }

    // Helper method to load life expectancy data from file
    private HashMap<String, GPSCoordinates> loadLifeExpectancyFromCSV(String fileName) {
        HashMap<String, GPSCoordinates> lifeExpMap = new HashMap<String, GPSCoordinates>();

        String[] rows = loadStrings(fileName);
        for (String row : rows) {
            // Reads country name and population density value from CSV row
            // NOTE: Splitting on just a comma is not a great idea here, because
            // the csv file might have commas in their entries, as this one
            // does.
            // We do a smarter thing in ParseFeed, but for simplicity,
            // we just use a comma here, and ignore the fact that the first
            // field is split.//7 coordinates //2 - asset
            String[] columns = row.split(",");
            if (columns.length == 7 && !columns[6].equals("GPSLocation")) {
            	String[] doubleRiversideCoordinates = columns[6].split(":");
            	GPSCoordinates tmpObj = new GPSCoordinates(Double.parseDouble(doubleRiversideCoordinates[0]), Double.parseDouble(doubleRiversideCoordinates[1]));
                lifeExpMap.put(columns[1],tmpObj);
                
            }
           }

        return lifeExpMap;
    }

}
class GPSCoordinates{
	public double x,y;
	public GPSCoordinates(double x, double y) {
		this.x = x;
		this.y =y;
		// TODO Auto-generated constructor stub
	}
	
}
