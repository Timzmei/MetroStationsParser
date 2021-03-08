import java.util.ArrayList;

public class Line {

    private String number, name;

    private ArrayList<String> stationName;


    public Line(String line) {
        number = line;
        name = null;
        stationName = new ArrayList<>();

    }

    public String getNumberLine() {
        return number;
    }

    public String getNameLine() {
        return name;
    }

    public void setNameLine(String nameLine) {
        this.name = nameLine;
    }

    public ArrayList<String> getStationName() {
        return stationName;
    }

    public void setStationName(String station) {

        stationName.add(station);
    }

}
