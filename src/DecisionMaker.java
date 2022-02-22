import java.util.*;

public class DecisionMaker {
    private ArrayList<Problem> problemList = new ArrayList<>();
    private boolean flagLocationFound = false;
    private String location = "";
    private ArrayList<String> keywordsFound = new ArrayList<>();
    private ArrayList<String> keywordsDiscarded = new ArrayList<>();

    private String nextQuestion = "";
    private String lastAskedAbout = "";

    private final String LOCATION_QUESTION = "Did the problem happen in *";
    private final String KEYWORD_QUESTION = "Was the problem related to *";
    private final String SUFFIX_QUESTION = "*?";
    private final String GUESS_START = "We think your problem is ";
    private final String GUESS_END = "\nWe would advice you to talk with a ";
    private final String GUESS_IF_WRONG = "\nIf we are wrong you should probably call us";

    public DecisionMaker(){
        //populate problem list
        problemList.add(new Problem("TV not turning on", "tv_company",
                new ArrayList<>(Arrays.asList("living_room")), new ArrayList<>(Arrays.asList( "tv", "electricity", "media"))));
        problemList.add(new Problem("Light not turning on", "electrician",
                new ArrayList<>(Arrays.asList("living_room", "kitchen", "bathroom")), new ArrayList<>(Arrays.asList("electricity", "light"))));
        problemList.add(new Problem("Appliance not turning on", "electrician",
                new ArrayList<>(Arrays.asList("living_room", "kitchen", "bathroom")), new ArrayList<>(Arrays.asList("electricity", "media", "appliance"))));
        problemList.add(new Problem("Electrical wiring problem", "electrician",
                new ArrayList<>(Arrays.asList("living_room", "kitchen")), new ArrayList<>(Arrays.asList("electricity", "media", "wall", "sparks", "appliance", "smoke", "bad_smell"))));
        problemList.add(new Problem("Leaking pipe", "plumber",
                new ArrayList<>(Arrays.asList("bathroom", "kitchen")), new ArrayList<>(Arrays.asList("water","floor"))));
        problemList.add(new Problem("Fridge not working", "electrician",
                new ArrayList<>(Arrays.asList("kitchen")), new ArrayList<>(Arrays.asList("water","floor","electricity"))));
        problemList.add(new Problem("Leaking toilet or bath", "plumber",
                new ArrayList<>(Arrays.asList("bathroom")), new ArrayList<>(Arrays.asList("water","floor"))));
        problemList.add(new Problem("Leaking faucet", "plumber",
                new ArrayList<>(Arrays.asList("bathroom","kitchen")), new ArrayList<>(Arrays.asList("water"))));
        problemList.add(new Problem("Gas leak", "gas_company",
                new ArrayList<>(Arrays.asList("kitchen")), new ArrayList<>(Arrays.asList("bad_smell"))));
        problemList.add(new Problem("Internet not working", "internet_provider",
                new ArrayList<>(Arrays.asList("living_room")), new ArrayList<>(Arrays.asList("media", "internet"))));
        problemList.add(new Problem("Water leak from roof", "construction_workers",
                new ArrayList<>(Arrays.asList("living_room", "kitchen", "bathroom")), new ArrayList<>(Arrays.asList("water", "wall"))));
    }

    public void discardLocation(String location){
        //saving values to discard later, to make sure we don't delete while iterating over
        ArrayList<Problem> toDelete = new ArrayList<>();
        for(Problem p: problemList){
            if(p.locationCheck(location)){
                if(p.getLocations().size() > 1){
                    p.getLocations().remove(location);
                }
                else{
                    toDelete.add(p);
                }
            }
        }
        //removing from the main list
        for(Problem p :toDelete){
            problemList.remove(p);
        }
    }

    public void confirmLocation(String location){
        if(flagLocationFound)
            return;
        //saving values to discard later, to make sure we don't delete while iterating over
        ArrayList<Problem> toDelete = new ArrayList<>();
        for(Problem p: problemList){
            if(p.locationCheck(location)){
                p.setLocations(new ArrayList<>(Arrays.asList(location)));
            }
            else{
                toDelete.add(p);
            }
        }
        //removing from the main list
        for(Problem p :toDelete) {
            problemList.remove(p);
        }
        flagLocationFound = true;
        this.location = location;
    }

