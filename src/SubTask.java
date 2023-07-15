

public class SubTask extends Task {

    private int epicId;
    private int subTaskId;
    public SubTask(String name, String description, String status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }
    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return id;
    }

    public int getEpiccId() {
        return epicId;
    }

    public void setEpicId(Integer id) {
       // this.subTaskId = subTaskId;
        this.id = id;
    }
}
