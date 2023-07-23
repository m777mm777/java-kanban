

public class SubTask extends Task {

    private int epicById;

    public SubTask(String name, String description, TaskStatus status, int epicById) {
        super(name, description, status);
        this.epicById = epicById;
    }
    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicById = epicId;
    }

    public Integer getSubTaskId() {
        return id;
    }

    public int getEpiccId() {
        return epicById;
    }

    public void setSubTaskId(Integer id) {
        this.id = id;
    }
}
