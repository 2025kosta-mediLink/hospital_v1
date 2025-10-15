package dto;

public class MonthOptionDTO {
    private String value; // "YYYY-MM"
    private String label; // "2024년 1월"

    public MonthOptionDTO() {}
    public MonthOptionDTO(String value, String label) {
        this.value = value; this.label = label;
    }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
