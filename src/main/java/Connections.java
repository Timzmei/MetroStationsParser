
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Connections {
    private ArrayList<List> stations;
    private ArrayList<String> connStationList = new ArrayList<>();



    public Connections(){
        stations = new ArrayList<>();
    }

    public ArrayList<List> getStations() {
        return stations;
    }

    public void setStations(List<String> lines, Map<String, List<String>> stationList) {


        for (String l:lines) {
            String[] text = l.split("/");
            if((text.length > 4)) {
                ArrayList<Map> st = new ArrayList<>();
                if (!connStationList.contains(text[1] + text[3])) {
                    st.add(addConnectStation(text, 1, 3, stationList));
                    st.add(addConnectStation(text, 5, 4, stationList));
                    if((text.length > 6)){
                        st.add(addConnectStation(text, 7, 6, stationList));
                    }
                    if((text.length > 8)){
                        st.add(addConnectStation(text, 9, 8, stationList));
                    }
                    stations.add(st);

                }

            }
        }


    }

    private Map addConnectStation (String[] text, int line, int station, Map<String, List<String>> stationList){

        HashMap<String, String> map = new HashMap<>();
        map.put("line", text[line]);
        stationList.get(text[line]).forEach(s -> {

            if (text[station].contains(s)){
                map.put("station", s);
                connStationList.add(text[line] + s);
            }
        });

        return map;
    }
}
