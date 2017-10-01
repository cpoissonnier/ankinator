package infra.forms;

import play.data.validation.Constraints;

public class ReportForm {

    @Constraints.Required
    private String words;

    @Constraints.Required
    private String outputType;

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }
}
