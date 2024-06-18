package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTasksIds = new ArrayList<>();

    private LocalDateTime endTime;


    public Epic(String name, String description) {
        super(name, description, 0L, null, Status.NEW);
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Integer> getSubTaskIds() {
        return subTasksIds;
    }


    public void addSubTaskId(Integer subTaskId) {
        subTasksIds.add(subTaskId);
    }


    public void removeSubTaskId(int id) {
        for (int i = 0; i < subTasksIds.size(); i++) {
            if (subTasksIds.get(i) == id) {
                subTasksIds.remove(i);
                return;
            }
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasksIds +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public void removeAllSubTask() {
        subTasksIds.clear();
    }
}