import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String name, String description, String status) {
        super(name, description, status);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubTaskId() {
        return subTaskIds;
    }

    public void setSubtaskIds(int id) {
        subTaskIds.add(id);
    }

    public void removeAllSubtaskIds() {
        subTaskIds.removeAll(subTaskIds);
    }

    public void removeIdFromSubtaskIds(int id) {
        subTaskIds.remove(id);
    }

    public void updateStatusEpik (String status) {
        this.status = status;
    }
}
