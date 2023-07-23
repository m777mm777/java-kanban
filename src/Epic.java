import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String name, String description, TaskStatus status) {
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

    public void updateStatusEpik (TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", subTaskIds='" + subTaskIds + '\'' +
                '}';
    }
}
