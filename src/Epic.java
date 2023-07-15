import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTaskId = new ArrayList<>();

    public Epic(String name, String description, String status) {
        super(name, description, status);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubTaskId() {
        return subTaskId;
    }

    public void setSubtaskIds(int id) {
        subTaskId.add(id);
    }

    public void removeAllSubtaskIds() {
        subTaskId.removeAll(subTaskId);
    }

    public void updateStatusEpik (String status) {
        this.status = status;
    }
}
