import java.util.ArrayList;

/**
 * Class saving data of a single problem with a list of keywords that define it
 */
public class Problem {
    private String description;
    private String solution;

    ArrayList<String> locations;
    ArrayList<String> keywords;

    /**
     *
     * @param description - string listing problem description
     * @param solution - string listing problem solution
     * @param locations - arraylist of locations that define the problem
     * @param keywords - arraylist of keywords that define the problem
     */
    public Problem(String description, String solution,  ArrayList<String> locations, ArrayList<String> keywords){
        this.description = description;
        this.solution = solution;
        this.keywords = keywords;
        this.locations = locations;
    }

    public boolean keywordCheck(String key){
        if(keywords.contains(key)){
            return true;
        }
        return false;
    }

    public boolean locationCheck(String loc){
        if(locations.contains(loc)){
            return true;
        }
        return false;
    }

    public ArrayList<String> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<String> locations) {
        this.locations = locations;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "description='" + description + '\'' +
                ", solution='" + solution + '\'' +
                ", locations=" + locations +
                ", keywords=" + keywords +
                "}\n";
    }
}