    public void discardProblemsWithKeyword(String keyword){
        ArrayList<Problem> toDelete = new ArrayList<>();
        for(Problem p: problemList){
            if(p.keywordCheck(keyword)){
                toDelete.add(p);
            }
        }
        for(Problem p :toDelete){
            problemList.remove(p);
        }
    }

    public void discardProblemsWithoutKeyword(String keyword){
        ArrayList<Problem> toDelete = new ArrayList<>();
        for(Problem p: problemList){
            if(!p.keywordCheck(keyword)){
                toDelete.add(p);
            }
        }
        for(Problem p :toDelete){
            problemList.remove(p);
        }
    }

    public void removeKeywordFromProblems(String keyword){
        ArrayList<Problem> toDelete = new ArrayList<>();
        for(Problem p: problemList){
            if(p.keywordCheck(keyword)){
               p.getKeywords().remove(keyword);
               if(p.getKeywords().isEmpty())
                   toDelete.add(p);
            }
        }
        if(problemList.size() > 1){
            for(Problem p :toDelete){
                problemList.remove(p);
            }
        }
    }

    public String getNextLocation(){
        if(flagLocationFound)
            return location;
        else{
            if(problemList.size() == 0){
                return "NOT_FOUND";
            }
            return problemList.get(0).getLocations().get(0);
        }
    }


    public boolean evaluateIfProblemFound() {
        return problemList.size() <= 1;
    }

    public String getNextKeyword(){
        HashMap<String,Integer> combinedKeywords = new HashMap<>();
        for(Problem p: problemList){
            for(String k : p.getKeywords()){
                if(combinedKeywords.containsKey(k)){
                    int currentCount = combinedKeywords.get(k);
                    combinedKeywords.put(k,currentCount + 1);
                }
                else {
                    combinedKeywords.put(k, 1);
                }
            }
        }
        return Collections.max(combinedKeywords.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public void receiveUserAnswer(String messageReceived) {
        //if we are still looking for location
        String FAILED = "Sadly we could not help you, please contact us in person";
        if(!flagLocationFound){
            if(messageReceived.equals("no")){
                discardLocation(lastAskedAbout);
                String nextLoc = getNextLocation();
                if(nextLoc.equals("NOT_FOUND")){
                    nextQuestion = FAILED;
                    return;
                }
                lastAskedAbout = nextLoc;
                nextQuestion = LOCATION_QUESTION + nextLoc + SUFFIX_QUESTION;
                return;
            } else if(messageReceived.equals("yes")){
                confirmLocation(lastAskedAbout);
                nextQuestion = KEYWORD_QUESTION + getNextKeyword() + SUFFIX_QUESTION;
                lastAskedAbout = getNextKeyword();
                return;
            } else {
                nextQuestion = FAILED;
                return;
            }
        }
        //here we know the location and start troubleshooting the symptoms
        if(messageReceived.equals("no")){
            keywordsDiscarded.add(lastAskedAbout);
            discardProblemsWithKeyword(lastAskedAbout);
        } else{
            keywordsFound.add(lastAskedAbout);
            discardProblemsWithoutKeyword(lastAskedAbout);
            removeKeywordFromProblems(lastAskedAbout);
        }
        if(!evaluateIfProblemFound()){
            nextQuestion = KEYWORD_QUESTION + getNextKeyword() + SUFFIX_QUESTION;
            lastAskedAbout = getNextKeyword();
            return;
        }
        if(problemList.isEmpty()){
            nextQuestion = FAILED;
            return;
        }
        Problem lastProblem = problemList.get(0);
        nextQuestion = GUESS_START + lastProblem.getDescription() + GUESS_END + lastProblem.getSolution()
                + GUESS_IF_WRONG + "\n\nkeywords related are " + keywordsFound.toString() +" "+ lastProblem.getKeywords().toString()
                +"\n\nkeywords discarded are " + keywordsDiscarded.toString();
    }

    public String getNextQuestion() {
        if(nextQuestion.equals("")){
            lastAskedAbout = getNextLocation();
            return "Let's start with the location:\n" + LOCATION_QUESTION + lastAskedAbout + SUFFIX_QUESTION;
        }
        return nextQuestion;
    }
}
